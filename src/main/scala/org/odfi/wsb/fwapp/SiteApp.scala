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

class DefaultSiteApp(path: String) extends SiteApp(path) {

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
    assetsManager.addDefaultResourcesAssetSource(path)
  }

  // Utilities
  //----------

  /**
   * Lookup
   */
  def findSiteBaseFolder = {
    var cl = getClass.getClassLoader
    var searchPath = getClass.getCanonicalName.replace(".", "/") + ".class"
    
    //println("Searching for: " + searchPath)

    var classResource = cl.getResource(searchPath)
   // println("Found: " + classResource.getFile)

    classResource match {

      case path if (path != null && new File(path.getFile).exists()) =>
        
        //-- Remove search part from file, and we'll have the folder
        Some(new File(path.getFile.stripSuffix(searchPath).stripSuffix("target/classes/")).getCanonicalFile)
        
      case other => None
    }
 
  }
  
  def findInSiteBaseFolder(path:String) = {
    findSiteBaseFolder match {
      case None => None
      case Some(f) => 
        //println("Site is :" +f)
        Some(new File(f,path).getCanonicalFile)
    }
  }

}

trait HarvesterSite extends HarvesterRegion {

  Harvest.addHarvester(this)

}