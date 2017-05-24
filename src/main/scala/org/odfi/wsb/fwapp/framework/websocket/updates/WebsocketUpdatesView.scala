package org.odfi.wsb.fwapp.framework.websocket.updates

import org.odfi.wsb.fwapp.framework.websocket.WebsocketView

trait WebsocketUpdatesView extends WebsocketView {
  
  // Add JS
  //---------------
  this.addLibrary("fwapp") {
    case (Some(l), target) =>
      onNode(target) {
        
        script(createAssetsResolverURI("/fwapp/websocket/websocket-updates.js")) {

        }
      }

    case other =>
  }
  
  
  def updateAttribute(targetId:String,name:String,value:String) = {
    
    var attrU = new UpdateAttribute
    attrU.targetID = targetId
    attrU.value = value
    attrU.name = name
    
    this.sendSOAPBackendMessage(attrU)
  }
  
}