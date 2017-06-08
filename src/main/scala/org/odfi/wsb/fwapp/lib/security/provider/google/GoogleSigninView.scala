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
package org.odfi.wsb.fwapp.lib.security.provider.google

import org.odfi.wsb.fwapp.views.LibraryView
import org.odfi.wsb.fwapp.lib.security.views.SigninView

trait GoogleSigninView extends  SigninView {
  
  var googleSigninScopes = "email"
  
  this.addLibrary("google") {
    case (_,targetNode) => 
      onNode(targetNode) {
        
        importHTML(<meta name="google-signin-scope"/>).+@("content" -> googleSigninScopes)
        importHTML(<meta name="google-signin-client_id"/>).+@("content" -> (GoogleAPIModule.configGetString("clientID", "-")))
    		//<meta name="google-signin-client_id" content="YOUR_CLIENT_ID.apps.googleusercontent.com">
         
        importHTML(<script src="https://apis.google.com/js/platform.js" async="true" defer=""></script>)
        
        script(createAssetsResolverURI("/fwapp/lib/security/google/google.js")) {
          
        }
      }
  }
  
  def googleSigninButton = {
    
    //importHTML(<div class="g-signin2" data-onsuccess="fwapp.lib.google.onSignIn"></div>)
    importHTML(<div class="g-signin2" data-onsuccess="googleOnSignIn"></div>)
    
  }
  
}
