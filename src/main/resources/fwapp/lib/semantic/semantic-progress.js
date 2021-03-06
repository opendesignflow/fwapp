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

	// fwapp.websocket.debug = true;
	fwapp.websocket.makeEventConnection();
	fwapp.websocket.onPushData("SemanticProgressUpdate", function(payload) {
		
		console.log("Update Progress for: "+payload.TargetID+", to "+payload.Percent);
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
