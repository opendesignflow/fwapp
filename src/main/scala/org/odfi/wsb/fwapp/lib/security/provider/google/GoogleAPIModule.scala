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
