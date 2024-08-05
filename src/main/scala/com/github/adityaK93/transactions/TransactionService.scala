package com.github.adityaK93.transactions

import cats.effect._
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

import entities.Transaction
import com.github.adityaK93.transactions.entities._

object TransactionService extends Http4sDsl[IO] {

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
            case None =>
              F.pure(Response(status = Status.NotFound).withEntity(_Response("Bad Request").asJson))
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
              .flatMap(t =>
                t.fold(
                  F.pure(
                    Response[F](status = Status.BadRequest)
                      .withEntity(_Response("Bad Request").asJson)
                  )
                )(_ =>
                  F.pure(
                    Response[F](status = Status.Created).withEntity(_Response("Created").asJson)
                  )
                )
              )
          )

      case req @ PUT -> Root / TS / T / LongVar(transactionId) =>
        req
          .decodeJson[Transaction]
          .flatMap(
            transactionRepo
              .updateTransactionWithId(_, transactionId)
              .flatMap(t =>
                t.fold(
                  F.pure(
                    Response[F](status = Status.BadRequest)
                      .withEntity(_Response("Bad Request").asJson)
                  )
                )(_ =>
                  F.pure(
                    Response[F](status = Status.Accepted).withEntity(_Response("Accepted").asJson)
                  )
                )
              )
          )

      case GET -> Root / TS / SM / LongVar(transactionId) =>
        transactionRepo
          .getTransactionSum(transactionId)
          .flatMap(s => F.pure(Response(status = Status.Ok).withEntity(s.asJson)))
    }
}
