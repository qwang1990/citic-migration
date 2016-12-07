package com.yimei.citicmigration.service

import java.sql.Timestamp
import java.time.Instant

import akka.event.{Logging, LoggingAdapter}
import com.yimei.citicmigration.config.Config._
import com.yimei.citicmigration.config.DatabaseConfig._
import com.yimei.citicmigration.exception.DatabaseException
import com.yimei.citicmigration.service.entity.{UserFundAccountsEntity, UserFundAccountsTable}
import com.yimei.citicmigration.util.Utils._
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.xml.XML
/**
  * Created by wangqi on 16/12/6.
  */
object AccountService extends UserFundAccountsTable{
  import driver.api._
  def logger = LoggerFactory.getLogger(this.getClass)


  /**
    *
    * @param accNo
    * @param payPhone
    * @return
    */
  def createAccount(accNo:String,payPhone:String) = {
    //找到该账户，且账户状态为申请成功，中信地址为上海，并且没有被删除
    val acc: Future[UserFundAccountsEntity] = dbrun(userFundAccount.filter(u=>
      u.account===accNo &&
      u.payPhone===payPhone &&
      u.flag===1            &&
      u.status===2          &&
      u.deleted===0).result.head)

    def create(acc:UserFundAccountsEntity): Future[String] = {
      val sendMessage = "<?xml version=\"1.0\" encoding=\"GBK\"?>" +
        "<stream>" +
        "<action>DLBREGST</action>" +
        "<userName>" + userName + "</userName>" +
        "<mainAccNo>" + accountNo + "</mainAccNo>" +
        "<appFlag>" + 2 + "</appFlag>" +
        "<subAccNm>" + acc.companyName + "</subAccNm>" +
        "<accGenType>" + "0" + "</accGenType>" +
        "<accType>" + "03" + "</accType>" +
        "<overFlag>" + 0 + "</overFlag>" +
        "<autoAssignInterestFlag>" + 0 + "</autoAssignInterestFlag>" +
        "<autoAssignTranFeeFlag>" + 0 + "</autoAssignTranFeeFlag>" +
        "<calInterestFlag>" + 0 + "</calInterestFlag>" +
        "<feeType>" + 0 + "</feeType>" +
        "<subAccPrintParm>" + 0 + "</subAccPrintParm>" +
        "<realNameParm>" + 2 + "</realNameParm>" +
        "<mngNode>" + mngNode + "</mngNode>" +
        "</stream>"
      sendRequest(sendMessage)
    }

    def processResp(rs:String, acc:UserFundAccountsEntity): Future[String] = {
      val xmlRes = XML.loadString(rs)
      logger.info("original accNO:{}, payPhone:{} ,response message: {}",accNo, payPhone,xmlRes)
      checkStatus(xmlRes ~ "status") match {
        case BankSuccess => {

          val a = (for {
            in <- userFundAccount += acc.copy(id=None,account = xmlRes~"subAccNo",createTime = Timestamp.from(Instant.now)
              ,lastUpdateTime = Timestamp.from(Instant.now),status = 2,flag = 2,accountChildBankName = "呵呵呵，我也不知道")
            up <- userFundAccount.filter(_.id===acc.id).map(t=>t.deleted).update(1)
          } yield{
            if (in == 1 && up == 1){
              logger.info("success")
              logger.info("oldAccNo:{},newAccNo:{},payPhone:{},companyName:{}",acc.account,xmlRes~"subAccNo",acc.payPhone,acc.companyName)
              "success"
            } else {
              throw new DatabaseException("这次错了")
            }
          } ).transactionally
          dbrun(a)
          }
        }
    }

    for {
      ac <- acc
      rs <- create(ac)
      a <- processResp(rs,ac)
    } yield{
      a
    }

  }

}
