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
package org.odfi.wsb.fwapp.views.ui

import org.odfi.wsb.fwapp.views.LibraryView
import org.odfi.wsb.fwapp.framework.FWAppFrameworkView

trait SemanticUIImplView extends FWAppFrameworkView {

  this.addLibrary("fwapp") {
    case (Some(source), target) =>
      onNode(target) {
        script(createAssetsResolverURI("/fwapp/impl/ui-semantic-impl.js")) {

        }
        stylesheet(createAssetsResolverURI("/fwapp/impl/ui-semantic-impl.css")) {

        }
      }
    case (None, target) =>

  }

}
