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
package org.odfi.wsb.fwapp.module.semantic

import org.odfi.wsb.fwapp.views.LibraryView
import com.idyria.osi.vui.html.HTMLNode
import org.w3c.dom.html.HTMLElement
import org.odfi.wsb.fwapp.assets.AssetsManager
import java.net.URI
import org.odfi.wsb.fwapp.views.ui.SemanticUIImplView
import com.idyria.osi.vui.html.Form
import org.odfi.wsb.fwapp.assets.generator.AssetsGenerator
import org.odfi.wsb.fwapp.assets.generator.AssetsGeneratorView
import org.odfi.wsb.fwapp.framework.FWAppFrameworkView
import scala.collection.mutable.ArrayBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.ElementBuffer
import com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer
import com.idyria.osi.ooxoo.core.buffers.datatypes.DoubleBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.xelement
import com.idyria.osi.vui.html.Div
import org.odfi.wsb.fwapp.framework.websocket.WebsocketView
import com.idyria.osi.vui.html.Input
import com.idyria.osi.vui.html.Select
import org.odfi.wsb.fwapp.framework.FWAppTempBufferView

trait SemanticView extends LibraryView with FWAppFrameworkView with SemanticUIImplView with AssetsGeneratorView with WebsocketView with FWAppTempBufferView {

  this.addLibrary("semantic") {
    case (Some(source), target) =>
      onNode(target) {
        stylesheet(createAssetsResolverURI(s"/semantic/semantic.min.css")) {
          +@("async" -> true)
        }

        script(createAssetsResolverURI(s"/semantic/semantic.min.js")) {
          //+@("async" -> true)
        }

        script(createAssetsResolverURI(s"/fwapp/lib/semantic/semantic-progress.js")) {
          //+@("async" -> true)
        }
      }
    case (None, target) =>

      onNode(target) {
        stylesheet(createAssetsResolverURI(s"/fwapp/external/semantic/semantic.min.css")) {
          +@("async" -> true)
        }

        script(createAssetsResolverURI(s"/fwapp/external/semantic/semantic.min.js")) {
          // +@("async" -> true)
        }

        script(createAssetsResolverURI(s"/fwapp/lib/semantic/semantic-progress.js")) {
          //+@("async" -> true)
        }
      }

  }

  def ribbonHeaderDiv(color: String, name: String)(cl: => Any) = {

    "ui segment" :: div {
      classes("ribbon-header")
      s"ui $color ribbon label" :: a("#") { text(name) }
      div {
        cl
      }

    }

  }

  // Form validation
  //--------------
  override def form(cl: => Any) = {

    //println("Semantic form")
    //-- Create Form
    var sform = super.form {
      classes("ui form")
      +@("method" -> "post")
      id("semantic-" + currentNode.hashCode())

      // FIXME If no error, add
      hasActionErrors match {
        case true =>
          actionResults.foreach {
            case (actionid, res) =>
              res.errors.foreach {
                err =>
                  "ui error message" :: div {
                    cssForceBlockDisplay
                    text(err.message)
                  }
              }

          }
        case false =>
          importHTML(<div class="ui error message"></div>)
      }

      cl

    }

    //-- Make sire and id is present
    /*sform.id match {
      case "" =>
        sform.id = "semantic-" + sform.hashCode()
      case other =>
    }*/

    //-- Take Validation and generate script
    //-- look for required to prepare constraints
    var contraints = sform.onSubNodesMap {
      case node: HTMLNode[HTMLElement, _] =>

        // prepare constaints
        var nodeContraints = List[String]()

        if (node.hasAttribute("required"))
          nodeContraints = nodeContraints :+ "empty"

        node.onDataOfType[String]("semantic-validation-type") {
          ftype =>
            nodeContraints = nodeContraints :+ ftype
        }
        node.onDataOfType[List[String]]("semantic-validation-not") {
          nots =>
            nodeContraints = nodeContraints ++ (nots.map { n => s"not[$n]" })

        }

        // If no constraints, return empty
        nodeContraints.size match {
          case 0 =>
            ""
          case other =>
            // Make constraints string
            val nodeConstraintsString = nodeContraints.mkString("['", "','", "']")

            s"'" + node.attribute("name") + s"' : $nodeConstraintsString"
        }

    }.filterNot(_.isEmpty())

    contraints.isEmpty match {
      case false =>
        this.jqueryGenerateOnLoad("form-validation-" + sform.getId) match {
          case Some(generator) =>

            generator.println("console.info('From generated');")

            generator.println(s"""$$("#${sform.getId}").form({ on: 'blur',fields: { ${contraints.mkString(",")} }});""")

            generator.close()

          case None =>
        }
      case true =>
    }

    //-- Return
    sform

  }

