package taack.ssh

import groovy.transform.CompileStatic
import org.apache.sshd.common.config.keys.AuthorizedKeyEntry
import org.apache.sshd.git.GitLocationResolver
import org.apache.sshd.git.pack.GitPackCommandFactory
import org.apache.sshd.server.ServerBuilder
import org.apache.sshd.server.SshServer
import org.apache.sshd.server.auth.AsyncAuthException
import org.apache.sshd.server.auth.pubkey.PublickeyAuthenticator
import org.apache.sshd.server.auth.pubkey.RejectAllPublickeyAuthenticator
import org.apache.sshd.server.channel.ChannelSession
import org.apache.sshd.server.command.CommandFactory
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider
import org.apache.sshd.server.session.ServerSession
import org.apache.sshd.server.shell.ShellFactory
import org.apache.sshd.sftp.server.SftpSubsystemFactory
import org.eclipse.jgit.api.Git
import taack.ssh.command.CommandContextToken
import taack.ssh.command.CommandTree
import taack.ssh.command.ICommand
import taack.ssh.command.SshCommandPrompt
import taack.ssh.git.GitPath
import taack.ssh.vfs.FileTree
import taack.ssh.vfs.impl.VfsFactory
import taack.ssh.vfs.impl.VfsPath
import taack.ssh.vfs.impl.VfsPosixFileAttributes
import taack.ssh.vfs.impl.sftp.VfsSftpEventListener
import taack.ssh.vfs.impl.sftp.VfsSftpFileSystemAccessor

import java.nio.channels.SeekableByteChannel
import java.nio.file.*
import java.security.PublicKey
import java.util.logging.Logger

@CompileStatic
enum SshEventRegistry {
    INSTANCE

    private static final Logger log = Logger.getLogger(SshEventRegistry.class.name)

    private static UserPubKeyRetriever userPubKeyRetriever
    private static Set<UserGitRepoChecker> userGitRepoCheckers = []

    interface UserPubKeyRetriever {
        String userPubKeys(String username)
    }

    interface UserGitRepoChecker {
        String rootRepoDirectory(String username, String repo)

        boolean userCanAccessGitRepo(String username, String repo)

        boolean userCanCreateGitRepo(String username, String repo)
    }

    static initUserPubKeyRetriever(UserPubKeyRetriever userPubKeyRetriever) {
        this.userPubKeyRetriever = userPubKeyRetriever
    }

    static final class SshUserRegistrar {
        static initUserPubKeyRetriever(UserPubKeyRetriever userPubKeyRetriever) {
            SshEventRegistry.initUserPubKeyRetriever(userPubKeyRetriever)
        }

        static initSsh() {
            SshEventRegistry.initSsh()
        }

        static destroySsh() {
            SshEventRegistry.destroySsh()
        }
    }

    interface VfsEvent {
        FileTree initVfsAppEvent(String username)

        void closeVfsConnection(String username)
    }

    interface CommandEvent {
        CommandRegister initCommandAppEvent(String username)

        String processCommandEvent(Iterator<CommandContextToken> tokens, InputStream inputStream, OutputStream outputStream)

        void closeCommandConnection(String username)
    }

    static final class GitHelper {
        static void registerUserGitRepoChecker(UserGitRepoChecker userGitRepoChecker) {
            userGitRepoCheckers.add(userGitRepoChecker)
        }

        static boolean canAccess(String username, String repo) {
            for (def gitRepoChecker : userGitRepoCheckers) {
                if (gitRepoChecker.userCanAccessGitRepo(username, repo)) return true
            }
            false
        }

        static boolean canCreate(String username, String repo) {
            for (def gitRepoChecker : userGitRepoCheckers) {
                if (gitRepoChecker.userCanCreateGitRepo(username, repo)) return true
            }
            false
        }

        static String root(String username, String repo) {
            for (def gitRepoChecker : userGitRepoCheckers) {
                final String dirname = gitRepoChecker.rootRepoDirectory(username, repo)
                if (dirname) return dirname
            }
            null
        }
    }

    @CompileStatic
    static final class Vfs {
        final private static Map<String, Set<ServerSession>> serverSessions = [:]
        final private static Map<String, Map<String, FileTree>> vfsPerUserPerApp = [:]
        final private static Map<String, VfsEvent> vfsEventPerApp = [:]

