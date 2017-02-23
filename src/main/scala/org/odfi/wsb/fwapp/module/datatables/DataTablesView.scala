package org.odfi.wsb.fwapp.module.datatables

import org.odfi.wsb.fwapp.views.LibraryView
import org.odfi.wsb.fwapp.assets.AssetsManager

trait DataTablesView extends LibraryView {

  this.addLibrary("datatables") {
    case (Some(source), target) =>
      onNode(target) {
        stylesheet(createAppURI(s"${source.basePath}/datatables.min.css")) {

        }

        script(createAppURI(s"${source.basePath}/datatables.min.js")) {

        }
        
        script(createAssetsResolverURI(s"/fwapp/external/datatables/datatables.js")) {

        }

      }
      
    case (None,target) =>
      
      onNode(target) {
        stylesheet(createAssetsResolverURI(s"/fwapp/external/datatables/1.10.13/media/css/dataTables.semanticui.min.css")) {

        }
        script(createAssetsResolverURI(s"/fwapp/external/datatables/1.10.13/media/js/jquery.dataTables.min.js")) {

        }
        script(createAssetsResolverURI(s"/fwapp/external/datatables/1.10.13/media/js/dataTables.semanticui.min.js")) {

        }
        
        script(createAssetsResolverURI(s"/fwapp/external/datatables/datatables.js")) {

        }

      }
       
  }
  
  

}