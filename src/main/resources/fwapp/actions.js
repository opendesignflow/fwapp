/*-
 * #%L
 * FWAPP Framework
 * %%
 * Copyright (C) 2016 - 2017 Open Design Flow
 * %%
 * This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
fwapp.actions = {

	callViewAction : function(actionName,sendData,reload) {
		
		//-- Add action to send data
		sendData._action = actionName;
		sendData._format = "json";
		sendData._render = "none";
		
		//-- Create Path
		var path = window.location;
		
		//-- Send
		var deferred = $.get(path,sendData)
		deferred.always(function() {
			if(reload) {
				window.location = window.location.href;
				//location.reload();
			}
		});
		
	},
		
	callAction : function(sender, path, sendData) {
		console.log("Call Action");
		

		
		
		// Request parameters
		//---------------
		var responseFormat = "text";
		sendData = sendData || {};
		
		// Look for form
		//-----------
		//FIXME
		if ($(sender).parents('form').length) {
			
			var parentForm = $(sender).parents('form');
			
			console.info("Inside form: "+parentForm.serialize())
			$.each(parentForm.serializeArray(),function(i, field) {
				sendData[field.name] = field.value;
			});
			//path = path+"&"+parentForm.serialize()
			
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
		
		
		
		
		// Make JSON if no reload
		//-----------
		if ($(sender).attr("reload"))  {
			reload = true;
		} else {
			reload = false;
		}
			
		if (!reload) {
			sendData._format = "json";
			responseFormat = "json";
		}
		
		// Add Send Data to path
		//--------------------------
		/*$(Object.keys(sendData)).each(function(i,e) {
			
			console.log("Found send data: "+e);
			path = path+"&"+e+"="+encodeURI(sendData[e]);
			
		});*/
		
		
		// Disable
		// ------------
		fwapp.ui.disable(sender);

		// Wait?
		//--------
		var doWait = $(sender).attr("wait");
		if (doWait) {
			fwapp.ui.waitStart(sender);
		}
		
		
		
		// Send
		//-------------
		console.info("Running action, sending remote request for " + path);
		console.info("Send data is: " + JSON.stringify(sendData));
		
		
		var deffered = $.post(path,sendData,jQuery.noop,responseFormat);
		deffered.done(function(data) {

			console.log("Done..." + JSON.stringify(data));
			//console.log("Done..." + data.responseText);

			fwapp.ui.enable(sender);

			

			// Handle reload/wait etc
			// -------------
			if (doWait) {
				fwapp.ui.waitStop(sender);
			}
			console.log("Reload..."+$(sender).attr("reload"));
			if ($(sender).attr("reload")) {

				window.location = window.location.href;
				//location.reload();
			}
			
			// Get Action Result
			// --------
			fwapp.ui.resultFor(sender, data);
			
			return false;
		});
		deffered.fail(function(data) {

			if (doWait) {
				fwapp.ui.waitStop(sender);
			}
			
			fwapp.ui.enable(sender);
			
			console.log("Error in action :" + data.responseText);
			var error = $.parseJSON(data.responseText);

			fwapp.ui.errorFor(sender, error);
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
