package bitlap.scalikejdbc

/** @author
 *    梦境迷离
 *  @version 1.0,2023/3/8
 */
package object binders {

  enum OType(val name: String):
    self =>
    case String extends OType("string")
    case Int    extends OType("int")
    case Long   extends OType("long")
    case Double extends OType("double")
}
