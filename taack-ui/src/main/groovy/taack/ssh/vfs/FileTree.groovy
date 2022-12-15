package taack.ssh.vfs

import groovy.transform.CompileStatic
import taack.ssh.vfs.impl.VfsFileSystem
import taack.ssh.vfs.impl.VfsPath
import taack.ssh.vfs.impl.VfsPosixFileAttributes

import javax.naming.OperationNotSupportedException
import java.nio.channels.SeekableByteChannel
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import java.nio.file.attribute.FileTime
import java.util.logging.Logger

interface FileCallback {
    VfsPosixFileAttributes onGetFileAttributes(FileTree fileTree, FileTree.File file)

    SeekableByteChannel onFileOpen(FileTree fileTree, FileTree.File file, String handle, Set<? extends OpenOption> options)

    void onFileClose(FileTree fileTree, FileTree.File file, String handle)

    void onRenameFile(FileTree fileTree, FileTree.File file, VfsPath oldPath, VfsPath newPath)
}

interface FolderCallback {
    void onGetFolder(FileTree fileTree, FileTree.Folder folder)

    SeekableByteChannel onFolderFileCreate(FileTree fileTree, FileTree.Folder folder, VfsPath path, String handle, Set<? extends OpenOption> options)

    void onFolderFileCreateClose(FileTree fileTree, FileTree.Folder folder, VfsPath path, String handle)
}

@CompileStatic
final class FileTree {

    private static final Logger log = Logger.getLogger(FileTree.class.name)
    private static final Map<String, Folder> localFileNameToFolder = [:]

    final Date creationDate
    final String username
    Folder root

    FileTree(String username) {
        this.creationDate = new Date()
        this.username = username
    }

    final class Folder implements INode {
        final boolean isWritable
        final String directoryName
        INode parent
        final List<INode> directChildren = []
        final int inode
        final FolderCallback folderCallback
        private VfsPosixFileAttributes pAttributes = null
        final String realDirectoryPath

        Folder(FolderCallback folderCallback, String directoryName, boolean isWritable = false, String realDirectoryPath = null) {
            this.directoryName = directoryName
            this.inode = System.identityHashCode(this)
            this.folderCallback = folderCallback
            this.isWritable = isWritable
            this.realDirectoryPath = realDirectoryPath
            localFileNameToFolder.put(realDirectoryPath, this)
        }

        VfsPosixFileAttributes getAttributes() {
            if (!pAttributes && folderCallback) {
                folderCallback.onGetFolder(FileTree.this, this)
                pAttributes = new VfsPosixFileAttributes(false, directChildren.size(), inode, username, creationDate, isWritable)
            } else if (!pAttributes && !folderCallback) {
                pAttributes = new VfsPosixFileAttributes(false, directChildren.size(), inode, username, creationDate, isWritable)
            }
            pAttributes
        }

        @Override
        String getName() {
            directoryName
        }

        @Override
        void addChild(INode node) {
            directChildren.add(node)
        }

        @Override
        Iterator<INode> getChildren() {
            directChildren.iterator()
        }

        String getRemoteName() {
            StringBuffer p = new StringBuffer()
            Folder f = this
            while(f) {
                p.insert(0, "/" + f.name)
                f = f.parent as Folder
            }
            p.insert(0, "/" + username)
            p.toString()
        }
    }

    final class File implements INode {
        final boolean isWritable
        String fileName
        final long inode
        final FileCallback fileCallback
        String realFilePath
        INode parent
        Long internalId

        File(FileCallback fileCallback, String fileName, boolean isWritable = false, String realFilePath = null, Long internalId = null) {
            this.fileName = fileName
            this.fileCallback = fileCallback
            this.inode = internalId ?: System.identityHashCode(this)

            this.isWritable = isWritable
            this.realFilePath = realFilePath
            this.internalId = internalId
        }

        VfsPosixFileAttributes getAttributes(long fileSize) {
            java.io.File f = realFilePath ? new java.io.File(realFilePath) : null
            if (f && f.exists()) {
                FileTime creationTime = (FileTime) Files.getAttribute(f.toPath(), "creationTime")
                FileTime lastModified = (FileTime) Files.getAttribute(f.toPath(), "lastModifiedTime")
                fileSize = f ? f.size() : fileSize
                new VfsPosixFileAttributes(true, fileSize, inode, username, creationTime, lastModified, isWritable)
            } else if (f && !f.exists()) {
                log.warning "${f.path} does not exist..."
                new VfsPosixFileAttributes(true, fileSize, inode, username, creationDate, isWritable)
            } else new VfsPosixFileAttributes(true, fileSize, inode, username, creationDate, isWritable)
        }

