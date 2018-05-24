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
package org.odfi.wsb.fwapp.lib.security.provider.passwordless

import org.odfi.wsb.fwapp.module.semantic.SemanticView
import org.odfi.wsb.fwapp.lib.security.views.SecurityView
import java.util.UUID
import com.idyria.osi.tea.hash.HashUtils
import com.idyria.osi.ooxoo.core.buffers.datatypes.DateTimeBuffer
import org.odfi.wsb.fwapp.lib.security.views.SigninView
import org.odfi.indesign.core.module.email.EmailModule
import org.odfi.wsb.fwapp.lib.security.User
import org.odfi.wsb.fwapp.lib.markdown.MarkdownView
import java.net.URLEncoder
import org.odfi.wsb.fwapp.lib.security.provider.google.GoogleRecaptchaView
import org.odfi.wsb.fwapp.lib.security.provider.google.GoogleRecaptchaModule

trait PasswordLessView extends SemanticView with SigninView with MarkdownView with GoogleRecaptchaView {

  def passwordLessButton(cl: => Any) = {

    // If request contains token -> Signin
    //-----------
    request.get.getURLParameter("tokenID") match {
      case Some(token) =>

        //-- Call Signin Action
        println(s"Signing in")
        securitySignin(request.get) match {
          case ESome(user) if (user.isPermanent) =>

            user.cleanTokensFor("passwordless", save = true)
            "ui success message" :: "Login Successful"
            redirectToOrigin

          case ESome(user) if (!user.isPermanent) =>

            "ui success message" :: "Authentication Successful"
            markdown("""|
                        |It seems you are signing in for the first time.
                        |Do you want to save your user?
                        |
                        |""".stripMargin)

            "ui success button" :: button("Save User") {

              onClickReload {
                //user.save
              }

            }

          case EError(err) =>
            "ui error message" :: "An Error Occured while signin you in using PasswordLess: " + err.getLocalizedMessage
          case ENone =>
        }

      // NOrmal form
      case None =>

        securitySessionGetLoginUser(request) match {

          //-- A login is being done by another provider
          case Some(loginUser) if (!loginUser.hasProvider("passwordless")) =>

          case Some(loginUser) if (loginUser.hasProvider("passwordless")) =>

            "ui info message" :: s"An Identification Link has been sent to your email (s: ${request.get.getSession.get})"

          case other =>
            securitySessionGetUser match {

              //-- User in session, not permanent and with PL provider
              //case Some(user) if (!user.isPermanent && user.hasProvider("passwordless")) =>

              //-- A User is present in session, no need to show any Identification GUI
              case Some(user) =>

              case other =>

                "ui form" :: form {

                  label("Enter your Email:") {
                    input {
                      fieldName("passwordless-email")
                      semanticFieldEmail
                    }
                  }
                  
                  googleRecaptcha

                  semanticSubmitButton("Login/Register")

                  onSubmit {

                    // Check Recaptcha
                    GoogleRecaptchaModule.verify(request.get,googleRecaptchaResponse)
                    
                    val userEmail = request.get.getURLParameter("passwordless-email").get

                    //-- If a permanent user with same email is present:
                    //--  - If PWLess not enabled, error
                    //--  - Otherwise save token to user
                    val targetUser = securityGetUserByEmail(userEmail) match {
                      case Some(user) => user
                      case None =>
                        val u = User()
                        u.email = userEmail
                        u
                    }
                    if (targetUser.isPermanent && !targetUser.hasProvider("passwordless")) {
                      "ui error message" :: "Current User exists and has not enabled Password Less as an authentication method"
                    } else {

                      //-- Create Token
                      val token = PasswordLessModule.generateToken(userEmail)

                      //-- Save to user
                      securitySessionSaveLoginUser(request, targetUser)
                      val identity = targetUser.federate("passwordless", token)
                      if (targetUser.isPermanent) {
                        targetUser.save
                      }

                      //-- Send token
                      try {

                        EmailModule.sendEmail("leys.richard@gmail.com", userEmail, "Identification Token", s"""|
                               |
                               |To Authenticate, click here: ${createFullDomainURI(s"?providerID=passwordless&tokenID=${URLEncoder.encode(token.token.toString(), "UTF8")}")}
                               """.stripMargin)
                      } catch {
                        case e: Throwable => e.printStackTrace()
                      }
                    }

                  }

                  cl
                }

            }

        }

    }

  }

}
