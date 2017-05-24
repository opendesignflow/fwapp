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