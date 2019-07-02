package org.odfi.wsb.fwapp.module.jquery

import org.odfi.wsb.fwapp.assets.generator.AssetsGeneratorView
import org.odfi.wsb.fwapp.views.LibraryView

trait JqueryUIView extends JQueryView {

    var jqueryUiVersion =  "1.12.1.custom"

    this.addLibrary("jquery-ui") {
        case (Some(source), target) =>

        case (None, target) =>
            onNode(target) {

                script(createAssetsResolverURI(s"/fwapp/external/jquery-ui-$jqueryUiVersion/jquery-ui.js")) {

                }
                stylesheet(createAssetsResolverURI(s"/fwapp/external/jquery-ui-$jqueryUiVersion/jquery-ui.min.css")) {

                }
                stylesheet(createAssetsResolverURI(s"/fwapp/external/jquery-ui-$jqueryUiVersion/jquery-ui.structure.min.css")) {

                }
                stylesheet(createAssetsResolverURI(s"/fwapp/external/jquery-ui-$jqueryUiVersion/jquery-ui.theme.min.css")) {

                }
                stylesheet(createAssetsResolverURI(s"/fwapp/lib/jquery-ui/fwapp-jquery-ui.css")) {

                }

            }

    }


    def jqueryUISortableList(lid:String,els:List[String]) = {
        ul {
            id(lid+"-ui-sortable")
            classes("ui-sortable-list")



            jqueryGenerateOnLoad(s"jquery-ui-sortable-$lid") match {
                case Some(g) =>
                    g.println(
                        s"""
                          |console.log("Jquery UI Sortqble List");
                          |$$("#$lid-ui-sortable").sortable({ scroll: false, scrollSensitivity: 100 }).disableSelection();
                          |//$$("#$lid-ui-sortable").sortable().disableSelection();
                        """.stripMargin)

                    g.close();
                case None =>
            }
            els.foreach {
                elt =>

                    "ui-state-default" :: li {
                        "ui-icon ui-icon-arrowthick-2-n-s" :: span {

                        }
                        text(elt)
                    }

            }
        }
    }
}
