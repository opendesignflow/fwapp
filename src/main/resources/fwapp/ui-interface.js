fwapp.ui = {

	// Wait: Show on the page something is happening
	// -------------
	waitStart : function(target) {
		fwapp.ui.impl.waitStart(target);
	},
	waitStop : function(target) {
		fwapp.ui.impl.waitStop(target);
	},

	// Enable Disable
	// ---------------
	disable : function(target) {
		fwapp.ui.impl.disable(target);
	},

	enable : function(target) {
		fwapp.ui.impl.enable(target);
	},

	// Report Error
	// --------------------
	errorFor : function(element, faultJsonString) {

		// console.info("Error Reason: "+faultJsonString[0]);
		var fault = $.parseJSON("{" + faultJsonString + "}").Fault;

		// -- Get text
		var text = localWeb.decodeHTML(fault.Reason.Text);

		console.info("Error Reason: " + text);

		// -- Look for error in parent neighbor
		var errorBlockNeighbor = $(element).parent().find(".error:first");
		if (errorBlockNeighbor) {
			console.log("Found Error Container: " + errorBlockNeighbor);
			$(errorBlockNeighbor).html(text);
			$(errorBlockNeighbor).css("display", "block");
			return true;

		} else {
			console.log("Error Container Not Found");
			return false;
		}

	}

};