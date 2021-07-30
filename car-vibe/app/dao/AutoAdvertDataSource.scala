package dao

import anorm.Macro.ColumnNaming
import anorm.SqlParser.scalar
import anorm._
import models._
import models.errors._
import play.api.db.{DBApi, Database}

import javax.inject.Inject
import scala.collection.mutable

class AutoAdvertDataSource @Inject()(
  var dbApi: DBApi,
  var fuelTypesRepository: FuelTypesRepository
)(implicit ec: DatabaseExecutionContext) {
  private val db: Database = dbApi.database("default")

  implicit def intRowToBool: Column[Boolean] = Column.nonNull { (value, meta) =>
    val MetaDataItem(name, _, _) = meta
    if (!name.qualified.startsWith("is_")) {
      Left(TypeDoesNotMatch("Cannot convert " + value + ":" + value.asInstanceOf[AnyRef].getClass + " to Boolean for column " + name.qualified))
    }

    value match {
      case e: Int => Right(if (e == 0) false else true)
      case _ => Left(TypeDoesNotMatch("Field " + name.qualified + " starts with \"is_\" but is not integer"))
    }
  }

  private val rowParser = Macro.namedParser[AutoAdvert](ColumnNaming.SnakeCase)

  def all(sortBy: Option[String] = None): List[AutoAdvert] = db.withConnection { implicit c =>
    val fieldName = sortBy match {
      case Some(name) => name
      case None => "id"
    }

    SQL"""
         select AutoAdverts.id AS id, title, FuelTypes.name AS fuel_type, price, mileage, is_new, first_registration from AutoAdverts
         inner join FuelTypes on AutoAdverts.fuel_type_id = FuelTypes.id
         order by #$fieldName asc
       """
      .as(rowParser.*)
  }

  def byId(id: Long): Option[AutoAdvert] = db.withConnection { implicit c =>
    SQL(
      """
         select AutoAdverts.id, title, FuelTypes.name AS fuel_type, price, mileage, is_new, first_registration from AutoAdverts
         inner join FuelTypes on AutoAdverts.fuel_type_id = FuelTypes.id
         where AutoAdverts.id = {id}
         """)
      .on("id" -> id)
      .as(rowParser.*)
      .headOption
  }

  def newAdvert(advert: CreateUpdateAutoAdvert): Either[BusinessError, AutoAdvert] = db.withConnection { implicit c =>
    validate(advert) match {
      case Some(errorsList) => return Left(ValidationErrors(errorsList.toList))
      case None =>
    }

    val fuelTypeId = fuelTypesRepository.byName(advert.fuelType).get.id

    val result =
      SQL(
        """
            insert into AutoAdverts (title, fuel_type_id, price, mileage, is_new, first_registration)
            values ({title}, {fuelTypeId}, {price}, {mileage}, {is_new}, {first_registration})
         """)
        .on(
          "title" -> advert.title,
          "fuelTypeId" -> fuelTypeId,
          "price" -> advert.price,
          "mileage" -> advert.mileage,
          "is_new" -> (if (advert.isNew) 1 else 0),
          "first_registration" -> advert.firstRegistration
        )
        .executeInsert1("id")(scalar[Long].singleOpt)

    if (result.isFailure) {
      Left(GenericErrors(List("Cannot execute statement")))
    }

    result.get match {
      case Some(value) => byId(value) match {
        case Some(advert) => Right(advert)
        case _ => Left(GenericErrors(List("Invalid Id")))
      }
      case None => Left(GenericErrors(List("Cannot execute statement")))
    }
  }

  def delete(itemId: Long): Boolean = db.withConnection { implicit c =>
    val result: Int = SQL("delete from AutoAdverts where id = {id}").on("id" -> itemId).executeUpdate()
    result match {
      case 1 => true
      case _ => false
    }
  }

  def editAdvert(advert: CreateUpdateAutoAdvert, itemId: Long): Either[BusinessError, AutoAdvert] = db.withConnection { implicit c =>
    validate(advert) match {
      case Some(errorsList) => return Left(ValidationErrors(errorsList.toList))
      case None =>
    }
    val fuelTypeId = fuelTypesRepository.byName(advert.fuelType).get.id

    try {
      SQL(
        """
            update
              AutoAdverts
            set
                title = {title},
                fuel_type_id = {fuelTypeId},
                price = {price},
                mileage = {mileage},
                is_new = {is_new},
                first_registration = {first_registration}
            where id = {id}
         """)
        .on(
          "id" -> itemId,
          "title" -> advert.title,
          "fuelTypeId" -> fuelTypeId,
          "price" -> advert.price,
          "mileage" -> advert.mileage,
          "is_new" -> (if (advert.isNew) 1 else 0),
          "first_registration" -> advert.firstRegistration
        )
        .executeUpdate()

      byId(itemId) match {
        case Some(item) => Right(item)
        case None => Left(GenericErrors(List("Element not found")))
      }

    } catch {
      case r: Throwable => return Left(GenericErrors(List(r.getMessage)))
    }
  }

  private def validate(advert: CreateUpdateAutoAdvert) = {
    val errors = new mutable.ListBuffer[String]

    if (advert.price <= 0) {
      errors += "Price cannot be negative"
    }

    if (advert.isNew && advert.mileage.nonEmpty) {
      errors += "A new car cannot have mileage"
    }

    if (!advert.isNew && advert.mileage.isEmpty) {
      errors += "A used car must have mileage"
    }

    if (!advert.isNew && advert.firstRegistration.isEmpty) {
      errors += "A used car must have registration date (first_registration)"
    }

    if (advert.mileage.nonEmpty && advert.mileage.get <= 0) {
      errors += "Mileage cannot be negative"
    }

    val types = fuelTypesRepository.all.map(_.name)

    if (!types.contains(advert.fuelType)) {
      errors += "Invalid fuelType. Supported fuelTypes: " + types.mkString(",")
    }

    if (errors.nonEmpty) {
      Some(errors)
    } else {
      None
    }
  }

}
