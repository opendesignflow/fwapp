package views

import org.odfi.wsb.fwapp.views.FWappView

class HelloWorldView extends FWappView {
  
  viewContent {

    // Well know HTML tree with head and body
    html {
      head {

      }

      body {
        h1("Hello") {

        }
        h2("World") {

        }
      }
    }
    // Top html node returned
  }
  // EOF View content
}