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

import scala.annotation.implicitNotFound
import scala.deriving.*
import scala.compiletime.*

/** Generate `fromOrdinal` for scala3 enum.
 *  @author
 *    梦境迷离
 *  @version 1.0,2023/4/27
 */
@implicitNotFound("Cannot find IntToEnum[${T}] instance for ${T}")
trait IntToEnum[T]:
  def from(ordinal: Int): T
end IntToEnum

object IntToEnum:

  inline def derived[T <: reflect.Enum](using m: Mirror.SumOf[T]): IntToEnum[T] =
    val elemInstances =
      summonAll[Tuple.Map[m.MirroredElemTypes, ValueOf]].productIterator.asInstanceOf[Iterator[ValueOf[T]]].map(_.value)
    val productArity = summonAll[Tuple.Map[m.MirroredElemLabels, ValueOf]].productArity
    val mapping      = ((0 until productArity).toSeq zip elemInstances.toSeq).toMap
    (ordinal: Int) => {
      if (ordinal < 0 || ordinal >= productArity) {
        throw new ArrayIndexOutOfBoundsException(s"Valid enumeration ordinal is 0 to $productArity")
      }
      mapping(ordinal)
    }
  end derived

end IntToEnum
