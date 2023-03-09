package bitlap.scalikejdbc.binders

import scalikejdbc.{ ParameterBinderFactory, TypeBinder }

/** @author
 *    梦境迷离
 *  @version 1.0,2023/3/9
 */
trait JsonBinders extends ArrayBinders {

  given type2Jsonb[T](using map: T => String): ParameterBinderFactory[T] =
    DeriveParameterBinder.jsonb[T](map)

  given json2Type[T](using map: String => T): TypeBinder[T] =
    DeriveTypeBinder.string[T](map)

}
