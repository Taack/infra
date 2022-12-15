package taack.ssh.vfs.impl

import groovy.transform.CompileStatic

import java.nio.file.Files
import java.nio.file.attribute.*

@CompileStatic
final class VfsPosixFileAttributes implements PosixFileAttributes {
    final boolean isRegFile
    final boolean isWritable
    final FileTime creationTime
    final FileTime lastModifiedTime
    final Object key
    final long size
    final String username

    VfsPosixFileAttributes(File file, String username, boolean isWritable) {
        PosixFileAttributes attrs = (Files.getFileAttributeView(file.toPath(), PosixFileAttributeView) as PosixFileAttributeView).readAttributes()
        this.isRegFile = true
        this.isWritable = isWritable
        this.username = username
        this.size = attrs.size()
        this.key = attrs.fileKey()
        this.lastModifiedTime = attrs.lastModifiedTime()
        this.creationTime = attrs.creationTime()
    }

    VfsPosixFileAttributes(boolean isRegFile, long size, Long key, String username, Date creationDate, boolean isWritable = false) {
        this.isRegFile = isRegFile
        this.size = size
        this.key = key
        this.username = username
        this.isWritable = isWritable
        creationTime = FileTime.fromMillis(creationDate.time)
        lastModifiedTime = creationTime
    }

    VfsPosixFileAttributes(boolean isRegFile, long size, Long key, String username, FileTime creationTime, FileTime lastModifiedTime, boolean isWritable = false) {
        this.isRegFile = isRegFile
        this.size = size
        this.key = key
        this.username = username
        this.isWritable = isWritable
        this.creationTime = creationTime
        this.lastModifiedTime = lastModifiedTime
    }

    @Override
    FileTime lastModifiedTime() {
        lastModifiedTime
    }

    @Override
    FileTime lastAccessTime() {
        creationTime
    }

    @Override
    FileTime creationTime() {
        creationTime
    }

    @Override
    boolean isRegularFile() {
        return isRegFile
    }

    @Override
    boolean isDirectory() {
        return !isRegFile
    }

    @Override
    boolean isSymbolicLink() {
        return false
    }

    @Override
    boolean isOther() {
        return false
    }

    @Override
    long size() {
        return size
    }

    @Override
    Object fileKey() {
        return key
    }

    @Override
    UserPrincipal owner() {
        return new UserPrincipal() {
            @Override
            String getName() {
                return username
            }
        }
    }

    @Override
    GroupPrincipal group() {
        return new GroupPrincipal() {
            @Override
            String getName() {
                return username
            }
        }
    }

    @Override
    Set<PosixFilePermission> permissions() {
        if (isWritable) {
            if (isRegFile) return Collections.unmodifiableSet([PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE, PosixFilePermission.GROUP_READ, PosixFilePermission.GROUP_WRITE, PosixFilePermission.OTHERS_READ, PosixFilePermission.OTHERS_WRITE] as Set<PosixFilePermission>)
            else return Collections.unmodifiableSet([PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.GROUP_READ, PosixFilePermission.GROUP_WRITE, PosixFilePermission.GROUP_EXECUTE, PosixFilePermission.OTHERS_READ, PosixFilePermission.OTHERS_WRITE, PosixFilePermission.OTHERS_EXECUTE] as Set<PosixFilePermission>)
        } else {
            if (isRegFile) return Collections.unmodifiableSet([PosixFilePermission.OWNER_READ, PosixFilePermission.GROUP_READ, PosixFilePermission.OTHERS_READ] as Set<PosixFilePermission>)
            else return Collections.unmodifiableSet([PosixFilePermission.OWNER_READ, PosixFilePermission.GROUP_READ, PosixFilePermission.OTHERS_READ, PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.GROUP_EXECUTE, PosixFilePermission.OTHERS_EXECUTE] as Set<PosixFilePermission>)
        }
    }

    Map<String, Object> basicMap() {
        [
                "lastAccessTime"  : creationTime,
                "lastModifiedTime": lastModifiedTime,
                "size"            : size as Long,
                "creationTime"    : creationTime,
                "isSymbolicLink"  : false,
                "isRegularFile"   : isRegFile,
                "fileKey"         : key,
                "isOther"         : false,
                "isDirectory"     : !isRegFile
        ] as Map<String, Object>
    }

    Map<String, Object> posixMap() {
        [
                "lastAccessTime"  : creationTime,
                "lastModifiedTime": lastModifiedTime,
                "size"            : size as Long,
                "creationTime"    : creationTime,
                "isSymbolicLink"  : false,
                "isRegularFile"   : isRegFile,
                "fileKey"         : key,
                "isOther"         : false,
                "isDirectory"     : !isRegFile,
                "owner"           : owner().name,
                "group"           : group().name,
                "permissions"     : permissions()
        ] as Map<String, Object>
    }

}
