package org.odfi.wsb.fwapp.module.semantic

trait SemanticMenuView extends SemanticView {

    /*
     *
     */
    def semanticVerticalRightPointingMenu(content: Map[String, Any]) = {

        def makeItem(value: (String, Any)): Unit = {

            value._2 match {
                case link: String =>

                    "item" :: div {
                        "header" :: a(link)(text(value._1))
                    }

                //"item" :: a(link)(text(value._1))

                case sub: Map[_, _] =>

                    "item" :: div {

                        // Header
                        "header" :: value._1

                        // sub menu
                        "menu" :: div {

                            // Items
                            sub.foreach {
                                case (k, v) =>
                                    makeItem(k.toString -> v)
                            }

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

    def semanticHorizontalBottomPointingMenu(content: Map[String, Any]) = {

        def makeItem(value: (String, Any)): Unit = {

            value._2 match {
                case link: String =>
                    "item" :: a(link)(text(value._1))
                case sub: Map[_, _] =>
                    "ui menu" :: div {
                        sub.foreach {
                            case v => makeItem((v.toString, v))
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