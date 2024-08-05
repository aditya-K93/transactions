import cats.effect._
import com.github.adityaK93.transactions._
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext.Implicits.global

object Main extends IOApp {
  def run(
      args: List[String]
  ): IO[ExitCode] =
    TransactionRepositoryMap.empty[IO].flatMap { transactionRepo =>
      BlazeServerBuilder[IO](global)
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(TransactionService.service(transactionRepo).orNotFound)
        .serve
        .compile
        .drain
        .as(ExitCode.Success)
    }
}
