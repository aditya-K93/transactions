package com.github.adityaK93.transactions

import cats.effect._
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext.Implicits.global

import entities.Transaction
import entities.ResponseOK

object TransactionService extends IOApp with Http4sDsl[IO] {

  val TS = "transactionservice"
  val T  = "transaction"
  val TT = "types"
  val SM = "sum"

  def service[F[_]](
      transactionRepo: TransactionController[F]
  )(implicit F: Effect[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {

      case GET -> Root / TS / T / LongVar(transactionId) =>
        transactionRepo
          .getTransaction(transactionId)
          .flatMap {
            case Some(transaction) =>
              F.pure(Response(status = Status.Ok).withEntity(transaction.asJson.dropNullValues))
            case None => F.pure(Response(status = Status.NotFound))
          }

      case GET -> Root / TS / T =>
        transactionRepo.getAllTransactions
          .flatMap(s => F.pure(Response(status = Status.Ok).withEntity(s.asJson.dropNullValues)))

      case GET -> Root / TS / TT / _type =>
        transactionRepo
          .getTransactionTypes(_type)
          .flatMap(s => F.pure(Response(status = Status.Ok).withEntity(s.asJson.dropNullValues)))

      case req @ POST -> Root / TS / T / LongVar(transactionId) =>
        req
          .decodeJson[Transaction]
          .flatMap(
            transactionRepo
              .addTransactionWithId(_, transactionId)
              .flatMap(_ =>
                F.pure(Response(status = Status.Ok).withEntity(ResponseOK("ok").asJson))
              )
          )

      case req @ PUT -> Root / TS / T / LongVar(transactionId) =>
        req
          .decodeJson[Transaction]
          .flatMap(
            transactionRepo
              .addTransactionWithId(_, transactionId)
              .flatMap(_ =>
                F.pure(Response(status = Status.Ok).withEntity(ResponseOK("ok").asJson))
              )
          )

      case GET -> Root / TS / SM / LongVar(transactionId) =>
        transactionRepo
          .getTransactionSum(transactionId)
          .flatMap(s => F.pure(Response(status = Status.Ok).withEntity(s.asJson)))
    }

  def run(
      args: List[String]
  ): IO[ExitCode] =
    TransactionRepositoryMap.empty[IO].flatMap { transactionRepo =>
      BlazeServerBuilder[IO](global)
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(service(transactionRepo).orNotFound)
        .serve
        .compile
        .drain
        .as(ExitCode.Success)
    }

}
