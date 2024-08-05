package com.github.adityaK93.transactions

import cats.effect._
import cats.implicits._
import com.github.adityaK93.transactions.entities._

import scala.annotation.tailrec
import scala.collection.concurrent.TrieMap

final case class TransactionController[F[_]](
    private val transactions: TrieMap[Long, Transaction]
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

  def addTransactionWithId(
      transaction: Transaction,
      id: Long
  ): F[Option[TrieMap[Long, Transaction]]] =
    for {
      t <- e.delay(
        validateTransaction(transactions, id, transaction).map(_ =>
          transactions.addOne(id -> transaction)
        )
      )
    } yield t

  def updateTransactionWithId(
      transaction: Transaction,
      id: Long
  ): F[Option[TrieMap[Long, Transaction]]] =
    for {
      t <- e.delay(
        validateTransaction(transactions, id, transaction).map(_ =>
          transactions.addOne(id -> transaction)
        )
      )
    } yield t

  def getTransactionSum(id: Long): F[Sum] =
    for {
      sm <- e.delay(
        Sum(
          traverseChildrenRecursively(id, transactions, children) + transactions
            .get(id)
            .fold(transactions.get(id).map(_.amount).getOrElse(0.0))(_.amount)
        )
      )
    } yield sm

  def traverseChildrenRecursively(
      parent_id: Long,
      tmap: TrieMap[Long, Transaction],
      f: (Long, TrieMap[Long, Transaction]) => Iterable[(Long, Transaction)]
  ): Double = {

    @tailrec
    def recurseAcc(
        acc: Double,
        children: Iterable[(Long, Transaction)]
    ): Double =
      children.toList match {

        case Nil => acc
        case (id, Transaction(_, bal, _)) :: tail =>
          recurseAcc(acc + bal, tail ++ f(id, tmap))
      }

    recurseAcc(0.0, f(parent_id, tmap))

  }
  def children(parent_id: Long, tmap: TrieMap[Long, Transaction]): Iterable[(Long, Transaction)] = {
    val kk = tmap.filter(x => x._2.parent_id.contains(parent_id))
    kk.keys.zip(kk.values)
  }
  def validateTransaction(
      trieMap: TrieMap[Long, Transaction],
      id: Long,
      transaction: Transaction
  ): Option[Transaction] = {

    @tailrec
    def detectCycle(currentId: Long, visited: Set[Long]): Boolean =
      if (visited.contains(currentId)) true
      else {
        trieMap.get(currentId) match {
          case Some(Transaction(_, _, Some(parentId))) =>
            detectCycle(parentId, visited + currentId)
          case _ => false
        }
      }

    // Check if the new transaction would create a cycle
    val wouldCreateCycle = transaction.parent_id match {
      case Some(parentId) => detectCycle(parentId, Set(id))
      case None           => false
    }

    if (wouldCreateCycle) {
      None
    } else {
      trieMap.put(id, transaction)
      Some(transaction)
    }
  }

}
object TransactionRepositoryMap {
  def empty[F[_]](implicit m: Effect[F]): IO[TransactionController[F]] = IO {
    new TransactionController[F](TrieMap())
  }
}
