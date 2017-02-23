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

	$('.ui.dropdown.item').dropdown();

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