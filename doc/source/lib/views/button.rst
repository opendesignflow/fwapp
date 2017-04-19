
.. include:: ../../defs.rst


  
    
Actions
==============


.. contents::
    :local:

Action calling is an important part of the FWAPP framework, as it handles communication between the View and the Server upon specific events.
Those events are typically linked to a user interaction on the View, like clicking on a button, and should trigger a server-side function.

For example, if we look at a button definition with and action definition:
  
.. code-block:: scala
    :linenos:

    button("Click Me") {
        
        onClick {
            println("Hello World")
        }
        
    }
    


When the button is clicked, it should trigger the **onClick** function and print the Hello World line to the console.

The **onClick** utility is provided by the FWAppFramework View, and is backed by a general purpose action facility.
The logic detail behind the action calling is not of relevance here, but for general understanding, it will add the required event handlers
in the generated HTML, and use some javascript calls using `JQuery <https://jquery.com/>`_

The Action facility is used by all components requiring communication with the server, like buttons, input fields and so on

Buttons
=================


.. contents::
    :local:
    
The button library is quite simple and is provided by the |scala.package.framework|.FWappFrameworkView Trait.

Usage example:

.. code-block:: scala
    :linenos:

    
    class MyView extends org.odfi.wsb.fwapp.framework.FWappFrameworkView {
        
        
        viewContent {
            ...
            
            button("Click Me") {
                
                onClick {
                    
                    // Remote Code
                }
            }
            
            ...
        }
        
        
    
    }


button(text)
""""""""""""""""""""""""""""""""""""""""""""

A Simple Button

buttonClick(text) { REMOTE CLOSURE } 
""""""""""""""""""""""""""""""""""""""""""""
 
A button, whose provided closure is executed on click.
This variant is useful for simple buttons linked to a remote action
    
.. code-block:: scala
    :linenos:
   
    buttonClick("Click Me") {
                  
        // Remote Code
        
    }
    

buttonClickReload(text) { REMOTE CLOSURE }
""""""""""""""""""""""""""""""""""""""""""""

This button works like the **buttonClick**, but will trigger a page reload after the remote closure is done.

.. code-block:: scala
    :linenos:

    buttonClickReload("Click Me") {
                  
        // Remote Code
        // Page reloads at the end
        
    }

    
User Input
=================

The User input facility helps getting data from the user by using the "input" element from HTML

There are generally two ways to get user data:

1. Using an HTML Form and a submit button which will call the remote code.

This is the way a classical web application would work, and is useful to collect a set of data which are relevant together.
A typical example would be filling up a registration form for a user, where all the data has to be submitted at once and stay consistent.

.. .. odfi.code:: html
    
.. code-block:: html
    :linenos:
       
    <form>
        <input name="name"></input>
        <input name="lastname"></input>
        <submit></submit>
    </form>

2. Using the HTML input element alone to get a single data elements from the user, and use its value on the server side.

This method is not standard but very quick to implement, and quite useful to easily edit properties of an object or set configuration values
and so on.

Simple Binding
-------------------------

To use the simple data binding facility, the user can use the **bindValue** method which works like the button **onClick** method.
It binds to the HTML **onchanged** event, which is triggered when the user has changed the input value, then sends the data to an action
registered on the server.

The **bindValue** method needs a closure as input argument with the prototype ( T => Unit ), where T is a base datatype fetched from the user like: 

- String
- Long
- Double 
- Boolean etc...

Example for a String:

.. code-block:: scala
    :linenos:
    
    input {
        bindValue {
            
            text : String => 
                
                // Code
                println("User Entered: "+text)
        }
    }   

The **bindValue** takes care of configuring the input element for the user. For example, if a Boolean is requested, a checkbox will be created:

.. code-block:: scala
    :linenos:
    
    input {
        bindValue {
            
            ack : Boolean => 
                
                // Code
                println("User Entered: "+ack)
        }
    }





