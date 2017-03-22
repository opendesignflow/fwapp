fwapp.actions = {

	callAction : function(sender, path, sendData) {
		console.log("Call Action");
		

		sendData = sendData || {};
		
		
		
		
		
		/*
		 * $(data).each(function(key,val) {
		 * 
		 * var key = Object.keys(val)[0] var valScript = Object.values(val)[0]
		 * console.log("Function name: "+valScript);
		 * 
		 * var val = window["'"+valScript+"'"](sender); //= eval(valScript);
		 * 
		 * console.log("Sending data: "+key+", value: "+val); });
		 */
		
		// Look for form
		//-----------
		if ($(sender).parents('form').length) {
			
			var parentForm = $(sender).parents('form');
			
			console.info("Inside form: "+parentForm.serialize())
			path = path+"&"+parentForm.serialize()
			//sendData.form = parentForm.serializeArray();
		}
		
		// Look for extra data on the sender
		//-------------------
		console.log("Known data: "+$(sender).data("value-text"));
		for (var key in $(sender).data()) {
			console.log("Found data: "+key+ "-> "+$(sender).data(key));
		}
		var keys = Object.keys($(sender).data());
		var valueKeys = keys.filter(function(key){
		    return key.match(/value[\w]+/)
		  
		});
		valueKeys.forEach(function(key){
			
			var parameterName = key.replace(/^value/, '').toLowerCase();
		    console.log("Found value data: "+parameterName);
		    
		    //-- Evaluate
		    var evalValue = $(sender).data("value-"+parameterName);
		    console.log("script for data: "+evalValue);
		    var resultValue = eval(evalValue);
		    console.log("Result value: "+resultValue);
		    
		    sendData[parameterName] = resultValue;
		    
		});
		
		
		console.info("Running action, sending remote request for " + path);
		console.info("Send data is: " + JSON.stringify(sendData));
		
		
		// Add Send Data to path
		//--------------------------
		$(Object.keys(sendData)).each(function(i,e) {
			
			console.log("Found send data: "+e);
			path = path+"&"+e+"="+encodeURI(sendData[e]);
			
		});
		
		
		// Disable
		// ------------
		fwapp.ui.disable(sender);

		// Wait?
		//--------
		var doWait = $(sender).attr("wait");
		if (doWait) {
			fwapp.ui.waitStart(sender);
		}
		
		var deffered = $.get(path);
		deffered.done(function(data) {

			console.log("Done..." + data.responseText);

			fwapp.ui.enable(sender);

			// Get Action Result
			// --------

			// Handle reload/wait etc
			// -------------
			if (doWait) {
				fwapp.ui.waitStop(sender);
			}
			if ($(sender).attr("reload")) {

				location.reload();
			}
			/*
			 * if (data != "OK") { console.log("Reloading Page")
			 * $("body").html(data); }
			 * 
			 * if ($(sender).attr("reload")) {
			 * 
			 * location.reload(); }
			 */

		});
		deffered.fail(function(data) {

			if (doWait) {
				fwapp.ui.waitStop(sender);
			}
			
			fwapp.ui.enable(sender);
			
			console.log("Error in action :" + data.responseText);

			fwapp.ui.errorFor(sender, data.responseText);
			/*
			 * if (localWeb.faultFor(sender, data.responseText)==false) {
			 * $(sender).html(data.responseText); }
			 */

		});
	},

	bindValue : function(element, urlPath, sendData) {

		console.info("Value changed sending remote request for " + urlPath);

		// -- Prepare send data
		if (!sendData) {
			sendData = {
				_format : "json"
			};
		}

		// -- Get value
		var elt = $(element);
		if (elt.attr("type") == "checkbox") {
			var value = elt.is(":checked");
		} else {
			var value = elt.val();
		}
		//sendData.value = encodeURIComponent(value)

		// -- Send Value
		var name = $(element).attr("name");
		sendData[name] = value;

		console.info("Sending " + value + " as name " + name);

		fwapp.actions.callAction(element, urlPath, sendData);

		/*
		 * var deffered = $.get(actionPath + "?format=json&" + name + "=" +
		 * encodeURIComponent(value)); deffered.done(function(data) {
		 * console.log("Done 2...");
		 *  // -- Hide error var errorNode = localWeb.errorNode(element);
		 * console.log("Error node: " + errorNode); if (errorNode) {
		 * $(errorNode).css("display", "none"); }
		 *  // -- reRender if necessary console.log("Reload node: " +
		 * $(element).attr("reload")); if ($(element).attr("reload") &&
		 * $(element).attr("reload") == true) { location.reload(); } else if
		 * (data != "OK") { console.log("Reloading Page") $("body").html(data); }
		 * 
		 * }); deffered.fail(function(data) { console.log("Error in value
		 * binding"); localWeb.faultFor(element, data.responseText); });
		 */
	}

}