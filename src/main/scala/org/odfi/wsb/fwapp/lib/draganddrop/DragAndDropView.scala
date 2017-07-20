package org.odfi.wsb.fwapp.lib.draganddrop

import org.odfi.wsb.fwapp.views.LibraryView
import org.odfi.wsb.fwapp.framework.FWAppFrameworkView
import java.io.File

trait DragAndDropView extends LibraryView with FWAppFrameworkView {

  /**
   * Run Closure
   */
  def onDrop(cl: => Any) = {
    //+@("ondrop" -> "handleDrop();")

    //-- Register actions
    var jscall = createJSCallAction(this.getActionString(cl, "drop"))

    script(s"""
 
      this.addEventListener("drop",function(e) {
        
        e.preventDefault();
        e.stopPropagation(); // stops the browser from redirecting
      
        //console.info("Dropped");
        ${jscall};
      },false);
      

      this.addEventListener('dragover', function(e) {
        e.stopPropagation();
        e.preventDefault();
        e.dataTransfer.dropEffect = 'copy';}, false);
      this.addEventListener('dragleave', function(e) {}, false);
      this.addEventListener('dragend', function(e) {}, false);
      """)
  }

  /*def onDropFile(cl: File => Any) = {

    // var files = e.dataTransfer.files;
    //-- Register actions
    var jscall = createJSCallActionWithData(this.getActionString(cl, "drop"), List("file" -> "fileName"))

    script(s"""
 
      this.addEventListener("drop",function(e) {
        
        e.preventDefault();
        e.stopPropagation(); // stops the browser from redirecting
      
        //console.info("Dropped");
        var files = e.dataTransfer.files;
        for (var i = 0, f; f = files[i]; i++) {
          // Read the File objects in this FileList.
          var fileName = f.name;
          console.info("Dropped file: "+ URL.createObjectURL(f).toString());
          console.info("Dropped file: "+ URL.createObjectURL(f).href);
          ${jscall};
        }
        
      },false);
      

      this.addEventListener('dragover', function(e) {
        e.stopPropagation();
        e.preventDefault();
        e.dataTransfer.dropEffect = 'link';}, false);
      this.addEventListener('dragleave', function(e) {}, false);
      this.addEventListener('dragend', function(e) {}, false);
      """)
  }*/

}