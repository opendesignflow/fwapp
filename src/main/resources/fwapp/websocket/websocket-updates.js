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
	
});