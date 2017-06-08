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
package fmacros

import scala.reflect.macros.blackbox
import scala.language.experimental.macros

trait FWAppValueBindingViewTrait {
   
  def bindValue[V](expr: V => Any): Unit  = macro FWAppMacros.bindValueImpl[V]
}

object FWAppMacros {
  
  def bindValueImpl[V](c: blackbox.Context)(expr: c.Expr[V => Any]): c.Expr[Unit] = {
    import c.universe._

    // Element 0 in expression is the input arg
    var inputArg = expr.tree.children(0)

    // Input Name is the product Element 1 :=> val NAME : V
    //                                               ^ 1
    var inputName = inputArg.productElement(1).toString
    
    // Type of input is product arity 2
    var typeName = inputArg.productElement(2).toString

    //c.warning(c.enclosingPosition, s"Calling $inputName of type $typeName ")
    
    //reify {
      //c.mirror.bindValueWithName
     // bindValueWithName[V]("test", {})
    //}
    c.Expr(q"""bindValueWithName($inputName,${reify(expr.splice)})""")
   // c.Expr(q"""bindValueWithName[V]($inputName,${reify(expr.splice)})""")
  }
}
