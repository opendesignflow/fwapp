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
package org.odfi.wsb.fwapp.swing

import org.odfi.wsb.fwapp.SiteApp
import java.awt.GraphicsEnvironment
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.Icon
import javax.swing.ImageIcon
import java.awt.BorderLayout
import org.odfi.wsb.fwapp.Site
import org.apache.batik.swing.JSVGCanvas
import org.apache.batik.swing.svg.JSVGComponent
import org.apache.batik.util.XMLResourceDescriptor
//import org.apache.batik.anim.dom.SAXSVGDocumentFactory
import org.w3c.dom.svg.SVGDocument
import com.idyria.osi.wsb.webapp.http.connector.HTTPConnector
import java.awt.Font
import java.awt.Color
import javax.swing.SwingUtilities
import org.odfi.indesign.core.module.swing.SwingUtilsTrait
import java.awt.event.MouseEvent
import java.awt.event.MouseAdapter
import java.awt.Desktop
import java.net.URI
import java.awt.Cursor

class SwingPanelSite(path: String) extends Site(path) with SwingUtilsTrait {

  var disableGUI = false
  var startupFrame: Option[JFrame] = None

  this.onStart {
    GraphicsEnvironment.isHeadless() match {
      case true                  =>
      case false if (disableGUI) =>
      case other =>

        //frame.add(new JLabel(new ImageIcon(getClass.getClassLoader.getResource("fwapp/ui/logo.png"))), BorderLayout.CENTER)

        this.engine.network.connectors.foreach {
          case hc: HTTPConnector =>
            onSwingThreadLater {

              var frame = new JFrame()
              startupFrame = Some(frame)
              frame.setSize(600, 300)
              frame.getContentPane.setBackground(Color.WHITE)

              var svgPanel = new JSVGCanvas
              svgPanel.loadSVGDocument(getClass.getClassLoader.getResource("fwapp/ui/logo.svg").toString())

              frame.add(svgPanel, BorderLayout.CENTER)

              var l = new JLabel(s"${getDisplayName} : http://localhost:${hc.port}${this.basePath}/")
              l.setCursor(new Cursor(Cursor.HAND_CURSOR))

              l.setFont(new Font("Sans Serif", Font.BOLD, 22))

              l.addMouseListener(new MouseAdapter {
                override def mouseClicked(e: MouseEvent) = {
                  if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop.browse(new URI(s"http://localhost:${hc.port}${basePath}/"))
                  }
                }
              })
              frame.add(l, BorderLayout.SOUTH)

              frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)

              frame.setVisible(true)
              centerOnScreen(frame)

              // Shutdown hook
              this.onShutdown {
                onSwingThreadAndWait {
                  startupFrame.get.dispose()
                   
                }
              }

            }
          case other =>
        }

    }
  }

}
