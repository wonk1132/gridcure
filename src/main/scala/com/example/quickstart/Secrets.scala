package com.example.quickstart

import cats.effect.Sync

trait Secrets[F[_]] {
  def getAll(): String
}

object Secrets {

  //import cats.implicits._
  def impl[F[_]: Sync] = new Secrets[F] {

    def getAll(): String = {

      try {

        /*
        val projectId = "numeric-chassis-304421"
        val secretId = "postgres_pw"
        val versionId = "latest"
        val secretVersionName = SecretVersionName.of(projectId, secretId, versionId)
        val client = SecretManagerServiceClient.create()
        val result = client.accessSecretVersion(secretVersionName)
        val dbPassword =  result.getPayload().getData().toStringUtf8
        println(s"--------0000 $dbPassword")
         */

        // this does not error

        /*
        val hostname = "numeric-chassis-304421:us-west1:wonk"
        //val hostname = "localhost"

        val DB_USER = "lampoon"
        val DB_PASS = "lampoon"
        import com.zaxxer.hikari.HikariConfig
        val config = new HikariConfig
        config.setJdbcUrl(String.format("jdbc:postgresql:///%s/postgres", hostname))
        config.setUsername(DB_USER)
        config.setPassword(DB_PASS)
        config.addDataSourceProperty("socketFactory", "com.google.cloud.sql.postgresql.SocketFactory")
        config.addDataSourceProperty("cloudSqlInstance", "wonk")
        import com.zaxxer.hikari.HikariDataSource
        //import javax.sql.DataSource
        val pool = new HikariDataSource(config)
        val conn = pool.getConnection()
        val ps = conn.prepareStatement("select now()")
        val r = ps.executeQuery()
        r.getString(0)

         */

        import scalikejdbc._
        Class.forName("org.postgresql.Driver")
        val hostname = "wonk"// 34.82.104.45"

        ConnectionPool.singleton(
          s"jdbc:postgresql://$hostname", // /gridcure
          "postgres",
          "Tristan1"//dbPassword
        )

        val now = DB readOnly { implicit session =>
          sql"""select now()""".map(r => r.string(0))
            .single().apply()
        }
        now.getOrElse("waaat")


      } catch {
        case e: Exception =>
          throw new Exception(s"----failure: ${e.getMessage}")
      }

    }
  }

}
