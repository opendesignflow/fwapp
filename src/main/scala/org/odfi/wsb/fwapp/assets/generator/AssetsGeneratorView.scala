package org.odfi.wsb.fwapp.assets.generator

import org.odfi.wsb.fwapp.framework.FWAppFrameworkView
import org.odfi.wsb.fwapp.views.FWappView

trait AssetsGeneratorView extends FWappView {

  def getAssetsGenerator = {
    getAssetsResolver match {
      case Some(resolver) =>
        
        // Return or create Assets Generator
        resolver.findChildOfType[AssetsGenerator] match {
          case Some(generator) =>

           Some(generator)

          case None =>
            
            var generator = new AssetsGenerator
            resolver.addAssetsSource("/generator", generator)
            
            Some(generator)
        }
      case None =>
        None
    }
  }

}