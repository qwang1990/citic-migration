package com.yimei.citicmigration.util

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest}
import akka.stream.ActorMaterializer
import akka.util.ByteString
import com.yimei.citicmigration.config.Config._
import com.yimei.citicmigration.config.DatabaseConfig._
import com.yimei.citicmigration.exception.InternetException
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import scala.xml.Elem

/**
  * Created by wangqi on 16/12/6.
  */
object Utils {
  import driver.api._
  import slick.dbio.DBIOAction

  implicit val actorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executor: ExecutionContext = actorSystem.dispatcher


  def logger = LoggerFactory.getLogger(this.getClass)
  //封装数据库操作
  def dbrun[R](a: DBIOAction[R, NoStream, Nothing]): Future[R] ={
    val result = db.run(a)
    result onFailure {
      case a => {logger.info("database err: {}",a); throw new Exception(a.getMessage)}
    }
    result
  }

  //发送报文,并取得回复
  def sendRequest(citicRequest :String): Future[String] = {
    logger.info("request message {}",citicRequest)
    //发送请求,并得到结果
    Http().singleRequest(
      HttpRequest(uri =url , entity = ByteString(citicRequest,"GBK"),method = HttpMethods.POST)
    ) flatMap { r =>
      val strictEntity = r.entity.toStrict(10.seconds)
      val byteString: Future[ByteString] = strictEntity flatMap { e =>
        e.dataBytes
          .runFold(ByteString.empty) { case (acc, b) => acc ++ b }
      }
      byteString map(_.decodeString("GBK"))
    } recover {
      case _ => throw new InternetException("中信银行网络异常")
    }
  }


  //银行处理状态
  sealed trait BankProcessStatus
  case object BankInProcess  extends BankProcessStatus      //1
  case object BankSuccess extends BankProcessStatus         //2
  case object BankFailed  extends BankProcessStatus         //3

  //检查信息
  def checkStatus(status:String) = {
    status match {
      case "AAAAAAA" => BankSuccess
      case "ES88363"|"ES88051"|"ES88045"|"PSR0003"|"ED02083"|"ED11308"|"ES88062" => BankFailed
      case _ => BankInProcess
    }
  }

  implicit def x: (Elem) => Temp = (xml:Elem)=>new Temp(xml)

}

class Temp(xml:Elem) {
  def ~(element:String) = {
    (xml\element).text
  }
}