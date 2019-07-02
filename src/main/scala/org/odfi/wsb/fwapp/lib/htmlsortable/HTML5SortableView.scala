package org.odfi.wsb.fwapp.lib.htmlsortable

import org.odfi.wsb.fwapp.views.LibraryView

trait HTML5SortableView extends LibraryView {

    var html5SortableVersion = "0.9.10"

    this.addLibrary("html5sortable") {
        case (Some(source), target) =>

        case (None, target) =>
            onNode(target) {

                script(createAssetsResolverURI(s"/fwapp/external/html5sortable-$html5SortableVersion/html5sortable.min.js")) {

                }
                /*script(createAssetsResolverURI(s"/fwapp/external/html5sortable-$html5SortableVersion/html5sortable.es.js")) {

                }
                script(createAssetsResolverURI(s"/fwapp/external/html5sortable-$html5SortableVersion/html5sortable.cjs.js")) {

                }
                script(createAssetsResolverURI(s"/fwapp/external/html5sortable-$html5SortableVersion/html5sortable.amd.js")) {

                }*/
                script(createAssetsResolverURI(s"/fwapp/lib/html5sortable/fwapp-html5sortable.js")) {

                }

            }

    }

}