        private static void registerSession(ServerSession session) {
            log.fine "registerSession $session"
            synchronized (serverSessions) {
                if (!serverSessions.containsKey(session.username)) {
                    serverSessions.put(session.username, new HashSet<ServerSession>())
                }
                serverSessions[session.username].add(session)
                if (!vfsPerUserPerApp[session.username]) vfsPerUserPerApp.put(session.username, [:])
            }
        }

        private static void unregisterSession(ServerSession session) {
            log.fine "unregisterUser $session"
            synchronized (serverSessions) {
                serverSessions[session.username].remove(session)
                if (serverSessions[session.username].isEmpty()) {
                    log.info "unregisterSession, remove user: ${session.username}"
                    serverSessions.remove(session.username)
                    // Why ?? vfsEventPerApp.remove(session.username)
                    vfsPerUserPerApp.remove(session.username)
                }
            }
        }

        private static void registerAppForSession(ServerSession session, String appPath, FileTree vfsRegister) {
            log.fine "registerAppForUser $appPath $session"
            synchronized (serverSessions) {
                vfsPerUserPerApp[session.username].put(appPath, vfsRegister)
            }
        }

        /**
         * App will receive vfs events after this call. Typically done once at application startup.
         * @param appName
         * @param vfsEvent
         */
        static void initVfsEventProvider(String appName, VfsEvent vfsEvent) {
            log.fine "registerAppForUser $appName $vfsEvent"
            synchronized (serverSessions) {
                vfsEventPerApp[appName] = vfsEvent
            }
        }

        /**
         * When a new connection is established, create the user home directory
         * @param username
         */
        static void newVfsConnection(ServerSession session) {
            log.fine "newVfsConnection $session"
            synchronized (serverSessions) {
                registerSession(session)
                vfsEventPerApp.find {
                    FileTree fs = it.value.initVfsAppEvent(session.username)
                    if (fs) {
                        log.info "FS: $fs for $it on ${session}"
                        registerAppForSession(session, it.key, fs)
                    } else {
                        log.warning "newVfsConnection NO FS.. for ${session}"
                    }
                }
            }
        }

        /**
         * When a new connection is closed, release the user home directory. If user role changes, modifications will be applied after.
         * @param username
         */
        static void closeVfsConnection(ServerSession session) {
            log.fine "closeVfsConnection $session"
            synchronized (serverSessions) {
                vfsEventPerApp.find {
                    it.value.closeVfsConnection(session.username)
                }
                unregisterSession(session)
            }
        }

        static VfsPosixFileAttributes readAttributes(final VfsPath path, final ServerSession serverSession) {
            log.fine "readAttributes $path $serverSession"
            // TODO: Add code for relative path
            VfsPath p = path.toAbsolutePath().normalize() as VfsPath
            final int nc = p.nameCount
            if (nc == 1 || (nc == 2 && ((VfsPath) p.getName(1)).first == ".")) {
                final String username = ((VfsPath) p.getName(0)).first
                if (username)
                    return new VfsPosixFileAttributes(false, 0, 0, username, dateInitialized)
            } else if (nc >= 2) {
                final String username = ((VfsPath) p.getName(0)).first
                final String appName = ((VfsPath) p.getName(1)).first
                if (username) {
                    synchronized (serverSessions) {
                        return vfsPerUserPerApp[serverSession.username][appName]?.readAttributes(p)
                    }
                }
            }
            log.severe "return null for $path !!!"
            throw new IOException("$path does not exists ...")
        }

        static DirectoryStream<Path> newDirectoryStream(VfsPath path, ServerSession serverSession) {
            log.fine "newDirectoryStream $path"
            // TODO: Add code for relative path
            VfsPath p = path.normalize() as VfsPath
            final int nc = p.nameCount

            new DirectoryStream<VfsPath>() {

                @Override
                Iterator<Path> iterator() {
                    if (nc == 1) {
                        final String username = ((VfsPath) p.getName(0)).first
                        if (username) {
                            synchronized (serverSessions) {
                                List<VfsPath> ret = []
                                for (def k in vfsPerUserPerApp[serverSession.username].keySet()) {
                                    ret.add(new VfsPath(p.vfsFileSystem, k, null))
                                }
                                //return vfsPerUserPerApp[serverSession.username].keySet().collect { new VfsPath(p.vfsFileSystem, it, null) }.iterator() as Iterator<Path>
                                return ret.iterator() as Iterator<Path>
                            }
                        }
                    } else if (nc >= 2) {
                        final String username = ((VfsPath) p.getName(0)).first
                        final String appName = ((VfsPath) p.getName(1)).first

                        if (username) {
                            // Delegates to the App FileSystem
                            synchronized (serverSessions) {
                                return vfsPerUserPerApp[serverSession.username][appName]?.newDirectoryIterator(p)
                            }
                        }
                    }
                    null
                }

                @Override
                void close() throws IOException {

                }
            } as DirectoryStream<Path>
        }

