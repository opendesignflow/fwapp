package org.odfi.wsb.fwapp

import org.odfi.wsb.fwapp.framework.FWAppValueBindingView

object TryBindValue extends App   {
  
  val vb = new FWAppValueBindingView {
    
    this.bindValue {
      va : Boolean => 
        
    }
    
    this.bindValue[Integer] {
      vb  => 
        
    }
    
  }
  
  println("Done")
  
}