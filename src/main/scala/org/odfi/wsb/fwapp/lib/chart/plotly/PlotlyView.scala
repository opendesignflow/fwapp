package org.odfi.wsb.fwapp.lib.chart.plotly

import org.odfi.wsb.fwapp.module.jquery.JQueryView
import org.odfi.wsb.fwapp.module.semantic.SemanticView
import org.w3c.dom.html.HTMLElement
import com.idyria.osi.vui.html.Div
import com.idyria.osi.tea.listeners.ListeningSupport

trait PlotlyView extends JQueryView {

  this.addLibrary("plotly") {
    case (Some(l), targetNode) =>
    case (None, targetNode) =>
      onNode(targetNode) {
        script(createAssetsResolverURI("/fwapp/external/plotly/plotly-latest.min.js")) {

        }
      }

  }

  class PlotlyDiv(val d: Div[HTMLElement, Div[HTMLElement, _]]) extends ListeningSupport{

    def onDataAvailable(cl: => Unit) = {
      this.on("data"){
        cl
      }
    }
    def triggerData = {
      this.@->("data")
    }
    
    def makeLineChart(points: Array[Double]) = {

      onNode(d) {
        var generator = jqueryGenerateOnLoad("plotly-linechart").get
        generator.print(s"""|
                         |var trace = { 
                         |    x: ${(0 until points.size).map(_.toString).mkString("[", ",", "]")}, 
                         |    y: ${points.map(_.toString).mkString("[", ",", "]")},
                         |    type: 'scatter'
                         |}
                         |
                         |Plotly.newPlot('${d.getId}', [trace]);
                         |
                         |
                         |
                         |""".stripMargin)
        generator.close()
        
        
      } 
      
      triggerData
    }
  }

  def plotlyPlot(tid: String)(cl: => Any) = {

    /*val d = new PlotlyDiv
    d.id = tid
    d*/
    new PlotlyDiv(div {
      id(tid)
      //+@("style"->"width:400px;height:400px")
      cl
    })
  }
  /*  script(s"""
$$(function() {
      
var trace1 = {
  x: [1, 2, 3, 4],
  y: [10, 15, 13, 17],
  type: 'scatter'
};

var trace2 = {
  x: [1, 2, 3, 4],
  y: [16, 5, 11, 9],
  type: 'scatter'
};

var data = [trace1, trace2];

Plotly.newPlot('$tid', data);
      
      });
      
      """)
  }*/

}

trait PlotlySemantic extends PlotlyView with SemanticView {

  /**
   *
   */
  def semanticPlotly(tid: String, message: String , widthPercent : Int = 100)(cl: => Any) = {

    val plot = plotlyPlot(tid) {
      data("ui-load" -> "fwapp.ui.heightToRatio( this, 4.0 / 3.0)")
      ++@("style" -> s"with:$widthPercent%;")
    }
    "ui segment" :: div {
      val dimmerDiv = s"ui active dimmer" :: div {
        classes("")
        //data("vui-size-ratio" -> 4.0 / 3.0)
        
        
        "ui large text" :: message
        plot.d.detach
        add(plot.d)
     
        cl

      }
      plot.onDataAvailable {
        dimmerDiv.removeClass("active").removeClass("dimmer")
      }
    }
    plot

  }

}