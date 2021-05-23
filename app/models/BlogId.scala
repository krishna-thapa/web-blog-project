package models

import reactivemongo.api.bson.BSONObjectID

trait BlogId {

  def _id: BSONObjectID
}
