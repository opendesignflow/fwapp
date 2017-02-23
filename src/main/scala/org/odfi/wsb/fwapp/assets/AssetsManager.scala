package org.odfi.wsb.fwapp.assets

import org.odfi.indesign.core.module.IndesignModule
import org.odfi.indesign.core.config.ConfigSupport

object AssetsManager extends IndesignModule with ConfigSupport {

  // Sources
  var assetsSources = Map[String, AssetsSource]()

  def addAssetsSource[T <: AssetsSource](name:String,source:T) : T = {
    this.assetsSources = this.assetsSources + (name -> source)
    source
  }
  
  def onNoAssetSource(name: String)(cl: => Any) = {
    this.findAssetSource(name) match {
      case None => cl
      case _ => 
    }
  }
  
  def findAssetSource(name: String): Option[AssetsSource] = {

    assetsSources.get(name) match {
      case Some(source) => Some(source)
      case None =>
        this.config.get.getKey(name) match {

          case Some(key) if (key.keyType.toString() == "file") =>

            var source = new ResourcesAssetSource(key.values.head.toString())
            source.addFilesSource(key.values(1).toString())

            assetsSources = assetsSources + (name -> source)
            
            Some(source)

          case None => None
        }
    }

  }

}