$(function() {

	// fwapp.websocket.debug = true;
	fwapp.websocket.makeEventConnection();
	fwapp.websocket.onPushData("SemanticProgressUpdate", function(payload) {
		
		var tid = "#"+payload.TargetID;
		$(tid).show();
		$(tid).progress({
			percent : payload.Percent
		});
		if (payload.Message) {
			$(tid+" .label").text(payload.Message);
		}

	});

});
