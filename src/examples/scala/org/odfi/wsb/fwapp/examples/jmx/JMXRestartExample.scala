package org.odfi.wsb.fwapp.examples.jmx

import org.odfi.wsb.fwapp.DefaultSiteApp
import org.odfi.wsb.fwapp.jmx.FWAPPJMX
import org.odfi.wsb.fwapp.swing.SwingPanelSite
import org.odfi.wsb.fwapp.SiteApp
import org.odfi.wsb.fwapp.views.InlineView
import org.odfi.wsb.fwapp.jmx.JMXInfoView
import org.odfi.wsb.fwapp.DefaultSite

object JMXRestartExample extends App {
  
  
  val jmxSite = new DefaultSite("/jmxdemo") with FWAPPJMX
  jmxSite.view(new InlineView with JMXInfoView {
    html {
      head {
        placeLibraries
      }
      body {
        "ui page container" :: div {
          jmxInfoTable
        }
      }
    }
  })
  jmxSite.listenWithJMXClose(8999)
  jmxSite.start
  
}