package dao

import anorm.{Macro, RowParser, SQL, SqlStringInterpolation}
import models.FuelTypes
import play.api.db.{DBApi, Database}

import javax.inject.Inject

class FuelTypesRepository @Inject()(var dbApi: DBApi)(implicit ec: DatabaseExecutionContext) {
  private val db: Database = dbApi.database("default")

  private val rowParser: RowParser[FuelTypes] = Macro.namedParser[FuelTypes]

  def all: Seq[FuelTypes] = db.withConnection { implicit c =>
    SQL"select * from FuelTypes".as(rowParser.*)
  }

  def byName(name: String): Option[FuelTypes] = db.withConnection { implicit c =>
    SQL("select * from FuelTypes where name = {query_name}").on("query_name" -> name)
      .as(rowParser.*)
      .headOption
  }
}
