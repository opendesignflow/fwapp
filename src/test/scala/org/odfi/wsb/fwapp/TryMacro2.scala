package org.odfi.wsb.fwapp

import scala.reflect.macros.blackbox
import scala.language.experimental.macros

object TryMacro2 {
  def hello: Unit = macro helloImpl

  def helloImpl(c: blackbox.Context): c.Expr[Unit] = {
    import c.universe._
    c.Expr(q"""println("hello!")""")
  }
}