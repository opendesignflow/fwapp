package org.odfi.wsb.fwapp.framework

import java.util.prefs.Preferences
import scala.reflect.ClassTag

trait FWAppPreferencesBinding extends FWAppValueBindingView {

  /**
   * CL is always called with default value
   */
  def inputToPreference[V](p: Preferences, key: String, default: V)(cl: V => Unit)(implicit tag: ClassTag[V]) = {

    val i = input {

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
      val bindClosure = bindValueWithName[V](key, {

        value: V =>

          //println(s"Binding pref to: "+value)
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
              //println("PREF STRING Update: " + value)
              p.put(key, value.toString())
          }

          cl(value)
      })

      //--- Call CL
      default match {
        case value: Boolean =>
          cl(p.getBoolean(key, value).asInstanceOf[V])
        case value: Int =>
          cl(p.getInt(key, value).asInstanceOf[V])
        case value: Long =>
          cl(p.getLong(key, value).asInstanceOf[V])
        case value: Double =>
          cl(p.getDouble(key, value).asInstanceOf[V])
        case value =>
          cl(p.get(key, value.toString).asInstanceOf[V])
      }

    }

    i
  }

  /**
   *
   */
  def inputToPreferenceRadio(groupName: String, p: Preferences, key: String, radioValue: String)(cl: Boolean => Unit) = {

    // Checked if pref value is the one for this radio
    //--------------------
    val checkedAttr = if(p.get(key, radioValue) == radioValue) {
      "@checked=true"
    } else {
      ""
    }

    
    // Create Radio
    //-----------
    s"@type=radio @name=$groupName @value=$radioValue $checkedAttr" :: inputToPreference(p, key, radioValue) {
      v =>
        //p.put(key, value)
        //println(s"Pref value is now : "+p.get(key, radioValue)) 
        cl(true)
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
      case None               => default
    }

    val s = select {

      // set values
      objects.foreach {
        case (obj, displayName) =>
          option(obj.toString()) {
            if (obj.toString() == prefValue.toString() || displayName == prefValue.toString()) {
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

    // Return select HTML
    s
  }

}