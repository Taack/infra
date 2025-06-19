package taack.ssh.git

import groovy.transform.CompileStatic

import java.nio.file.*
import java.util.logging.Logger

@CompileStatic
class GitPath implements Path {

    private static final Logger log = Logger.getLogger(GitPath.class.name)

    final String rootDirectory
    final String pathString

    GitPath(String rootDirectory, String pathString) {
        log.fine("new GitPath $rootDirectory $pathString")
        this.rootDirectory = rootDirectory
        this.pathString = pathString
    }

    private Collection<String> sliced() {
        pathString.split('/').findAll { !it.empty }
    }

    @Override
    FileSystem getFileSystem() {
        log.fine("getFileSystem $rootDirectory $pathString")
        return FileSystems.getDefault()
    }

    @Override
    boolean isAbsolute() {
        log.fine("isAbsolute $rootDirectory $pathString")
        return pathString.startsWith(rootDirectory)
    }

    @Override
    Path getRoot() {
        log.fine("getRoot $rootDirectory $pathString")
        return new GitPath(rootDirectory, '')
    }

    @Override
    Path getFileName() {
        log.fine("getFileName $rootDirectory $pathString")
        new GitPath(rootDirectory, pathString.split('/').last())
    }

    @Override
    Path getParent() {
        log.fine("getParent $rootDirectory $pathString")
        if (pathString.lastIndexOf('/') != -1)
            return new GitPath(rootDirectory, pathString.substring(0, pathString.lastIndexOf('/')))
        else return null
    }

    @Override
    int getNameCount() {
        log.fine("getNameCount $rootDirectory $pathString")
        return (pathString).count('/')
    }

    @Override
    Path getName(int i) {
        log.fine("getName $rootDirectory $pathString $i")
        return new GitPath(rootDirectory, pathString.split('/')[i])
    }

    @Override
    Path subpath(int i, int i1) {
        log.fine("subpath $rootDirectory $pathString $i $i1")
        return new GitPath(rootDirectory, pathString.split('/')[i..i1].join('/'))
    }

    @Override
    String toString() {
        return pathString
    }

    @Override
    boolean startsWith(Path path) {
        log.fine("startsWith $rootDirectory $pathString $path")
        return path.toString().startsWith(pathString)
    }

    @Override
    boolean endsWith(Path path) {
        log.fine("endsWith $rootDirectory $pathString $path")
        return path.toString().endsWith(pathString)
    }

    @Override
    Path normalize() {
        log.fine("normalize $rootDirectory $pathString")
        def sl = sliced().toList()
        sl.remove('.')
        boolean contains2dots = true
        while (contains2dots) {
            int index = sl.findIndexOf { it == '..' }
            if (index > 0) { // '../toto' stop here !!
                sl.remove(index - 1)
                sl.remove(index - 1)
            } else contains2dots = false
        }
        String newFirst = (pathString.startsWith('/') ? '/' : '') + sl.join('/') + (pathString.endsWith('/') ? '/' : '')
        if (newFirst == pathString) this
        else new GitPath(rootDirectory, newFirst)
    }

    @Override
    Path resolve(Path other) {
        log.fine("resolve $rootDirectory $pathString $other")
        if (other.isAbsolute()) other
        else if (other.toString().empty) this
        else {
            if (isAbsolute()) new GitPath(rootDirectory, (pathString - other.toString() + '/' + other.toString()).replace('//', '/'))
            else new GitPath(rootDirectory, (rootDirectory + '/' + pathString - other.toString() + '/' + other.toString()).replace('//', '/'))
        }
    }

    @Override
    Path relativize(Path path) {
        log.fine("relativize $rootDirectory $pathString $path")
        return new GitPath(rootDirectory, path.toString() - pathString)
    }

    @Override
    URI toUri() {
        log.fine("toUri $rootDirectory $pathString")
        return new URI("file://${toAbsolutePath()}")
    }

    @Override
    Path toAbsolutePath() {
        log.fine("toAbsolutePath $rootDirectory $pathString")
        if (isAbsolute()) this
        else new GitPath(rootDirectory, (rootDirectory + '/' + pathString).replace('//', '/'))
    }

    @Override
    Path toRealPath(LinkOption... linkOptions) throws IOException {
        log.fine("toRealPath $rootDirectory $pathString $linkOptions")
        return this
    }

    @Override
    WatchKey register(WatchService watchService, WatchEvent.Kind<?>[] kinds, WatchEvent.Modifier... modifiers) throws IOException {
        log.fine("register $rootDirectory $pathString")
        return null
    }

    @Override
    int compareTo(Path path) {
        log.fine("compareTo $rootDirectory $pathString $path")
        return path.toString() <=> pathString
    }
}
