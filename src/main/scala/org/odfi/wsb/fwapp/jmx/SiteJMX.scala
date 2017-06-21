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
package org.odfi.wsb.fwapp.jmx

import org.odfi.wsb.fwapp.FWappApp
import java.lang.management.ManagementFactory
import javax.management.ObjectName
import javax.management.DynamicMBean
import javax.management.StandardMBean
import org.odfi.indesign.core.module.jmx.ToolVirtualMachine
import org.odfi.indesign.core.module.jmx.VMConnection
import javax.management.JMX
import com.idyria.osi.tea.logging.TLogSource

/**
 * FWappApp with JMX
 */
trait FWAPPJMX extends FWappApp {

  val jmxManager = new FWAPPJMXManagerImpl(this)
  val jmxManagerBeanObjectName = new ObjectName("fwapp:manager=true")

  def listenWithJMXClose(port: Int) = {

    /**
     * Load Beans to local JMX
     *
     *
     * WARNING: Make sure this runs before the the start lifecycle which listens on the port,
     *  otherwise it will try to close itself and won't bind correctely in the first place
     */
    this.onInit {

      // First close existing VM if possible
      //---------------
      logInfo[FWAPPJMX]("Search for VM managing given port: " + port)
      VMConnection.getOracleToolVirtualMachine.attachToVMForNetworkPort(port) match {
        case Some(vm) =>
          logInfo[FWAPPJMX]("Found VM for closing")
          var server = vm.getManagementServer

          val managerBean = server.getObjectInstance(jmxManagerBeanObjectName)

          val managerInterface = JMX.newMBeanProxy(server, jmxManagerBeanObjectName, classOf[FWAPPJMXManager])
          ///managerBean.asInstanceOf[FWAPPJMXManager].stop

          logInfo[FWAPPJMX]("Found manager bean: " + managerBean)

          try {
            managerInterface.stop
          } catch {
            case e: Throwable =>
              logWarn[FWAPPJMX]("During JMX Stopping of previous instance, an error occured: " + e.getLocalizedMessage)
          }
        case None =>
      }

      // Now Register Management Bean
      //----------------------
      //ManagementFactory.getPlatformMBeanServer.createMBean(classOf[FWAPPJMXManagerImpl].getCanonicalName, new ObjectName("fwapp:manager=true"))

      val mbean = new StandardMBean(jmxManager, classOf[FWAPPJMXManager].asInstanceOf[Class[Object]], false)

      ManagementFactory.getPlatformMBeanServer.registerMBean(mbean, jmxManagerBeanObjectName)

      logInfo[FWAPPJMX]("Registered management MBean in local jmx")

    }

    this.listen(port)

  }

}

trait FWAPPJMXManager {

  def stop: Unit

}
class FWAPPJMXManagerImpl(val app: FWappApp) extends FWAPPJMXManager with TLogSource {

  def stop = {
    logWarn[FWAPPJMX]("Stopping App from JMX")
    app.moveToShutdown
    Thread.sleep(500)
    sys.exit()
  }
}