  def semanticSubmitButton(btext: String) = {
    "ui primary submit button" :: button(btext) {

    }
  }

  def semanticOnSubmitButton(btext: String)(cl: => Any) = {
    /*val b = "ui submit primary button" :: div {
      text(btext)
    }*/
    val b = "ui submit primary button" :: button(btext) {
      //text(btext)
    }

    onSubmit(cl)
    b
  }

  // Semantic Input
  //-------------------------

  /**
   * @param id the name of the input field used for form
   */
  def semanticFormField(id: String, name: String)(cl: => Any): Div[HTMLElement, Any] = {

    // Make div
    val res = "field" :: div {
      label(name) {

      }
      cl
    }

    // Apply name to input or select
    res.findDescendantOfType[Input[HTMLElement, _]] match {
      case Some(input) =>
        input.+@("name" -> id)
      case None =>

        res.findDescendantOfType[Select[HTMLElement, _]] match {
          case Some(input) =>
            input.+@("name" -> id)
          case None =>
        }
    }

    res

  }

  def semanticFormField(name: String)(cl: => Any): Div[HTMLElement, Any] = {

    "field" :: div {
      label(name) {

      }
      cl
    }

  }

  /**
   * "field" :: div {
   * 	 input {
   * 			CLOSURE
   *   }
   * }
   */
  def semanticFormFieldInput(cl: => Any) = {
    "field" :: div {
      input {
        cl
      }
    }
  }

  // Semantic inputs
  //----------------------

  /**
   * Creates div and input
   */
  def semanticFluidInput(cl: => Any) = {
    "ui fluid input" :: div {
      input {
        cl
      }
    }
  }

  /**
   * Creates Fluid Input and standard HTML label
   */
  def semanticLabelFluidInput(name: String)(cl: => Any) = {
    "ui fluid input" :: div {
      "ui label" :: label(name) {
        input {
          cl
        }
      }

    }
  }

  /**
   * Create Fluid input with semantic Label on the left
   */
  def semanticLeftLabeledFluidInput(name: String)(cl: => Any) = {
    "ui fluid labeled input" :: div {

      "ui label" :: name

      input {
        cl
      }

    }
  }

  def semanticActionFluidInput(label: String)(action: String)(cl: => Any) = {

    "ui fluid action input" :: div {

      input {
        +@("placeholder" -> label)
        cl
      }

      semanticSubmitButton(action)
      //"ui submit button" :: action

    }
  }

  // Semantic Form Validation
  //-------------------

  def semanticFieldRequire = {
    currentNode.findParentOfType[Form[HTMLElement, _]] match {
      case Some(parent) =>
        +@("required" -> true)
      case None =>
        semanticLocalError("Field Require not possible if not in Form")
    }
  }

  def semanticFieldNot(strings: ArrayBuffer[String]): Unit = semanticFieldNot(strings.toList)
  def semanticFieldNot(strings: List[String]): Unit = {
    currentNode.findParentOfType[Form[HTMLElement, _]] match {
      case Some(parent) =>
        data("semantic-validation-not", strings)
      case None =>
        semanticLocalError("Field Not not possible if not in Form")
    }
  }

