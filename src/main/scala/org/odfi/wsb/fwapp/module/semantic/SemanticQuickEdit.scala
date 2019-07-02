package org.odfi.wsb.fwapp.module.semantic

import org.odfi.wsb.fwapp.framework.FWAppTempBufferView
import com.idyria.osi.ooxoo.core.buffers.structural.ElementBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.XList
import com.idyria.osi.ooxoo.core.buffers.id.ElementWithID
import scala.reflect.ClassTag
import com.idyria.osi.ooxoo.lib.validation.ValidationContainer
import com.idyria.osi.vui.html.Div
import org.w3c.dom.html.HTMLElement

trait SemanticQuickEdit extends SemanticView {

    def quickEditIconTd(obj: Any, level: Int = 1) = {

        rtd {
            classes("collapsing")
            //-- Edit

            "ui icon edit" :: iconClickReload {
                putToTempBuffer("quickedit." + (0 until level).map("entity").mkString("."), obj)
            }

        }
    }

    def quickEditXIconTd[T <: ElementBuffer](objs: XList[T], obj: T, level: Int = 1, modal: Option[Div[HTMLElement, Any]] = None)(implicit tag: ClassTag[T]) = {

        rtd {
            classes("collapsing")

            //-- Edit
            "ui mini yellow icon button" :: button("") {
                semanticTooltipTopCenter("Edit")
                "icon edit" :: i {

                }

                modal match {
                    case Some(modal) =>
                        semanticModalShowOnClick(modal)
                    case None =>
                        onClickReload {
                            putToTempBuffer("quickedit." + (0 until level).map("entity").mkString("."), obj)
                        }
                }

            }

            //-- Remove
            quickEditRemoveIcon(objs, obj, "Are you sure you want to delete this element")

            //-- Validate
            if (classOf[ValidationContainer].isAssignableFrom(tag.runtimeClass)) {
                quickEditValidateIcon(obj.asInstanceOf[ValidationContainer])
            }

        }
    }

    def quickEditRemoveIcon[T <: ElementBuffer](objs: XList[T], obj: T, message: String) = {

        //-- Remove
        "ui mini red icon button" :: button("") {
            semanticTooltipTopCenter("Remove Element")
            "icon delete" :: i {
            }
            onClickReloadConfirm(message) {
                println(s"Removing")
            }
        }

    }

    def quickEditValidateIcon[T <: ValidationContainer](obj: T) = {

        //-- Remove
        "ui mini green icon button" :: button("") {
            semanticTooltipTopCenter("Validate Element")
            "eye icon" :: i {
            }
            onClickReload {
                println("validating")
                obj.validate
            }
        }

    }

    def quickEditSetEntity(obj: Any, level: Int = 1) = {
        obj match {
            case null =>
                deleteFromTempBuffer("quickedit." + (0 until level).map("entity").mkString("."))
            case other =>
                putToTempBuffer("quickedit." + (0 until level).map("entity").mkString("."), obj)
        }

    }

