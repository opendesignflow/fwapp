package org.odfi.wsb.fwapp.views.ui

import org.odfi.wsb.fwapp.views.LibraryView
import org.odfi.wsb.fwapp.framework.FWAppFrameworkView

trait SemanticUIImplView extends FWAppFrameworkView {

  this.addLibrary("fwapp") {
    case (Some(source), target) =>
      onNode(target) {
        script(createAssetsResolverURI("/fwapp/impl/ui-semantic-impl.js")) {

        }
      }
    case (None, target) =>

  }

}