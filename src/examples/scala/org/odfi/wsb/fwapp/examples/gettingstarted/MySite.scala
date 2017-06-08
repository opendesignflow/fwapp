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
package org.odfi.wsb.fwapp.examples.gettingstarted

import org.odfi.wsb.fwapp.DefaultSiteApp
import org.odfi.wsb.fwapp.views.InlineView

object MySite extends DefaultSiteApp("/mysite") {

  listen(8585)

  "/foo/bar" view new InlineView {

    // The code here is used to render the view.
    // Produce HTML here
    html {
      head {

      }

      body {
        h1("Hello World") {

        }
        
       
      }

    }

  }
  
  // Added 404 Handler
  add404Intermediary

  // Start here :-)
  start

}
