package com.github.adityaK93.transactions.entities

import io.circe.Encoder, io.circe.Decoder

case class Transaction(_type: String, amount: Double, parent_id: Option[Long])
case class Sum(sum: Double)

object Transaction {

  implicit val decoderTransaction: Decoder[Transaction] =
    Decoder.forProduct3("type", "amount", "parent_id")(Transaction.apply)

  implicit val encoderTransaction: Encoder[Transaction] =
    Encoder.forProduct3("type", "amount", "parent_id")(b => (b._type, b.amount, b.parent_id))
}
