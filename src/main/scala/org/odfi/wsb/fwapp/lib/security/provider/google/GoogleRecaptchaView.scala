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