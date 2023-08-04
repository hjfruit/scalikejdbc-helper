/*
 * Copyright (c) 2023 jxnu-liguobin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package bitlap.scalikejdbc.core.internal

import scala.quoted.*

/** copy from
 *  https://github.com/scalikejdbc/scalikejdbc/blob/4.0.0/scalikejdbc-syntax-support-macro/src/main/scala-3/scalikejdbc/EntityUtil.scala
 *  and update it
 */
object EntityUtil {

  final case class ConstructorParam[Q <: Quotes](name: String, excluded: Boolean)(using val quotes: Q)(
    val typeTree: quotes.reflect.TypeTree,
    val defaultValue: Option[quotes.reflect.Ref]
  )

  def constructorParams[T: Type](
    excludes: Expr[Seq[String]]
  )(using quotes: Quotes): List[ConstructorParam[quotes.type]] = {
    import quotes.reflect.*
    val sym                = TypeTree.of[T].symbol
    val primaryConstructor = sym.primaryConstructor
    if (primaryConstructor.isNoSymbol) {
      report.errorAndAbort(
        s"Could not find the primary constructor for ${sym.fullName}. type ${sym.fullName} must be a class, not trait or type parameter"
      )
    }
    val excludeNames: Set[String] = (excludes match {
      case Varargs(expr) if expr.exists(_.value.isEmpty) =>
        report.errorAndAbort(
          s"You must use String literal values for field names to exclude from case class ${sym.fullName}",
          excludes.asTerm.pos
        )
      case Varargs(expr) =>
        expr.map(_.value.get)
    }).toSet

    val comp               = sym.companionClass
    val body               = comp.tree.asInstanceOf[ClassDef].body
    val defaultValuePrefix = "$lessinit$greater$default"
    val defaultValues: Map[String, Ref] = body.collect {
      case deff @ DefDef(name, _, _, _) if name.startsWith(defaultValuePrefix) =>
        name -> Ref(deff.symbol)
    }.toMap
    primaryConstructor.tree.asInstanceOf[DefDef] match {
      case DefDef(_, params, _, _) =>
        params.head.params.zipWithIndex.map {
          case (ValDef(name, typeTree, _), index) =>
            ConstructorParam(
              name,
              excludeNames.contains(name)
            )(using quotes)(
              typeTree,
              defaultValues.get(s"$defaultValuePrefix$$${index + 1}")
            )
          case (tdf: TypeDef, _) =>
            report.errorAndAbort(
              s"You must remove the field of ${tdf.name} type in params from case class ${sym.fullName}",
              excludes.asTerm.pos
            )
        }
    }
  }
}
