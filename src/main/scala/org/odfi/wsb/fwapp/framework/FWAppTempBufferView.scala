package org.odfi.wsb.fwapp.framework

import scala.reflect.ClassTag
import org.w3c.dom.html.HTMLElement

trait FWAppTempBufferView extends FWAppValueBindingView {
  
  // Temp Buffer
  //------------------
  var tempBuffer = Map[String, Any]()
  
   /**
   * Value will be set to temp buffer map
   */
  def inputToTempBuffer[VT <: Any](name: String, value: String)(cl: => Any)(implicit tag: ClassTag[VT]) = {

    putToTempBuffer(name, value)
 
    var node = input {
      +@("value" -> value.toString)    
      bindValue {
        v: VT =>
          v.toString match {
            case "" => tempBuffer = tempBuffer - name
            case _ => tempBuffer = tempBuffer.updated(name, v)
          }

      }
    }
    switchToNode(node, cl)
    node
  }
  def inputToTempBufferWithDefault[VT <: Any](name: String, default: VT)(cl: => Any)(implicit tag: ClassTag[VT]) = {

    //-- Set Default Value
    var actualValue = getTempBufferValue[VT](name) match {
      case None =>
        default
      case Some(v) =>
        v
    }

    //-- Create UI
    inputToTempBuffer[VT](name, actualValue.toString)(cl)

  }

  /**
   * Assume Strig if class tag was not overriden
   */
  def getTempBufferValue[VT <: Any](name: String)(implicit tag: ClassTag[VT]): Option[VT] = this.tempBuffer.get(name) match {
    case None => None
    case Some(v) if (tag.runtimeClass.isInstance(v)) => Some(v.asInstanceOf[VT])
    case Some(v) =>
      throw new RuntimeException(s"Getting input buffer value for $name failed because requested type $tag does not match value's ${v.getClass()}")
  }

  def putToTempBuffer(name: String, v: Any) = {
    tempBuffer = tempBuffer.updated(name, v)
  }

  def deleteFromTempBuffer(name: String) = {
    tempBuffer = tempBuffer - name
  }

  def tempBufferSelect(name: String, values: (String, String)*): com.idyria.osi.vui.html.Select[HTMLElement, com.idyria.osi.vui.html.Select[HTMLElement, _]] = tempBufferSelect(name, values.toList)
  def tempBufferSelect(name: String, values: List[(String, String)]): com.idyria.osi.vui.html.Select[HTMLElement, com.idyria.osi.vui.html.Select[HTMLElement, _]] = {

    //-- Set Default to First
    var selectedValue = this.getTempBufferValue[String](name) match {
      case None if (values.size > 0) =>
        this.putToTempBuffer(name, values.head._1)
        values.head._1
      // Reset default if necessary
      case Some(v) if (values.find { case (value, text) => value == v }.isEmpty) =>
        this.putToTempBuffer(name, values.head._1)
        values.head._1

      case Some(v) =>

        v
    }

    //-- Create GUI
    select {
      +@("name" -> name)
      values.foreach {
        case (value, text) =>
          option(value) {
            if (selectedValue == value) {
              +@("selected" -> "true")
            }
            textContent(text)
          }
      }

      bindValue {
        str: String =>
          putToTempBuffer(name, str)
      }
    }
  }

  def tempBufferRadio[VT <: Any: ClassTag](text: String)(valueNameAndObject: Tuple2[String, VT])(cl: => Any) = {

    // Get actual value
    var actualValue = getTempBufferValue[VT](valueNameAndObject._1)

    // Create Radio
    //-----------------
    var r = input {
      +@("type" -> "radio")
      +@("name" -> valueNameAndObject._1)

      // If actual Value is this one, preselect
      actualValue match {
        case Some(actual) if (actual.toString == valueNameAndObject._2.toString) =>
          +@("checked" -> "true")
        case _ =>
      }

      // Bind value to select current
      bindValue {
        str: String =>
          //str match {
          // case v if (v==valueNameAndObject._2.toString) => 
          putToTempBuffer(valueNameAndObject._1, valueNameAndObject._2)
        //case _ => 
        //}
      }

      textContent(text)

      // Extra stuff
      cl
    }

  }
}