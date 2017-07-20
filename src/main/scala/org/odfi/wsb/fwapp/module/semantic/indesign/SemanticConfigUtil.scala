package org.odfi.wsb.fwapp.module.semantic.indesign

import org.odfi.wsb.fwapp.module.semantic.SemanticView
import org.odfi.indesign.core.config.model.CommonConfig
import org.odfi.wsb.fwapp.lib.indesign.FWappResourceValueBindingView

trait SemanticConfigUtil extends SemanticView with FWappResourceValueBindingView {

  /**
   * Table to set the configs of a common config based on config support
   */
  def configSupportTable(config: CommonConfig) = {

    println(s"Config support for: ")
    "ui table" :: table {
      thead("Config", "Type", "Value")

      tbodyTrLoopWithDefaultLine("No Supported Configuration Defined")(config.supportedConfig.configValues) {
        case supported =>
          td(supported.name) {

          }
          td(supported.keyType) {

          }
          rtd {
            supported match {
              case str if (str.isString) =>

                inputToBufferAfter500MS(config.getStringAsBuffer(supported.name, ""))

              case bool if (bool.isBoolean) =>

                inputToBuffer(config.getBooleanAsBuffer(supported.name, bool.getBooleanDefault)) {

                }

              case d if (d.isDouble) =>

                inputToBuffer(config.getDoubleAsBuffer(supported.name, d.getDoubleDefault)) {

                }

              case i if (i.isInteger) =>

                inputToBuffer(config.getIntAsBuffer(supported.name, i.getIntDefault)) {

                }

              case other => text("Not Supported: " + other.keyType)
            }
            if (supported.keyType.toString == "string") {

            } else if (supported.keyType.toString == "double") {

            }
          }

      }
      
      tfoot {
        tr {
          rtd{}
          rtd {
            colspan(tableColumnsCount-1)
            "ui primary button" :: buttonClickReload("Save") {
              
            }
          }
        }
      }
    }

  }

}