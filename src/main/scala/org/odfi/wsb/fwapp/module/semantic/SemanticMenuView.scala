package org.odfi.wsb.fwapp.module.semantic

trait SemanticMenuView extends SemanticView {
  
  
  // Simple Menus
  //----------------
  
  def semanticVerticalRightPointingMenu(content: Map[String,Any]) = {
    
    
    def makeItem(value: (String,Any)): Unit = {
      
      value._2 match {
        case link : String => 
          "item" :: a(link)(text(value._1))
        case sub : Map[_,_] =>
          "ui menu" :: div {
            sub.foreach {
              case v =>  makeItem((v.toString,v))
            }
           
          }
        case other => 
          "item" :: a(value._2.toString)(text(value._1))
      }
      
    }
    
    "ui fluid vertical menu" :: div {
      
      content.foreach {
        case v => makeItem(v) 
          
      }
      
    }
  }
  
  /**
   * Map format: 
   * 
   * PATH -> TITLE
   */
  def semanticHorizontalBottomPointingMenu(content: Map[String,Any]) = {
    
    
    def makeItem(value: (String,Any)): Unit = {
      
      value._2 match {
        case link : String => 
          "item" :: a(link)(text(value._1))
        case sub : Map[_,_] =>
          "ui menu" :: div {
            sub.foreach {
              case v =>  makeItem((v.toString,v))
            }
           
          }
        case other => 
          "item" :: a(value._2.toString)(text(value._1))
      }
      
    }
    
    "ui fluid pointing menu" :: div {
      
      content.foreach {
        case v => makeItem(v) 
          
      }
      
    }
  }
  
  
}