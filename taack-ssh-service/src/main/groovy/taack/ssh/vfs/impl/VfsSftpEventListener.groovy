package taack.ssh.vfs.impl

import groovy.transform.CompileStatic
import org.apache.sshd.server.session.ServerSession
import org.apache.sshd.sftp.server.DirectoryHandle
import org.apache.sshd.sftp.server.FileHandle
import org.apache.sshd.sftp.server.Handle
import org.apache.sshd.sftp.server.SftpEventListener
import taack.ssh.SshEventRegistry

import java.nio.file.CopyOption
import java.nio.file.Path
import java.util.logging.Logger

@CompileStatic
class VfsSftpEventListener implements SftpEventListener {
    private static final Logger log = Logger.getLogger(VfsSftpEventListener.class.name)

    @Override
    void received(ServerSession session, int type, int id) throws IOException {
        log.fine "received $session $type $id"
    }

    @Override
    void receivedExtension(ServerSession session, String extension, int id) throws IOException {
        log.info "receivedExtension($session, $extension, $id)"
    }

    @Override
    void initialized(ServerSession session, int version) throws IOException {
        log.info "initialized $session $version"
        SshEventRegistry.Vfs.newVfsConnection(session)
    }

    @Override
    void exiting(ServerSession session, Handle handle) throws IOException {
        log.info "exiting $session $handle"
    }

    @Override
    void destroying(ServerSession session) throws IOException {
        log.info "destroying $session"
        SshEventRegistry.Vfs.closeVfsConnection(session)
    }

    @Override
    void opening(ServerSession session, String remoteHandle, Handle localHandle) throws IOException {
        log.info "opening($session, $remoteHandle, $localHandle)"
    }

    @Override
    void open(ServerSession session, String remoteHandle, Handle localHandle) throws IOException {
        log.info "open $session $remoteHandle $localHandle"
    }

    @Override
    void openFailed(ServerSession session, String remotePath, Path localPath, boolean isDirectory, Throwable thrown) throws IOException {
        log.info "openFailed($session, $remotePath, $localPath, $isDirectory, $thrown)"
    }

    @Override
    void readingEntries(ServerSession session, String remoteHandle, DirectoryHandle localHandle) throws IOException {
        log.info "readingEntries($session, $remoteHandle, $localHandle)"
    }

    @Override
    void readEntries(ServerSession session, String remoteHandle, DirectoryHandle localHandle, Map<String, Path> entries) throws IOException {
        log.info "readEntries($session, $remoteHandle, $localHandle, $entries)"
    }

    @Override
    void reading(ServerSession session, String remoteHandle, FileHandle localHandle, long offset, byte[] data, int dataOffset, int dataLen) throws IOException {
        log.finest "reading($session, $remoteHandle, $localHandle, $offset, ****, $dataOffset, $dataLen)"
    }

    @Override
    void read(ServerSession session, String remoteHandle, FileHandle localHandle, long offset, byte[] data, int dataOffset, int dataLen, int readLen, Throwable thrown) throws IOException {
        log.finest "read $session $remoteHandle $localHandle $offset $dataOffset $dataLen $readLen"
    }

    @Override
    void writing(ServerSession session, String remoteHandle, FileHandle localHandle, long offset, byte[] data, int dataOffset, int dataLen) throws IOException {
        log.fine "writing($session, $remoteHandle, $localHandle, $offset, ****, $dataOffset, $dataLen)"
    }

    @Override
    void written(ServerSession session, String remoteHandle, FileHandle localHandle, long offset, byte[] data, int dataOffset, int dataLen, Throwable thrown) throws IOException {
        log.fine "written($session, $remoteHandle, $localHandle, $offset, ****, $dataOffset, $dataLen, $thrown)"
    }

    @Override
    void blocking(ServerSession session, String remoteHandle, FileHandle localHandle, long offset, long length, int mask) throws IOException {
        log.info "blocking($session, $remoteHandle, $localHandle, $offset, $length, $mask)"
    }

    @Override
    void blocked(ServerSession session, String remoteHandle, FileHandle localHandle, long offset, long length, int mask, Throwable thrown) throws IOException {
        log.info "blocked($session, $remoteHandle, $localHandle, $offset, $length, $mask, $thrown)"
    }

    @Override
    void unblocking(ServerSession session, String remoteHandle, FileHandle localHandle, long offset, long length) throws IOException {
        log.info "unblocking($session, $remoteHandle, $localHandle, $offset, $length)"
    }

    @Override
    void unblocked(ServerSession session, String remoteHandle, FileHandle localHandle, long offset, long length, Throwable thrown) throws IOException {
        log.info "unblocked($session, $remoteHandle, $localHandle, $offset, $length, $thrown)"
    }

    @Override
    void closing(ServerSession session, String remoteHandle, Handle localHandle) throws IOException {
        log.fine "closing($session, $remoteHandle, $localHandle)"
    }

    @Override
    void closed(ServerSession session, String remoteHandle, Handle localHandle, Throwable thrown) throws IOException {
        log.fine "closed($session, $remoteHandle, $localHandle, $thrown)"
    }

    @Override
    void creating(ServerSession session, Path path, Map<String, ?> attrs) throws IOException {
        log.info "creating($session, $path, $attrs)"
    }

    @Override
    void created(ServerSession session, Path path, Map<String, ?> attrs, Throwable thrown) throws IOException {
        log.info "created($session, $path, $attrs, $thrown)"
    }

    @Override
    void moving(ServerSession session, Path srcPath, Path dstPath, Collection<CopyOption> opts) throws IOException {
        log.info "moving($session, $srcPath, $dstPath, $opts)"
    }

    @Override
    void moved(ServerSession session, Path srcPath, Path dstPath, Collection<CopyOption> opts, Throwable thrown) throws IOException {
        log.info "moved($session, $srcPath, $dstPath, $opts, $thrown)"
    }

    @Override
    void removing(ServerSession session, Path path, boolean isDirectory) throws IOException {
        log.info "removing($session, $path, $isDirectory)"
    }

    @Override
    void removed(ServerSession session, Path path, boolean isDirectory, Throwable thrown) throws IOException {
        log.info "removed($session, $path, $isDirectory, $thrown)"
    }

    @Override
    void linking(ServerSession session, Path source, Path target, boolean symLink) throws IOException {
        log.info "linking($session, $source, $target, $symLink)"
    }

    @Override
    void linked(ServerSession session, Path source, Path target, boolean symLink, Throwable thrown) throws IOException {
        log.info "linked($session, $source, $target, $symLink, $thrown)"
    }

    @Override
    void modifyingAttributes(ServerSession session, Path path, Map<String, ?> attrs) throws IOException {
        log.info "modifyingAttributes($session, $path, $attrs)"
    }

    @Override
    void modifiedAttributes(ServerSession session, Path path, Map<String, ?> attrs, Throwable thrown) throws IOException {
        log.info "modifiedAttributes($session, $path, $attrs, $thrown)"
    }
}
