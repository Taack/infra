package taack.ssh.vfs.impl

import groovy.transform.CompileStatic
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory
import org.apache.sshd.common.session.SessionContext
import org.apache.sshd.server.session.ServerSession

import java.nio.file.FileSystem
import java.util.logging.Logger

@CompileStatic
class VfsFactory extends VirtualFileSystemFactory {
    private static final Logger log = Logger.getLogger(VfsFactory.class.name)

    @Override
    FileSystem createFileSystem(SessionContext session) throws IOException {
        log.info "createFileSystem($session)"
        def provider = new VfsProvider(session as ServerSession)
        return new VfsFileSystem(provider)
    }
}
