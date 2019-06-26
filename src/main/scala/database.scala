import java.sql.DriverManager
import java.sql.SQLException


object database {
  val url = "jdbc:sqlite:./db/project_scala.db"

  def connect(): Unit = {
    try {
      val conn = DriverManager.getConnection(url)
      try
          if (conn != null) {
            System.out.println("Connection to SQLite has been established.")
          }
      catch {
        case e: SQLException =>
          System.out.println(e.getMessage)
      } finally if (conn != null) conn.close()
    }
  }

  def selectAll(): Unit = {
    val sql = "SELECT name FROM sqlite_master WHERE type ='table' AND name NOT LIKE 'sqlite_%';"
    try {
      val conn = DriverManager.getConnection(url)
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(sql)
      try // loop through the result set
      while ( {
        rs.next
      }) System.out.println(rs.getString("name"))
      catch {
        case e: SQLException =>
          System.out.println(e.getMessage)
      } finally {
        if (conn != null) conn.close()
        if (stmt != null) stmt.close()
        if (rs != null) rs.close()
      }
    }
  }

  def createNewTable(): Unit = { // SQLite connection string
    // SQL statement for creating a new table
    val sql_array = new Array[String](5)
    sql_array(0) = "CREATE TABLE IF NOT EXISTS user (" +
      "	id_user integer PRIMARY KEY AUTOINCREMENT," +
      "	name_user text NOT NULL," +
      " status_blacklist boolean,"  +
      " status_sub boolean);"

    sql_array(1) = "CREATE TABLE IF NOT EXISTS tips (" +
      "	id_donation integer PRIMARY KEY AUTOINCREMENT," +
      "	id_user integer NOT NULL," +
      " donation integer);"

    sql_array(2) = "CREATE TABLE IF NOT EXISTS giveaway (" +
      "	id_giveaway integer PRIMARY KEY AUTOINCREMENT," +
      "	id_user integer NOT NULL," +
      "	giveaway text NOT NULL);"

    sql_array(3) = "CREATE TABLE IF NOT EXISTS survey_answer (" +
      "	id_survey_answer integer PRIMARY KEY AUTOINCREMENT," +
      "	id_user integer NOT NULL," +
      " response boolean NOT NULL," +
      " id_survey int NOT NULL);"

    sql_array(4) = "CREATE TABLE IF NOT EXISTS survey (" +
      "	id_survey integer PRIMARY KEY AUTOINCREMENT," +
      "	survey text NOT NULL);"

    for(sql <- sql_array) {
      try {
        val conn = DriverManager.getConnection(url)
        val stmt = conn.createStatement
        try // create a new table
        stmt.execute(sql)
        catch {
          case e: SQLException =>
            System.out.println(e.getMessage)
        } finally {
          if (conn != null) conn.close()
          if (stmt != null) stmt.close()
        }
      }
    }
  }

  def insert(): Unit = {
    val sql_array = new Array[String](24)
    sql_array(0) = "INSERT INTO user(name_user,status_blacklist, status_sub) VALUES('Homer',0,1)"
    sql_array(1) = "INSERT INTO user(name_user,status_blacklist, status_sub) VALUES('Bart',0,1)"
    sql_array(2) = "INSERT INTO user(name_user,status_blacklist, status_sub) VALUES('Lisa',0,0)"
    sql_array(3) = "INSERT INTO user(name_user,status_blacklist, status_sub) VALUES('Maggie',0,1)"
    sql_array(4) = "INSERT INTO user(name_user,status_blacklist, status_sub) VALUES('Marge',0,0)"
    sql_array(5) = "INSERT INTO user(name_user,status_blacklist, status_sub) VALUES('Moe',1,1)"
    sql_array(6) = "INSERT INTO tips(id_user,donation) VALUES(1,1000)"
    sql_array(7) = "INSERT INTO tips(id_user,donation) VALUES(2,0)"
    sql_array(8) = "INSERT INTO tips(id_user,donation) VALUES(3,5)"
    sql_array(9) = "INSERT INTO tips(id_user,donation) VALUES(4,0)"
    sql_array(10) = "INSERT INTO tips(id_user,donation) VALUES(5,0)"
    sql_array(11) = "INSERT INTO tips(id_user,donation) VALUES(6,300)"
    sql_array(12) = "INSERT INTO giveaway(id_user,giveaway) VALUES(1,600)"
    sql_array(13) = "INSERT INTO giveaway(id_user,giveaway) VALUES(3,5)"
    sql_array(14) = "INSERT INTO giveaway(id_user,giveaway) VALUES(6,300)"
    sql_array(15) = "INSERT INTO giveaway(id_user,giveaway) VALUES(1,400)"
    sql_array(16) = "INSERT INTO survey(survey) VALUES('Aime tu le poulet ?')"
    sql_array(17) = "INSERT INTO survey(survey) VALUES('Aime tu le poisson ?')"
    sql_array(18) = "INSERT INTO survey(survey) VALUES('Aime tu les burgers ?')"
    sql_array(19) = "INSERT INTO survey_answer(id_user,response,id_survey) VALUES(1,1,1)"
    sql_array(20) = "INSERT INTO survey_answer(id_user,response,id_survey) VALUES(2,1,1)"
    sql_array(21) = "INSERT INTO survey_answer(id_user,response,id_survey) VALUES(3,0,2)"
    sql_array(22) = "INSERT INTO survey_answer(id_user,response,id_survey) VALUES(1,1,1)"
    sql_array(23) = "INSERT INTO survey_answer(id_user,response,id_survey) VALUES(5,0,3)"

    for(sql <- sql_array) {
      try {
        val conn = DriverManager.getConnection(url)
        val stmt = conn.createStatement
        try // create a new table
        stmt.execute(sql)
        catch {
          case e: SQLException =>
            System.out.println(e.getMessage)
        } finally {
          if (conn != null) conn.close()
          if (stmt != null) stmt.close()
        }
      }
    }
  }

  def main(args: Array[String]): Unit = {
    connect()
    createNewTable()
    selectAll()
    insert()
  }
}
