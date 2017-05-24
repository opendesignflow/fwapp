package org.odfi.wsb.fwapp.lib.security.provider.google

import org.odfi.indesign.core.module.IndesignModule
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import org.odfi.wsb.fwapp.lib.security.SecurityLibModule
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import scala.collection.convert.DecorateAsJava
import scala.collection.convert.DecorateAsScala

object GoogleAPIModule extends IndesignModule with DecorateAsJava with DecorateAsScala {

  // ID Token Verifier
  //------------------
  var idTokenVerifierBuilder = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
  var idTokenVerifier: Option[GoogleIdTokenVerifier] = None

  def checkOAuthTokenReturnEmailAndUUID(token: String) = idTokenVerifier match {
    case Some(verifier) =>

      verifier.verify(token) match {
        case null =>

          sys.error("Provided Auth Token is Invalid, signout and signin again")

        case validToken =>

          (validToken.getPayload.getEmail, validToken.getPayload.getEmailVerified==true) match {
            case (null, _) => sys.error("Cannot Authenticate if the Email is not provided by the user, please accept the access to the email for our service")
            case (_, false) => sys.error("Cannot Authenticate if the Email was not verified, please verify your email at Google before retrying")
            case other => 
             
              (validToken.getPayload.getEmail, validToken.getPayload.getSubject)
          }

      }

    case None =>
      sys.error("Not Google Token Verifier, make sure you set the client ID in the config")
  }

  // Lifeycle
  //-----------
  this.onSetup {
    requireModule(SecurityLibModule)
  }

  this.onStart {

    idTokenVerifier = Some(idTokenVerifierBuilder.setAudience(List(configGetString("clientID", "")).asJava).build())

  }

}