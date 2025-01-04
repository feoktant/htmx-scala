package io.feoktant.htmx

import scalatags.Text.all.*
import scalatags.generic

object Templates {

  private def layout(content: Frag*): doctype =
    doctype("html")(
      html(lang := "")(
        head(
          title := "Contact App",
          link(rel := "stylesheet", href := "https://unpkg.com/missing.css@1.1.3"),
          link(rel := "stylesheet", href := "/static/site.css"),
        ),
        body()(
          header(
            h1(
              tag("all-caps")("contacts.app"),
              tag("sub-title")("A Demo Contacts Application"),
            )
          ),
          content,
        )
      )
    )

  def index(contacts: Seq[Contact]): doctype = {
    // Start of index.html content
    def aForm(maybeQ: Option[String]): Frag =
      form(action := "/contacts", method := "get", `class` := "tool-bar")( // 3
        label(`for` := "search")("Search Term"),
        input(id := "search", `type` := "search", name := "q", value := maybeQ.getOrElse("")), // 4
        input(`type` := "submit", value := "Search"),
      )

    // The contacts table
    def contactsTable(contacts: Seq[Contact]): Frag =
      table(
        thead(
          tr(
            th("First"), th("Last"), th("Phone"), th("Email"), th(), // 1
          )
        ),
        tbody(
          contacts.map { contact => // 2
            tr(
              td(contact.first),
              td(contact.last),
              td(contact.phone),
              td(contact.email), // 3
              td(
                a(href := s"/contacts/${contact.id}/edit")("Edit"),
                a(href := s"/contacts/${contact.id}")("View"), // 4
              ),
            )
          }
        )
      )

    // The “add contact” link
    def newContactLink: Frag =
      p(
        a(href := "/contacts/new")("Add Contact") // 1
      )

    layout(
      aForm(None),
      contactsTable(contacts),
      newContactLink,
    )
  }

  def `new`(
      maybeContact: Option[Contact] = None,
      errors: Map[String, String] = Map.empty,
  ): doctype =
    //1 A form that submits to the /contacts/new path, using an HTTP POST.
    //2 A label for the first form input.
    //3 The first form input, of type email.
    //4 Any error messages associated with this field.
    layout(
      form(action := "/contacts/new", method := "post")( // 1
        legend("Contact Values"),
        p(
          label(`for` := "email")("Email"), // 2
          input(name := "email", id := "email", `type` := "email",
            placeholder := "Email", value := maybeContact.map(_.email).getOrElse("")), // 3
          span(`class` := "error")(errors.get("email")), // 4
        ),
        p(
          label(`for` := "first_name")("First Name"),
          input(name := "first_name", id := "first_name", `type` := "text",
            placeholder := "First Name", value := maybeContact.flatMap(_.first).getOrElse("")),
          span(`class` := "error")(errors.get("first")),
        ),
        p(
          label(`for` := "last_name")("Last Name"),
          input(name := "last_name", id := "last_name", `type` := "text",
            placeholder := "Last Name", value := maybeContact.flatMap(_.last).getOrElse("")),
          span(`class` := "error")(errors.get("last")),
        ),
        p(
          label(`for` := "phone")("Phone"),
          input(name := "phone", id := "phone", `type` := "text",
            placeholder := "Phone", value := maybeContact.flatMap(_.phone).getOrElse("")),
          span(`class` := "error")(errors.get("phone")),
        ),
        button("Save")
      ),
      p(
        a(href := "/contacts")("Back")
      ),
    )

  def show(contact: Contact): doctype =
    layout(
      h1(Seq(contact.first, contact.last).flatten.mkString(" ")),
      div(
        div(s"Phone: ${contact.phone.getOrElse("")}"),
        div(s"Email: ${contact.email}")
      ),
      p(
        a(href := s"/contacts/${contact.id}/edit")("Edit"),
        a(href := "/contacts")("Back")
      ),
    )

  def edit(
    contact: Contact,
    errors: Map[String, String] = Map.empty,
  ): doctype =
    layout(
      form(action := s"/contacts/${contact.id}/edit", method := "post")(
        legend("Contact Values"),
        p(
          label(`for` := "email")("Email"),
          input(name := "email", id := "email", `type` := "email",
                placeholder := "Email", value := contact.email),
          span(`class` := "error")(errors.get("email")),
        ),
        p(
          label(`for` := "first_name")("First Name"),
          input(name := "first_name", id := "first_name", `type` := "text",
               placeholder := "First Name", value := contact.first.getOrElse("")),
          span(`class` := "error")(errors.get("first")),
        ),
        p(
          label(`for` := "last_name")("Last Name"),
          input(name := "last_name", id := "last_name", `type` := "text",
               placeholder := "Last Name", value := contact.last.getOrElse("")),
          span(`class` := "error")(errors.get("last")),
        ),
        p(
          label(`for` := "phone")("Phone"),
          input(name := "phone", id := "phone", `type` := "text",
               placeholder := "Phone", value := contact.phone.getOrElse("")),
          span(`class` := "error")(errors.get("phone")),
        ),
        button("Save")
      ),
      form(action := s"/contacts/${contact.id}/delete", method := "post")(
        button("Delete")
      ),
      p(
        a(href := "/contacts")("Back")
      ),
    )
}
