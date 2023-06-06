package bitlap.scalikejdbc

import scalikejdbc.*
import scalikejdbc.DB
import scalikejdbc.DB.*
import zio.*
import zio.internal.Blocking

/** @author
 *    梦境迷离
 *  @version 1.0,2023/6/6
 */
extension (db: DB.type)
  def zioLocalTx[A](execution: DBSession => Task[A])(implicit
    context: CPContext = NoCPContext,
    settings: SettingsProvider = SettingsProvider.default
  ): Task[A] =
    localTx(execution)(context, BitlapTxBoundary.zioTxBoundary, settings)
