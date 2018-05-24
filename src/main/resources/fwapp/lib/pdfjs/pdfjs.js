fwapp.pdfjs = {
			
	
	
	/*
	 * var canvas = document.getElementById('lecturepdf'); if (canvas) {
	 * 
	 * var url = "http://localhost:8585"+$(canvas).attr("data-url");
	 * 
	 * console.log("Loading pdf from "+url);
	 * 
	 * 
	 * PDFJS.getDocument(url).then(function(pdf) {
	 * 
	 * console.log("Got PDF..."); pdfDoc = pdf; changeToPage(1);
	 * 
	 * }); }
	 */
	
	
		
}
// EOF Namespace



fwapp.pdfjs.init = function() {
	
	
	// Init
	// -----------------
	console.log("Init pdf js....");
	PDFJS.imageResourcesPath = fwapp.pdfjs.appPath+'/assets/fwapp/external/pdfjs/1.9.426/web/images/';
	PDFJS.workerSrc = fwapp.pdfjs.appPath+'assets/fwapp/external/pdfjs/1.9.426/build/pdf.worker.js';
	PDFJS.cMapUrl = fwapp.pdfjs.appPath+'assets/fwapp/external/pdfjs/1.9.426/web/cmaps/';
	PDFJS.cMapPacked = true;
			
	// -- get Canvas
	$("canvas[id*='pdfjs-'").each(function(i,canvas) {
		
		console.log("Found PDF Canvas..."+$(canvas).attr("id"));
		
		if ($(canvas).attr("data-url"))  {
			
			var dataURL = $(canvas).attr("data-url");
			var parsedURL = fwapp.decodeHTML(dataURL);
			
			console.log("Found PDF Canvas..."+parsedURL);
			
			//"http://localhost:8585"+
			//var url = "http://"+location.host+$(canvas).attr("data-url");
			var url = "http://"+location.host+parsedURL;
			PDFJS.getDocument(url).then(function(pdf) {
				
				fwapp.pdfjs.pdfDoc[$(canvas).attr("id")] = pdf; 
				fwapp.pdfjs.changeToPage($(canvas).attr("id"),$(canvas).attr("page"));
				
				
				
			});
		}
		 
		
	});
	
	//-- Remote
	/*fwapp.websocket.onPushData("PDFJSPageForward",function(payload) {
		
		console.log("Next page for: "+payload.targetId);
		fwapp.pdfjs.nextPage(payload.targetId)
		
		
	});*/
}

fwapp.pdfjs.loadPDF = function(canvasId,url) {

	var realId = "pdfjs-"+canvasId;
	var canvas = $("#"+realId);
	//console.log("Starting Loading PDF...: ");
	if (canvas) {
		var targetUrl = "http://"+location.host+url;
		console.log("Loading PDF from: "+targetUrl);
		PDFJS.getDocument(targetUrl).then(function(pdf) {
			
			//console.log("Loaded PDF from: "+targetUrl);
			fwapp.pdfjs.pdfDoc[$(canvas).attr("id")] = pdf; 
			fwapp.pdfjs.changeToPage($(canvas).attr("id"),1);
			
		});
		
	} else {
		console.log("PDF Canvas not found");
	}
	
	
	
}


// Variables
//-----------

fwapp.pdfjs.pdfDoc = [];
/*fwapp.pdfjs.pdfPage
var  = 0;*/

// Functions
// -----------------------

fwapp.pdfjs.changeToPage =  function(id,p) {
	
	p = parseInt(p);
	
	//-- Get Doc
	var doc = fwapp.pdfjs.pdfDoc[id];
	console.log("Changing page of: "+id+" to "+p+ " with available "+doc.numPages);
	
	
	if (p<1 ) {
		return;
	}
	if (p>doc.numPages) {
		p = 1;
	}
	
	
	// Change Local Page
	// --------------------
	doc.getPage(p).then(function(page) {
		
		// Update Page
		//----------
		var pdfPage = p;
		console.log("PDF Page...");
		var canvas = document.getElementById(id);
		$(canvas).attr("page",p);
		var context = canvas.getContext('2d');
		
		// Remote Update
		//-------------
		var viewport = page.getViewport(1);
		//if (!$(canvas).data("scaled")) {
		
			//-- Desired Width
			var desiredWidth = $(canvas).parent().width();
			var desiredHeight = $(canvas).parent().height();
			var scale = desiredWidth / viewport.width; 
			var scaledViewport = page.getViewport(scale);
			
			//-- If height is higher than desired and it was scaled before, reverse scaling
			if ($(canvas).data("scaled") && scaledViewport.height>desiredHeight) {
				console.log("Reversing scaling to height");
				scale = desiredHeight / viewport.height; 
				scaledViewport = page.getViewport(scale);
			}
			
			 canvas.height = scaledViewport.height;
			 canvas.width = scaledViewport.width;
			 
			 
			 /* var scale = 1.0;
			var viewport = page.getViewport(scale);
			var context = canvas.getContext('2d');
			canvas.height = viewport.height;
			canvas.width = viewport.width;*/
			 $(canvas).data("scaled",true);
		//}
		
		
		var renderContext = {
		  canvasContext: context,
		  viewport: scaledViewport
		};
		page.render(renderContext);
		
		$(canvas).trigger("pdfjs.pageLoaded");
		
	});
	
}

fwapp.pdfjs.nextPage =  function(id) {
	
	// -- Get Current Page
	fwapp.pdfjs.changeToPage("pdfjs-"+id,parseInt($("#pdfjs-"+id).attr("page"))+1);
}
fwapp.pdfjs.previousPage =  function(id) {
	
	// -- Get Current Page
	fwapp.pdfjs.changeToPage("pdfjs-"+id,parseInt($("#pdfjs-"+id).attr("page"))-1);
}

	

$(function() {
	
	fwapp.pdfjs.init();
	
});