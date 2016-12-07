package com.yimei.citicmigration.config

import com.typesafe.config.ConfigFactory

/**
  * Created by wangqi on 16/12/6.
  */
object Config {
  private val config = ConfigFactory.load()
  private val databaseConfig = config.getConfig("database")
  private val citic = config.getConfig("citic")

  val jdbcUrl = databaseConfig.getString("url")
  val dbUser = databaseConfig.getString("user")
  val dbPassword = databaseConfig.getString("password")

  val url = citic.getString("url")
  val userName = citic.getString("userName")
  val accountNo = citic.getString("accountNo")
  val subaccno = citic.getString("subaccno")
  val onConnectionDemand = citic.getBoolean("onConnectionDemand")
  val mngNode = citic.getInt("mngNode")
}
