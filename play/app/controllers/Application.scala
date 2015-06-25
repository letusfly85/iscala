package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json._
import model._

object Application extends Controller {

  implicit val runResponseWrites = Json.writes[RunResponse]

  val runRequestForm = Form(
    mapping(
      "source" -> nonEmptyText()
    )(RunRequest.apply)(RunRequest.unapply)
  )

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def run = Action { implicit request =>
    runRequestForm.bindFromRequest.fold(
      error => BadRequest(error.errorsAsJson),
      form  => {
        eval(form.source) match {
          case Left(x)  => BadRequest(x)
          case Right(x) => Ok(Json.toJson(RunResponse(x)))
        }
      }
    )
  }

  import scala.reflect.runtime.currentMirror
  import scala.tools.reflect.ToolBox
  import scala.tools.reflect.ToolBoxError

  private def eval(source: String): Either[String, String] = try {
    val toolbox = currentMirror.mkToolBox()
    val tree = toolbox.parse(source)
    val result = toolbox.eval(tree)

    Right(result.toString)

  } catch {
    case e: ToolBoxError => Left(e.getMessage)
    case e: Throwable => Left(e.toString)
  }


}

