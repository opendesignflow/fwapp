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
