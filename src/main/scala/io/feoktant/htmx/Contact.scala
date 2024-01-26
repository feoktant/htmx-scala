package io.feoktant.htmx

import upickle.default.*

import java.nio.file.{Files, Path, Paths}
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
  
  import OptionPickler.*
  private given Reader[Contact] = Reader.derived
  private given Writer[Contact] = Writer.derived

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

  def loadDb(contactsPath: Path = Paths.get("contacts.json")): Unit =
    val contacts = read[List[Contact]](contactsPath)
    db.clear()
    db.addAll(contacts.map(c => c.id -> c))

  def saveDb(contactsPath: Path = Paths.get("contacts.json")): Unit =
    val contactsFile = Files.newBufferedWriter(contactsPath)
    writeTo[Seq[Contact]](db.values.toSeq, contactsFile)

  private object OptionPickler extends upickle.AttributeTagged {
    override implicit def OptionWriter[T: Writer]: Writer[Option[T]] =
      implicitly[Writer[T]].comap[Option[T]] {
        case None => null.asInstanceOf[T]
        case Some(x) => x
      }

    override implicit def OptionReader[T: Reader]: Reader[Option[T]] = {
      new Reader.Delegate[Any, Option[T]](implicitly[Reader[T]].map(Some(_))) {
        override def visitNull(index: Int): Option[T] = None
      }
    }
  }

}
