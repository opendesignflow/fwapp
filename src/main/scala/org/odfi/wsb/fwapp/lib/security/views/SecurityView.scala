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
package org.odfi.wsb.fwapp.lib.security.views

import org.odfi.wsb.fwapp.views.LibraryView
import org.odfi.wsb.fwapp.framework.FWAppFrameworkView
import org.odfi.wsb.fwapp.lib.security.CommonSecurityUser
import org.odfi.wsb.fwapp.lib.security.SecurityLibModule
import com.idyria.osi.wsb.webapp.http.message.HTTPRequest

trait SecurityHelper {

  // Utilities
  //---------------

  /**
   * Get user from session
   */
  def securitySessionGetUser(request: Option[HTTPRequest]): Option[CommonSecurityUser] = {
    request.get.getSession match {
      case Some(session) if (session.hasValueOfNameAndType[CommonSecurityUser]("fwapp.lib.security.user")) =>
        session[CommonSecurityUser]("fwapp.lib.security.user")
      case other => None
    }
  }

  /**
   * Get Login user from session
   */
  def securitySessionGetLoginUser(request: Option[HTTPRequest]): Option[CommonSecurityUser] = {
    request.get.getSession match {
      case Some(session) if (session.hasValueOfNameAndType[CommonSecurityUser]("fwapp.lib.security.loginUser")) =>
        session[CommonSecurityUser]("fwapp.lib.security.loginUser")
      case other => None
    }
  }
  
  def securitySessionGetAndCleanLoginUser(request: Option[HTTPRequest]): Option[CommonSecurityUser] = {
    securitySessionGetLoginUser(request) match {
      case Some(user) => 
        request.get.getSession.get.removeValue("fwapp.lib.security.loginUser")
        Some(user) 
      case other => 
        request.get.getSession.get.removeValue("fwapp.lib.security.loginUser")
        other
    }
  }

  def securitySessionSetUser(request: Option[HTTPRequest], user: CommonSecurityUser): Unit = {
    request.get.getSession.get("fwapp.lib.security.user" -> user)
  }

  def securitySessionSaveUser(request: Option[HTTPRequest]): Unit = securitySessionGetUser(request) match {

    case Some(user) =>

      //println(s"Saving USER")
      //SecurityLibModule.saveUser(user)
      sys.error("REDESIGN")

    case other =>
      println(s"No user on session")
  }
  
  def securitySessionSaveLoginUser(request: Option[HTTPRequest],user:CommonSecurityUser): CommonSecurityUser = {
    request.get.getSession match {
      case Some(session) =>
        session("fwapp.lib.security.loginUser" -> user)
        
      case other => None
    }
    user
  }

  /*def securitySessionCreateUser(request: Option[HTTPRequest]): CommonSecurityUser = securitySessionGetUser(request) match {
    case None =>

      var user = CommonSecurityUser()
      request.get.getSession match {
        case Some(session) =>
          session("fwapp.lib.security.user" -> user)
        case other =>
      }
      user
    case other => other.get
  }*/

  /**
   * Kick CommonSecurityUser from session
   */
  def securitySessionSignout(request: Option[HTTPRequest]): Unit = {
    request.get.getSession match {
      case Some(session) =>
        session.removeValue("fwapp.lib.security.user")

      case other =>
    }

  }
  
   def securityGetUserByEmail(email: String) = {
    SecurityLibModule.getUsers.find {
      user =>
        println(s"Testing user: /" + user.email + "/ against /" + email + "/")
        user.email != null && user.email.toString() == email
    }
  }

}

trait SecurityView extends LibraryView with FWAppFrameworkView with SecurityHelper {

  this.addLibrary("fwapp") {
    case (_, targetNode) =>

      ensureSession

      onNode(targetNode) {

        script(createAssetsResolverURI("/fwapp/lib/security/security.js")) {

        }

      }
  }

  // To Be Implemented
  //-----------------------
  def securitySessionCreateUser: CommonSecurityUser = {
    sys.error("Not Implemented")
  }
  
  // Utilities, mapped from helper
  //---------------

  /**
   * Get user from session
   */
  def securitySessionGetUser: Option[CommonSecurityUser] = securitySessionGetUser(request)

  def securitySessionSetUser(user: CommonSecurityUser): Unit = {
    request.get.getSession.get("fwapp.lib.security.user" -> user)
  }

  def securitySessionSaveUser: Unit = securitySessionSaveUser(request)

  //def securitySessionCreateUser: CommonSecurityUser = securitySessionCreateUser(request)

  /**
   * Kick CommonSecurityUser from session
   */
  def securitySessionSignout: Unit = securitySessionSignout(request)
  
  def securitySessionOnUserRole(r:String)(cl: => Any) = securitySessionGetUser match {
    case Some(user) if(user.isValid && user.hasRole(r)) => 
      cl
    case other => 
  }
  

}
