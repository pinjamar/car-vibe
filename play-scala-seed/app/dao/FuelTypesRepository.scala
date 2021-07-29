package dao

import anorm.{Macro, RowParser, SqlStringInterpolation}
import models.FuelTypes
import play.api.db.{DBApi, Database}

import javax.inject.Inject

class FuelTypesRepository @Inject()(var dbApi: DBApi)(implicit ec: DatabaseExecutionContext) {
  private val db: Database = dbApi.database("default")

  private val rowParser: RowParser[FuelTypes] = Macro.namedParser[FuelTypes]

  def all: Seq[FuelTypes] = db.withConnection { implicit c =>
    SQL"select * from FuelTypes".as(rowParser.*)
  }
}
