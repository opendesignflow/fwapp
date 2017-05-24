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
import org.odfi.wsb.fwapp.framework.ActionsResultActionResult
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

