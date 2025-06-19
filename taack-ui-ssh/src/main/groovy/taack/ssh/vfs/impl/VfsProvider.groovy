package taack.ssh.vfs.impl

import groovy.transform.CompileStatic
import org.apache.sshd.server.session.ServerSession
import taack.ssh.SshEventRegistry

import java.nio.channels.SeekableByteChannel
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileAttribute
import java.nio.file.attribute.FileAttributeView
import java.nio.file.spi.FileSystemProvider
import java.util.logging.Logger

/**
 * VfsProvider provides 1 VfsFileSystem per user
 */
@CompileStatic
final class VfsProvider extends FileSystemProvider {

    private static final Logger log = Logger.getLogger(VfsProvider.class.name)
    final ServerSession serverSession
    private VfsFileSystem fileSystem
//    static final Map<String, VfsFileSystem> userVfs = [:]

    VfsProvider(ServerSession serverSession) {
        this.serverSession = serverSession
    }

    @Override
    String getScheme() {
        return 'intranet'
    }

    @Override
    FileSystem newFileSystem(URI uri, Map<String, ?> env) throws IOException {
        log.fine "newFileSystem $uri $env"
        if (scheme == uri.scheme) {
            fileSystem = fileSystem ?: new VfsFileSystem(this)
            fileSystem
        } else null
    }

    @Override
    FileSystem getFileSystem(URI uri) {
        log.fine "getFileSystem $uri"
        fileSystem
    }

    @Override
    Path getPath(URI uri) {
        log.fine "getPath $uri"
        return null
    }

    @Override
    SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> set, FileAttribute<?>... fileAttributes) throws IOException {
        log.fine "newByteChannel $path $set $fileAttributes"
        return null
    }

    @Override
    DirectoryStream<Path> newDirectoryStream(Path path, DirectoryStream.Filter<? super Path> filter) throws IOException {
        log.fine "newDirectoryStream $path $filter"
        if (path instanceof VfsPath) SshEventRegistry.Vfs.newDirectoryStream(path, serverSession)
        else null
    }

    @Override
    void createDirectory(Path path, FileAttribute<?>... fileAttributes) throws IOException {
        log.fine "createDirectory $path $fileAttributes"

    }

    @Override
    void delete(Path path) throws IOException {
        log.fine "delete $path"

    }

    @Override
    void copy(Path path, Path path1, CopyOption... copyOptions) throws IOException {
        log.fine "copy $path $path1"

    }

    @Override
    void move(Path path, Path path1, CopyOption... copyOptions) throws IOException {
        log.fine "move $path $path1"

    }

    @Override
    boolean isSameFile(Path path, Path path1) throws IOException {
        log.fine "isSameFile $path $path1"
        return false
    }

    @Override
    boolean isHidden(Path path) throws IOException {
        log.fine "isHidden $path"
        return false
    }

    @Override
    FileStore getFileStore(Path path) throws IOException {
        log.fine "getFileStore $path"
        return null
    }

    /**
     * For relative path access is granted
     * @param path
     * @param accessModes
     * @throws IOException
     */
    @Override
    void checkAccess(Path path, AccessMode... accessModes) throws IOException {
        log.fine "checkAccess $path $accessModes"
//        if (!path instanceof VfsPath) throw new IOException("path: ${path.first()} not VfsPath class")
//        else {
//            VfsPath p = path as VfsPath
//            if (p.isAbsolute()) {
//                final String username = p.first.split().first()
//                if (p.vfsFileSystem.username == username) throw new SecurityException("User ${p.vfsFileSystem.username} try to access ${username} folder")
//            }
//        }
    }

    @Override
    final <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> aClass, LinkOption... linkOptions) {
        log.fine "getFileAttributeView $path $aClass $linkOptions"
        return null
    }

    @Override
    final <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> aClass, LinkOption... linkOptions) throws IOException {
        log.fine "readAttributes $path $aClass $linkOptions"
        if (path instanceof VfsPath) SshEventRegistry.Vfs.readAttributes(path, serverSession) as A
        else null
    }

    @Override
    Map<String, Object> readAttributes(Path path, String s, LinkOption... linkOptions) throws IOException {
        log.fine "readAttributes $path $s $linkOptions"
        if (path instanceof VfsPath) {
            final attr = SshEventRegistry.Vfs.readAttributes(path, serverSession)
            if (s.contains('posix')) {
                attr?.posixMap()
            } else {
                attr?.basicMap()
            }
        } else null
    }

    @Override
    void setAttribute(Path path, String s, Object o, LinkOption... linkOptions) throws IOException {
        log.fine "setAttribute $path $s $o $linkOptions"

    }
}
