/*-
 * #%L
 * FWAPP Framework
 * %%
 * Copyright (C) 2016 - 2017 Open Design Flow
 * %%
 * This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.odfi.wsb.fwapp.lib.chart.plotly

import org.odfi.wsb.fwapp.module.jquery.JQueryView
import org.odfi.wsb.fwapp.module.semantic.SemanticView
import org.w3c.dom.html.HTMLElement
import com.idyria.osi.vui.html.Div
import com.idyria.osi.tea.listeners.ListeningSupport
import com.idyria.osi.ooxoo.core.buffers.structural.ElementBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.xelement
import com.idyria.osi.ooxoo.core.buffers.datatypes.IntegerBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.XList
import com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer
import com.idyria.osi.ooxoo.lib.json.JSonUtilTrait
import com.idyria.osi.ooxoo.core.buffers.datatypes.DoubleBuffer
import org.odfi.wsb.fwapp.framework.websocket.WebsocketView

trait PlotlyView extends JQueryView with WebsocketView{

  this.addLibrary("plotly") {
    case (Some(l), targetNode) =>
    case (None, targetNode) =>
      onNode(targetNode) {
        script(createAssetsResolverURI("/fwapp/external/plotly/plotly-latest.min.js")) {

        }
        script(createAssetsResolverURI("/fwapp/lib/plotly/plotly-update.js")) {

        }
      }

  }

  // Updates
  //-----------------
  class PlotlyLineChart extends ElementBuffer with JSonUtilTrait {

    @xelement
    var TargetID: XSDStringBuffer = _

   @xelement
   var yPoints : XList[DoubleBuffer] = XList{new DoubleBuffer}

  }
  
  class PlotlyAddPoint extends ElementBuffer with JSonUtilTrait {

    @xelement
    var TargetID: XSDStringBuffer = _

   @xelement
   var point :DoubleBuffer = 0.0

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
    
    def sendPoint(p:Double) = {
      val ap = new PlotlyAddPoint
      ap.TargetID = d.getId
      ap.point = p
      broadCastSOAPBackendMessage(ap)
    }
    
    def sendLineChart(points:Array[Double]) = {
      
      var lineUpdate = new PlotlyLineChart
      lineUpdate.TargetID = d.getId
      lineUpdate.yPoints ++= points.map { p => DoubleBuffer(p)}.toList
      
      broadCastSOAPBackendMessage(lineUpdate)
    }
    
    def makeLineChart(points: Array[Int]) : Unit = {
      makeLineChart(points.map(_.toDouble))
    }
    def makeLineChart(points: Array[Double]) : Unit = {

      onNode(d) {
        var generator = jqueryGenerateOnLoad("plotly-linechart-"+d.getId).get
        //x: ${(0 until points.size).map(_.toString).mkString("[", ",", "]")}, 
        generator.print(s"""|
                         |var trace = { 
                         |    
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

    val targetId = currentNodeUniqueId(tid)
    /*val d = new PlotlyDiv
    d.id = tid
    d*/
    new PlotlyDiv(div {
      id(targetId)
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
    /**val segmentd = "ui segment" :: div {
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
    plot.d = segmentd*/
    plot

  }

}
