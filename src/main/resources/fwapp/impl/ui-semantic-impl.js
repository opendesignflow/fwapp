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
// GUI Initialisation
//-------------------
$(function() {

	$('.popup-hover').popup({
		inline : true,
		hoverable : true

	});

	$('.popup-activate').popup({
		on : 'click'
	});

	$('.ui.dropdown').dropdown();

});

// Functions for main Framework
// ------------
fwapp.ui.impl = {

	currentModal : "",
		
	disable : function(target) {
		$(target).prop('disabled', true);
	},

	enable : function(target) {
		$(target).prop('disabled', false);
	},

	waitStart : function(target) {
		console.log("Wait start");
		var html = $('<div class="ui modal"><i class="close icon"></i><div class="header">Waiting for action to stop</div></div>')
		//var html = $.get()
		currentModal = $(html).modal({
			blurring : true,
			closable : false
		}).modal('show');
	},

	waitStop : function(target) {
		console.log("Wait stop");
		//$('.ui.modal').modal('close');
		
		/*var wait = $.Deferred();
		currentModal.onHidden = function() {
			console.log("Closed");
			wait.done();
		};*/
		currentModal.modal('close');
		//wait
		currentModal = "";
	}

};
