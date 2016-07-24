# A RESTful listener service for FollowApp's interaction with Exotel

## Overview
A simple service exposed to Exotel that responds to GET/POST requests

There are three service endpoints that this service exposes:

1. GET: Generate a response based on the user's input on the "Gather" applet of the Exotel workflow (webapi/exotel/userinput)
2. GET: Generate and return an audio file based on query parameters passed. (webapi/exotel/audioresponse)
3. POST: Call the user, specified via a JSON object. (webapi/exotel/call)

## Build using Maven

    mvn clean package

A .war file will be generated, which can be deployed to the container of your choice.

## Deployment on heroku
    
    heroku deploy:war --war exotelListener.war --app <your-app_name>

## Eclipse
1. Download M2E, and import the project into Eclipse directly using "Import existing Maven project"
2. You need to define two properties files in src/main/resources, exotel.properties containing the sid and token, and audio.properties, containing the base path to look for audio files.
3. You also need to ensure that the output folder for src/main/resources is set to WEB-INF/classes, or the properties files will not be found. Ideally, this should be done via Maven, but I don't know how.

## Using POSTMAN
To test these services, I use the Chrome plugin POSTMAN
Some example URL's

    GET: http://localhost:8080/exotelListener/webapi/exotel/audioresponse
    HEAD: http://localhost:8080/exotelListener/webapi/exotel/audioresponse
    GET: http://localhost:8080/exotelListener/webapi/exotel/userinput?digits=2
    POST: http://localhost:8080/exotelListener/webapi/exotel/call

## Documentation for Exotel API

1. http://support.exotel.in/support/solutions/articles/48278-outbound-call-to-connect-a-customer-to-an-app 
2. http://support.exotel.in/support/solutions/articles/48283-working-with-passthru-applet
3. http://support.exotel.in/support/solutions/articles/48285-greeting-using-dynamic-text-or-audio-from-url

## TODO List

1. Integrate with the scheduling service, that will trigger the call function

