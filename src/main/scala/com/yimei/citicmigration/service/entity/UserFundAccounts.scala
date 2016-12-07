package com.yimei.citicmigration.service.entity
import com.yimei.citicmigration.config.DatabaseConfig._
import java.sql.Timestamp

import slick.lifted.MappedTo


/**
  * Created by wangqi on 16/12/6.
  */
case class MyID(value: String)

case class UserFundAccountsEntity(id:Option[Int],userId:String,account:String,password:String,status:Int,payPhone:String,companyName:String,createTime:Timestamp,
                                  lastUpdateTime:Timestamp,cashBankAccount:Option[String],cashBankOpenCode:Option[Long],accountBankName:String,deleted:Int,accountChildBankName:String,flag:Int)
trait UserFundAccountsTable {
  import driver.api._

  class UserFundAccounts(tag:Tag) extends Table[UserFundAccountsEntity](tag,"pay_userfundaccounts") {
    def id = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    def userId = column[String]("userId")
    def account = column[String]("account")
    def password = column[String]("password")
    def status = column[Int]("status")
    def payPhone = column[String]("payPhone")
    def companyName = column[String]("companyName")
    def createTime = column[Timestamp]("createTime")
    def lastUpdateTime = column[Timestamp]("lastUpdateTime")

    def cashBankAccount = column[Option[String]]("cashBankAccount")
    def cashBankOpenCode = column[Option[Long]]("cashBankOpenCode")
    def accountBankName = column[String]("accountBankName")
    def deleted = column[Int]("deleted")
    def accountChildBankName = column[String]("accountChildBankName")
    def flag = column[Int]("flag")

    def * = (id,userId,account,password,status,payPhone,companyName,createTime,lastUpdateTime,cashBankAccount,
      cashBankOpenCode,accountBankName,deleted,accountChildBankName,flag) <> (UserFundAccountsEntity.tupled,UserFundAccountsEntity.unapply)



  }
  protected val userFundAccount = TableQuery[UserFundAccounts]
}
