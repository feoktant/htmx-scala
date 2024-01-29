package io.feoktant.htmx

import upickle.default.*

import java.nio.file.{Files, Path, Paths}
import java.util.concurrent.atomic.AtomicInteger
import scala.collection.concurrent.TrieMap

case class Contact(
    first: Option[String],
    last: Option[String],
    phone: Option[String],
    email: String,
    id: Int = -1,
)

object Contact {
  // mock contacts database
  private val db = TrieMap.empty[Int, Contact]
  private val nextId = new AtomicInteger()
  private val PageSize = 100
  
  import OptionPickler.*
  private given Reader[Contact] = Reader.derived
  private given Writer[Contact] = Writer.derived

  private def validate(contact: Contact): Either[Map[String, String], Contact] =
    if (contact.email.isEmpty)
      Left(Map("email" -> "Email Required"))
    else if (!db.contains(contact.id) && db.values.exists(_.email == contact.email))
      Left(Map("email" -> "Email Must Be Unique"))
    else
      Right(contact)

  def save(contact: Contact): Either[Map[String, String], Contact] =
    val newContact =
      if (contact.id == -1) contact.copy(id = nextId.getAndIncrement())
      else contact
    validate(newContact).map { validatedContact =>
      db.put(validatedContact.id, validatedContact)
      saveDb()
      validatedContact
    }

  def all(page: Int = 1): Seq[Contact] =
    val start = 0.max(page - 1) * PageSize
    val end = start + PageSize
    db.values.toSeq.sortBy(_.id).slice(start, end)

  def search(text: String): Seq[Contact] =
    db.values.collect {
      case c @ Contact(Some(first), _, _, _, _) if first.contains(text) => c
      case c @ Contact(_, Some(last), _, _, _) if last.contains(text) => c
      case c @ Contact(_, _, Some(phone), _, _) if phone.contains(text) => c
      case c @ Contact(_, _, _, email, _) if email.contains(text) => c
    }.toSeq.sortBy(_.id)

  def loadDb(contactsPath: Path = Paths.get("contacts.json")): Unit =
    val contacts = read[List[Contact]](contactsPath)
    nextId.set(contacts.map(_.id).max + 1)
    db.clear()
    db.addAll(contacts.map(c => c.id -> c))

  private[htmx] def saveDb(contactsPath: Path = Paths.get("contacts.json")): Unit =
    val contactsOut = Files.newOutputStream(contactsPath)
    writeToOutputStream[Seq[Contact]](db.values.toSeq.sortBy(_.id), contactsOut, 2)

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
