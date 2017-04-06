package org.odfi.wsb.fwapp.lib.security.views

import org.odfi.wsb.fwapp.views.LibraryView
import org.odfi.wsb.fwapp.lib.security.User
import org.odfi.wsb.fwapp.lib.security.SecurityLibModule
import com.idyria.osi.tea.hash.HashUtils
import org.odfi.wsb.fwapp.lib.security.provider.google.GoogleAPIModule
import com.idyria.osi.wsb.webapp.http.message.HTTPRequest
import org.odfi.wsb.fwapp.lib.security.provider.passwordless.PasswordLessModule
import org.odfi.wsb.fwapp.navigation.NavigationUtilsView

trait SigninView extends SecurityView with NavigationUtilsView {

  this.addLibrary("fwapp") {
    case (_, targetNode) =>

      request.get.getSession

      onNode(targetNode) {

        script(createAssetsResolverURI("/fwapp/lib/security/security-signin.js")) {

        }

      }
  }

  // Register Actions
  //-------------
 

  def securitySignin(request: HTTPRequest) = {

    val request = this.request.get
    try {
      

      //-- Check parameters
      println("Calling registerd action for singin: " + request)
      request.ensureURLParameters(List("providerID", "tokenID"))

      println("Calling Signin for: " + request.getURLParameter("tokenID"))

      //-- Look for User
      securitySessionGetUser match {

        //-- Not Doing Anything if alreaddy defined
        case Some(user) if (user.isValid) =>
          securitySessionSignout
        case other =>
      }

      //-- Verify Token by checking it and getting Email
      val providerID = request.getURLParameter("providerID").get
      val tokenID = request.getURLParameter("tokenID").get
      //val email = request.getURLParameter("email").get

      val (verifiedEmail, uuid) = providerID match {
        case "google" =>

          GoogleAPIModule.checkOAuthTokenReturnEmailAndUUID(tokenID)

        case "passwordless" =>

          PasswordLessModule.checkToken(tokenID)
          //(email, tokenID)

        case other =>
          throw new RuntimeException(s"Cannot authenticate, provider $other unknown")
      }

      //-- Verify Provider Email and URL Email
      //if (verifiedEmail != email) {
      //  sys.error("Your verified Identity provided us with a different email than the one provided by the signing call...this may be a hacking trial.")
//
      //}

      // println(s"Verified Email: " + verifiedEmail)
      //val UUIDSHA256 = HashUtils.hashBytesAsBase64(uuid.getBytes, "SHA-256")

      //-- Look for a user to authenticate against
      val foundUser = securityGetUserByEmail(verifiedEmail) match {
        case None =>
          securitySessionGetAndCleanLoginUser(Some(request))
        case Some(other) =>
          Some(other)
      }

      foundUser match {
        case None =>
          //sys.error("Signin can only be made against an existing user or a temporary login user")

          //-- Create user
          var user = securitySessionCreateUser
          user.email = verifiedEmail

          //-- Add Federated Identity
          user.federate(providerID, uuid)

          ESome(user)

        case Some(user) =>

          //-- Check Identities
          user.findValidIdentity(providerID, uuid) match {
            case Some(id) =>
              println(s"ID User")
              securitySessionSetUser(user)
              ESome(user)
            case None =>

              EError(new IllegalArgumentException("Could not find valid Identity to authenticate for current user"))
            //sys.error("Could not find valid Identity to authenticate for current user")

            //println(s"Merging Identity")
            //user.federate(providerID, uuid)
          }

      }

    } finally {

      // Signin final actions -> clean everything
      println("Cleaning Login User: "+ request.getSession.get)
      securitySessionGetAndCleanLoginUser(Some(request))
      
    }

  }

  registerNamedAction("fwapp.lib.security.signin") {

    request =>
      securitySignin(request)

  }

}