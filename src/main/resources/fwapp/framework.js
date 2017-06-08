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
