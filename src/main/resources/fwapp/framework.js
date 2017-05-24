var fwapp = {
		
	lib : {
		
	},
	/**
	 * Decodes the URL Encoded HTML back to normal html that can be loaded in
	 * the page
	 * 
	 * @param content
	 */
	decodeHTML : function(uriHTML) {
		return decodeURI(uriHTML).replace(/\++/g, " ").replace(/(%2F)+/g, "/")
				.replace(/(%3A)+/g, ":").replace(/(%3B)+/g, ";").replace(
						/(%3D)+/g, "=").replace(/(%23)+/g, "#").replace(
						/(%40)+/g, "@").replace(/(%2C)+/g, ",").replace(
						/(%2B)+/g, "+").replace(/(%28)+/g, "(").replace(
						/(%29)+/g, ")")
	}

}