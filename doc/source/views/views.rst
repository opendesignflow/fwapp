

    
#####
Views
#####
 
.. contents:: Table of Contents
    :local:
    
.. toctree::
    :hidden:
     
    htmlbuilder/htmlbuilder
    preferences


As introduced in  :ref:`gettingstarted`, the actual Pages presented to the user are called "Views", as typically named in user interfaces
following the concepts of `Model View Controller structure (MVC) <https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller>`_

In the context of a Web Application, a View will be an HTML content returned to the browser.
The FWAPP framework defines a base class which needs to be shared by all Views, and a set of Utility View Traits which add functionalities
to the HTML builder.

The base API provided by a View definition allows a quick and easy building of HTML.
Unlike some script based frameworks like PHP, the application logic is not embedded in some HTML, but is just implemented by a View Class.
Whenever a View is request by the browser, a method called "render" is called, and the View must return a tree of HTML objects, which are 
later converted to a textual HTML representation and send back to the browser.

During the render process, the View class can generate the HTML tree according to the application state, available data etc... simply by
using a standard coding scheme.
  
 
Defining a View Class
=====================

Here we will modify the example from :ref:`gettingstarted` to implement the Hello World view using a plain class.
Make sure you have understood the example, and are ready to modify it.

The first step would be to declare a new Scala Class, and make it inherit the FWAppView trait

.. odfi.code::
    
    package example.views

    import org.odfi.wsb.fwapp.views.FWappView
    
    class HelloWorldView  extends FWappView {
      
    }

Now let'create an application just like in the :ref:`gettingstarted` section :

.. odfi.code::
    
    package example.views

    import org.odfi.wsb.fwapp.DefaultSiteApp
    
    object SimpleViewClassApp extends DefaultSiteApp("/mysite") {
      
      this.listen(8585)
      start
      
      // map the Hello World to "/" by giving the class name
      "/" view classOf[HelloWorldView]
    }

Here you can notice that we have mapped to view to the Class Definition using **classOf[HelloWordView]**, and not to an instance
of the **HelloWordView** class. The Request handler is responsible for building new instances of the Class as required.

Now Navigate to

    http://localhost:8585/mysite
    
You will see and error or a blank page. This makes sense because we have declared our View with no content.

To add content to the view, we have to defined a function called during the render process. This way, each time the view is rendered,
the function will be called and the HTML output regenerated.

This is done by using the  **viewContent( content : => HTMLNode)** method. 
This method takes a closure as input argument, which in turn returns an HTML node to be used as output.

To add a simple HTML structure to our View, we can update our view like this:


.. odfi.code::
    
    package example.views

    import org.odfi.wsb.fwapp.views.FWappView
    
    class HelloWorldView  extends FWappView {
    
        viewContent {
            
            // Well know HTML tree with head and body
            html {
                head {
                
                }
                
                body {
                    h1("Hello") {
                    
                    }
                    h2("World") {
                    
                    }
                }
            }
            // Top html node returned
        }
        // EOF View content
      
    }
    

Now restart the application and navigate again to:

    http://localhost:8585/mysite
    
You should see two titles



Simple Templating using inheritance
===================================
