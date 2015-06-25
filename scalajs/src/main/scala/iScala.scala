
import scala.scalajs.js
import scala.scalajs.js.Dynamic.global
import model._
import org.scalajs.jquery._
import upickle._
import com.scalawarrior.scalajs.ace._

object iScala extends js.JSApp {

  def main(): Unit = {
    var editor = ace.edit("editor")
    editor.setTheme("ace/theme/monokai")
    editor.getSession().setMode("ace/mode/scala")

    editor.commands.addCommand(js.Dynamic.literal(
      name = "Run Scala code",
      bindKey = "Command-Enter",
      exec    = (e: Editor) => {
        // Send source code to the server
        val range = e.getSelectionRange()
        val source = e.getCopyText match {
          case "" => e.getValue()
          case x => x
        }
        println(source)
        run(source)
      }
    ).asInstanceOf[EditorCommand])

    println("Hello iScala!!")
  }

  def run(source: String): Unit = {
    jQuery("#indicator").show()
    jQuery.ajax(js.Dynamic.literal(
      url     = "/run",
      `type`  = "POST",
      data    = js.Dynamic.literal("source" -> source),
      success = (data: js.Any, status: String, jqXHR: JQueryXHR) => {
        val response = read[RunResponse](jqXHR.responseText)
        jQuery("#result").append(jQuery("<div>").text("> " + response.result))
        jQuery("#indicator").hide()
      },
      error = (jqXHR: JQueryXHR, textStatus: String, errorThrow: String) => {
        jQuery("#indicator").hide()
        global.alert(jqXHR.responseText)
      }
    ).asInstanceOf[JQueryAjaxSettings])
  }

}
