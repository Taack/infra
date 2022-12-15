/**
 * Provide class necessary to trigger AST Transformation
 */
package taack.ast.annotation

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Class annotation that trigger AST Transformations and
 * duplicate user symbols with their '_' underscore counterpart.
 *
 * Underscore added symbols return additional information
 * (see the {@link taack.ast.type.FieldInfo} class) to be used
 * on UI closures (in taack-ui gradle module)
 *
 * @since 1.0
 */
@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.FIELD])
@interface TaackEnumName {
    String name();
}