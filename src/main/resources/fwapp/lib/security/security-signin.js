
fwapp.lib.security.signin = function(providerID,tokenID,email) {
	
	/**
	 * Call action and don't reload ourselves
	 */
	fwapp.actions.callViewAction("fwapp.lib.security.signin",{providerID: providerID, tokenID:tokenID, email:email},false);
	
}