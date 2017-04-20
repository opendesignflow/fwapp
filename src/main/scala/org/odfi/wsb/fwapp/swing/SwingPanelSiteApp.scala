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
import org.apache.batik.anim.dom.SAXSVGDocumentFactory
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

  this.onStart {
    GraphicsEnvironment.isHeadless() match {
      case true =>

      case false =>

        //frame.add(new JLabel(new ImageIcon(getClass.getClassLoader.getResource("fwapp/ui/logo.png"))), BorderLayout.CENTER)

        this.engine.network.connectors.foreach {
          case hc: HTTPConnector =>

            var frame = new JFrame()
            frame.setSize(600, 300)
            frame.getContentPane.setBackground(Color.WHITE)

            var svgPanel = new JSVGCanvas
            svgPanel.setDocumentState(JSVGComponent.ALWAYS_DYNAMIC)
            svgPanel.loadSVGDocument(getClass.getClassLoader.getResource("fwapp/ui/logo.svg").toString())

            //println("Website " + getDisplayName + s" available at: http://localhost:${hc.port}${this.basePath}")

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
          case other =>
        }

    }
  }

}
