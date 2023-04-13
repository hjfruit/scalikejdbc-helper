/*
 * Copyright (c) 2023 bitlap
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

package bitlap.scalikejdbc.core

import bitlap.scalikejdbc.core.EntityUtil.ConstructorParam

import scala.quoted.*
import scalikejdbc.*

import language.`3.0`

/** @author
 *    梦境迷离
 *  @version 1.0,2023/4/13
 */
object autoNamedValues {

  def applyImpl[E: Type](table: Expr[SQLSyntaxSupport[E]], entity: Expr[E], excludes: Expr[Seq[String]])(using
    quotes: Quotes
  ): Expr[List[(SQLSyntax, ParameterBinder)]] = {
    import quotes.reflect.*
    Expr.ofList(
      EntityUtil.constructorParams(excludes).collect {
        case c: ConstructorParam[quotes.type] if !c.excluded =>
          val name     = c.name
          val typeTree = c.typeTree
          val parameterBinderExpr =
            Implicits.search(
              TypeRepr.of[ParameterBinderFactory].appliedTo(typeTree.tpe)
            ) match {
              case result: ImplicitSearchSuccess =>
                Apply(
                  Select.unique(result.tree, "apply"),
                  Select.unique(entity.asTerm, name) :: Nil
                ).asExprOf[ParameterBinder]
              case _ =>
                report.errorAndAbort(
                  s"could not find ParameterBinderFactory[${typeTree.show}]"
                )
            }

          '{
            ($table.column.selectDynamic(${ Expr(name) }), ${ parameterBinderExpr })
          }
      }
    )
  }

  inline def apply[E](
    table: SQLSyntaxSupport[E],
    entity: E,
    inline excludes: String*
  ): List[(SQLSyntax, ParameterBinder)] =
    ${ applyImpl('table, 'entity, 'excludes) }
}
