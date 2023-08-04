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

package bitlap.scalikejdbc.core

import scalikejdbc.*
import scalikejdbc.DB.*
import zio.*

object ZIOTxBoundary:

  private def logTxError[A](effect: => A): URIO[Any, Unit] =
    ZIO
      .attempt(effect)
      .onExit {
        case Exit.Success(_)     => ZIO.unit
        case Exit.Failure(cause) => ZIO.logErrorCause(cause)
      }
      .ignore

  given zioTxBoundary[A]: TxBoundary[Task[A]] = new TxBoundary[Task[A]]:

    def finishTx(result: Task[A], tx: Tx): Task[A] =
      result.onExit(cleanup =>
        cleanup match
          case Exit.Success(_) => logTxError(tx.commit())
          case Exit.Failure(cause) =>
            ZIO.logErrorCause(cause) *> logTxError(tx.rollback())
      )

    override def closeConnection(result: Task[A], doClose: () => Unit): Task[A] =
      result.ensuring(logTxError(doClose.apply()))
