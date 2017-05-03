$(function() {
	
	//fwapp.websocket.debug = false;
	fwapp.websocket.makeEventConnection();
	fwapp.websocket.onPushData("UpdateAttribute",function(payload) {
		
		//console.log("Updating Attribute: "+payload.TargetID);
		
		var target = $("#"+payload.TargetID);
		if (target) {
			target.attr(payload.Name,payload.Value);
		}
		
	});
	
	//Update text
	//-------------------
	fwapp.websocket.onPushData("UpdateText",function(updateText) {
		
		var id = fwapp.decodeHTML(updateText.TargetID)
		//console.log("Got update text for: "+id)
		
		var targetElement = $("#"+id);
		if (targetElement) {
			if (targetElement.is("input") && targetElement.attr("value")!=fwapp.decodeHTML(updateText.Text) ) {
				
				targetElement.attr("value",fwapp.decodeHTML(updateText.Text));
			} else {
				targetElement.html(fwapp.decodeHTML(updateText.Text));
			}
			
		}
		
	});
	
});