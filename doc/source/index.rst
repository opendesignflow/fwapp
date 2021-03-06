.. Dev Tools Doc documentation master file, created by
   sphinx-quickstart on Mon Jan  9 13:55:08 2017.
   You can adapt this file completely to your liking, but it should at least
   contain the root `toctree` directive.


 

#########################################
Welcome to FWapp Documentation  !
#########################################
 
.. contents::
    :local:
    
    
.. Use the Toc tree to list pages
.. toctree::
    :hidden:
 
   
    quickstart/quickstart
    views/views
    lib/lib
   
  

This library provides a fullstack lightweight Web Application Framework for Scala.
It is **not** based on Standard Java EE Servlet environment and Servelet Containers like Jetty or Tomcat.

FWApp provides its functionalities based on the following libraries:

- Webservice Broker (WSB-CORE) which passes Messages Through a user defined tree of functions
- Webservice Broker Web app (WSB-WEBAPP) which provides low level HTTP Message implementation for WSB-CORE
- The Indesign Library to manage components lifecycle (not needed to fully understand the library)
- FWApp provides some higher level functions to easily create a Website and Web page views served using the WSB-CORE and WSB-WEBAPP functionalities.





Quickstart
--------------------
  
FWapp is accessible using Maven and the ODFI maven repositories::

    <groupId>org.odfi.wsb.fwapp</groupId>
    <artifactId>fwapp-core</artifactId>
    <version>0.0.1-SNAPSHOT</version>

The start a simple website, you can use the `DefaultSiteApp` class, which can be started like an normal scala App, and has a default asset resolver setup to server static files
 
.. odfi.code:: scala

    package myapplication
    
    import org.odfi.wsb.fwapp.DefaultSiteApp
    
    // My site can be started as an application because of the "App" extension
    // Navigate to http://localhost:8082/mysite after starting
    object MySite extends DefaultSiteApp("/mysite") {
        
        //-- Listen to a port
        this.listen(8082)
        
        
         //-- Define the main view
         //-- The InlineView constructor is used to define the HTML content
        "/" view new InlineView {
            
            html {
                head {
                
                }
                
                body {
                    h1("Hello World") {
                    
                    }
                }
            }
            
        }
        
        //-- Start, always at the end
        start
    }

Now you can run this main, navigate to http://localhost:8082/mysite and see the Hello World displaying

   

Details
^^^^^^^^^^^^^^^^^^^^^^^^
   
This documentation is written with  Sphinx: http://www.sphinx-doc.org/en/stable/contents.html 




Indices and tables
^^^^^^^^^^^^^^^^^^^^^^^^

* :ref:`genindex`
* :ref:`modindex`
* :ref:`search`
