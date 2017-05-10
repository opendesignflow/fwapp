package org.odfi.wsb.fwapp.framework.websocket

trait WebsocketUpdatesView extends WebsocketView {
  
  // Add JS
  //---------------
  this.addLibrary("fwapp") {
    case (_, target) =>
      onNode(target) {
        
        
        script(createAssetsResolverURI("/fwapp/websocket/websocket-updates.js")) {

        }
      } 

    case other =>
  }
}