package org.odfi.wsb.fwapp.lib.markdown

import org.odfi.wsb.fwapp.views.FWappView
import org.markdown4j.Markdown4jProcessor
import com.idyria.osi.vui.html.jsoup.JSoupHTMLBuilder

trait MarkdownView extends FWappView with JSoupHTMLBuilder {
  
   var markdownProcessor = new  Markdown4jProcessor
  
  def markdown(str:String) = {
    
    jsoupHTML(markdownProcessor.process(str)) {
      
    }
    
  }
  
}