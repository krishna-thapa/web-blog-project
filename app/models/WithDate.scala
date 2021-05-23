package models

import reactivemongo.api.bson.BSONDateTime

trait WithDate extends BlogId {
  def createdDate: BSONDateTime
  def updatedDate: BSONDateTime
}
