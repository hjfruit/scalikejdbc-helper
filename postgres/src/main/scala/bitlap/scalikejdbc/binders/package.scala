package bitlap.scalikejdbc.binders

import scala.quoted.*

/** @author
 *    梦境迷离
 *  @version 1.0,2023/3/8
 */

enum OType(val name: String):
  self =>
  case String extends OType("string")
  case Int    extends OType("int")
  case Json   extends OType("json")
  case Jsonb  extends OType("jsonb")

inline def showCode_[A](inline a: A): String = ${ showCode[A]('{ a }) }

inline def typeName[A]: String = ${ typeName_[A] }

def showCode[A: Type](a: Expr[A])(using quotes: Quotes): Expr[String] =
  import quotes.reflect.*
  Expr(Printer.TreeShortCode.show(a.asTerm))

def typeName_[A: Type](using quotes: Quotes): Expr[String] =
  import quotes.reflect.*
  Expr(Type.show[A].split("\\.").lastOption.getOrElse(Type.show[A]))
