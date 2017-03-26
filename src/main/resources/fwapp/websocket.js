fwapp.websocket = {
		
	debug : false,
	wsConnection : null , 
	
	// Event Connection
	// ---------------------------
	makeEventConnection : function() {

		var targetURL = "ws://" + window.location.hostname
				+ (window.location.port ? ':' + window.location.port : '')
				+ window.location.pathname+"/websocket";
		
		
		console.log("Making Websocket connection to: " + targetURL);
		fwapp.websocket.wsConnection = new WebSocket(targetURL, [ 'soap' ]);

		// Log messages from the server
		//---------------
		fwapp.websocket.wsConnection.onopen = function (event) {
			if (fwapp.websocket.debug == true) {
				console.log('Connection Done');
				
				
				//Update text
				//-------------------
				fwapp.websocket.onPushData("UpdateText",function(updateText) {
					
					var id = fwapp.websocket.decodeHTML(updateText.Id)
					//console.log("Got update text for: "+updateText.Id)
					
					var targetElement = $("#"+id);
					if (targetElement) {
						if (targetElement.is("input") && targetElement.attr("value")!=fwapp.websocket.decodeHTML(updateText.Text) ) {
							
							targetElement.attr("value",fwapp.websocket.decodeHTML(updateText.Text));
						} else {
							targetElement.html(fwapp.websocket.decodeHTML(updateText.Text));
						}
						
					}
					
				});
			}
		};
		
		// Message Handler which maps messages to application's message-specific handler
		//--------------------
		fwapp.websocket.wsConnection.onmessage = function(e) {

			if (fwapp.websocket.debug == true) {
				console.log('Server: ' + e.data);
			}

			// Get SOAP JSON
			// -------------
			var soap = $.parseJSON("{" + e.data + "}");

			$(fwapp.websocket.wsConnection).trigger("soap", soap);

			// Handle messages
			// --------------
			var body = soap.Envelope.Body;

			if (fwapp.websocket.debug == true) {
				console.log("Keys: " + Object.keys(body));
			}
			
			
			$(fwapp.websocket.wsConnection).triggerHandler("payload",
					[ Object.keys(body)[0], body[Object.keys(body)[0]] ]);
			
			if (fwapp.websocket.debug == true) {
				console.log("Triggering on "+Object.keys(body)[0]+"  "+$._data( $(fwapp.websocket.wsConnection), 'events' ));
			}
			
			if (body.Ack) {
				console.log("Got Acknowledge");
			}

			if (body.UpdateHtml) {
				console.log("Updating HTML");
				// $("body").html(body.UpdateHtml.HTML);
				// $("body").html(fwapp.websocket.decodeHTML(body.UpdateHtml[0].HTML));

				$("body")
						.html(
								$(
										$
												.parseHTML(fwapp.websocket
														.decodeHTML(body.UpdateHtml[0].HTML)))
										.select("body"));
			}
			
			
			
		
		};
		//-- EOF Update message
		
		
	},
	// EOF make event connection




	/*
	 * Function: f(payload)
	 */
	onPushData :  function(name, f) {

		// Running
		if (fwapp.websocket.debug == true) {
			console.log("Registering handler for: "+name);
		}
		
		// Registering function
		$(fwapp.websocket.wsConnection).on("payload", function(event, pname, payload) {

			// Running
			if (fwapp.websocket.debug == true) {
				console.log("Got payload " + pname + " filtering for " + name);
			}
			
			// Call Function
			
				if (pname == name) {
					try {
						//f(payload[0]);
						//requestAnimationFrame(function(ts) {
							f(payload[0]);
						//});
					} finally {
						// Done
						//fwapp.websocket.sendMessageToServer("Done");
					}
					
				}
			
			

		});

	},

	sendMessageToServer : function(json, f) {
		
		var soap = {
		    Envelope: {
		    	Header:{
		    		
		    	},
		    	Body: {
		    		Done : {
		    			
		    		}
		    	}
		
		    }
		  };
		var str = JSON.stringify(soap);
		
		if (fwapp.websocket.debug == true) {
			console.log("Sending back: "+str);
		}
		
		fwapp.websocket.wsConnection.send(str);
	}
};