        @Override
        String getName() {
            fileName
        }

        @Override
        void addChild(INode node) {
            throw new OperationNotSupportedException("cannot add child to a file")
        }

        @Override
        Iterator<INode> getChildren() {
            return null
        }
    }

    class Builder {
        final String name
        final List<INode> nodes = []

        Builder(String name = null) {
            this.name = name
        }

        Builder addNodes(INode... node) {
            node.each {
                nodes.add(it)
            }
            this
        }

        Builder addNode(INode node) {
            nodes.add(node)
            this
        }

        Builder addFile(FileCallback cb, String name, boolean isWritable = false, String realPath = null, Long internalId = null) {
            nodes.add(new File(cb, name, isWritable, realPath, internalId))
            this
        }

        Builder addFile(FileCallback cb, String name, boolean isWritable = false, Long internalId) {
            nodes.add(new File(cb, name, isWritable, null, internalId))
            this
        }

        Builder addFileFromRealFolder(FileCallback cb, String folder, boolean isWritable = false) {
            try {
                for (Path p : (Files.list(new java.io.File(folder).toPath()).toArray() as List<Path>)) {
                    def f = new File(cb, p.fileName.toString(), isWritable)
                    f.realFilePath = folder + "/" + p.fileName.toString()
                    nodes.add(f)
                }
            } catch (e) {
                log.warning(e.message)
            }
            this
        }

        Builder addFiles(FileCallback cb, boolean isWritable = false, String... names) {
            for (String name : names) {
                nodes.add(new File(cb, name, isWritable))
            }
            this
        }

        Folder toFolder(FolderCallback folderCallback = null) {
            final f = new Folder(folderCallback, name)
            for (INode node : nodes) {
                node.parent = f
                f.addChild(node)
            }
            f
        }

        Folder toFolder(Folder folder) {
            for (INode node : nodes) {
                node.parent = folder
                folder.addChild(node)
            }
            folder
        }
    }

    Builder createBuilder(String name = null) {
        new Builder(name)
    }

    Folder createFolder(FolderCallback folderCallback = null, String name, boolean isWritable = false, String realDirectoryPath = null) {
        new Folder(folderCallback, name, isWritable, realDirectoryPath)
    }

    private boolean checkPath(final VfsPath path) {
        if (!root || !username || !path.absolute) {
            log.warning "checkPath fails for ${path} with username: ${username}, absolute: ${path.absolute}"
            return false
        }
        final int nc = path.nameCount
        if (nc >= 2 && ((VfsPath) path.getName(0)).first == username && ((VfsPath) path.getName(1)).first == root.name) return true
        else {
            log.warning "checkPath fails for ${path} with nc: ${nc}"
            return false
        }
    }

    private INode pathToINode(final VfsPath path) {
        final p = path
        final int nc = p.nameCount
        final String lastName = ((VfsPath) p.getName(nc - 1)).first
        final newNc = lastName == "." ? nc - 1 : nc
        Iterator<INode> traversing = [(INode) root].iterator()
        INode matching = null
        for (int i = 1; i < newNc; i++) {
            final String partName = p.getName(i)
            matching = null
            for (INode adjacent : traversing.toList()) {
                if (adjacent.name == partName) {
                    matching = adjacent
                    if (matching instanceof Folder) {
                        traversing = matching.children
                    }
                    break
                }
            }
        }
        log.fine "pathToINode for $path = ${matching}"
        return matching
    }

    VfsPosixFileAttributes readAttributes(final VfsPath path) {
        log.fine "readAttributes $path $root"
        if (checkPath(path)) {
            INode iNode = pathToINode(path)
            if (iNode instanceof Folder) {
                Folder matching = iNode
                return matching.attributes
            } else if (iNode instanceof File) {
                File matching = iNode
                return matching.fileCallback.onGetFileAttributes(this, matching)
            } else null
        } else null
    }

