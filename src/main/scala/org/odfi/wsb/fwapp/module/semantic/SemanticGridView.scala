package org.odfi.wsb.fwapp.module.semantic

import com.idyria.osi.vui.html.HTMLNode

trait SemanticGridView extends SemanticView {
 
  
  def sgrid(cl: => Any) = {
    
    "ui grid" :: div {
      cl
      
      // Look for columns and set
      /*val columns = this.currentNode.children.filter {
        case elt : HTMLNode[_,_] if(elt.hasClass("column")) => true
        case other => false
      }
      */
      //
      
    }
  }
  
  def scolumn(cl: => Any) = {
    "column" :: div {
      cl
    }
  }
  
  def swidth(percent:Int) =  {
    
  }
  
}