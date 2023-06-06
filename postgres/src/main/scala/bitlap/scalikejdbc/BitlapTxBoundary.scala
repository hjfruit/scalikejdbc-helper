package bitlap.scalikejdbc

import scalikejdbc.*
import scalikejdbc.DB.*
import zio.*

object BitlapTxBoundary:

  given zioTxBoundary[A]: TxBoundary[Task[A]] = new TxBoundary[Task[A]]:

    def finishTx(result: Task[A], tx: Tx): Task[A] =
      ZIO.blocking(
        result.onExit(cleanup =>
          cleanup match
            case Exit.Success(_) => ZIO.attempt(tx.commit()).ignore
            case Exit.Failure(_) => ZIO.attempt(tx.rollback()).ignore
        )
      )

    override def closeConnection(result: Task[A], doClose: () => Unit): Task[A] =
      ZIO.blocking(result.ensuring(ZIO.attempt(doClose.apply()).ignore))
