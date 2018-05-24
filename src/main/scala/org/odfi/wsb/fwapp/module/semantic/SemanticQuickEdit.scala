package org.odfi.wsb.fwapp.module.semantic

import org.odfi.wsb.fwapp.framework.FWAppTempBufferView
import com.idyria.osi.ooxoo.core.buffers.structural.ElementBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.XList
import com.idyria.osi.ooxoo.core.buffers.id.ElementWithID

trait SemanticQuickEdit extends SemanticView {

  def quickEditIconTd(obj: Any, level: Int = 1) = {

    rtd {
      "ui icon edit" :: iconClickReload {
        putToTempBuffer("quickedit." + (0 until level).map("entity").mkString("."), obj)
      }
    }
  }
  
  def quickEditSetEntity(obj: Any, level: Int = 1) = {
    putToTempBuffer("quickedit." + (0 until level).map("entity").mkString("."), obj)
  }

  def quickEditEntity(level: Int = 1)(pf: PartialFunction[Option[Any], Unit]) = {
    val tp = getTempBufferValue[Any]("quickedit." + (0 until level).map("entity").mkString("."))
    pf.isDefinedAt(tp) match {
      case true =>
        pf(tp)
      case false =>
    }

  }


  def semanticQuickEditTable[T](entities: List[T], emptyMessage: String = "No Data Available", level: Int = 1)(headers: String*)(cl: T => Any): Any = {
    "ui definition table" :: table {
      thead(List("") ::: headers.toList)

      tbodyTrLoopWithDefaultLine(emptyMessage)(entities) {
        o =>
          quickEditIconTd(o, level)

          cl(o)
      }

    }
  }

  def semanticQuickEditAddEntityByName(label: String, actionLabel: String)(cl: String => Any) = {
    form {
      semanticActionFluidInput(label)(actionLabel) {
        fieldName("name")
      }
      onSubmit {
        println("Quick Add: " + withRequestParameter("name"))
        cl(withRequiredRequestParameter("name"))

      }
    }

  }
  
  def semanticQuickEditAddEntityByEmail(label: String, actionLabel: String)(cl: String => Any) = {
    form {
      semanticActionFluidInput(label)(actionLabel) {
        fieldName("email")
        semanticFieldEmail
      }
      onSubmit {
        println("Quick Add: " + withRequestParameter("email"))
        cl(withRequiredRequestParameter("email"))

      }
    }

  }
  
  def semanticQuickEditAddEidEntityByName[T<:ElementWithID](label:String,actionLabel:String, newCl: => T)(cl: (String,T) => Any) = {
    semanticQuickEditAddEntityByName(label,actionLabel) {
      name => 
        val e = newCl
        e.eid = e.stringToStdEId(name)
        cl(name,e)
        
    }
  }

}