package org.odfi.wsb.fwapp.navigation

import com.idyria.osi.wsb.webapp.http.message.HTTPRequest
import scala.collection.mutable.ArrayStack

trait NavigationUtils {

  def stackViewLocation(req: HTTPRequest) = {

    //-- Get or create Navigation Stack
    var stack = req.getSession.get("navigation.stack") match {
      case Some(obj: ArrayStack[String]) => obj
      case other =>
        var s = ArrayStack[String]()

        req.getSession.get("navigation.stack" -> s)
        
        s
    }

    //-- Push
    stack.push(req.originalPath)
  }

}