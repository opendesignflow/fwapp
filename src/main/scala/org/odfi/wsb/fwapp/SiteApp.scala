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
package org.odfi.wsb.fwapp

import org.odfi.indesign.core.main.IndesignPlatorm
import org.odfi.indesign.core.config.ooxoo.OOXOOConfigModule
import java.io.File
import org.odfi.indesign.core.harvest.HarvesterRegion
import org.odfi.indesign.core.harvest.Harvest
import org.odfi.wsb.fwapp.swing.SwingPanelSite



class DefaultSite(path:String) extends SwingPanelSite(path) {
  
  
   //-- Setup default assets
  val assetsManager = this.useDefaultAssets
  

  //-- Setup default config
  //IndesignPlatorm use OOXOOConfigModule
  //OOXOOConfigModule.configFolder = new File(s"config/$path")

  /* override def delayedInit(body: => Unit) = {
    super.delayedInit(body)
    println("Now SiteApp delayed start")
    body

    IndesignPlatorm use this
    IndesignPlatorm.start
  }*/

  this.onLoad {
     println("Loadding ooxoo config")
    requireModule(OOXOOConfigModule)
  }
  
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

  def findInSiteBaseFolder(path: String) = {
    findSiteBaseFolder match {
      case None => None
      case Some(f) =>
        //println("Site is :" +f)
        Some(new File(f, path).getCanonicalFile)
    }
  }
}

class SiteApp(path: String) extends SwingPanelSite(path) with App {

  

}

class DefaultSiteApp(path: String) extends DefaultSite(path) with App {

  //-- Environment
  val environment = args.find(_ == "--dev") match {
    case Some(_) => "test"
    case other => "production"
  }
  
  
  def isTestEnvironment = environment=="test"
  
  //-- Setup default assets
 /* val assetsManager = this.useDefaultAssets

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

  def findInSiteBaseFolder(path: String) = {
    findSiteBaseFolder match {
      case None => None
      case Some(f) =>
        //println("Site is :" +f)
        Some(new File(f, path).getCanonicalFile)
    }
  }*/

}

trait HarvesterSite extends HarvesterRegion {

  Harvest.addHarvester(this)

}
