package com.yimei.citicmigration.config

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import com.yimei.citicmigration.config.Config._

/**
  * Created by wangqi on 16/12/6.
  */
object DatabaseConfig {
  private val hikariConfig = new HikariConfig()
  hikariConfig.setJdbcUrl(jdbcUrl)
  hikariConfig.setUsername(dbUser)
  hikariConfig.setPassword(dbPassword)

  private val dataSource = new HikariDataSource(hikariConfig)
  val driver = slick.driver.MySQLDriver

  import driver.api.Database
  val db = Database.forDataSource(dataSource)
  db.createSession()
}