        static SeekableByteChannel openFile(VfsPath file, ServerSession serverSession, String handle, Set<? extends OpenOption> options) {
            log.info "openFile $file"
            final int nc = file.nameCount
            if (nc >= 2) {
                final String username = ((VfsPath) file.getName(0)).first
                final String appName = ((VfsPath) file.getName(1)).first

                if (username) {
                    // Delegates to the App FileSystem
                    synchronized (serverSessions) {
                        return vfsPerUserPerApp[serverSession.username][appName]?.openFile(file, handle, options)
                    }
                }
            }
        }

        static void closeFile(VfsPath file, ServerSession serverSession, String handle, boolean forWriting = false) {
            log.fine "closeFile $file"
            final int nc = file.nameCount
            if (nc >= 2) {
                final String username = ((VfsPath) file.getName(0)).first
                final String appName = ((VfsPath) file.getName(1)).first

                if (username) {
                    // Delegates to the App FileSystem
                    synchronized (serverSessions) {
                        vfsPerUserPerApp[serverSession.username][appName]?.closeFile(file, handle)
                    }
                }
            }
        }

        static void renameFile(ServerSession serverSession, VfsPath oldPath, VfsPath newPath) {
            log.info "renameFile $oldPath $newPath"
            final int ncOp = oldPath.nameCount
            final int ncNp = newPath.nameCount
            if (ncOp >= 2 && ncNp == ncOp) {
                final String username = ((VfsPath) oldPath.getName(0)).first
                final String appName = ((VfsPath) oldPath.getName(1)).first

                if (username) {
                    // Delegates to the App FileSystem
                    synchronized (serverSessions) {
                        vfsPerUserPerApp[serverSession.username][appName]?.renameFile(oldPath, newPath)
                    }
                }
            }
        }

        static Path resolveLocalFilePath(ServerSession serverSession, Path rootDir, String remotePath) {
            log.info "resolveLocalFilePath $serverSession $rootDir $remotePath"
            VfsPath path = new VfsPath(null, remotePath)
            final int nc = path.nameCount
            if (nc > 2) {
                final String username = ((VfsPath) path.getName(0)).first
                final String appName = ((VfsPath) path.getName(1)).first

                if (username) {
                    // Delegates to the App FileSystem
                    synchronized (serverSessions) {
                        return vfsPerUserPerApp[serverSession.username][appName]?.resolveLocalFilePath(rootDir as VfsPath, remotePath)
                    }
                }
            } else {
                if (rootDir instanceof VfsPath) {
                    return rootDir.resolve(remotePath)
                } else {
                    log.warning "${rootDir} not instance of VfsPath"
                }
            }
        }
    }

    static final class VfsProvider {
        static void initVfsEventProvider(String appName, VfsEvent vfsEvent) {
            log.fine "initVfsEventProvider $appName $vfsEvent"
            Vfs.initVfsEventProvider(appName, vfsEvent)
        }
    }

    @CompileStatic
    static final class Command {
        private enum DefaultCommands {
            LS(new CommandTree("ls", [], "list contextual command")),
            CD(new CommandTree("cd", [
                    new CommandTree.Arg(CommandTree.Arg.Type.STRING, false, true, null, "App Name"),
            ] as List<ICommand>, "change directory context"))

            DefaultCommands(CommandTree commandTree) {
                this.commandTree = commandTree
            }

            final CommandTree commandTree
        }

        private static final CommandRegister commonCommands = new CommandRegister(
                DefaultCommands.LS.commandTree,
                DefaultCommands.CD.commandTree,
        )

