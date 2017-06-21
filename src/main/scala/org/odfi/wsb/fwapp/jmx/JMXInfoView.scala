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
package org.odfi.wsb.fwapp.jmx

import org.odfi.wsb.fwapp.module.semantic.SemanticView
import java.lang.management.ManagementFactory

trait JMXInfoView extends SemanticView {
  
  def jmxInfoTable = {
    
    
    "ui table" :: table {
    
      thead("Parameter", "Value")
      
      // Memory
      //---------------
      trtd("Memory")(colspan(2))
      val memoryBean = ManagementFactory.getMemoryMXBean
      trvalues("Heap: Inital",memoryBean.getHeapMemoryUsage.getInit)
      trvalues("Heap: Max",memoryBean.getHeapMemoryUsage.getMax)
      trvalues("Heap: Used",memoryBean.getHeapMemoryUsage.getUsed)
      trvalues("Non Heap: Inital",memoryBean.getNonHeapMemoryUsage.getInit)
      trvalues("Non Heap: Max",memoryBean.getNonHeapMemoryUsage.getMax)
      trvalues("Non Heap: Used",memoryBean.getNonHeapMemoryUsage.getUsed)
      tr {
        td("Run Garbage Collection") {
          
        }
        rtd {
          "ui button" :: buttonClickReload("Run") {
            memoryBean.gc()
          }
        }
      }
    }
    
  }
  
}
