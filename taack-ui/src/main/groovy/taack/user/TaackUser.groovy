package taack.user

import grails.compiler.GrailsCompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@GrailsCompileStatic
@EqualsAndHashCode(includes = 'username')
@ToString(includes = 'username', includeNames = true, includePackage = false)
abstract class TaackUser implements Serializable, IUserNotification {

    private static final long serialVersionUID = 1

    String username
    String password
    Boolean enabled = true
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired
}
