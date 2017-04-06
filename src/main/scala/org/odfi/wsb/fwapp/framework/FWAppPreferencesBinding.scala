package org.odfi.wsb.fwapp.framework

import java.util.prefs.Preferences
import scala.reflect.ClassTag

trait FWAppPreferencesBinding extends FWAppValueBindingView {

  def inputToPreference[V](p: Preferences, key: String, default: V)(cl: V => Unit)(implicit tag: ClassTag[V]) = {

    input {

      //-- Set Default
      default.isInstanceOf[Boolean] match {
        case true =>
          p.getBoolean(key, default.asInstanceOf[Boolean]) match {
            case true =>
              +@("checked" -> "true")
            case false =>
          }
        case other =>
          +@("value" -> p.get(key, default.toString()))
      }

      //-- Bind
      bindValueWithName[V](key, {

        value: V =>

          value match {
            case value: Boolean =>
              p.putBoolean(key, value)
            case value: Int =>
              p.putInt(key, value)
            case value: Long =>
              p.putLong(key, value)
            case value: Double =>
              p.putDouble(key, value)
            case value =>
              println("PREF STRING Update: " + value)
              p.put(key, value.toString())
          }

          cl(value)
      })
    }

  }

  /**
   * Bind Object to Preference, using String representation
   * First argument contains Objects and Displayable String tuples
   */
  def selectObjectToPreference[V](objects: Iterable[(V, String)], p: Preferences, key: String, default: V)(cl: V => Unit)(implicit tag: ClassTag[V]) = {

    //-- Current Value
    var prefValue = p.get(key, default.toString())
    var currentObjectValue = objects.find {
      case (obj, dname) => obj.toString == prefValue
    } match {
      case Some((obj, dname)) => obj
      case None => default
    }

    select {

      // set values
      objects.foreach {
        case (obj, displayName) =>
          option(obj.toString()) {
            if (obj.toString() == prefValue.toString()) {
              isSelected
            }
            text(displayName)
          }
      }

      // ON change, call closure
      bindValue {

        value: String =>

          //-- Call Closure
          objects.find {
            case (obj, dname) => obj.toString == value
          } match {
            case Some((obj, dname)) =>
              cl(obj)
            case None =>
              throw new RuntimeException("Cannot update Object select, provided value is not in selection")
          }

          //-- Update
          p.put(key, value)

      } 

    }

    //-- Run once with actual found value
    cl(currentObjectValue)
  }

}