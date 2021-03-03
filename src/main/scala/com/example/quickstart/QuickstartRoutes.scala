package com.example.quickstart

import cats.effect.Sync
import com.typesafe.scalalogging.LazyLogging
import org.http4s.HttpRoutes
import org.http4s.dsl.{Http4sDsl}
import org.typelevel.ci.CIString

object QuickstartRoutes extends LazyLogging {

  import cats.implicits._

  def jokeRoutes[F[_]: Sync](J: Jokes[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "joke" =>
        for {
          joke <- J.get
          resp <- Ok(joke)
        } yield resp
    }
  }

  def helloWorldRoutes[F[_]: Sync](H: HelloWorld[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "hello" / name =>
        for {
          greeting <- H.hello(HelloWorld.Name(name))
          resp <- Ok(greeting)
        } yield resp
    }
  }

  def secretRoutes[F[_]: Sync](s: Secrets[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "secret"=> Ok(s.getAll())
    }
  }

  def healthcheckRoute[F[_]: Sync]: HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "up" => Ok()
    }
  }

  def jobRoutes[F[_]: Sync](environment: String): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case request @ POST -> Root / "jobs" / "job1" =>

        logger.info("--------job1 here!!!----------")

        val result = request.headers.find(_.name === CIString(s"Accept-gc-job"))
          .map {
            case hMatch if hMatch.value.trim.toLowerCase() === environment.trim.toLowerCase() => Ok()
            case _ => NotAcceptable()
          }.getOrElse(NotAcceptable())

        result
    }
  }
}