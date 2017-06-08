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
package org.odfi.wsb.fwapp

import java.time.Instant
import java.time.ZoneId
import java.time.chrono.IsoChronology

import org.odfi.indesign.core.harvest.HarvestedResource
import org.odfi.wsb.fwapp.views.FWappView

import com.idyria.osi.wsb.core.broker.tree.IntermediaryLanguage
import com.idyria.osi.wsb.webapp.http.message.HTTPIntermediary
import com.idyria.osi.wsb.webapp.http.message.HTTPPathIntermediary
import com.idyria.osi.wsb.webapp.http.session.Session
import com.idyria.osi.wsb.webapp.http.message.HTTPResponse
import org.odfi.wsb.fwapp.framework.FWAppFrameworkView
import java.io.PrintStream
import java.io.StringWriter
import java.io.PrintWriter
import com.idyria.osi.wsb.core.message.soap.Fault
import org.odfi.wsb.fwapp.framework.Errors
import java.nio.ByteBuffer
import org.odfi.wsb.fwapp.framework.ActionsResult

trait FWappTree extends HTTPIntermediary with IntermediaryLanguage {

  // Language
  //-------------------

  def path(str: String) {

  }

}