  def semanticFieldEmail = {
    currentNode.findParentOfType[Form[HTMLElement, _]] match {
      case Some(parent) =>
        data("semantic-validation-field-type" -> "email")
      case None =>
        semanticLocalError("Field Email not possible if not in Form")
    }
  }

  // Local Errors
  //-----------------
  def semanticActionResult(defaultText: String = "") {
    "ui error fwapp-action-result-placeholder fwapp-action-error message hidden" :: div()
    "ui info fwapp-action-result-placeholder fwapp-action-results message hidden" :: div()
    if (defaultText != "") {
      "ui message fwapp-action-result-placeholder fwapp-action-info" :: defaultText
    }
  }
  def semanticLocalError(err: String) = {
    +@("local-error" -> err)
  }

  // Popup
  //-------------

  //-- Preset components
  //---------------
  def semanticIconClickPopup(iconName: String)(cl: => Any) {
    s"$iconName icon" :: i {
      semanticPopupOnClick
    }
    semanticFlowingPopup {
      cl
    }
  }

  // Elements Value Copy
  //---------------------
  /*$('.autumn.leaf')
  .transition('bounce')
;*/

  //-- Low LEvel utils
  //-----------------------
  def semanticPopupOnClick = {
    classes("popup-activate")
  }
  def semanticFlowingPopup(cl: => Any) = {
    "ui flowing popup hidden" :: div {
      cl
    }
  }

  def semanticPopupOnClickCurrent(cl: => Any) = {
    semanticPopupOnClick
    onNode(currentNode.parent.get.asInstanceOf[HTMLNode[HTMLElement, _]]) {
      semanticFlowingPopup {
        cl
      }
    }
  }

  def semanticTopLeft = classes("top left")
  def semanticTopRight = classes("top right")
  def semanticEffectTransition = classes("transition")

  // Divider
  //------------------
  def semanticContentThenDivider(cl: => Any) = {
    cl
    semanticDivider
  }

  def semanticDividerThenContent(cl: => Any) = {
    semanticDivider
    cl
  }

  def semanticDivider = "ui divider " :: div()

  def semanticH1Divider(text: String) = {
    h1(text) {

    }
    semanticDivider
  }
  def semanticH2Divider(text: String) = {
    h2(text) {

    }
    semanticDivider
  }
  def semanticH3Divider(text: String) = {
    h3(text) {

    }
    semanticDivider
  }
  def semanticH4Divider(text: String) = {
    h4(text) {

    }
    semanticDivider
  }
  def semanticH5Divider(text: String) = {
    h5(text) {

    }
    semanticDivider
  }

  def semanticH6Divider(text: String) = {
    h6(text) {

    }
    semanticDivider
  }

  // Messages and checks
  //---------------
  def semanticWarningIfEmpty(obj: Option[_])(str: String) = obj match {
    case None =>
      "ui warning message" :: str
    case other =>

  }

  def semanticWarningIfEmptyString(obj: XSDStringBuffer)(str: String) = obj match {
    case b if (b == null || b.data.toString == "") =>
      "ui warning message" :: str
    case other =>

  }
  def semanticWarningIfEmptyString(obj: String)(str: String) = obj match {
    case s if (s == null || s == "") =>
      "ui warning message" :: str
    case other =>

  }
  def semanticWarningIfEmptyStringElse(obj: String)(str: String)(cl: => Any) = obj match {
    case s if (s == null || s == "") =>
      "ui warning message" :: str
    case other =>
      cl

  }

  def semanticWarningIfEmptyListElseForeach[T](obj: Iterable[T])(str: String)(cl: T => Any) = obj.size match {
    case 0 =>
      "ui warning message" :: str
    case other =>
      obj.foreach(cl)

  }

