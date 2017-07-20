$(function() {

	// Setup Form switch
	// -------------------
	$(".client-utils-switch-on-input").each(function(i, e) {

		
		var inputName = $(e).data("switch-on-input");
		var requestedValue = $(e).data("switch-on-value");
		
		console.log("Found Switch on input: "+inputName+" -> "+requestedValue);

		// -- Get Parent form
		if ($(e).parents('form').length) {
			
			//-- Get form and input with selection name
			//-- If selection does not have the same value as the requested, hide/show
			var parentForm = $(e).parents('form');
			var foundInput = $(parentForm).find("*[name='"+inputName+"']");
			if (foundInput) {
				
				//-- Show/Hide
				var currentValue = $(foundInput).val();
				console.log("Current value: "+currentValue);
				
				if (currentValue==requestedValue) {
					$(e).show();
				} else {
					$(e).hide();
				}
				
				//-- React on change
				$(foundInput).change(function() {
					
					if ($(foundInput).val()==requestedValue) {
						$(e).show();
					} else {
						$(e).hide();
					}
					
					
				});
				
				
				
			} else {
				console.warning("No input in form with name: "+inputName);
			}
			console.log("Found input: "+foundInput);
			
			
		} else {
			
			console.warning("data-switch-on feature only available inside a form");
			
		}

	});

});
