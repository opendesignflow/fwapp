package org.odfi.wsb.fwapp.module.jquery

import java.net.URLEncoder

trait JQueryTreetable extends JQueryView {

  this.addLibrary("jquery-treetable") {
    case (Some(source), target) =>

    case (None, target) =>
      onNode(target) {
        stylesheet(createAssetsResolverURI("/fwapp/external/jquery-treetable/3.2.0/css/jquery.treetable.css")) {

        }
        script(createAssetsResolverURI("/fwapp/external/jquery-treetable/3.2.0/jquery.treetable.js")) {

        }
        script(createAssetsResolverURI("/fwapp/external/jquery-treetable/jquery-treetable.js")) {

        }
      }

  }
  
  
  def treeTableLineId(id:String) = {
    +@("data-tt-id" ->URLEncoder.encode(id,"UTF8"))
  }
  
  def treeTableParent(id:String) = {
     +@("data-tt-parent-id" -> URLEncoder.encode(id,"UTF8"))
  }

}