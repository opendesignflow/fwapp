package org.odfi.wsb.fwapp.framework.websocket

import org.odfi.wsb.fwapp.views.FWappView
import org.odfi.wsb.fwapp.views.FWAppViewIntermediary
import com.idyria.osi.wsb.webapp.http.connector.websocket.WebsocketPathIntermediary
import org.odfi.wsb.fwapp.framework.FWAppFrameworkView
import com.idyria.osi.ooxoo.core.buffers.structural.ElementBuffer

trait WebsocketView extends FWAppFrameworkView {

  def getWebsocketIntermediary = this.parentResource.get.asInstanceOf[FWAppViewIntermediary].parentIntermediary.intermediaries.collectFirst {
    case i: WebsocketPathIntermediary => i
  } match {

    //-- Add Websocket intermediary
    case None =>

      println("Requesting Websocket intermediary, none could be found in tree")
      None

    case Some(i) => Some(i)
  }

  // Make sure Websocket intermediary is present in tree
  //--------------
  this.onParentResourceAdded {
    println("**** Adding Websocket intermediary")
    val p = this.parentResource.get.asInstanceOf[FWAppViewIntermediary]
    p.intermediaries.find {
      case i: WebsocketPathIntermediary => true
      case i => false
    } match {
      //-- Add Websocket intermediary
      case None =>

        var websocket = new WebsocketPathIntermediary("/websocket")
        p.parentIntermediary <= websocket

        println("Adding ws to " + p.fullURLPath)

      case other =>
    }
  }

  // Add JS
  //---------------
  this.addLibrary("fwapp") {
    case (Some(l), target) =>
      onNode(target) {
        
        //-- Make sure Session is created
        this.request.get.getSession
        
        script(createAssetsResolverURI("/fwapp/websocket.js")) {

        }
      }

    case other =>
  }

  // Send backend Message
  //------------------------

  /**
   * Elt must not be soap, it will be packed in SOAP
   */
  def sendSOAPBackendMessage(elt: ElementBuffer) = {
    (this.request, getWebsocketIntermediary) match {
      case (Some(req), Some(wi)) =>
        wi.getInterface(req) match {
          case Some(interface) =>
            interface.writeSOAPPayload(elt)
          case None =>
            println(s"Sending Back message not possible, request and websocket intermediary (${wi.hashCode()}) set, but no WS interface for session: " + req.getSession)
        }
      case other =>
    }
  }
  def broadCastSOAPBackendMessage(elt: ElementBuffer) = {

    getWebsocketIntermediary match {
      case Some(wi) =>

        wi.websocketPool.values.foreach {
          interface =>

            interface.writeSOAPPayload(elt)
        }

      case None =>
    }
    getTopParentView.@->("soap.broadcast", elt)
  }

}