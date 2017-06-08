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


import org.odfi.wsb.fwapp.lib.chart.plotly.PlotlyView


object Sandbox extends App {
  
  val lc = new PlotlyView {
    
    val c = new PlotlyLineChart
    c.TargetID = "abc"
    
    c.yPoints ++= List(0,1,2,3)
    
    println(c.toJSONString)
  }
  
  
  
}