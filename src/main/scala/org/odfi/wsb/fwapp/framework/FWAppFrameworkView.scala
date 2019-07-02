/*-
 * #%L
 * FWAPP Framework
 * %%
 * Copyright (C) 2016 - 2017 Open Design Flow
 * %%
 * This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.odfi.wsb.fwapp.framework

import com.idyria.osi.vui.html.HTMLNode
import org.w3c.dom.html.HTMLElement
import org.odfi.wsb.fwapp.views.LibraryView
import org.odfi.wsb.fwapp.assets.AssetsManager
import org.odfi.wsb.fwapp.module.jquery.JQueryView
import com.idyria.osi.wsb.webapp.http.message.HTTPRequest
import org.odfi.wsb.fwapp.views.FWappView

trait FWAppFrameworkView extends JQueryView {

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
  var actionResults = Map[String, ActionResult]()

  def cleanActionResults = actionResults = actionResults.empty

  def hasActionErrors = this.actionResults.find {
    case (k, v) => v.__success.toBool == false
  }.isDefined

  def hasActionResult = !this.actionResults.isEmpty

  /**
   * Save result of action
   */
  def saveActionResult(code: String, res: ActionResult) = {
    this.actionResults = this.actionResults + (code -> res)
  }
  
  /**
   * Get actions results for action code or create one
   */
  def getActionsResultOrCreate(code:String) = {
    this.actionResults.get(code) match {
      case Some(r) => r
      case None => 
        var r = new ActionResult
        r.ID = code
        saveActionResult(code, r)
        r
    }
  }
  
  
  
  
  // Actions Registration
  //---------

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
    val parentViewsStrings = this.mapUpResources[FWappView, String] {
      view =>
        view.hashCode.toString()
    }

    val pathToCurrentView = (parentViewsStrings.reverse :+ this.hashCode().toString).mkString(".")

    //-- Register 
    this.actions = actions + (code.toString -> (node, { node =>
      cl
    }))

    /// println(s"Registered action $code on "+this.hashCode())
    pathToCurrentView + "." + code.toString

  }

  // Action support
  //----------

  def createJSCallAction(code: String, render: String = "none") = {

    //-- Make data json string
    var dataString = (List("_action" -> code, "_render" -> render, "_format" -> "json")).map { case (name, value) => s""" $name : '$value' """ }.mkString(",")

    //s"fwapp.actions.callAction(this,'${getViewPath}?_action=${code}&_render=none',{$dataString})"
    s"fwapp.actions.callAction(this,'${getViewPath}',{$dataString})"
  }

  def createJSCallActionWithData(code: String, data: List[(String, String)], render: String = "none") = {

    //-- Make data json string
    var dataString = (data :+ ("_format" -> "\"json\"")).map { case (name, value) => s""" "$name": $value """ }.mkString(",")

    s"fwapp.actions.callAction(this,'${getViewPath}?_action=${code}&_render=none',{$dataString})"
  }

  def registerNamedAction(name: String, render: String = "none")(cl: Function1[HTTPRequest, Unit]) = {

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

  def buttonClickReload(text: String)(cl: => Any) = {
    button(text) {
      val actioncode = onClickReload {
        cl
      }
      data("fwapp-action-code" -> actioncode)
    }
  }
  def buttonClickReloadConfirm(text:String,message:String)(cl: => Any) = {
      val ri = buttonClickReload(text)(cl)
     ri.attributeAppend("data-ui-confirm" -> message)
      ri
  }

  def buttonClick(text: String)(cl: => Any) = {
    button(text) {
      val actioncode = onClick {
        cl
      }
      data("fwapp-action-code" -> actioncode)

    }
  }

  def iconClick(cl: => Any) = {
    i {
      val actioncode = onClick(cl)
      data("fwapp-action-code" -> actioncode)
    }
  }

  def iconClickReload(cl: => Any) = {
    i {
      val actioncode = onClickReload(cl)
      data("fwapp-action-code" -> actioncode)
    }
  }

  def iconClickReloadConfirm(message:String)(cl: => Any) = {
      val ri = iconClickReload(cl)
     ri.attributeAppend("data-ui-confirm" -> message)
      ri
  }
  
  
  /**
   * Returns the Action Code
   */
  def onClickReload(cl: => Any): String = {
    reloadPage
    onClick(cl)
  }

  def onClickReloadConfirm(message:String)(cl: => Any) = {
     currentNode.attributeAppend("data-ui-confirm" -> message)
     onClickReload(cl)
  }
  
  /**
   * Returns the Action Code
   */
  def onClick(cl: => Any): String = {

    var actionCode = this.getActionString(cl)
    //+@("onclick" -> (s"fwapp.actions.callAction(this,'${createSpecialPath("action", actionCode)}')").noDoubleSlash)
    //+@("onclick" -> (s"""fwapp.actions.callAction(this,'${getViewPath}',{ '_action' : '${actionCode}', '_render' : 'none' })"""))
    +@("onclick" -> createJSCallAction(actionCode))

    actionCode
  }

  // Form
  //----------------
  override def form(cl: => Any) = {
    super.form {
      +@("method" -> "post")
      cl
    }
  }
  def onSubmit(cl: => Any) {
    var actionCode = this.getActionString(cl)
    input {
      +@("type" -> "hidden")
      +@("value" -> actionCode)
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