    def quickEditGetEntity(level: Int = 1) = {
        getTempBufferValue[Any]("quickedit." + (0 until level).map("entity").mkString("."))
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

                    // Edit icon
                    quickEditIconTd(o, level)

                    cl(o)
            }

        }
    }

    def semanticQuickXEditTable[T <: ElementBuffer](entities: List[T], entitiesSource: XList[T], emptyMessage: String = "No Data Available", level: Int = 1)(headers: String*)(cl: T => Any)(implicit tag: ClassTag[T]) = {
        "ui definition table" :: table {
            thead(List("") ::: headers.toList)

            tbodyTrLoopWithDefaultLine(emptyMessage)(entities) {
                o =>

                    // Edit icon
                    quickEditXIconTd(entitiesSource, o, level)

                    cl(o)
            }

        }
    }

    def semanticQuickModalXEditTable[T <: ElementBuffer](entities: List[T], entitiesSource: XList[T], emptyMessage: String = "No Data Available", level: Int = 1)(headers: List[String])(cl: T => Any)(ecl: T => Any)(implicit tag: ClassTag[T]) = {
        "ui celled compact definition table" :: table {
            thead(List("") ::: headers)

            tbodyTrLoopWithDefaultLine(emptyMessage)(entities) {
                o =>


                    val modal = "ui longer modal" :: div {
                        semanticInitModal
                        ecl(o)
                    }
                    modal.getId

                    //-- Edit icon
                    quickEditXIconTd(entitiesSource, o, level, Some(modal))

                    cl(o)
            }

        }
    }

    def semanticQuickModalEditTable[T <: ElementBuffer](entities: XList[T], emptyMessage: String = "No Data Available", level: Int = 1)(headers: String*)(cl: T => Any)(ecl: T => Any)(implicit tag: ClassTag[T]): Any = {
        "ui definition table" :: table {
            thead(List("") ::: headers.toList)

            tbodyTrLoopWithDefaultLine(emptyMessage)(entities) {
                o =>

                    rtd {
                        val modal = "ui longer modal" :: div {
                            semanticInitModal
                            ecl(o)
                        }
                        modal.getId

                        "ui mini icon edit" :: i {
                            semanticModalShowOnClick(modal)
                        }
                        "ui mini icon delete" :: iconClickReloadConfirm("Are you sure you want to delete this element?") {

                            println(s"Removing")
                        }
                    }

                    cl(o)
            }

        }
    }

    def semanticQuickEditAddEntityByName(label: String, actionLabel: String)(cl: String => Any) = {
        form {
           "small" ::  semanticActionFluidInput(label)(actionLabel) {
                fieldName("name")
                semanticFieldRequire
            }
            onSubmit {
                println("Quick Add: " + withRequestParameter("name"))
                cl(withRequiredRequestParameter("name"))

            }
        }

    }

    def semanticQuickEditAddEntityByEmail(label: String, actionLabel: String)(cl: String => Any) = {
        form {
            "small" :: semanticActionFluidInput(label)(actionLabel) {
                fieldName("email")
                semanticFieldEmail
                semanticFieldRequire
            }
            onSubmit {
                println("Quick Add: " + withRequestParameter("email"))
                cl(withRequiredRequestParameter("email"))

            }
        }

    }

    def semanticQuickEditAddEidEntityByName[T <: ElementWithID](label: String, actionLabel: String, newCl: => T)(cl: (String, T) => Any) = {
        semanticQuickEditAddEntityByName(label, actionLabel) {
            name =>
                val e = newCl
                e.eid = e.stringToStdEId(name)
                cl(name, e)

        }
    }

    def semanticQuickEditAddUniqueEid[T <: ElementWithID](label: String, actionLabel: String, sourceList: XList[T])(cl: (String, T) => Any)(implicit ctag: ClassTag[T]) = {
        semanticQuickEditAddEntityByName(label, actionLabel) {
            name =>
                sourceList.findByEId(name) match {
                    case Some(elt) =>
                        sys.error(s"Cannot add ${ctag.runtimeClass} with EID=$name, an element with same ID already exists")
                    case None =>
                        val e = sourceList.add
                        e.eid = e.stringToStdEId(name)
                        cl(name, e)

                }

        }
    }

    def semanticQuickEditSelectRemove[T <: ElementWithID](source: XList[T], level: Int = 0) = {

        val unknown = "---"
        var selected = quickEditGetEntity(level)
        form {
            select {
                reloadPage
                // Unknown selection
                if (selected.isEmpty) {
                    option(unknown) {
                        +@("selected" -> "true")
                    }
                }

                // Elements
                source.foreach {
                    element =>
                        option(element.eid) {
                            text(element.eid)

                            if (selected.isDefined && selected.get == element) {
                                +@("selected" -> "true")
                            }
                        }

                }

                bindValue {
                    id: String =>
                       // println(s"Selecting: " + id)
                        source.findByEId[T](id) match {
                            case Some(element: T) =>
                                //println(s"Setting Entity")
                                quickEditSetEntity(element, level)
                            case None => quickEditSetEntity(null, level)
                        }
                }
            }

        }

    }

}