package taack.ssh.vfs.impl

import groovy.transform.CompileStatic

import java.nio.file.*
import java.nio.file.attribute.UserPrincipalLookupService
import java.nio.file.spi.FileSystemProvider
import java.util.logging.Logger

@CompileStatic
class VfsFileSystem extends FileSystem {

    private static final Logger log = Logger.getLogger(VfsFileSystem.class.name)

    final VfsProvider provider

    VfsFileSystem(VfsProvider provider) {
        this.provider = provider
    }

    @Override
    FileSystemProvider provider() {
        log.fine 'provider'
        provider
    }

    @Override
    void close() throws IOException {
        log.info 'close'

    }

    @Override
    boolean isOpen() {
        log.info 'isOpen'

        return true
    }

    @Override
    boolean isReadOnly() {
        log.info 'isReadOnly'

        return false
    }

    @Override
    String getSeparator() {
        log.fine 'getSeparator'

        return '/'
    }

    @Override
    Iterable<Path> getRootDirectories() {
        log.info 'getRootDirectories'

        return null
    }

    @Override
    Iterable<FileStore> getFileStores() {
        log.info 'getFileStores'

        return null
    }

    @Override
    Set<String> supportedFileAttributeViews() {
        log.fine 'supportedFileAttributeViews'

        return Collections.unmodifiableSet(['basic', 'posix'] as Set<String>)
    }

    @Override
    Path getPath(String s, String... strings) {
        log.fine "getPath $s $strings"

        return new VfsPath(this, s, strings)
    }

    @Override
    PathMatcher getPathMatcher(String s) {
        log.info "getPathMatcher $s"

        return null
    }

    @Override
    UserPrincipalLookupService getUserPrincipalLookupService() {
        log.info 'getUserPrincipalLookupService'

        return null
    }

    @Override
    WatchService newWatchService() throws IOException {
        log.info 'newWatchService'

        return null
    }
}
