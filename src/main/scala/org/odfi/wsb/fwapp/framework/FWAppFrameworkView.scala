package org.odfi.wsb.fwapp.framework

import com.idyria.osi.vui.html.HTMLNode
import org.w3c.dom.html.HTMLElement
import org.odfi.wsb.fwapp.views.LibraryView
import org.odfi.wsb.fwapp.assets.AssetsManager
import org.odfi.wsb.fwapp.module.jquery.JQueryView

trait FWAppFrameworkView extends JQueryView {

  this.addLibrary("fwapp") {
    case (Some(source), target) =>
      onNode(target) {

        /*AssetsManager.findAssetSource("jquery") match {
          case Some(sourceForJquery) =>
            script(createAssetsResolverURI(sourceForJquery.basePath + "/fwapp/external/jquery/jquery.js")) {

            }
          case None =>
            script(createAssetsResolverURI("/fwapp/external/jquery/jquery-3.1.1.min.js")) {

            }
        }*/

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
    case (k, v) => v.__success == false
  }.isDefined

  def hasActionResult = !this.actionResults.isEmpty

  def saveActionResult(code: String, res: ActionsResultActionResult) = {
    this.actionResults = this.actionResults + (code -> res)
  }

  def getActionString(cl: => Any, codeprefix: String = ""): String = {

    //-- Make sure a session was set
    ensureSession

    //-- Get Hash code
    val node = currentNode
    var code = codeprefix + "" + node.hashCode()

    //-- Register 
    this.actions = actions + (code.toString -> (node, { node =>
      cl
    }))

    // println(s"Registered action $code on "+v.viewPath)
    code.toString

  }

  // Action support
  //----------
  
  def createJSCallAction(code: String, render: String = "none") = {
    s"fwapp.actions.callAction(this,'${getViewPath}?_action=${code}&_render=none',{_format: 'json'})"
  }
  
  def createSimpleNamedAction(name:String, render: String = "none")(cl: => Unit) = {
    
     //-- Make sure a session was set
    ensureSession
    
    //-- Register 
    val node = currentNode
    this.actions = actions + (name -> (node, { node =>
      cl
    }))
    
    s"fwapp.actions.callAction(this,'${getViewPath}?_action=${name}&_render=none',{_format: 'json'})"
    
  }
  

  // Cliking
  //----------------
  def onClickReload(cl: => Any): Unit = {
    reloadPage
    onClick(cl)
  }
  def onClick(cl: => Any): Unit = {

    var actionCode = this.getActionString(cl)
    //+@("onclick" -> (s"fwapp.actions.callAction(this,'${createSpecialPath("action", actionCode)}')").noDoubleSlash)
    +@("onclick" -> (s"fwapp.actions.callAction(this,'${getViewPath}?_action=${actionCode}&_render=none')"))

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