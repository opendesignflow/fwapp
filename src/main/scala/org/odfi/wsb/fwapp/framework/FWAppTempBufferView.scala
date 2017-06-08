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
package org.odfi.wsb.fwapp.framework

import scala.reflect.ClassTag
import org.w3c.dom.html.HTMLElement

trait FWAppTempBufferView extends FWAppValueBindingView {

  // Temp Buffer
  //------------------
  var tempBuffer = Map[String, Any]()

  def cleanTempBuffer = {
    this.tempBuffer = this.tempBuffer.empty
  }
  
  /**
   * Value will be set to temp buffer map
   */
  def inputToTempBuffer[VT <: Any](name: String, value: VT)(cl: => Any)(implicit tag: ClassTag[VT]) = {


    putToTempBuffer[VT](name, value)

   // println(s"Input to temp buffer: " + tag.runtimeClass)
    val rcl = {
      value: VT =>

       // println(s"Updating: " + value.getClass)
        putToTempBuffer[VT](name, value)

     
    }

    var node = input {
      +@("value" -> value.toString)

      bindValueWithName[VT](name, rcl)

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
    inputToTempBuffer[VT](name, actualValue)(cl)

  }

  /**
   * Assume Strig if class tag was not overriden
   */
  def getTempBufferValue[VT <: Any](name: String)(implicit tag: ClassTag[VT]): Option[VT] = this.tempBuffer.get(name) match {
    case None => None
    case Some(v) if (tag.runtimeClass == classOf[Int] && v.getClass == classOf[Integer]) => Some(v.asInstanceOf[Integer].toInt.asInstanceOf[VT])
    case Some(v) if (tag.runtimeClass.isInstance(v)) => Some(v.asInstanceOf[VT])
    case Some(v) =>
      throw new RuntimeException(s"Getting input buffer value for $name failed because requested type $tag does not match value's ${v.getClass()}")
  }

  /**
   * Assume Strig if class tag was not overriden
   */
  def getTempBufferValueDefault[VT <: Any](name: String, default: VT)(implicit tag: ClassTag[VT]): VT = this.tempBuffer.get(name) match {
    case None => default
    case Some(v) if (tag.runtimeClass == classOf[Int] && v.getClass == classOf[Integer]) => v.asInstanceOf[Integer].toInt.asInstanceOf[VT]
    case Some(v) if (tag.runtimeClass.isInstance(v)) => v.asInstanceOf[VT]
    case Some(v) =>
      throw new RuntimeException(s"Getting input buffer value for $name failed because requested type $tag does not match value's ${v.getClass()}")
  }

  def getTempBufferValueOrSet[VT <: Any](name: String, default: => VT)(implicit tag: ClassTag[VT]): VT = this.tempBuffer.get(name) match {
    case None =>
      putToTempBuffer(name, default)
      default
    case Some(v) if (tag.runtimeClass == classOf[Int] && v.getClass == classOf[Integer]) => v.asInstanceOf[Integer].toInt.asInstanceOf[VT]
    case Some(v) if (tag.runtimeClass.isInstance(v))                                     => v.asInstanceOf[VT]
    case Some(v) =>
      throw new RuntimeException(s"Getting input buffer value for $name failed because requested type $tag does not match value's ${v.getClass()}")
  }
  /**
   * Run closure if value is defined
   */
  def onTempBufferValue[VT <: Any: ClassTag](name: String)(cl: VT => Any) = getTempBufferValue[VT](name) match {
    case Some(v) => cl(v)
    case None    =>
  }

  /**
   * Save value, remove all values which start with name. to allow hierarchical usage of variables
   */
  def putToTempBuffer[VT](name: String, v: VT) = {

    // Clean 
    tempBuffer = tempBuffer.filterNot {
      case (k, value) if (name == k && k.startsWith(name + ".")) => true
      case other => false
    }

    // Set value
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
