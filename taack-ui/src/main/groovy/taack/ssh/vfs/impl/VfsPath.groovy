package taack.ssh.vfs.impl

import groovy.transform.CompileStatic

import java.nio.file.*
import java.util.logging.Logger

@CompileStatic
class VfsPath implements Path {

    private static final Logger log = Logger.getLogger(VfsPath.class.name)

    final VfsFileSystem vfsFileSystem
    final String first
    final String[] more

    VfsPath(VfsFileSystem fileSystem, String first, String[] more) {
        this.vfsFileSystem = fileSystem
        this.first = first
        this.more = more
    }

    private Collection<String> sliced() {
        first.split("/").findAll { !it.empty }
    }

    private Collection<String> sliced(Range<Integer> range) {
        def c = first.split("/").findAll { !it.empty }
        c.toList()[range]
    }

    @Override
    FileSystem getFileSystem() {
        vfsFileSystem
    }

    @Override
    boolean isAbsolute() {
        first.startsWith("/")
    }

    @Override
    Path getRoot() {
        return new VfsPath(vfsFileSystem, "/")
    }

    @Override
    Path getFileName() {
        if (nameCount == 0) null
        else new VfsPath(vfsFileSystem, sliced().last())
    }

    @Override
    Path getParent() {
        if (nameCount >= 2) new VfsPath(vfsFileSystem, "/" + sliced(0..(nameCount - 2)).join("/"))
        else null
    }

    @Override
    int getNameCount() {
        sliced().size()
    }

    @Override
    Path getName(int i) {
        if (i >= nameCount) throw new IllegalArgumentException()
        new VfsPath(vfsFileSystem, sliced()[i])
    }

    @Override
    Path subpath(int i, int i1) {
        log.fine "subpath $i $i1"

        return null
    }

    @Override
    boolean startsWith(Path other) {
        log.fine "startsWith $this $other"
        return false
    }

    @Override
    boolean endsWith(Path other) {
        log.fine "endsWith $this $other"
        return false
    }

    @Override
    Path normalize() {
        def sl = sliced().toList()
        sl.remove(".")
        boolean contains2dots = true
        while (contains2dots) {
            int index = sl.findIndexOf { it == ".." }
            if (index > 0) { // "../toto" stop here !!
                sl.remove(index - 1)
                sl.remove(index - 1)
            } else contains2dots = false
        }
        String newFirst = (first.startsWith("/") ? "/" : "") + sl.join("/") + (first.endsWith("/") ? "/" : "")
        if (newFirst == first) this
        else new VfsPath(vfsFileSystem, newFirst, more)
    }

    @Override
    Path resolve(Path other) {
        log.fine "resolve $this $other"
        if (other instanceof VfsPath) {
            if (other.isAbsolute()) other
            else if (other.first.empty) this
            else new VfsPath(vfsFileSystem, (this.first + "/").replace("//", "/") + other.first, null)
        } else null
    }

    @Override
    Path relativize(Path other) {
        log.info "relativize $this $other"

        return null
    }

    @Override
    URI toUri() {
        log.info "toUri $this"

        return null
    }

    @Override
    Path toAbsolutePath() {
        if (!absolute) {
            new VfsPath(vfsFileSystem, "/${vfsFileSystem.provider.serverSession?.username}/$first", null)
        } else this
    }

    @Override
    Path toRealPath(LinkOption... linkOptions) throws IOException {
        log.info "toRealPath $this $linkOptions"

        return this
    }

    @Override
    WatchKey register(WatchService watchService, WatchEvent.Kind<?>[] kinds, WatchEvent.Modifier... modifiers) throws IOException {
        log.info "register $watchService $kinds $modifiers"

        return null
    }

    @Override
    int compareTo(Path other) {
        log.fine "compareTo $this $other"

        if (other instanceof VfsPath) this.first <=> other.first
        else 0
    }

    @Override
    String toString() {
        first
    }
}
