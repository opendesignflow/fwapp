package org.odfi.wsb.fwapp.views

import org.w3c.dom.html.HTMLElement
import com.idyria.osi.vui.html.HTMLNode

trait InlineView extends FWappView with DelayedInit {

  def delayedInit(body: => Unit) = {
    println("dummy text, printed before initialization of C")
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