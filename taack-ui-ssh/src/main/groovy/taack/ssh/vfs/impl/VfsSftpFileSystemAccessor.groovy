package taack.ssh.vfs.impl

import groovy.transform.CompileStatic
import org.apache.sshd.common.PropertyResolverUtils
import org.apache.sshd.common.util.GenericUtils
import org.apache.sshd.common.util.buffer.Buffer
import org.apache.sshd.common.util.io.IoUtils
import org.apache.sshd.sftp.server.DirectoryHandle
import org.apache.sshd.sftp.server.FileHandle
import org.apache.sshd.sftp.server.SftpFileSystemAccessor
import org.apache.sshd.sftp.server.SftpSubsystemProxy
import taack.ssh.SshEventRegistry
import taack.ssh.vfs.FileTree
import taack.ssh.vfs.impl.VfsPath
import taack.ssh.vfs.impl.VfsProvider

import java.nio.channels.Channel
import java.nio.channels.FileChannel
import java.nio.channels.FileLock
import java.nio.channels.SeekableByteChannel
import java.nio.file.*
import java.nio.file.attribute.*
import java.security.Principal
import java.util.logging.Logger

@CompileStatic
class VfsSftpFileSystemAccessor implements SftpFileSystemAccessor {

    private static final Logger log = Logger.getLogger(VfsSftpFileSystemAccessor.class.name)

//    @Override
//    Path resolveLocalFilePath(ServerSession session, SftpSubsystemProxy subsystem, Path rootDir, String remotePath) throws IOException, InvalidPathException {
//        log.fine "resolveLocalFilePath $session $subsystem $rootDir $remotePath"
//        if (rootDir instanceof VfsPath) {
////            SshEventRegistry.Vfs.resolveLocalFilePath(session, rootDir, remotePath) ?: rootDir.resolve(remotePath)
//            rootDir.resolve(remotePath)
//        } else {
//            log.warning('resolveLocalFilePath: rootDir not instanceof VfsPath')
//            null
//        }
//    }

    @Override
    LinkOption[] resolveFileAccessLinkOptions(SftpSubsystemProxy subsystem, Path file, int cmd, String extension, boolean followLinks) throws IOException {
        log.fine "resolveFileAccessLinkOptions($subsystem, $file, $cmd, $extension, $followLinks)"
        return IoUtils.getLinkOptions(followLinks)
    }

    @Override
    void putRemoteFileName(SftpSubsystemProxy subsystem, Path path, Buffer buf, String name, boolean shortName) throws IOException {
        String remoteName = FileTree.resolveRemoteFileName(name - '/..') ?: name
        log.fine "putRemoteFileName($subsystem, $path, $buf, $name, $shortName) -> $remoteName"
        buf.putString(remoteName)
    }

    @Override
    SeekableByteChannel openFile(SftpSubsystemProxy subsystem, FileHandle fileHandle, Path file, String handle, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
        log.fine "openFile($subsystem, $fileHandle, $file, $handle, $options, $attrs)"
        if (file instanceof VfsPath) {
            SshEventRegistry.Vfs.openFile(file, subsystem.serverSession, handle, options)
        } else {
            Files.newByteChannel(file, options, attrs)
        }
    }

    @Override
    FileLock tryLock(SftpSubsystemProxy subsystem, FileHandle fileHandle, Path file, String handle, Channel channel, long position, long size, boolean shared) throws IOException {
        log.fine "tryLock($subsystem, $fileHandle, $file, $handle, $channel, $position, $size, $shared)"
    }

    @Override
    void syncFileData(SftpSubsystemProxy subsystem, FileHandle fileHandle, Path file, String handle, Channel channel) throws IOException {
        log.fine "syncFileData($subsystem, $fileHandle, $file, $handle, $channel)"
    }

    @Override
    Path resolveLocalFilePath(SftpSubsystemProxy subsystem, Path rootDir, String remotePath) throws IOException, InvalidPathException {
        log.fine "resolveLocalFilePath($subsystem, ${rootDir}, $remotePath)"
        if (remotePath.contains('../')) return null

//        if (remotePath.startsWith(rootDir.toString())) {
//            if (remotePath.endsWith('/..')) return new VfsPath(rootDir.fileSystem as VfsFileSystem, remotePath.substring(0, (remotePath - '/..').lastIndexOf('/')))
//            else return new VfsPath(rootDir.fileSystem as VfsFileSystem, remotePath)
//        }
        boolean endWithParent = false
        if (remotePath.endsWith('/..')) {
            remotePath -= '/..'
            endWithParent = true
        }
        if (!remotePath.startsWith(rootDir.toString()) && remotePath.count('/') > 2) {
            remotePath = FileTree.resolveRemoteFileName(remotePath)
        }
        if (endWithParent) remotePath = remotePath.substring(0, remotePath.lastIndexOf('/'))

        Path p = SshEventRegistry.Vfs.resolveLocalFilePath(subsystem.serverSession, rootDir, remotePath)
        int i = remotePath.length() - 1
        String t = remotePath
        while (p == null && t.count('/') > 2) {
            i = t.lastIndexOf('/')
            t = t.substring(0, i)
            p = SshEventRegistry.Vfs.resolveLocalFilePath(subsystem.serverSession, rootDir, t)
        }
        if (t != remotePath) p = Paths.get(p.toString() + remotePath - t)
        p
    }

