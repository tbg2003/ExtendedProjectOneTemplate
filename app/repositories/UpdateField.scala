package repositories

sealed trait UpdateField

object UpdateField{
  case object Name extends UpdateField
  case object Description extends UpdateField
  case object PageCount extends UpdateField
}