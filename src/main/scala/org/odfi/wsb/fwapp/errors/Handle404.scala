package org.odfi.wsb.fwapp.errors

import com.idyria.osi.wsb.webapp.http.message.HTTPIntermediary
import com.idyria.osi.wsb.webapp.http.message.HTTPResponse
import com.idyria.osi.wsb.webapp.http.message.HTTPRequest

class Handle404  extends HTTPIntermediary {
  
  this.acceptDown[HTTPRequest] {
    req => 
      //println("Testing 404 against: "+req.path+s", upped=${req.upped} && errors=${req.hasErrors}")
      req.upped==false && req.hasErrors==false
  }
  
  
  this.onDownMessage {
    req => 
      println("404 for : "+req.path)
      var r = HTTPResponse.c404
      r.contentType = "text/plain"
      r.setTextContent("Not Found: "+req.originalPath)
      response(r,req) 
  }
}