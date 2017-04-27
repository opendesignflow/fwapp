package org.odfi.wsb.fwapp.framework

import com.idyria.osi.vui.html.HTMLNode
import org.w3c.dom.html.HTMLElement
import org.odfi.wsb.fwapp.views.LibraryView
import org.odfi.wsb.fwapp.assets.AssetsManager
import org.odfi.wsb.fwapp.module.jquery.JQueryView
import com.idyria.osi.wsb.webapp.http.message.HTTPRequest
import org.odfi.wsb.fwapp.views.FWappView

trait FWAppFrameworkView extends JQueryView  {

  this.addLibrary("fwapp") {
    case (Some(source), target) =>
      onNode(target) {

        script(createAssetsResolverURI("/fwapp/framework.js")) {

        }

        script(createAssetsResolverURI("/fwapp/ui-interface.js")) {

        }

        script(createAssetsResolverURI("/fwapp/actions.js")) {

        }

      }
    case (None, target) =>

  }

  var actions = Map[String, (HTMLNode[HTMLElement, _], HTMLNode[HTMLElement, _] => Any)]()
  var actionResults = Map[String, ActionsResultActionResult]()

  def cleanActionResults = actionResults = actionResults.empty

  def hasActionErrors = this.actionResults.find {
    case (k, v) => v.__success.toBool == false
  }.isDefined

  
  
  def hasActionResult = !this.actionResults.isEmpty

  def saveActionResult(code: String, res: ActionsResultActionResult) = {
    this.actionResults = this.actionResults + (code -> res)
  }

  /**
   * only function creating action code and registering it
   */
  def getActionString(cl: => Any, codeprefix: String = ""): String = {

    //-- Make sure a session was set
    ensureSession

    //-- Get Hash code
    val node = currentNode
    var code = codeprefix + "" + node.hashCode()
    
    //-- Create action path to current view
    val parentViewsStrings = this.mapUpResources[FWappView,String] {
      view => 
        view.hashCode.toString()
    }
    
    val pathToCurrentView = (parentViewsStrings.reverse :+ this.hashCode().toString).mkString(".")

    //-- Register 
    this.actions = actions + (code.toString -> (node, { node =>
      cl
    }))

    /// println(s"Registered action $code on "+this.hashCode())
    pathToCurrentView+"."+code.toString

  }

  // Action support
  //----------

  def createJSCallAction(code: String, render: String = "none") = {

    //-- Make data json string
    var dataString = (List("_action" -> code, "_render" -> render,"_format" -> "json")).map { case (name, value) => s""" $name : '$value' """ }.mkString(",")

    //s"fwapp.actions.callAction(this,'${getViewPath}?_action=${code}&_render=none',{$dataString})"
    s"fwapp.actions.callAction(this,'${getViewPath}',{$dataString})"
  }

  def createJSCallActionWithData(code: String, data: List[(String, String)], render: String = "none") = {

    //-- Make data json string
    var dataString = (data :+ ("_format" -> "\"json\"")).map { case (name, value) => s""" "$name": $value """ }.mkString(",")

    s"fwapp.actions.callAction(this,'${getViewPath}?_action=${code}&_render=none',{$dataString})"
  }

  def registerNamedAction(name: String, render: String = "none")(cl: Function1[HTTPRequest,Unit]) = {

    //-- Make sure a session was set
    ensureSession

    //-- Register 
    val node = currentNode
    this.actions = actions + (name -> (node, { node =>
      cl
    }))
    
    cl

    //s"fwapp.actions.callAction(this,'${getViewPath}?_action=${name}&_render=none',{_format: 'json'})"

  }

  // Data from components and between component
  //------------------
  def withData(name: String, valueJS: String) = {

    data("value-" + name -> valueJS)

  }
  
  

  // Cliking
  //----------------
  
  def buttonClickReload(text:String)(cl: => Any) = {
    button(text) {
      onClickReload {
        cl
      }
    }
  }
  
  def buttonClick(text:String)(cl: => Any) =  {
    button(text) {
      onClickReload {
        cl
      }
    }
  }
  
  def iconClick(cl: => Any) = {
    i {
      onClick(cl)
    }
  }
  
  def iconClickReload(cl: => Any) = {
    i {
      onClickReload(cl)
    }
  }
  
  
  def onClickReload(cl: => Any): Unit = {
    reloadPage
    onClick(cl)
  }
  def onClick(cl: => Any): Unit = {

    var actionCode = this.getActionString(cl)
    //+@("onclick" -> (s"fwapp.actions.callAction(this,'${createSpecialPath("action", actionCode)}')").noDoubleSlash)
    //+@("onclick" -> (s"""fwapp.actions.callAction(this,'${getViewPath}',{ '_action' : '${actionCode}', '_render' : 'none' })"""))
    +@("onclick" -> createJSCallAction(actionCode))

  }

  // Form
  //----------------
  override def form(cl: => Any) = {
    super.form {
      +@("method"->"post")
      cl
    }
  }
  def onSubmit(cl: => Any) {
    var actionCode = this.getActionString(cl)
    input {
      +@("type" -> "hidden")
      +@("value"->actionCode)
      fieldName("_action")
      
    }
   // +@("onsubmit" -> (s"fwapp.actions.callAction(this,'${getViewPath}?_action=${actionCode}&_render=none')"))
  }
  // JS Interface
  //----------------
  def reloadPage = {
    +@("reload" -> "true")
  }

  def waitReloadPage = {
    +@("wait" -> "true")
    reloadPage
  }

}