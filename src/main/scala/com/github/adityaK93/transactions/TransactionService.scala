package com.github.adityaK93.transactions

import cats.effect.IO

import cats.implicits._
import cats.effect._
import fs2.StreamApp
import fs2.Stream
import io.circe.generic.auto._
import io.circe.syntax._

import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.blaze.BlazeBuilder

import scala.concurrent.ExecutionContext.Implicits.global

import entities.Transaction
import entities.ResponseOK

object TransactionService extends StreamApp[IO] with Http4sDsl[IO] {

  val TS = "transactionservice"
  val T  = "transaction"
  val TT = "types"
  val SM = "sum"

  def service[F[_]](
      transactionRepo: TransactionController[F]
  )(implicit F: Effect[F]): HttpService[F] =
    HttpService[F] {

      case GET -> Root / TS / T / LongVar(transactionId) =>
        transactionRepo
          .getTransaction(transactionId)
          .flatMap {
            case Some(transaction) =>
              Response(status = Status.Ok).withBody(transaction.asJson.dropNullValues)
            case None => F.pure(Response(status = Status.NotFound))
          }

      case GET -> Root / TS / T =>
        transactionRepo.getAllTransactions
          .flatMap(s => Response(status = Status.Ok).withBody(s.asJson.dropNullValues))

      case GET -> Root / TS / TT / _type =>
        transactionRepo
          .getTransactionTypes(_type)
          .flatMap(s => Response(status = Status.Ok).withBody(s.asJson.dropNullValues))

      case req @ POST -> Root / TS / T / LongVar(transactionId) =>
        req
          .decodeJson[Transaction]
          .flatMap(
            transactionRepo
              .addTransactionWithId(_, transactionId)
              .flatMap(_ => Response(status = Status.Ok).withBody(ResponseOK("ok").asJson))
          )

      case req @ PUT -> Root / TS / T / LongVar(transactionId) =>
        req
          .decodeJson[Transaction]
          .flatMap(
            transactionRepo
              .addTransactionWithId(_, transactionId)
              .flatMap(_ => Response(status = Status.Ok).withBody(ResponseOK("ok").asJson))
          )

      case GET -> Root / TS / SM / LongVar(transactionId) =>
        transactionRepo
          .getTransactionSum(transactionId)
          .flatMap(s => Response(status = Status.Ok).withBody(s.asJson))
    }

  def stream(
      args: List[String],
      requestShutdown: IO[Unit]
  ): Stream[IO, StreamApp.ExitCode] =
    Stream.eval(TransactionRepositoryMap.empty[IO]).flatMap { transactionRepo =>
      BlazeBuilder[IO]
        .bindHttp(8080, "0.0.0.0")
        .mountService(service(transactionRepo), "/")
        .serve
    }
}
