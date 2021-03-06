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
fwapp.websocket = {

	debug : false,
	wsConnection : null,

	// Event Connection
	// ---------------------------
	makeEventConnection : function() {

		if (fwapp.websocket.wsConnection == null) {

			var targetURL = "ws://" + window.location.hostname
					+ (window.location.port ? ':' + window.location.port : '')
					+ window.location.pathname + "/websocket";

			console.log("Making Websocket connection to: " + targetURL);
			fwapp.websocket.wsConnection = new WebSocket(targetURL, [ 'soap' ]);

			// Log messages from the server
			// ---------------
			fwapp.websocket.wsConnection.onopen = function(event) {
				if (fwapp.websocket.debug == true) {
					console.log('Connection Done');

				}
			};

			// Message Handler which maps messages to application's
			// message-specific handler
			// --------------------
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
					console.log("Triggering on "
							+ Object.keys(body)[0]
							+ "  "
							+ $
									._data($(fwapp.websocket.wsConnection),
											'events'));
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
			// -- EOF Update message

		}
	},
	// EOF make event connection

	/*
	 * Function: f(payload)
	 */
	onPushData : function(name, f) {

		// Running
		if (fwapp.websocket.debug == true) {
			console.log("Registering handler for: " + name);
		}

		// Registering function
		$(fwapp.websocket.wsConnection).on(
				"payload",
				function(event, pname, payload) {

					// Running
					if (fwapp.websocket.debug == true) {
						console.log("Got payload " + pname + " filtering for "
								+ name);
					}

					// Call Function

					if (pname == name) {
						try {
							// f(payload[0]);
							// requestAnimationFrame(function(ts) {
							f(payload[0]);
							// });
						} finally {
							// Done
							fwapp.websocket.sendMessageToServer("Done");
						}

					}

				});

	},

	onPushDataAnimation : function(name, f) {

		// Running
		if (fwapp.websocket.debug == true) {
			console.log("Registering handler for: " + name);
		}

		// Registering function
		$(fwapp.websocket.wsConnection).on(
				"payload",
				function(event, pname, payload) {

					// Running
					if (fwapp.websocket.debug == true) {
						console.log("Got payload " + pname + " filtering for "
								+ name);
					}

					// Call Function

					// -- Create Defered object to wait on function done
					var waitObject = $.Deferred();

					// -- Prepare callback
					waitObject.done(function() {

						// Done
						if (fwapp.websocket.debug == true) {
							console.log("Delayed finish resolved");
						}
						fwapp.websocket.sendMessageToServer("Done");
					});

					// Execute on Animation Frame

					if (pname == name) {
						try {
							// f(payload[0]);
							requestAnimationFrame(function(ts) {

								try {
									f(payload[0]);
								} finally {
									// Make sure callback is always resolved to
									// avoid deadlocks
									waitObject.resolve();
								}

							});
						} catch (err) {
							// Make sure callback is always resolved to avoid
							// deadlocks
							waitObject.resolve();
						}

					}

				});

	},

	/**
	 * No Feedback send to server, the user has to trigger the processing
	 * finished
	 */
	onPushDataDelayedFinish : function(name, f) {

		// Running
		if (fwapp.websocket.debug == true) {
			console.log("Registering handler for: " + name);
		}

		// Registering function
		$(fwapp.websocket.wsConnection)
				.on(
						"payload",
						function(event, pname, payload) {

							// Running
							if (fwapp.websocket.debug == true) {
								console.log("Got payload " + pname
										+ " filtering for " + name);
							}

							// Call Function

							if (pname == name) {
								try {

									// -- Create Defered object
									var waitObject = $.Deferred();

									// -- Prepare callback
									waitObject.done(function() {

										// Done
										// Done
										if (fwapp.websocket.debug == true) {
											console.log("Delayed finish resolved");
										}
										fwapp.websocket
												.sendMessageToServer("Done");
									});

									// -- Execute
									if (fwapp.websocket.debug == true) {
										console
												.log("Running Message handler with delayed finish");
									}
									// requestAnimationFrame(function(ts) {
									f(waitObject, payload[0]);
									// });

								} catch (err) {
									
									// Done
									console.log("Error during message processing: "+err);
									fwapp.websocket.sendMessageToServer("Done");
								}

							}

						});

	},

	sendMessageToServer : function(json, f) {

		var soap = {
			Envelope : {
				Header : {

				},
				Body : {
					Done : {

					}
				}

			}
		};
		var str = JSON.stringify(soap);

		if (fwapp.websocket.debug == true) {
			console.log("Sending back: " + str);
		}

		fwapp.websocket.wsConnection.send(str);
	}
};
