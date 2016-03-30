package pl.jan

import com.softwaremill.id.DefaultIdGenerator
import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._
import scala.language.postfixOps

/*
* Gatling simulation scripts are written in Scala DSL
* You can use all the basic functions of Gatling without knowing much about Scala.
*
* */
class BootzookaRegistrationScn extends Simulation {

  val otOnceUsers = 400
  val generator = new DefaultIdGenerator

  val httpProtocol = http.baseURL(s"http://localhost:${serverPort()}")
    .acceptHeader("application/json, text/plain, */*")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .connection("keep-alive")
    .contentTypeHeader("application/json;charset=utf-8")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:40.0) Gecko/20100101 Firefox/40.0")

  val headers = Map("Pragma" -> "no-cache")

  val home = http("bootzooka").get("/").check(status.is(200))

  val register = http("registration")
    .post("/api/users/register")
    .headers(headers)
    .body(buildBodyJson)
    //.asJSON
    .check(regex("success").find(0).exists)

  val scn = scenario("load test")
    .exec(home)
    .exec { session => session.set("randomId", generator.nextId()) }
    .pause(0 milliseconds, 500 milliseconds)
    .exec(register)
    .pause(0 milliseconds, 500 milliseconds)

  setUp(//http://gatling.io/docs/2.0.0-RC2/general/simulation_setup.html
    scn.inject(
      atOnceUsers(otOnceUsers)
    )
  ).assertions(global.failedRequests.count.is(0), global.responseTime.max.lessThan(1200))
    .protocols(httpProtocol)

  private def buildBodyJson = {
    StringBody( """{"login":"userLogin${randomId}","email":"userEmail${randomId}@bootzooka.com","password":"bootzooka"}""")
  }

  private def serverPort(): String = {
    import com.typesafe.config.ConfigFactory
    val conf = ConfigFactory.load()
    conf.getString("server.port")

  }
}