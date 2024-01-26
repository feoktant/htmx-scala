package io.feoktant.htmx

import scala.collection.concurrent.TrieMap

case class Contact(
    id: Int,
    first: Option[String],
    last: Option[String],
    phone: Option[String],
    email: String,
)

object Contact {
  // mock contacts database
  private val db = TrieMap.empty[Int, Contact]

  private val PageSize = 100

  def all(page: Int = 1): Seq[Contact] =
    val start = 0.max(page - 1) * PageSize
    val end = start + PageSize
    db.values.slice(start, end).toSeq

  def search(text: String): Seq[Contact] =
    db.values.collect {
      case c @ Contact(_, Some(first), _, _, _) if first.contains(text) => c
      case c @ Contact(_, _, Some(last), _, _) if last.contains(text) => c
      case c @ Contact(_, _, _, Some(phone), _) if phone.contains(text) => c
      case c @ Contact(_, _, _, _, email) if email.contains(text) => c
    }.toSeq

}
