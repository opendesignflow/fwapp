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
    
    // Confirmation
    //-----------------
     confirm : function(target,message) {
        return fwapp.ui.impl.confirm(target,message);
     },

	// Report Error and results
	// --------------------
	
	hideResultPlaceHolders : function(baseElement) {
		$(baseElement).find(".fwapp-action-result-placeholder").hide(100);
	},
	
	resultFor : function(element, actionResult) {
		
		//-- Look for placeholder container and hide all 
		var placeHolderContainer = $(element).closest("div:has(div.fwapp-action-result-placeholder)");
		fwapp.ui.hideResultPlaceHolders(placeHolderContainer);
		

		console.log("Parents search: "+placeHolderContainer.length);
		
		//-- Look for results placeholder
		var resultsPlaceHolder =  $(placeHolderContainer).children(".fwapp-action-results:first");
		
		console.log("Results ->"+resultsPlaceHolder);
		
		if (resultsPlaceHolder)  {
			
			$(resultsPlaceHolder).show(100);
			
			//-- Update result
			//---------
			if (actionResult.ActionResult) {
				
				//-- Create a div per result
				//-- Support text output for now
				actionResult.ActionResult.Result.forEach(function(result) {
					console.log("Adding result..."+result.Text+" *> "+result.Hint);
					if (result.Text) {
						$(resultsPlaceHolder).html($("<div>"+result.Text+"</div>"));
					}	
				});
			}
			
			
		}
	},
	
	errorFor : function(element, error) {

		// console.info("Error Reason: "+faultJsonString[0]);
		//var fault = $.parseJSON("{" + faultJsonString + "}").Fault;
		
		// -- Look for error in parent neighbor
		//var allErrorBlocks = $(".fwapp-action-error")
		//var errorBlockNeighbor = $(element).parent().find(".fwapp-action-error:first");
		//console.log("search on: "+$(element).html())
		var errorBlockContainer = $(element).closest("div:has(div.fwapp-action-error)");
		//console.log("Parents search: "+errorBlockNeighborParents.length);
		//console.log("Parents search 2: "+$(element).parents().length);
		//var errorBlockNeighborContainer = $(element).parents().find(function(parent){  if ($(parent).children(".fwapp-action-error").length>0) { return true; } else { return false;} })
		
		if (errorBlockContainer.length>0)  {
			
			var errorBlockNeighbor =  $(errorBlockContainer).children(".fwapp-action-error:first");
			
			console.log("Found Error Container: " + errorBlockNeighbor+ "-> ");
			
			//-- Hide all
			//----------------
			$(errorBlockContainer).find(".fwapp-action-result-placeholder").hide(100);
			
			/*$(errorBlockContainer).find(".fwapp-action-info").forEach(function(e) {
				console.log("Hidding..."+$(e).html());
				$(e).hide();
				$(e).css("display","none");
			});*/
			
			/*var infoBlock = $(errorBlockContainer).find(".fwapp-action-info:first")
			if (infoBlock) {
				$(infoBlock).hide();
			}*/
			//-- Display Error
			//-------------
			if (error.ActionResult) {
				
				
				// -- Get text
				var text = fwapp.decodeHTML(error.ActionResult.Error[0].Message);
				
				console.log("Got Action Result: "+text);
				
				$(errorBlockNeighbor).html(text);
				//$(errorBlockNeighbor).css("display", "block");
				$(errorBlockNeighbor).show(200);
				
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
