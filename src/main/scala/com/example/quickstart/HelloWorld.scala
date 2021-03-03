package com.example.quickstart

import cats.Applicative
import cats.effect.Sync
import cats.implicits._
//import com.example.quickstart.Jokes.{Joke, JokeError}
import io.circe.{Encoder, Json}
import org.http4s.EntityEncoder
import org.http4s.circe._
import scalikejdbc.ConnectionPool
import scalikejdbc._
import com.typesafe.scalalogging.LazyLogging
//import org.http4s.client.blaze.BlazeClientBuilder
//import org.http4s._
//import org.http4s.implicits._
import org.http4s.client.Client
//import org.http4s.client.dsl.Http4sClientDsl
//import org.http4s.Method._

//import scala.concurrent.duration.Duration

trait HelloWorld[F[_]]{
  def hello(n: HelloWorld.Name): F[HelloWorld.Greeting]
  def job1: F[Unit]
}

object HelloWorld extends LazyLogging {
  implicit def apply[F[_]](implicit ev: HelloWorld[F]): HelloWorld[F] = ev

  final case class Name(name: String) extends AnyVal
  /**
    * More generally you will want to decouple your edge representations from
    * your internal data structures, however this shows how you can
    * create encoders for your data.
    **/
  final case class Greeting(greeting: String) extends Product with Serializable
  object Greeting {
    implicit val greetingEncoder: Encoder[Greeting] = new Encoder[Greeting] {
      final def apply(a: Greeting): Json = Json.obj(
        ("message", Json.fromString(a.greeting)),
      )
    }
    implicit def greetingEntityEncoder[F[_]: Applicative]: EntityEncoder[F, Greeting] =
      jsonEncoderOf[F, Greeting]
  }

  def impl[F[_]: Sync](c:Client[F]): HelloWorld[F] = new HelloWorld[F]{

    def job1: F[Unit] = {
      logger.info("--------doing JOB1--------")
      ().pure[F]
    }

    def hello(n: HelloWorld.Name): F[HelloWorld.Greeting] = {

      import scala.jdk.CollectionConverters._
      logger.info("--------------------------------------------------------starting")

      val x = try {

        val environmentVars = System.getenv().asScala
        val dbUser = environmentVars.getOrElse("QUICKSTART_DB_USER", "lampoon")
        val dbPassword = environmentVars.getOrElse("QUICKSTART_DB_PASSWORD", "lampoon")
        val myKey = environmentVars.getOrElse("MY_KEY", "nooooooo")

        logger.info(s"-----mykey------ $myKey  $dbUser $dbPassword")

        println(c)
        ConnectionPool.singleton(
          "jdbc:postgresql:///quickstart?host=/cloudsql/gridcure-dev:us-west1:quickstart-psql",
          "lampoon", //dbUser,
          "lampoon"//dbPassword
        )

        val name = DB readOnly { implicit session =>
          sql"""
         SELECT name from customers
         """.map(r => r.string("name"))
            .single().apply().getOrElse("no!!!")
        }

        logger.info(name)
        name
      } catch {
        case e: Exception =>
          logger.error(s"----error---------> ${e.getMessage}")
      }

      logger.info("-----------------------111")

      /*
      val dsl = new Http4sClientDsl[F]{}
      import dsl._

      // call to another service in the cluster
      for {
        joke <- c.expect[Joke](GET(uri"http://gridcure/joke"))
          .adaptError { case t => JokeError(t) }

      } yield {
        Greeting("Hi " + n.name + s" $x joke ----> $joke [$myKey]")
      }
       */

      Greeting("-5--- " + x.toString).pure[F]
    }
  }
}