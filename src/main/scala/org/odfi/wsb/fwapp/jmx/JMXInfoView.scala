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