        private static Map<String, CommandEvent> commandEventPerApp = [:]
        private static Map<String, String> commandUserContext = [:]
        private static Map<String, Map<String, CommandRegister>> commandsPerUserPerApp = [:]

        static void initCommandEventProvider(String appName, CommandEvent commandEvent) {
            commandEventPerApp[appName] = commandEvent
        }

        private static void registerUser(String username) {
            if (!commandsPerUserPerApp[username]) commandsPerUserPerApp.put(username, [:])
        }

        private static void unregisterUser(String username) {
            commandsPerUserPerApp.remove(username)
        }

        private static void registerPerUserPerApp(String username, String appPath, CommandRegister commandRegister) {
            if (!commandsPerUserPerApp[username][appPath]) commandsPerUserPerApp[username].put(appPath, commandRegister)
        }

        /**
         * App will receive command events after this call. Typically done once at application startup.
         * @param appName
         * @param vfsEvent
         */
        static void intCommandEventProvider(String appName, CommandEvent commandEvent) {
            commandEventPerApp[appName] = commandEvent
        }

        /**
         * When a new connection is established, create the user command registry.
         * @param username
         */
        static void newCommandConnection(String username) {
            registerUser(username)
            commandEventPerApp.find {
                CommandRegister cmds = it.value.initCommandAppEvent(username)
                if (cmds) {
                    registerPerUserPerApp(username, it.key, cmds)
                }
            }
        }

        /**
         * When a new connection is closed, release the user command registry. If user role changes, modifications will be applied after.
         * @param username
         */
        static void closeCommandConnection(String username) {
            commandEventPerApp.find {
                it.value.closeCommandConnection(username)
            }
            unregisterUser(username)
        }

        /**
         * Process the command in static context. 1 user can only open 1 session
         * @param username
         * @param command
         * @return
         */
        static String processCommandForUser(String username, String command, InputStream inputStream, OutputStream outputStream) {
            def cc = commonCommands.parseL2R(command)
            String appName = commandUserContext[username]
            log.info "processCommandForUser $username $appName $cc"
            if (!cc && appName) return commandEventPerApp[appName].processCommandEvent(commandsPerUserPerApp[username][appName].parseL2R(command), inputStream, outputStream)
            else if (cc) {
                def c1 = cc.next()
                switch (c1.command.substring(c1.startPos, c1.endPos)) {
                    case "ls":
                        if (commandUserContext.keySet().contains(username)) return "none."
                        else return commandsPerUserPerApp[username].keySet().join("\n")
                    case "cd":
                        def a = cc.next()
                        if (a) {
                            def cdNewName = a.command.substring(a.startPos + 1, a.endPos - 1)
                            if (commandEventPerApp.keySet().contains(cdNewName)) {
                                commandUserContext[username] = cdNewName
                                return "done."
                            }
                        } else {
                            commandUserContext.remove(username)
                        }
                }
            }
            return null
        }

        static CommandRegister contextualCommandRegisterForUser(String username) {
            def app = commandUserContext[username]
            if (!app) return null
            commandsPerUserPerApp[username][app]
        }
    }

    static SshServer sshd = null
    static Date dateInitialized = new Date()

