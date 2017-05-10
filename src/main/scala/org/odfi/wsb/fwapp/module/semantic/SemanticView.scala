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

trait SemanticView extends LibraryView with FWAppFrameworkView with SemanticUIImplView with AssetsGeneratorView with WebsocketView {

  this.addLibrary("semantic") {
    case (Some(source), target) =>
      onNode(target) {
        stylesheet(createAssetsResolverURI(s"/semantic/semantic.min.css")) {
          +@("async" -> true)
        }

        script(createAssetsResolverURI(s"/semantic/semantic.min.js")) {
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
      cl

      // FIXME If no error, add 
      importHTML(<div class="ui error message"></div>)

    }

    //-- Make sire and id is present
    /*sform.id match {
      case "" =>
        sform.id = "semantic-" + sform.hashCode()
      case other =>
    }*/

    //-- Take Validation and generate script
    this.jqueryGenerateOnLoad("form-validation-" + sform.getId) match {
      case Some(generator) =>

        generator.println("console.info('From generated');")

        //-- look for required
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

        /*var requiredConstraints = sform.onSubNodesMap {
          case node: HTMLNode[HTMLElement, _] if (node.hasAttribute("required")) =>
            s"" + node.attribute("name") + " : 'empty'"
        }

        val emailConstaints = sform.onSubNodesMap {
          case node: HTMLNode[HTMLElement, _] if (node.hasAttribute("field-type") && node.attribute("field-type") == "email") =>
            s"" + node.attribute("name") + " : 'email'"
        }*/

        generator.println(s"""$$("#${sform.getId}").form({ on: 'blur',fields: { ${contraints.mkString(",")} }});""")

        generator.close()

      case None =>
    }

    //-- Return
    sform

  }

  def semanticSubmitButton(btext: String) = {
    "ui primary submit button" :: div {
      text(btext)
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

  def semanticContentThenDivider(cl: => Any) = {
    cl
    semanticDivider
  }
  def semanticDivider = "ui divider " :: div()

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

  def semanticTabDiv(tab: String)(cl: => Any) {

  }

  def semanticTabLoad = {
    script("""|
              |$(function() {
              |  $(".ui.tabular .item").tab();
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

  def semanticObjectsBottomTab[T <: Any](names: Iterable[(T, String)])(cl: (Int, String, T) => Unit) = {

    def cleanName(name: String) = {
      name.replaceAll("\\s+", "_").toLowerCase()
    }

    div {

      //-- Header
      val header = "ui top attached tabular menu" :: div {

      }

      //-- Content
      names.zipWithIndex.foreach {
        case ((obj, name), i) =>

          val tabId = cleanName(name)

          // add to header
          onNode(header) {
            "item" :: div {
              data("tab", tabId)
              if (i == 0) classes("active")
              text(name)

            }

          }

          // build content
          "ui bottom attached active tab segment" :: div {
            data("tab", tabId)
            if (i == 0) classes("active")
            cl(i, name, obj)
          }

      }

      //-- Contents

    }
    script("""|
              |$(function() {
              |  $(".ui.tabular .item").tab();
              |});
              |""".stripMargin)

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
        //println(s"Progress Bar sending data...")
        broadCastSOAPBackendMessage(pmessage)
      }

    }
  }

  def semanticProgress(pid: String) = {

    new SemanticProgressBar("ui progress" :: div {
      id("progress-" + pid)
      +@("style" -> "display:none")
      "bar" :: div {
        importHTML(<div class="progress"></div>)
      }
      "label" :: div {

      }

      jqueryGenerateOnLoad("progress-" + pid) match {
        case Some(generator) =>

          generator.println(s"""|
                                |console.log("Semantic Progress");
                                |$$("#progress-$pid").progress();
                                |
                                |//fwapp.websocket.debug = true;
                                |fwapp.websocket.makeEventConnection();
                                |fwapp.websocket.onPushData("SemanticProgressUpdate",function(payload) {
                                |  
                                |  $$("#progress-$pid").show();
                                |  $$("#progress-$pid").progress({percent: payload.Percent});
                                |  if(payload.Message) {
                                |    $$("#progress-$pid .label").text(payload.Message);
                                |  }
                                |  
                                |});
                                |
                                |
                                |""".stripMargin)
          generator.close()

        case None =>
      }
    })
  }

}