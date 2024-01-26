package io.feoktant.htmx

import scalatags.Text.all.*

object Templates {

  private def layout(content: Seq[Modifier]): doctype =
    doctype("html")(
      html(lang := "")(
        head(
          title := "Contact App",
          link(rel := "stylesheet", href := "https://unpkg.com/missing.css@1.1.1"),
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

    layout(Seq(
      aForm(None),
      contactsTable(contacts),
      newContactLink,
    ))
  }

}
