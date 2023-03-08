package bitlap.scalikejdbc.binders

import java.sql.Connection

/** @author
 *    梦境迷离
 *  @version 1.0,2023/3/8
 */
object Test1 extends App {

  given Connection = ???

  // TODO
  println(DeriveParameterBinderFactory.array[String, List](OType.String, _.toArray))

}