  // Sticky
  //---------------
  def semanticSticky = {

    val nodeId = currentNode.getId
    jqueryGenerateOnLoad("sticky-" + nodeId) match {
      case Some(gen) =>

        gen.println(s"""$$("#$nodeId").sticky();""")
        gen.close()
      case None =>
    }

  }

  // Tabs
  //----------------
  def semanticMakeTabId(str: String) = {
    str.replaceAll("\\s+", "_").toLowerCase()
  }
  def semanticTabLink(tab: String) = {
    data("tab" -> tab)
  }

  def semanticNamedTabs(names: String*)(cl: PartialFunction[(Int, String), Any]) = {
    div {

      //-- Header
      val header = "ui tabular menu top attached " :: div {

      }

      // Load tabs
      //semanticTabLoad

      val selectedTab = getTempBufferValue[String]("semantic.tabs.namedTabs.currentTab")

      println(s"Selected tab: " + selectedTab)

      //-- Content
      names.zipWithIndex.foreach {
        case (name, i) =>

          val tabId = semanticMakeTabId(name)

          // add to header
          onNode(header) {
            "item" :: a("#") {
              data("tab", tabId)
              currentNode.attributes = currentNode.attributes - "href"
              if ((selectedTab.isEmpty && i == 0) || (selectedTab.isDefined && selectedTab.get.toString == tabId)) classes("active")
              text(name)
              onClick {
                putToTempBuffer("semantic.tabs.namedTabs.currentTab", tabId)
              }

            }

          }

          // build content
          "ui bottom attached tab segment" :: div {
            data("tab", tabId)
            if ((selectedTab.isEmpty && i == 0) || (selectedTab.isDefined && selectedTab.get.toString == tabId)) classes("active")
            cl(i, name)
          }

      }

      //-- Contents

    }
    script("""|
              |$(function() {
              |  $(".tabular.menu .item").tab();
              |});
              |""".stripMargin)
  }

  def semanticTabDiv(tab: String)(cl: => Any) {
    div {
      semanticTabLink(tab)
    }
  }

  def semanticTabLoad = {
    script("""|
              |$(function() {
              |  $(".tabular.menu .item").tab();
              |});
              |""".stripMargin)
  }

  def semanticTabForObjects[T <: Any](nameAndObjects: Iterable[(String, T)], menu: HTMLNode[HTMLElement, _], content: HTMLNode[HTMLElement, _])(cl: (Int, String, T) => Unit) = {

    //-- Content
    nameAndObjects.zipWithIndex.foreach {
      case ((name, obj), i) =>

        val tabId = semanticMakeTabId(name)

        // add to header
        onNode(menu) {
          "item" :: a("#") {
            data("tab", tabId)
            if (i == 0) classes("active")
            text(name)

          }

        }

        // build content
        onNode(content) {
          "ui tab" :: div {
            data("tab", tabId)
            if (i == 0) classes("active")
            cl(i, name, obj)
          }
        }

    }

    semanticTabLoad

  }

  def semanticObjectsBottomTab[T <: Any](tabsId: String, names: Iterable[(String, T)])(cl: (Int, String, T) => Unit) = {

    def cleanName(name: String) = {
      name.replaceAll("\\s+", "_").toLowerCase()
    }

    div {

      //-- Header
      val header = "ui tabular menu top attached " :: div {

      }

      // Load tabs
      //semanticTabLoad

      val selectedTab = getTempBufferValue[String]("semantic.tabs." + tabsId + ".currentTab")

      println(s"Selected tab: " + selectedTab)

      //-- Content
      names.zipWithIndex.foreach {
        case ((name, obj), i) =>

          val tabId = cleanName(name)

          // add to header
          onNode(header) {
            "item" :: a("#") {
              data("tab", tabId)
              currentNode.attributes = currentNode.attributes - "href"
              if ((selectedTab.isEmpty && i == 0) || (selectedTab.isDefined && selectedTab.get.toString == tabId)) classes("active")
              text(name)
              onClick {
                putToTempBuffer("semantic.tabs." + tabsId + ".currentTab", tabId)
              }

            }

          }

          // build content
          "ui bottom attached tab segment" :: div {
            data("tab", tabId)
            if ((selectedTab.isEmpty && i == 0) || (selectedTab.isDefined && selectedTab.get.toString == tabId)) classes("active")
            cl(i, name, obj)
          }

      }

      //-- Contents

    }
    script("""|
              |$(function() {
              |  $(".tabular.menu .item").tab();
              |});
              |""".stripMargin)

  }

