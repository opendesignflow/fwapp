$(function() {

	
	fwapp.websocket.makeEventConnection();
	fwapp.websocket.onPushData("PlotlyLineChart", function(payload) {
		var tid = payload.TargetID;
		console.log("Updating plotly..."+tid);
		
		
		var trace = { 
                    y: payload.yPoints,
                    type: 'scatter'
                }
                
		Plotly.newPlot(tid, [trace]);
		
	});
	
});
