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

import org.odfi.wsb.fwapp.views.LibraryView
import java.net.URI

trait GoogleRecaptchaView extends LibraryView {

  this.addLibrary("google") {
    case (_, targetNode) =>

      importHTML(<script src="https://www.google.com/recaptcha/api.js" async="true" defer=""></script>)
      onNode(targetNode) {

        importHTML(<script src="https://www.google.com/recaptcha/api.js" async="true" defer=""></script>)
        script(new URI("https://www.google.com/recaptcha/api.js")) {

        }

      }
  }

  def googleRecaptcha = {

    GoogleRecaptchaModule.config.get.getString("clientSiteKey") match {
      case Some(siteKey) =>
        "g-recaptcha" :: div {
          data("sitekey" -> siteKey)
        }
      case other =>
        "fwapp-warning" :: "Cannot use Google Recaptcha, configure client and server keys"
    }

  }

  def googleRecaptchaResponse = {
    request.get.ensureURLParameters(List("g-recaptcha-response"))
    request.get.getURLParameter("g-recaptcha-response").get
  }

}
