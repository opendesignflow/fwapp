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

import com.idyria.osi.ooxoo.core.buffers.datatypes.BooleanBuffer
import com.idyria.osi.ooxoo.core.buffers.datatypes.IntegerBuffer
import com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer
import com.idyria.osi.vui.html.Textarea
import scala.reflect.ClassTag
import com.idyria.osi.ooxoo.core.buffers.structural.AbstractDataBuffer

//import scala.reflect.macros.blackbox
//import scala.language.experimental.macros

import fmacros.FWAppMacros
import fmacros.FWAppValueBindingViewTrait

trait FWAppValueBindingView extends FWAppFrameworkView with FWAppValueBindingViewTrait {

  def createJSBindAction(code: String, render: String = "none") = {
    s"fwapp.actions.bindValue(this,'${getViewPath}?_action=${code}',{_render:'none'})"
  }

  import reflect.runtime.universe._

  //def bindValue[V](expr: V => Any): Unit  = macro FWAppMacros.bindValueImpl[V]

  def bindValueWithName[V](name: String, cl: V => Any)(implicit tag: ClassTag[V]): Unit = {

    //println(s"Inside bind value with name $name: "+tag)

    var eventName = currentNode match {
      case t: Textarea[_, _] => "onchange"
      case other => "onchange"
    }

    val bindSupportedTypes = List(classOf[Boolean], classOf[Long], classOf[Int], classOf[Integer], classOf[Double], classOf[Number], classOf[String], classOf[AbstractDataBuffer[_]])

    bindSupportedTypes.find { parentClass => parentClass.isAssignableFrom(tag.runtimeClass) } match {

      //-- Number
      //-------------------
      case Some(baseClass) if (baseClass == classOf[Number] || baseClass == classOf[Int] || baseClass == classOf[Integer] || baseClass == classOf[Long] || baseClass == classOf[Double]) =>

        // Ensure input type is number
        +@("type" -> "number")

        // Enable float if necessary
        if (baseClass == classOf[Double]) {
          +@("step" -> "0.1")
        }

        // Set name on element
        val targetNode = currentNode
        currentNode.attributes.get("name") match {
          case Some(name) => name
          case None =>
            +@("name" -> name)
        }

        // Register Action
        var action = this.getActionString {

          // Check URL parameters

          request.get.getURLParameter(targetNode.attributes("name").toString) match {

            case Some(v) =>

              // Check type
              //---------------------
              tag.runtimeClass match {

                // Numbers
                //--------------------
                case c if (classOf[Long].isAssignableFrom(c)) =>

                  //cl(java.lang.Long.parseLong(v).toLong.asInstanceOf[V])
                  cl(v.toLong.asInstanceOf[V])

                case c if (classOf[Int].isAssignableFrom(c)) =>

                  cl(v.toInt.asInstanceOf[V])

                case c if (classOf[Integer].isAssignableFrom(c)) =>

                  cl(Integer.parseInt(v).asInstanceOf[V])

                case c if (classOf[Double].isAssignableFrom(c)) =>

                  cl(v.toDouble.asInstanceOf[V])

                case c =>

                  sys.error(s"Data type " + c + " is not supported")
              }
            case None =>
              sys.error("Cannot run bind value action if value parameter is not set")
          }

        }

        // bind to on change
        +@(eventName -> createJSBindAction(action))

      // String
      //------------------
      case Some(baseClass) if (baseClass == classOf[String]) =>

        // Set name on element
        val targetNode = currentNode
        currentNode.attributes.get("name") match {
          case Some(name) => name
          case None =>
            +@("name" -> name)
        }

        // Register Action
        var action = this.getActionString {

          //println("Processing String: "+targetNode.attributes("name")+" -> "+  request.get.getURLParameter(targetNode.attributes("name").toString))

          // Check URL parameters
          request.get.getURLParameter(targetNode.attributes("name").toString) match {

            case Some(v) =>

              cl(v.asInstanceOf[V])
            case None =>
              sys.error("Cannot run bind value action if value parameter is not set")
          }

        }

        // bind to on change
        +@(eventName -> createJSBindAction(action))

      // Boolean
      //------------------
      case Some(baseClass) if (baseClass == classOf[Boolean]) =>

        // Make sure it is a checkbox
        +@("type" -> "checkbox")

        // Set name on element
        val targetNode = currentNode
        currentNode.attributes.get("name") match {
          case Some(name) => name
          case None =>
            +@("name" -> name)
        }

        // Register Action
        var action = this.getActionString {

          // Check URL parameters
          request.get.getURLParameter(targetNode.attributes("name").toString) match {

            case Some("on") =>
              cl(true.asInstanceOf[V])
            case Some("off") =>
              cl(false.asInstanceOf[V])
            case Some(v) =>

              cl(v.toBoolean.asInstanceOf[V])
            case None =>
              sys.error("Cannot run bind value action if value parameter is not set")
          }

        }

        // bind to on change
        +@(eventName -> createJSBindAction(action))

      //-- No Match error
      case None =>
        sys.error("Bind value on supports input types: " + bindSupportedTypes)
    }
    
    cl

  }

  // Utils
  //----------
  def inputBind[V: ClassTag](cl: V => Any) = {
    input {
      bindValue {
        value: V => cl(value)
      }
    }
  }

  /**
   * List: (id -> OBJ)
   */
  def selectFromObjects[T](lst: Iterable[(String,T)], current: String)(cl: T => Unit) = {

    var foundSelected = false
    select {

      var selectedFound = false
      lst.foreach {
        case (value,obj) =>

          option(value) {
            
            //-- Preselect
            if (current!=null && (value == current)) {
              +@("selected" -> "true")
              selectedFound = true
            }
            
            //-- Content value
            text(obj.toString)
          }

      }
      
      //-- Add empty option
      if(!selectedFound) {
        option("") {
          +@("selected" -> "true")
          currentNode.moveToFirstChild
        }
      }
      
      bindValue {
        v : String => 
          cl(lst.find { case (rv,obj) => rv==v}.get._2)
      }
    }

  }

}

