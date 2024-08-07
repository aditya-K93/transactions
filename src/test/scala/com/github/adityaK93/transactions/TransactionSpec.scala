package com.github.adityaK93.transactions

import cats.effect.IO
import com.github.adityaK93.transactions.entities._
import io.circe.Json
import io.circe.syntax.EncoderOps
import org.http4s._
import org.http4s.circe.jsonEncoder
import org.http4s.implicits._
import org.specs2.matcher.MatchResult

import scala.collection.concurrent.TrieMap

class TransactionSpec extends org.specs2.mutable.Specification {

  "Post Transaction" >> {
    "return 201" >> postTrnsReturns201()
  }

  "Get Transaction" >> {
    "return 200" >> {
      getTransReturns200
    }
    "return trans" >> {
      getTranReturnsTran
    }
  }

  val transaction: Transaction = Transaction("car", 5000.0, None)
  val id: Long                 = 10.toLong
  val TransactionCont: TransactionController[IO] =
    TransactionController[IO](TrieMap(id -> transaction))

  def testService(): HttpRoutes[IO] = TransactionService.service[IO](TransactionCont)
  val uri: Uri                      = Uri.fromString(f"/transactionservice/transaction/$id").fold(throw _, identity)

  private[this] val retPostTransaction: Response[IO] = {

    val postLstngs =
      Request[IO](Method.POST, uri)
        .withEntity(transaction.asJson)

    testService().orNotFound(postLstngs).unsafeRunSync()
  }
  private[this] def postTrnsReturns201(): MatchResult[Status] =
    retPostTransaction.status must beEqualTo(Status.Created)

  private[this] val retGetTran: Response[IO] = {
    val getLstngs = Request[IO](Method.GET, uri)
    testService().orNotFound(getLstngs).unsafeRunSync()
  }

  private[this] def getTransReturns200: MatchResult[Status] =
    retGetTran.status must beEqualTo(Status.Ok)

  private[this] def getTranReturnsTran: MatchResult[String] = {
    val tran = Json.fromString("""{"type":"car","amount":5000.0}""")
    retGetTran.as[String].unsafeRunSync() must beEqualTo(tran.asString.get)
  }

}
