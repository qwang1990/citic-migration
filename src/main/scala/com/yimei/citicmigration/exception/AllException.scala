package com.yimei.citicmigration.exception

/**
  * Created by wangqi on 16/12/6.
  */
case class AuthenticException(message:String) extends Exception

case class BusinessException(message:String) extends Exception

case class DatabaseException(message:String) extends Exception

case class InternetException(message:String) extends Exception

case class FatalException(message:String) extends Exception
