package org.odfi.wsb.fwapp.lib.security.views

import org.odfi.wsb.fwapp.views.LibraryView
import org.odfi.wsb.fwapp.framework.FWAppFrameworkView
import org.odfi.wsb.fwapp.lib.security.User
import org.odfi.wsb.fwapp.lib.security.SecurityLibModule
import com.idyria.osi.wsb.webapp.http.message.HTTPRequest

trait SecurityHelper {

  // Utilities
  //---------------

  /**
   * Get user from session
   */
  def securitySessionGetUser(request: Option[HTTPRequest]): Option[User] = {
    request.get.getSession match {
      case Some(session) if (session.hasValueOfNameAndType[User]("fwapp.lib.security.user")) =>
        session[User]("fwapp.lib.security.user")
      case other => None
    }
  }

  /**
   * Get Login user from session
   */
  def securitySessionGetLoginUser(request: Option[HTTPRequest]): Option[User] = {
    request.get.getSession match {
      case Some(session) if (session.hasValueOfNameAndType[User]("fwapp.lib.security.loginUser")) =>
        session[User]("fwapp.lib.security.loginUser")
      case other => None
    }
  }
  
  def securitySessionGetAndCleanLoginUser(request: Option[HTTPRequest]): Option[User] = {
    securitySessionGetLoginUser(request) match {
      case Some(user) => 
        request.get.getSession.get.removeValue("fwapp.lib.security.loginUser")
        Some(user) 
      case other => 
        request.get.getSession.get.removeValue("fwapp.lib.security.loginUser")
        other
    }
  }

  def securitySessionSetUser(request: Option[HTTPRequest], user: User): Unit = {
    request.get.getSession.get("fwapp.lib.security.user" -> user)
  }

  def securitySessionSaveUser(request: Option[HTTPRequest]): Unit = securitySessionGetUser(request) match {

    case Some(user) =>

      //println(s"Saving USER")
      SecurityLibModule.saveUser(user)

    case other =>
      println(s"No user on session")
  }
  
  def securitySessionSaveLoginUser(request: Option[HTTPRequest],user:User): User = {
    request.get.getSession match {
      case Some(session) =>
        session("fwapp.lib.security.loginUser" -> user)
        
      case other => None
    }
    user
  }

  def securitySessionCreateUser(request: Option[HTTPRequest]): User = securitySessionGetUser(request) match {
    case None =>

      var user = User()
      request.get.getSession match {
        case Some(session) =>
          session("fwapp.lib.security.user" -> user)
        case other =>
      }
      user
    case other => other.get
  }

  /**
   * Kick User from session
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

  // Utilities, mapped from helper
  //---------------

  /**
   * Get user from session
   */
  def securitySessionGetUser: Option[User] = securitySessionGetUser(request)

  def securitySessionSetUser(user: User): Unit = {
    request.get.getSession.get("fwapp.lib.security.user" -> user)
  }

  def securitySessionSaveUser: Unit = securitySessionSaveUser(request)

  def securitySessionCreateUser: User = securitySessionCreateUser(request)

  /**
   * Kick User from session
   */
  def securitySessionSignout: Unit = securitySessionSignout(request)
  
  def securitySessionOnUserRole(r:String)(cl: => Any) = securitySessionGetUser match {
    case Some(user) if(user.isValid && user.hasRole(r)) => 
      cl
    case other => 
  }
  

}