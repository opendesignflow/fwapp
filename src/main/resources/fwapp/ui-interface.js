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
	errorFor : function(element, error) {

		// console.info("Error Reason: "+faultJsonString[0]);
		//var fault = $.parseJSON("{" + faultJsonString + "}").Fault;
		
		// -- Look for error in parent neighbor
		var errorBlockNeighbor = $(element).parent().find(".error:first");
		if (errorBlockNeighbor) {
			console.log("Found Error Container: " + errorBlockNeighbor);
			
			if (error.ActionResult) {
				console.log("Got Action Result");
				
				// -- Get text
				var text = fwapp.decodeHTML(error.ActionResult.Error[0].Message);
				
				$(errorBlockNeighbor).html(text);
				$(errorBlockNeighbor).css("display", "block");
				
			} else {
				console.log("Got simple Fault");
				// -- Get text
				var text = fwapp.decodeHTML(error.Reason.Text);
				
				$(errorBlockNeighbor).html(text);
				$(errorBlockNeighbor).css("display", "block");
			}
			
			
			return true;

		} else {
			console.log("Error Container Not Found");
			return false;
		}
		
		//console.info("Error type: "+error[0])
		
		
		
		

		console.info("Error Reason: " + text);

		

	},
	
	// Sizing
	//-------------------
	heightToRatio : function(elt,ratio) {
		
		console.log("Adapting size: "+ratio+", of: "+elt+" -> "+$(elt).attr("id")+" -> "+$(elt).width());
		//$(elt).height($(elt).width()*ratio);
	}

};

$(function() {
	
	// load
	$('[data-ui-load]').each(function(i,elt) {
		
		console.log("Adapting size of: "+elt);
		eval($(elt).data("ui-load"));
		
	});
	
	
});