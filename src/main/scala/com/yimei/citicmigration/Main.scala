package com.yimei.citicmigration

import akka.actor.ActorSystem
import com.yimei.citicmigration.service.AccountService._
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
  * Created by wangqi on 16/12/6.
  */
object Main extends App{
  implicit val actorSystem = ActorSystem()

  def logger = LoggerFactory.getLogger(this.getClass)

  logger.info("开始处理:")
 // println("开始处理:")

  if(args.length!=2) {
    logger.error("参数错误")
    System.exit(1)
  }

  val a: Future[String] = createAccount(args(0),args(1))

  Await.result(a,100 second)
}
