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
package org.odfi.wsb.fwapp.module.jquery

import java.net.URLEncoder

trait JQueryTreetable extends JQueryView {

  this.addLibrary("jquery-treetable") {
    case (Some(source), target) =>

    case (None, target) =>
      onNode(target) {
        stylesheet(createAssetsResolverURI("/fwapp/external/jquery-treetable/3.2.0/css/jquery.treetable.css")) {

        }
        script(createAssetsResolverURI("/fwapp/external/jquery-treetable/3.2.0/jquery.treetable.js")) {

        }
        script(createAssetsResolverURI("/fwapp/external/jquery-treetable/jquery-treetable.js")) {

        }
      }

  }
  
  
  def treeTableLineId(id:String) = {
    +@("data-tt-id" ->URLEncoder.encode(id,"UTF8"))
  }
  
  def treeTableParent(id:String) = {
     +@("data-tt-parent-id" -> URLEncoder.encode(id,"UTF8"))
  }

}
