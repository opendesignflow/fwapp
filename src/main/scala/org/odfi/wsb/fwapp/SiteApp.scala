package org.odfi.wsb.fwapp

import org.odfi.indesign.core.main.IndesignPlatorm
import org.odfi.indesign.core.config.ooxoo.OOXOOConfigModule
import java.io.File
import org.odfi.indesign.core.harvest.HarvesterRegion
import org.odfi.indesign.core.harvest.Harvest
import org.odfi.wsb.fwapp.swing.SwingPanelSite

trait DelayedIndesignStart extends DelayedInit {

 
}
class SiteApp(path: String) extends SwingPanelSite(path) with App {

  /*override def delayedInit(body: => Unit) = {

    println("Now SiteApp delayed start")
    body

    IndesignPlatorm use this
    IndesignPlatorm.start
  }*/
  
  def start = {
    IndesignPlatorm use this
    IndesignPlatorm.start
  }

}

class DefaultSiteApp(path: String) extends SiteApp(path)  {

  //-- Setup default assets
  val assetsManager = this.useDefaultAssets


  //-- Setup default config
  IndesignPlatorm use OOXOOConfigModule
  OOXOOConfigModule.configFolder = new File(s"config/$path")
  
 /* override def delayedInit(body: => Unit) = {
    super.delayedInit(body)
    println("Now SiteApp delayed start")
    body

    IndesignPlatorm use this
    IndesignPlatorm.start
  }*/
  
  this.onStart {
    assetsManager.addDefaultResourcesAssetSource("/nadc")
  }

}

trait HarvesterSite extends HarvesterRegion {

  Harvest.addHarvester(this)

}