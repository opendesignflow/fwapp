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