    Iterator<Path> newDirectoryIterator(VfsPath path) {
        log.fine "newDirectoryIterator $path ${path.isAbsolute()} $root"
        if (checkPath(path)) {
            INode iNode = pathToINode(path)
            if (iNode instanceof File) {
                return null
            } else {
                Folder traversing = iNode as Folder
                // traversing.children.collect { new VfsPath(path.vfsFileSystem, path.first + "/" + it.name, null) }.iterator() as Iterator<Path>
                List<VfsPath> ret = []
                def it = traversing.children
                while (it.hasNext()) {
                    INode c = it.next()
                    ret.add(new VfsPath(path.vfsFileSystem, path.first + "/" + c.name, null))
                }
                return ret.iterator() as Iterator<Path>
            }
        } else {
            return null
        }
    }

    SeekableByteChannel openFile(VfsPath path, String handle, Set<? extends OpenOption> options) {
        log.fine "openFile $path $handle $options"
        if (checkPath(path)) {
            INode iNode = pathToINode(path)
            if (iNode instanceof File) {
                return ((File) iNode).fileCallback.onFileOpen(this, (File) iNode, handle, options)
            } else {
                VfsPath folderPath = path.parent as VfsPath
                if (checkPath(folderPath)) {
                    INode iNodeFolder = pathToINode(folderPath)
                    if (iNodeFolder instanceof Folder) {
                        return ((Folder) iNodeFolder).folderCallback.onFolderFileCreate(this, (Folder) iNodeFolder, path, handle, options)
                    } else null
                } else null
            }
        } else null
    }

    void closeFile(VfsPath path, String handle, boolean forWriting = false) {
        log.fine "closeFile $path $handle $forWriting"
        if (checkPath(path)) {
            INode iNode = pathToINode(path)
            if (iNode instanceof File) {
                ((File) iNode).fileCallback.onFileClose(this, iNode, handle)
            } else if (iNode instanceof Folder) {
                ((Folder) iNode).folderCallback.onFolderFileCreateClose(this, iNode, path, handle)
            } else null
        } else null
    }

    void renameFile(VfsPath oldPath, VfsPath newPath) {
        log.fine "renameFile $oldPath $newPath"
        if (checkPath(oldPath) && checkPath(newPath)) {
            INode newNode = pathToINode(newPath)
            INode oldNode = pathToINode(oldPath)

            if (!oldNode && newNode) {
                if (newNode instanceof File) {
                    ((File) newNode).fileCallback.onRenameFile(this, newNode, oldPath, newPath)
                }
            } else if (oldNode && !newNode) {
                if (oldNode instanceof File) {
                    ((File) oldNode).fileCallback.onRenameFile(this, oldNode, oldPath, newPath)
                }
            }
        }
    }

    Path resolveLocalFilePath(VfsPath rootDir, String remotePath) {
        log.fine "resolveLocalFilePath $rootDir $remotePath"
        VfsPath path = new VfsPath(rootDir.fileSystem as VfsFileSystem, remotePath)
        if (checkPath(path)) {
            INode iNode = pathToINode(path)
            if (iNode instanceof File) {
                File match = iNode
                log.info "matching ${match.fileName}: ${match.realFilePath}"
                return match.realFilePath ? new java.io.File(match.realFilePath).toPath() : path
            } else if (iNode instanceof Folder) {
                Folder match = iNode
                log.info "matching ${match.directoryName}: ${match.realDirectoryPath}"
                return match.realDirectoryPath ? new java.io.File(match.realDirectoryPath).toPath() : path
            } else {
                log.info "no match for ${remotePath}"
            }
        }
        return null
    }

    static String resolveRemoteFileName(String localPath) {
        if (localPath.count('/') <= 2) return null
        String lpParent = localPath
        String remotePath = localFileNameToFolder[lpParent]?.remoteName
        if (remotePath) return remotePath
        int i = lpParent.length()
        int prev = i
        while (i > 0 && !remotePath) {
            lpParent = lpParent.substring(0, i)
            remotePath = localFileNameToFolder[lpParent]?.remoteName
            prev = i
            i = lpParent.lastIndexOf('/')
        }
        if (prev != -1 && remotePath) remotePath + localPath.substring(prev)
        else null
    }

    static boolean localPathMatchFolder(String localPath) {
        localFileNameToFolder[localPath] != null
    }
}
