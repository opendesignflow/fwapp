package org.odfi.wsb.fwapp.framework

import com.idyria.osi.ooxoo.core.buffers.datatypes.BooleanBuffer
import com.idyria.osi.wsb.webapp.localweb.LocalWebHTMLVIew
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

    println(s"Inside bind value with name $name: "+tag)
    
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
            +@("name" ->name)
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

  }



}