    @Override
    void closeFile(SftpSubsystemProxy subsystem, FileHandle fileHandle, Path file, String handle, Channel channel, Set<? extends OpenOption> options) throws IOException {
        log.fine "closeFile($subsystem, $fileHandle, $file, $handle, $channel, $options)"

        if ((channel == null) || (!channel.isOpen())) {
            return
        }

        if ((channel instanceof FileChannel)
                && GenericUtils.containsAny(options, IoUtils.WRITEABLE_OPEN_OPTIONS)
                && PropertyResolverUtils.getBooleanProperty(
                subsystem.serverSession, PROP_AUTO_SYNC_FILE_ON_CLOSE, DEFAULT_AUTO_SYNC_FILE_ON_CLOSE)) {
            ((FileChannel) channel).force(true)
        }

        channel.close()
        if (file instanceof VfsPath) {
            if (!options.contains(StandardOpenOption.WRITE)) SshEventRegistry.Vfs.closeFile(file, subsystem.serverSession, handle)
            else SshEventRegistry.Vfs.closeFile(file, subsystem.serverSession, handle, true)
        } else null

    }

    @Override
    void closeDirectory(SftpSubsystemProxy subsystem, DirectoryHandle dirHandle, Path dir, String handle, DirectoryStream<Path> ds) throws IOException {
        log.fine "closeDirectory($subsystem, $dirHandle, $dir, $handle, $ds)"
    }

    @Override
    Map<String, ?> readFileAttributes(SftpSubsystemProxy subsystem, Path file, String view, LinkOption... options) throws IOException {
        log.fine "readFileAttributes($subsystem, $file, $view, $options)"
        if (file instanceof VfsPath) {
            final attr = SshEventRegistry.Vfs.readAttributes(file, subsystem.serverSession)
            if (!attr) throw new NoSuchFileException(file.toString())
            if (view.contains('posix')) {
                attr.posixMap()
            } else {
                attr.basicMap()
            }
        } else null
    }

    @Override
    void setFileAttribute(SftpSubsystemProxy subsystem, Path file, String view, String attribute, Object value, LinkOption... options) throws IOException {
        log.fine "setFileAttribute($subsystem, $file, $view, $attribute, $value, $options)"
    }

    @Override
    UserPrincipal resolveFileOwner(SftpSubsystemProxy subsystem, Path file, UserPrincipal name) throws IOException {
        log.fine "resolveFileOwner($subsystem, $file, $name)"
    }

    @Override
    void setFileOwner(SftpSubsystemProxy subsystem, Path file, Principal value, LinkOption... options) throws IOException {
        log.fine "setFileOwner($subsystem, $file, $value, $options)"
    }

    @Override
    GroupPrincipal resolveGroupOwner(SftpSubsystemProxy subsystem, Path file, GroupPrincipal name) throws IOException {
        log.fine "resolveGroupOwner($subsystem, $file, $name)"
    }

    @Override
    void setGroupOwner(SftpSubsystemProxy subsystem, Path file, Principal value, LinkOption... options) throws IOException {
        log.fine "setGroupOwner($subsystem, $file, $value, $options)"
    }

    @Override
    void setFilePermissions(SftpSubsystemProxy subsystem, Path file, Set<PosixFilePermission> perms, LinkOption... options) throws IOException {
        log.fine "setFilePermissions($subsystem, $file, $perms, $options)"
    }

    @Override
    void setFileAccessControl(SftpSubsystemProxy subsystem, Path file, List<AclEntry> acl, LinkOption... options) throws IOException {
        log.fine "setFileAccessControl($subsystem, $file, $acl, $options)"
    }

    @Override
    void createDirectory(SftpSubsystemProxy subsystem, Path path) throws IOException {
        log.fine "createDirectory($subsystem, $path)"
        Files.createDirectory(path)
    }

    @Override
    void createLink(SftpSubsystemProxy subsystem, Path link, Path existing, boolean symLink) throws IOException {
        log.fine 'createLink(session, subsystem, link, existing, symLink)'
    }

    @Override
    String resolveLinkTarget(SftpSubsystemProxy subsystem, Path link) throws IOException {
        log.fine "resolveLinkTarget($subsystem, $link)"
        return link.toString()
    }

    @Override
    void renameFile(SftpSubsystemProxy subsystem, Path oldPath, Path newPath, Collection<CopyOption> opts) throws IOException {
        log.fine "renameFile($subsystem, $oldPath, $newPath, $opts)"
        if (oldPath instanceof VfsPath && newPath instanceof VfsPath)
            SshEventRegistry.Vfs.renameFile(subsystem.serverSession, oldPath, newPath)
        else Files.move(oldPath, newPath)
    }

    @Override
    void copyFile(SftpSubsystemProxy subsystem, Path src, Path dst, Collection<CopyOption> opts) throws IOException {
        log.fine "copyFile($subsystem, $src, $dst, $opts)"
    }

    @Override
    void removeFile(SftpSubsystemProxy subsystem, Path path, boolean isDirectory) throws IOException {
        boolean isVfsFolder = FileTree.localPathMatchFolder(path.toString())
        log.info "removeFile($subsystem, $path (${path instanceof VfsPath}), $isDirectory) -> $isVfsFolder"
        if (!isVfsFolder && !path instanceof VfsPath) Files.delete(path)
    }

    @Override
    DirectoryStream<Path> openDirectory(SftpSubsystemProxy subsystem, DirectoryHandle dirHandle, Path dir, String handle, LinkOption... linkOptions) throws IOException {
        log.info("openDirectory $subsystem $dirHandle $dir $handle $linkOptions")
        if (dir instanceof VfsPath) {
            log.info "new VfsProvider $dir"
            return (new VfsProvider(subsystem.serverSession)).newDirectoryStream(dir, null)
        } else {
            log.info "newDirectoryStream $dir"
            return Files.newDirectoryStream(dir)
        }

    }
}
