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
package org.odfi.wsb.fwapp.framework.websocket

import org.odfi.wsb.fwapp.views.FWappView
import org.odfi.wsb.fwapp.views.FWAppViewIntermediary
import com.idyria.osi.wsb.webapp.http.connector.websocket.WebsocketPathIntermediary
import org.odfi.wsb.fwapp.framework.FWAppFrameworkView
import com.idyria.osi.ooxoo.core.buffers.structural.ElementBuffer

trait WebsocketView extends FWAppFrameworkView {

  // Add JS
  //---------------
  this.addLibrary("fwapp") {
    case (Some(l), target) =>
      onNode(target) {

        //-- Make sure Session is created
        ensureSession
        //this.request.get.getSession
        // println(s"Cleaning websocket connected listeners")
        // this.listeningPoints = listeningPoints - "websocket.connected"
        script(createAssetsResolverURI("/fwapp/websocket/websocket.js")) {

        }
      }

    case other =>
  }

  // Connection Made
  //-------------
  /* override def rerender = {
    println(s"Cleaning websocket connected listeners")
    this.listeningPoints = listeningPoints - "websocket.connected"
    super.rerender
  }*/
  override def render = {
    println(s"Cleaning websocket connected listeners")
    this.listeningPoints = listeningPoints - "websocket.connected"
    super.render
  }
  def onWebsocketConnectionMade(cl: => Any) = {
    this.on("websocket.connected") {
      cl
    }
  }

  def getWebsocketIntermediary = this.findUpchainResource[FWAppViewIntermediary].get.parentIntermediary.intermediaries.collectFirst {
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
    //println("**** Adding Websocket intermediary")
    this.findUpchainResource[FWAppViewIntermediary] match {
      case Some(p) =>
        p.intermediaries.find {
          case i: WebsocketPathIntermediary => true
          case i                            => false
        } match {
          //-- Add Websocket intermediary
          case None =>

            var websocket = new WebsocketPathIntermediary("/websocket")
            websocket.onWebsocketConnected {
              this.@->("websocket.connected")
            }
            p.parentIntermediary <= websocket

          // println("Adding ws to " + p.fullURLPath)

          case other =>
        }
      case None =>
    }
  

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
            interface.nc.waitForInputPayload(1000)
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
            interface.nc.waitForInputPayload(1000)

          //println("Got input payload -> keep going")
        }

      case None =>
    }
    // getTopParentView.@->("soap.broadcast", elt)
  }

}
