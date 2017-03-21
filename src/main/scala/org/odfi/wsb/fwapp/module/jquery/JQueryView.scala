package org.odfi.wsb.fwapp.module.jquery

import org.odfi.wsb.fwapp.views.LibraryView

trait JQueryView extends LibraryView {

  this.addLibrary("jquery") {
    case (Some(source), target) =>

    case (None, target) =>
      onNode(target) {
        
        
        script(createAssetsResolverURI("/fwapp/external/jquery/jquery-3.1.1.min.js")) {

        }
        
        /*script(createAssetsResolverURI("/fwapp/external/require/2.3.3/require.js")) {

        }*/
      }

    /*case Some((source, targetNode)) =>
      /*script(createAssetsResolverURI(source.basePath + "/jquery.js")) {

      }*/
    case None =>*/

  }

}