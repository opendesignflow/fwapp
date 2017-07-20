package org.odfi.wsb.fwapp.module.clientutils

import org.odfi.wsb.fwapp.views.FWappView
import scala.meta.Term.PartialFunction
import fmacros.FormClientUtilsMTrait
import org.odfi.wsb.fwapp.views.LibraryView

trait FormClientUtils extends LibraryView with FormClientUtilsMTrait {
  
  this.addLibrary("fwapp") {
    case (found,targetNode) => 
      onNode(targetNode) {
          
        script(createAssetsResolverURI("/fwapp/lib/client/client-utils.js")) {
          
        }
        
      }
  }
  
  def formInputMatchList(name:String)(cases: List[(String, ( () => Any) )] ) : Unit = {
    
    cases.foreach {
      case (name,closure) => 
        println(s"Client case: "+name)
    }
    
  }
  
  def clientFormForInput(inputName:String)(inputValue:String)(cl: => Any) = {
    
    s"""client-utils-switch-on-input @data-switch-on-input="$inputName" @data-switch-on-value="$inputValue"  """ :: div {
      
      cl
    }
    
  }
  
}