    static void initSsh() {
        if (sshd != null || userPubKeyRetriever == null) return

        PublickeyAuthenticator auth = new PublickeyAuthenticator() {
            @Override
            boolean authenticate(String username, PublicKey key, ServerSession session) throws AsyncAuthException {
            log.info "authenticate $username ${key.algorithm} ${key.format} ${key.getEncoded().encodeHex().toString()}"

            synchronized (INSTANCE) {
                final String incomingPk = key.getEncoded().encodeHex().toString().substring(23)
                for (String pubKey in userPubKeyRetriever.userPubKeys(username).split('\n')) {
                    pubKey = pubKey.trim()
                    if (pubKey.isEmpty()) continue
                    log.info "authenticate testing entry: $pubKey"
                    try {
                        final String pkUser = AuthorizedKeyEntry.parseAuthorizedKeyEntry(pubKey.trim()).keyData.encodeHex().toString().substring(37)
                        if (pkUser && pkUser == incomingPk) {
                            log.info "authenticate $username authenticated with $pkUser"
                            return true
                        } else {
                            log.fine "authenticate continue for $username, failed for $pkUser != $incomingPk"
                        }
                    } catch(e) {
                        log.info "authenticate exception: ${e}"
                        e.printStackTrace()
                    }
                }
            }

            log.severe "authenticate failed for $username"
            return false            }
        }
//                (String username, PublicKey key, ServerSession session) -> {
//            log.info "authenticate $username ${key.algorithm} ${key.format} ${key.getEncoded().encodeHex().toString()}"
//
//            synchronized (INSTANCE) {
//                final String incomingPk = key.getEncoded().encodeHex().toString().substring(23)
//                for (String pubKey in userPubKeyRetriever.userPubKeys(username)?.lines()?.toArray()) {
//                    pubKey = pubKey.trim()
//                    if (pubKey.isEmpty()) continue
//                    log.info "authenticate testing entry: $pubKey"
//                    final String pkUser = AuthorizedKeyEntry.parseAuthorizedKeyEntry(pubKey.trim()).keyData.encodeHex().toString().substring(37)
//                    if (pkUser && pkUser == incomingPk) {
//                        log.info "authenticate $username authenticated with $pkUser"
//                        return true
//                    } else {
//                        log.fine "authenticate continue for $username, failed for $pkUser != $incomingPk"
//                    }
//                }
//            }
//
//            log.severe "authenticate failed for $username"
//            return false
//        }

        def sshdBuilder = ServerBuilder.builder().publickeyAuthenticator(auth)
        sshd = sshdBuilder.build()
        sshd.setPort(22_222)
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(Path.of("hostkey.ser")))

        // Git
        final resolver = new GitLocationResolver() {
            @Override
            Path resolveRootDirectory(String command, String[] args2, ServerSession session, FileSystem fs) throws IOException {
                log.info "GitLocationResolver::resolveRootDirectory $command, $args2, ${session}, $fs"
                final String repoName = args2[1]
                final String username = session.username
                try {
                    if (GitHelper.canAccess(username, repoName)) {
                        final realRepoPath = GitHelper.root(username, repoName)
                        final f = new File(realRepoPath)
                        if (!f.exists()) {
                            if (!GitHelper.canCreate(username, repoName)) {
                                log.severe "User $username try to create $repoName, but he is not granted"
                                throw new SecurityException("User $username try to create $repoName, but he is not granted")
                            }
                            Files.createDirectory(f.toPath())
                            try {
                                Git git = Git.init()
                                        .setDirectory(f)
                                        .setBare(true)
                                        .call()
                                log.info "Created a new repository at ${git.getRepository().getDirectory()}"
                                //git.close()
                            } catch (e) {
                                log.severe "Cannot Create new repo !! $e"
                                throw e
                            }
                        }
                        log.info("GitLocationResolver::resolveRootDirectory returns ${f.path.toString() - repoName}")
                        return new GitPath(f.toPath().toString() - repoName, repoName)
                    } else log.severe "User $username cannot access repo $repoName"

                } catch (e) {
                    log.severe "resolveRootDirectory e: ${e.message}"
                    e.printStackTrace()
                }
            }
        }
        sshd.setCommandFactory(new GitPackCommandFactory()
                .withGitLocationResolver(resolver)
                .withDelegate(new CommandFactory() {
                    // Very important, will allow Rsync and other facilities to be supported
                    @Override
                    org.apache.sshd.server.command.Command createCommand(ChannelSession channel, String command) throws IOException {
                        println "setCommandFactory !!!! AUO $channel $command"
                        return null
                    }
                })
        )

        // Sftp
        SftpSubsystemFactory sftpSubsystemFactory = new SftpSubsystemFactory.Builder()
                .withFileSystemAccessor(new VfsSftpFileSystemAccessor())
                .build()
        sftpSubsystemFactory.addSftpEventListener(new VfsSftpEventListener())
        sshd.setSubsystemFactories(Collections.singletonList(sftpSubsystemFactory))
        sshd.setFileSystemFactory(new VfsFactory())

        // Ssh
        sshd.setShellFactory(new ShellFactory() {
            @Override
            org.apache.sshd.server.command.Command createShell(ChannelSession channel) throws IOException {
                log.info "ShellFactory::createCommand $channel"
                return new SshCommandPrompt()
            }
        })
        sshd.start()
        log.info "Ssh Started !!"
    }

    static void destroySsh() {
        if (sshd == null) return
        sshd.stop(true)
    }
}
