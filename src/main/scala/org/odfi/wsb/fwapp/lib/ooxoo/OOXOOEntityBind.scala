package org.odfi.wsb.fwapp.lib.ooxoo

import org.odfi.wsb.fwapp.framework.FWAppFrameworkView
import com.idyria.osi.ooxoo.core.buffers.structural.ElementBuffer
import scala.reflect.ClassTag
import com.idyria.osi.ooxoo.core.buffers.structural.XList
import com.idyria.osi.ooxoo.core.buffers.structural.AbstractDataBuffer

trait OOXOOEntityBindView extends FWAppFrameworkView {

  var currentEntity: Option[EntityBindBuffer] = None

  def setCurrentEntity(entity: EntityBindBuffer) = {
    currentEntity = Some(entity)
  }

  def withCurrentEntity(e: EntityBindBuffer)(cl: => Any) = {
    setCurrentEntity(e)
    try {
      cl
    } finally {
      currentEntity = None
    }
  }

  /**
   * Does not create a form!!
   * Must be called an create a form manually afterwards
   *
   */
  def entityBindForm[T <: EntityBindBuffer](entity: T)(cl: => Any) = {
    withCurrentEntity(entity) {
      form {
        cl
        entityBindSubmitAction(entity)
      }
    }

  }
  def entityBindFormXList[T <: EntityBindBuffer](lst: XList[T])(cl: T => Any)(implicit tag: ClassTag[T]) = {

    val entity = lst.createInstance

    withCurrentEntity(entity) {
      form {
        cl(entity)

        entityBindSubmitAction(entity, Some(lst))
      }
    }

  }

  def entityBindClassForm[T <: EntityBindBuffer](cl: T => Any)(implicit tag: ClassTag[T]) = {

    val entity = tag.runtimeClass.newInstance().asInstanceOf[T]
    withCurrentEntity(entity) {
      form {
        cl(entity)
        entityBindSubmitAction(entity)
      }
    }
  }

  /**
   * Bind a field of a form to the entity's field
   * Must be called within an entityBindForm
   */
  def entityBindInput(name: String) = {
    currentEntity match {
      case None => sys.error("Cannot call entityBindInput outside and entityBindForm")
      case Some(entity) =>
        entity.listDataElementAndAttributeFields.find {
          case (fname, field) => fname == name
        } match {
          case Some((fname, field)) =>
            fieldName(fname)
          case None => sys.error(s"Cannot bind entity $name field, no such XML attribute/element declared")
        }
    }
    //entity.listElementAndAttributeFields

  }

  /**
   * To be called by API
   */
  private def entityBindSubmitAction[T <: EntityBindBuffer](entity: T, list: Option[XList[T]] = None) = {
    onSubmit {

      // Feed
      try {
        println("Feeding entity")
        entity.listDataElementAndAttributeFields.foreach {
          case (xmlname, field) =>
            println("Found FIeld: "+xmlname)
            withRequestParameter(xmlname) match {
              case Some(param) =>
                println("-> found in request: "+param)
                field.setAccessible(true)
                val fieldInstance = field.get(entity) match {
                  case null  => 
                    val instance = field.getType.newInstance().asInstanceOf[AbstractDataBuffer[_]]
                    field.set(entity,instance)
                    instance
                  case other => other.asInstanceOf[AbstractDataBuffer[_]]
                }
                fieldInstance.dataFromString(param)
              //val instance = field.getType.newInstance().asInstanceOf[AbstractDataBuffer[_]]
              // instan
              case None =>
            }
        }

        // Add to List
        //-----------------
        list match {

          case Some(lst) if (!lst.contains(entity)) =>
            lst += entity
          case other =>
        }

        // Submit end
        //-----------
        entity.triggerEntitySubmit

      } catch {
        case e: Throwable =>

          // Rollback from list
          list match {

            case Some(lst) if (lst.contains(entity)) =>
              lst -= entity
            case other =>
          }
          throw e
      }
    }
  }
  
  
  /**
   * Called when the current entity is being submited
   */
  def entityOnSubmit(cl: => Any) =  {
    
    this.currentEntity match {
      case Some(e) =>
        e.onEntitySubmit {
          cl
        }
      case None => 
        sys.error("Cannot call entityOnSubmit outside an entityForm")
    }
  }

}