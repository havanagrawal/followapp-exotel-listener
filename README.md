# A RESTful listener service for FollowApp

## Overview
A simple service that responds to GET requests

There are two service endpoints that this service exposes:
1. Generate a response based on the user's input on the "Gather" applet of the Exotel workflow
2. Generate and return an audio file based on query parameters passed.

## Build using Maven

    mvn clean package

A .war file will be generated, which can be deployed to the container of your choice. (I use Tomcat 9)

## Using POSTMAN
To test these services, I use the Chrome plugin POSTMAN
Some example URL's
    GET: http://localhost:8080/exotelListener/webapi/exotel/audioresponse
    HEAD: http://localhost:8080/exotelListener/webapi/exotel/audioresponse
    GET: http://localhost:8080/exotelListener/webapi/exotel/audiomessage
    GET: http://localhost:8080/exotelListener/webapi/exotel/userinput?digits=2

## Documentation for Exotel API

1. http://support.exotel.in/support/solutions/articles/48283-working-with-passthru-applet
2. http://support.exotel.in/support/solutions/articles/48285-greeting-using-dynamic-text-or-audio-from-url

## TODO List
	1. Look for a file sharing/hosting service for audio files
	2. Implement logic for retrieving audio files based on input params (currently, all QueryParams are ignored)
	3. Write tests
	4. Deploy this war on a publicly hosted container (Heroku/Openshift or some other)

