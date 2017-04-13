package org.odfi.wsb.fwapp.examples.views

import org.odfi.wsb.fwapp.DefaultSiteApp

object SimpleViewClassApp extends DefaultSiteApp("/mysite") {
  
  this.listen(8585)
  start
  
  // map the Hello World to "/"
  "/" view classOf[HelloWorldView]
}