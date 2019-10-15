package org.odfi.wsb.fwapp.examples.vue

import org.odfi.wsb.fwapp.DefaultSiteApp
import org.odfi.indesign.core.module.jfx.JFXRun
import javafx.stage.Stage
import javafx.scene.layout.BorderPane
import javafx.scene.control.ScrollPane
import javafx.scene.web.WebView
import javafx.scene.Scene

object VueJFXApp extends DefaultSiteApp("/vue") {
  
  
  
  // JFX
  //---------------
  JFXRun.onJavaFX {
    
    var stage  =new Stage
    stage.setWidth(1024)
    stage.setHeight(768)
    
    var container = new BorderPane
    var scrollpane = new ScrollPane
    container.setCenter(scrollpane)
    
    var webview =new WebView
    scrollpane.setContent(webview)
    
    webview.getEngine.load("https://www.vuejs.org")
    
    
    stage.setScene(new Scene(container))
    stage.centerOnScreen()
    stage.show()
    
  }
  
  listen(8333)
  start
}