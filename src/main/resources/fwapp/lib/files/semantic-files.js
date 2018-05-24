$(function() {
	
	console.log("Setting UP Upload");
	$('.fileupload').fileupload({
        dataType: 'json',
        add: function (basenode, data) {
        	
        	console.log("Added File");
        	
        	var file = data.files[0];
        	
        	var contentBlock = $(this).parent().find(".files-content");
        	// contentBlock.append("<div>"+file.name+"</div>");
        	var items = contentBlock.find(".ui.divided.items");
        	
    		// contentBlock.append(items);
        	
        	// Activate Upload button
        	// console.log("Upload button: "+$(this).parent().find(".ui.buttons
			// .upload-button"));
        	$(this).parent().find(".upload-button").removeClass("disabled");
        	
        	var header = $("<div>",{class:"header",text: file.name});
			
			var pbar = $('<div class="ui progress"><div class="bar"><div class="progress"></div></div><div class="label">Uploading File</div></div>');
			var description = $("<div>",{class:"description"});
			description.append(pbar);
			
			$(pbar).progress();
			
			var content =  $("<div>", {class:'content'});
			content.append(header);
			content.append(description);
			
			var item =  $("<div>", {class:'item'});
			item.append(content);
			items.append(item);
        	
			data.context = pbar;
        	$(item).data("context",data);
        	
           /*
			 * data.context = $('<button/>').text('Upload')
			 * .appendTo($("#files").parent().find(".files-content"))
			 * .click(function () { data.context = $('<p/>').text('Uploading...').replaceAll($(this));
			 * data.submit(); });
			 */
        },
        progress : function(e,data) {
        	console.log("Progress for: "+data.context);
        	$(data.context).progress({percent: parseInt(data.loaded / data.total * 100, 10)});
        },
        done: function (e, data) {
            data.context.text('Upload finished.');
        }
    });
	
	
});

fwapp.lib.files = {
		
		uploadAll : function(button) {
			
			console.log("Uploading All");
			
			var form = $(button).closest("form");
			var fileInput = $(form).find("input:first");
			
			var items = $(button).parent().parent().find(".files-content .ui.divided.items .item");
			var i = 0;
			$(items).each(function(i,item) {
				
				
				var data = $(item).data("context");
				
				console.log("Uploading: "+data.files[0].name);
				
				data.submit();
				i++;
			});
			
			// Activate validation
			 $(button).parent().find(".validate-button").removeClass("disabled");
			
			// Trigger Uploaded form
			$(fileInput).prop("disabled",true);
			$(button).closest("form").trigger("uploaded");
			// $(button).closest("form").submit();
			$(fileInput).prop("enabled",true);
			
		},
		
		submitUploadForm : function(button) {
			
			console.log("Validate Form");
			
			var form = $(button).closest("form");
			var fileInput = $(form).find("input:first");
			
			// Submit Form
			$(fileInput).prop("disabled",true);
			$(button).closest("form").submit();
			$(fileInput).prop("enabled",true);
			
		},
		
	updateFilesList : function(basenode,files) {
		console.log("Updating Files List");
		
		/*
		 * var contentBlock = $(basenode).find(".files-content"); // Empty
		 * Content // ----------- contentBlock.empty();
		 */
		
		// Upload Button
		// -------------------
		/*
		 * var upButton=$("<div>",{class:"ui primary button",text:"Upload
		 * all"}) upButton.click(function(button) {
		 * fwapp.lib.files.uploadAll(files); }); contentBlock.append(upButton); //
		 * Create elements // ----------------- var items = $("<div class='ui
		 * divided items'></div>"); contentBlock.append(items);
		 * $(files).each(function(i,file) {
		 * 
		 * 
		 * 
		 * var header = $("<div>",{class:"header",text: file.name});
		 * 
		 * var pbar = $('<div class="ui progress"><div class="bar"><div
		 * class="progress"></div></div><div class="label">Uploading File</div></div>');
		 * var description = $("<div>",{class:"description"});
		 * description.append(pbar);
		 * 
		 * var content = $("<div>", {class:'content'}); content.append(header);
		 * content.append(description);
		 * 
		 * var item = $("<div>", {class:'item'}); item.append(content);
		 * items.append(item);
		 * 
		 * });
		 */
		
	}
		
}