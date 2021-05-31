package com.github.adityaK93.transactions

import cats.effect._
import cats.implicits._
import cats.effect.IO
import com.github.adityaK93.transactions.entities._

import scala.collection.mutable

final case class TransactionController[F[_]](
    private val transactions: mutable.HashMap[Long, Transaction]
)(implicit
    e: Effect[F]
) {

  def getTransaction(id: Long): F[Option[Transaction]] =
    e.delay(transactions.get(id))

  def getAllTransactions: F[List[Transaction]] =
    e.delay(transactions.values.toList)

  def getTransactionTypes(ttype: String): F[List[Long]] =
    for {
      res <- e.delay(transactions.filter(x => x._2._type == ttype))
    } yield res.keys.toList

  def addTransactionWithId(transaction: Transaction, id: Long): F[Long] =
    for {
      _ <- e.delay(transactions += (id -> transaction))
    } yield id

  def getTransactionSum(id: Long): F[Sum] =
    for {
      sm <- e.delay(
        Sum(
          traverseChildren(id, transactions)
            .foldRight(
              transactions
                .get(id)
                .fold(transactions.get(id).map(_.amount).getOrElse(0.0))(_.amount)
            )(_.amount + _)
        )
      )
    } yield sm

  def traverseChildren(
      parent_id: Long,
      m: mutable.HashMap[Long, Transaction]
  ): Iterable[Transaction] =
    m.filter(x => x._2.parent_id.contains(parent_id)).flatMap {
      _ match {
        case (k, t1 @ Transaction(_, _, Some(_))) =>
          traverseChildren(k, m) ++ List(t1)
        case (_, t2 @ Transaction(_, _, None)) => List(t2)
      }
    }

}
object TransactionRepositoryMap {
  def empty[F[_]](implicit m: Effect[F]): IO[TransactionController[F]] = IO {
    new TransactionController[F](mutable.HashMap())
  }
}
