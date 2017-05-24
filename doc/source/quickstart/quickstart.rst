.. _gettingstarted:

############################
Getting Started
############################

.. contents::
    :local:

Application Structure Basics
===============================

FWapp is designed to be easily used and embedded in a Java/Scala software, and thus does not follow the classic Application Package and Container
scheme well know in the standard Java EE world (Jetty/Tomcat/Glassfish etc...).

To start with an application, you simply need to start as if you were writing a normal Scala application:

- First Create a Scala App
- Create class exending the main "Site" class
- Declare a port to listen to
- start the environment

FWAPP configuration first relies on the API, there are no magical configuration files to enforce. 

A Configuration interface might be implemented in the future, but the core concept is to just rely on code.

The Site class
==============================

The base interface to create an application in FWAPP is called Site, like in "Website".
The Site class is meant to be inherited, and needs one construtor argument defining the URL base path where to find the Site

For this tutorial, we will use a special Site class which is meant to be started as a Scala App.
This way we can keep the code compact and focus on the main features.

.. odfi.code::
    
    // Site MySite will be available at http://.../mysite/
    object MySite extends DefaulteSiteApp("/mysite") {
    
    }


A Site acts from there as a local container, where the user setup pages and features locally to the Site.

Deployment example on localhost
===================================

For the purpose of this example, we will deploy our Site on the localhost.

In most standard cases, the user will want to use simple plain HTTP and listen on all network interfaces.
The Site provides a utility function called "listen(port") which create an HTTP Connector on the provided port

.. odfi.code::
    
    // Site MySite will be available at http://localhost:8585/mysite/
    object MySite extends DefaulteSiteApp("/mysite") {
        
        listen(8585)
    }



Declaring URL features and views in a Site
===============================

Within a Site, the user can declare the URLs supported programmatically using a small language.
The core idea is the be able to read the code and see what URLs are accessible and how they are handled.

For example, if we want to add a request handler at **/foo/bar** : 


.. odfi.code::
    
    package gettingstarted

    import org.odfi.wsb.fwapp.DefaultSiteApp

    // Site MySite will be available at http://localhost:8585/mysite/
    object MySite extends DefaulteSiteApp("/mysite") {
        
        listen(8585)
        
        "/foo/bar" is {
            
            // Add more handlers under /foo/bar
        }
        
    }

Adding an Hello World Page (i.e View)
===========================

In standard user interfaces language, the Graphical interface provided to a user is called a View, and the process responsible
for creating the content of a View is called "render".

In FWAPP, a View represents a handler mapped to a URL, which will produce some HTML content.
Basically, a View is a class inheriting the main "FWAppView" class, and providing an implementation for the "render" process.
The render process should return some HTML which will be returned to the User's web browser.


To keep these first steps simple, we will declare a View by using the "InlineView" utility, that is without creating a separated class. 

All the Views in FWAPP support an HTML builder, through which the user can add HTML nodes to the output, in a manner very similar to standard HTML.


.. odfi.code::
    
    package gettingstarted

    import org.odfi.wsb.fwapp.DefaultSiteApp
    import org.odfi.wsb.fwapp.views.InlineView
    
    object MySite extends DefaulteSiteApp("/mysite") {
        
        listen(8585)
        
        // Note the replacement of "is" with "view"
        "/foo/bar" view new InlineView {
                
            // The code here is used to render the view.
            // Produce HTML here
            html {
                head {
                
                }
                
                body {
                    h1("Hello World") {
                    
                    }
                }
            
            }
        }
  
    }


Starting the Site
----------------------

Now it is time to test our Site.
Before starting the App, we need to tell the site to actually start and listen on the output interface. 

This step is explicit for two reasons: 

- We are just writing a simple Standalone Application, so the network listeners have to be started somewhere
- The Site Class is meant to be reusable, so we always want to leave the Start/Stop responsibility to the main Application.

.. odfi.code::
    
    package gettingstarted

    import org.odfi.wsb.fwapp.DefaultSiteApp
    import org.odfi.wsb.fwapp.views.InlineView

    object MySite extends DefaulteSiteApp("/mysite") {
        
        listen(8585)
        
        "/foo/bar" view new InlineView {
                
            // The code here is used to render the view.
            // Produce HTML here
            html {
                head {
                
                }
                
                body {
                    h1("Hello World") {
                    
                    }
                }
            
            }
        }
        
        // Start here :-)
        start
        
    }


Now just run the MySite as any other Scala App. 

Looking at the Console output, you should see a line like:

    Website gettingstarted.MySite available at: http://localhost:8585/mysite
    
Now navigate to http://localhost:8585/mysite/foo/bar, you should see a Hello World Title.

First Site: what you actually get
----------------------------

The FWAPP library is meant to be lightweight, so the implementation of the various components tries to always fulfil their task with little default behaviour.
The Default Behaviour of the Site can usually be covered by correctly configuring the main application.

This behavior should enable a step by step precise completion of the Application's behavior, but also let framework developers 
easily create a custom site behaviour on top of FWAPP.

First example: The Page not Found Handler
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The be more precise, let's have a look at what happens if you try to navigate to a non handled URL like:

    http://localhost:8585/mysite/

In the site definition, we haven't specified anything to handle this "/" path, so the HTTP Request will go through the Site non-handled....
and the browser will hang and wait.

For a simple application where the user knows what he's doing, it might not matter, but answering "404 Page not found" for requests which have no
handler seems like a good idea anyway.

The Site provides a utility to add a default 404 handler, let's add the code before starting the application:

.. odfi.code:: scala

    // Added 404 Handler
    this.add404Intermediary
        
    // Start here :-)
    start
    
the method "add404Intermediary" adds an HTTP Request handler which checks the state of the request, and generates a default 404 Not found answer
if the request has not been answered.

Stop the application manually from your IDE, restart it, and enter the URL in your browser:

    http://localhost:8585/mysite/


You should see a page displaying a text message:

    Not Found: /mysite/