  // Table
  //-----------
  def semanticDefinitionTable(headers: String*)(cl: => Any) = {

    "ui definition table" :: table {
      thead(List("") ::: headers.toList)

      cl
    }
  }

  // Menu
  //---------

  /**
   * "header" :: { "icon $icon" :: i{}; text(headerText)}
   */
  def semanticMenuIconHeader(icon: String, headerText: String) = {
    "header" :: { s"icon $icon" :: i {}; text(headerText) }
  }

  // Dropdown
  //--------------

  /**
   * creates a
   *
   * "ui item @data-value=value" :: div {
   * 	TEXT
   * }
   *
   * for each (value -> TEXT) pairs provided in the list
   *
   */
  def semanticDropDownItems(options: List[(Any, Any)]) = {
    options.foreach {
      case (v, t) =>
        "ui item" :: div {
          data("value" -> v)
          text(t.toString)
        }
    }
  }

  // Progress
  //-----------------
  class SemanticProgressUpdate extends ElementBuffer {

    @xelement
    var TargetID: XSDStringBuffer = _

    @xelement
    var Message: XSDStringBuffer = _

    @xelement
    var Percent: DoubleBuffer = _

  }

  class SemanticProgressBar(val d: Div[_, _]) {

    var defaultMessage: Option[String] = None
    var lastValue: Double = 0.0

    // Gui update
    def indicating = {
      "indicating" :: d
    }

    def topAttached = {
      "top attached" :: d
    }

    def update(p: Int) = {
      /*val tp = if (p > 100.0) {
        100.0
      } else {
        p
      }*/

      if (p != lastValue) {
        lastValue = p
        val pmessage = new SemanticProgressUpdate
        pmessage.TargetID = d.getId
        pmessage.Percent = p
        defaultMessage match {
          case Some(m) =>
            pmessage.Message = m
          case None =>
        }
        //println(s"Progress Bar sending data..."+d.getId)
        broadCastSOAPBackendMessage(pmessage)
      }

    }
  }

  def semanticSetProgress(pid: String, percent: Int,message:String="") = {
    val pmessage = new SemanticProgressUpdate
    pmessage.TargetID = pid
    pmessage.Percent = percent
    message match {
      case "" => 
      case m =>
        pmessage.Message = m
     
    }
    //println(s"Progress Bar sending data..."+d.getId)
    broadCastSOAPBackendMessage(pmessage)
  }

  def semanticProgress(pid: String) = {

    val targetPid = pid

    new SemanticProgressBar("ui progress" :: div {

      id(targetPid)

      +@("style" -> "display:none")

      "bar" :: div {
        importHTML(<div class="progress"></div>)
      }
      "label" :: div {

      }

      jqueryGenerateOnLoad(targetPid) match {
        case Some(generator) =>

          generator.println(s"""|
                                |console.log("Semantic Progress");
                                |$$("#$targetPid").progress();
                                |
                                |""".stripMargin)
          generator.close()

        case None =>
      }
    })
  }

  // Accordion
  //-----------------
  def semanticStandardAccordion(title: String)(cl: => Any) = {

    "ui accordion" :: div {
      "ui title" :: div {
        "ui dropdown icon" :: i {

        }
        text(title)
      }
      "content" :: div {
        cl
      }
    }
  }

}
