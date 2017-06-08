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
package org.odfi.wsb.fwapp.lib.security.provider.kerberos

import org.odfi.indesign.core.harvest.HarvestedResource
import org.odfi.wsb.fwapp.lib.security.AuthenticationProvider
import org.odfi.wsb.fwapp.lib.security.APIConfiguration
import com.sun.security.auth.module.Krb5LoginModule
import javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag
import javax.security.auth.login.LoginContext
import javax.security.auth.kerberos.KerberosPrincipal
import javax.security.auth.Subject
import javax.security.auth.login.Configuration
import com.sun.security.auth.callback.TextCallbackHandler
import javax.security.auth.callback.PasswordCallback
import javax.security.auth.callback.NameCallback

class KerberosAuthProvider(val realm: String) extends AuthenticationProvider {

  def getId = s"${getClass.getCanonicalName}@$realm"

  def authenticate(login: String, pw: String) = {

    var config = new APIConfiguration()
    config.addApplicationConfiguration("KRB5", classOf[Krb5LoginModule], LoginModuleControlFlag.REQUIRED, Map[String, Any]("principal" -> "zm4632@KIT.EDU", "client" -> "true", "debug" -> "true", "useTicketCache" -> "true"))

    // Create Subject and Kerberos Principal
    //-----------------------
    var subject = new Subject

    var principal = new KerberosPrincipal(s"$login@$realm")
    //principal.getName
    subject.getPrincipals.add(principal)

    //var lm = new Krb5LoginModule
    //lm.initialize(subject, new TextCallbackHandler, arg2, arg3)

    // sys.props.put("java.security.krb5.conf", "krb5.conf")
    //sys.props.put("java.security.krb5.realm", "kit.edu")
    //sys.props.put("java.security.krb5.kdc", "kit.edu")

    /*println(s"Name: "+principal.getName)
  println(s"Realm: "+principal.getRealm)*/

    Configuration.getConfiguration.getParameters

    // Callback handler for Password
    //---------------------
    var handler = new javax.security.auth.callback.CallbackHandler {
      def handle(cbs: Array[javax.security.auth.callback.Callback]) = {
        cbs.foreach {
          case cb: NameCallback =>
            cb.setName("zm4632@KIT.EDU")
          case pb: PasswordCallback =>
            pb.setPassword(pw.toCharArray())
        }
      }
    }

    // Login Context and run login
    //------------
    var loginContext = new LoginContext("KRB5", subject, new TextCallbackHandler, config)
    loginContext.login()

  }

}
