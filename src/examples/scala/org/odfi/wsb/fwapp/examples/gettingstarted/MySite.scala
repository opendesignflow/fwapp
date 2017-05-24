package org.odfi.wsb.fwapp.examples.gettingstarted

import org.odfi.wsb.fwapp.DefaultSiteApp
import org.odfi.wsb.fwapp.views.InlineView

object MySite extends DefaultSiteApp("/mysite") {

  listen(8585)

  "/foo/bar" view new InlineView {

    // The code here is used to render the view.
    // Produce HTML here
    html {
      head {

      }

      body {
        h1("Hello World") {

        }
        
       
      }

    }

  }
  
  // Added 404 Handler
  add404Intermediary

  // Start here :-)
  start

}