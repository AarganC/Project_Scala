package utils

import java.sql.{DriverManager, ResultSet, SQLException}

import scala.util.{Failure, Success, Try}

class sqliteUtils {
  def query(url: String, query: String, cols: Seq[String]): Option[Vector[Map[String, Object]]] = {
    try {
      val conn = DriverManager.getConnection(url)
      val stmt = conn.createStatement
      val rs: Option[ResultSet] = Try {
        stmt.executeQuery(query)
      } match {
        case Success(output) => Some(output)
        case Failure(_) => None
      }
      val resultFormat = rs match{
        case Some(rs) => Some(Iterator.continually(buildMap(rs, cols)).takeWhile(!_.isEmpty).map(_.get).toVector)
        case None => None
      }

      if(conn != null) conn.close()
      resultFormat
    }
  }
  private def buildMap(queryResult: ResultSet, colNames: Seq[String]): Option[Map[String, Object]] =
    if (queryResult.next()){
      Some(colNames.map(n => n -> queryResult.getObject(n)).toMap)
    }
    else{
      None
    }
}
