package org.odfi.wsb.fwapp.navigation

import com.idyria.osi.wsb.webapp.http.message.HTTPRequest
import scala.collection.mutable.ArrayStack
import org.odfi.wsb.fwapp.views.FWappView
import com.idyria.osi.wsb.webapp.http.message.HTTPResponse

trait NavigationUtils {

  def stackViewLocation(req: HTTPRequest) = {

    //-- Get or create Navigation Stack
    var stack = req.getSession.get("navigation.stack") match {
      case Some(obj : ArrayStack[String]) => obj
      case other =>
        var s = ArrayStack[String]()

        req.getSession.get("navigation.stack" -> s)
        
        s
    }

    //-- Push
    stack.push(req.originalPath)
  }
  
  

}

trait NavigationUtilsView extends FWappView {
  
  def redirectToOrigin= {
    
   request.get.getSession.get[ArrayStack[String]]("navigation.stack") match {
      case Some(obj) if(!obj.isEmpty) => 
        
        var latest = obj.pop()
        
        println(s"Redirect to latest stack URL: "+latest)
        //-- Redirect
        var resp = HTTPResponse.temporaryRedirect(latest)
        this.getFirstIntermediary.get.response(resp, request.get)     
        
      case other =>
     
    }
    
  }
  
}