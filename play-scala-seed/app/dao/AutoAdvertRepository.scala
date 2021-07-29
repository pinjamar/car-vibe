package dao

import anorm.Macro.ColumnNaming
import anorm._
import models.AutoAdvert
import play.api.db.{DBApi, Database}

import javax.inject.Inject

class AutoAdvertRepository @Inject()(var dbApi: DBApi)(implicit ec: DatabaseExecutionContext) {
  private val db: Database = dbApi.database("default")

  implicit def intRowToBool: Column[Boolean] = Column.nonNull { (value, meta) =>
    val MetaDataItem(name, _, _) = meta
    if (!name.qualified.startsWith("is_")) {
      Left(TypeDoesNotMatch("Cannot convert " + value + ":" + value.asInstanceOf[AnyRef].getClass + " to Boolean for column " + name.qualified))
    }

    value match {
      case e: Int => Right(if (e == 0) false else true)
      case _ => Left(TypeDoesNotMatch("Field " + name.qualified + " starts with \"is_\" but is not boolean"))
    }
  }

  private val rowParser = Macro.namedParser[AutoAdvert](ColumnNaming.SnakeCase)

  def all: Seq[AutoAdvert] = db.withConnection { implicit c =>
    SQL"""
         select AutoAdverts.id, title, FuelTypes.name AS fuel_type, price, km, is_new, first_registration from AutoAdverts
         inner join FuelTypes on AutoAdverts.fuel_type_id = FuelTypes.id
       """.as(rowParser.*)
  }

  def delete(itemId: Long): Boolean = db.withConnection { implicit c =>
    val result: Int = SQL("delete from AutoAdverts where id = {id}").on("id" -> itemId).executeUpdate()
    result match {
      case 1 => true
      case _ => false
    }
  }

}
