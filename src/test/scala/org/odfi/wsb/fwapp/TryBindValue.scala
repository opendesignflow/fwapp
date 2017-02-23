package org.odfi.wsb.fwapp

import org.odfi.wsb.fwapp.framework.FWAppValueBindingView

object TryBindValue extends App   {
  
  val vb = new FWAppValueBindingView {
    
    this.bindValueAuto {
      va : Boolean => 
        
    }
    
    this.bindValueAuto[Integer] {
      vb  => 
        
    }
    
  }
  
  println("Done")
  
}