package org.odfi.wsb.fwapp.module.ionrange

import org.odfi.wsb.fwapp.views.FWappView
import org.odfi.wsb.fwapp.views.LibraryView
import org.odfi.wsb.fwapp.module.jquery.JQueryView
import com.idyria.osi.ooxoo.core.buffers.datatypes.DoubleBuffer
import org.odfi.wsb.fwapp.framework.FWAppFrameworkView
import com.idyria.osi.vui.html.Input
import org.w3c.dom.html.HTMLElement
import com.idyria.osi.ooxoo.core.buffers.datatypes.IntegerBuffer

/**
 * @see http://ionden.com/a/plugins/ion.rangeSlider/en.html
 */
trait IONRangeSlider extends JQueryView with FWAppFrameworkView {

  this.addLibrary("jquery") {
    case (Some(source), target) =>

    case (None, target) =>
      onNode(target) {

        script(createAssetsResolverURI("/fwapp/external/ion-rangeslider/ion.rangeSlider-2.1.6/js/ion-rangeSlider/ion.rangeSlider.min.js")) {

        }
        stylesheet(createAssetsResolverURI("/fwapp/external/ion-rangeslider/ion.rangeSlider-2.1.6/css/ion.rangeSlider.css")) {

        }
        stylesheet(createAssetsResolverURI("/fwapp/external/ion-rangeslider/ion.rangeSlider-2.1.6/css/ion.rangeSlider.skinHTML5.css")) {

        }
        stylesheet(createAssetsResolverURI("/fwapp/external/ion-rangeslider/fwapp-ion-rangeslider.css")) {

        }

      }

  }

  // Create Range Slider
  //--------------

  def ionSlider(min: Int, max: Int, value: IntegerBuffer)(cl: => Any): Input[HTMLElement, _] = {

    var db = new DoubleBuffer
    db.set(value.data.toDouble)
    db.onDataUpdate {
      value.set(db.data.toInt)
    }
    ionSlider(min.toDouble, max.toDouble, db) {
      data("step" -> "1")
      cl
    }
  }

  def ionSlider(min: Double, max: Double, value: DoubleBuffer)(cl: => Any): Input[HTMLElement, _] = {

    var i = input {
      id("ion-range-slider-" + currentNode.hashCode())
      data("type" -> "single")
      data("min" -> min)
      data("max" -> max)
      data("from" -> value)
      data("step" -> "0.1")
      cl
    }

    var updateFromAction = this.getActionString {

      //println(s"Updating from: "+request.get.getURLParameter("from"))
      value.set(request.get.getURLParameter("from").get.toDouble)

    }

    script(s"""
      $$(function() {
        
        var slider = $$("#${i.getId}").ionRangeSlider();
        slider.on("change",function() {
          var from = $$(this).data("from");
          //console.log("Slider updated: "+from);
          ${createJSCallActionWithData(updateFromAction, data = List("from" -> "from"))};
        });
        
      });
      
      """)

    i

  }

  def ionRangeSlider(min: Int, max: Int, from: IntegerBuffer, to: IntegerBuffer)(cl: => Any): Input[HTMLElement, _] = {

    var fromDB = new DoubleBuffer
    fromDB.set(from.data.toDouble)
    fromDB.onDataUpdate {
      from.set(fromDB.data.toInt)
    }
    var toDb = new DoubleBuffer
    toDb.set(to.data.toDouble)
    toDb.onDataUpdate {
      to.set(toDb.data.toInt)
    }
    ionRangeSlider(min.toDouble, max.toDouble, fromDB, toDb) {
      data("step" -> "1")
      cl
    }
  }

  def ionRangeSlider(min: Double, max: Double, from: DoubleBuffer, to: DoubleBuffer)(cl: => Any): Input[HTMLElement, _] = {

    var i = input {
      id("ion-range-slider-" + currentNode.hashCode())
      data("type" -> "double")
      data("min" -> min)
      data("max" -> max)
      data("from" -> from)
      data("to" -> to)
      data("step" -> "0.1")
      +@("style" -> "display:inline-block")
      cl
    }

    var updateFromAction = this.getActionString {
      println(s"Updating from: " + request.get.getURLParameter("from"))

      from.set(request.get.getURLParameter("from").get.toDouble)
      to.set(request.get.getURLParameter("to").get.toDouble)

    }

    script(s"""
      $$(function() {
        
        var slider = $$("#${i.getId}").ionRangeSlider();
        slider.on("change",function() {
          var from = $$(this).data("from");
          var to = $$(this).data("to");
          console.log("Slider updated: "+from);
          ${createJSCallActionWithData(updateFromAction, data = List("from" -> "from", "to" -> "to"))};
        });
        
      });
      
      """)

    i
  }

}