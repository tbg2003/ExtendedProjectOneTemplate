package repositories

case class Update(
                   id:String,
                   fieldToUpdate: UpdateField,
                   updatedValue: String
                 )

