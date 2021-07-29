package models

case class AutoAdvert(
id: Long,
title: String,
fuelType: String,
price: Double,
km: Option[Long],
isNew: Boolean,
firstRegistration: Option[String]
)
