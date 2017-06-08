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
package org.odfi.wsb.fwapp.views

import org.w3c.dom.html.HTMLElement
import com.idyria.osi.vui.html.HTMLNode

trait InlineView extends FWappView with DelayedInit {

  def delayedInit(body: => Unit) = {
    //println("dummy text, printed before initialization of C")
    val runBody : () => Unit = {
      () => 
      body
    }
    this.viewContent {
      
      this.topNodes = List()
      runBody()
      this.topNodes.head.asInstanceOf[HTMLNode[HTMLElement,HTMLNode[HTMLElement,_]]]
      
    }

  }

}

object InlineView {
  
  def apply(cl: => HTMLNode[HTMLElement,HTMLNode[_,_]]) = {
    new InlineView {
      cl
    }
  }
  
}
