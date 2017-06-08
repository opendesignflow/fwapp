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
