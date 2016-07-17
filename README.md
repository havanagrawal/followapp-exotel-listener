# A RESTful listener service for FollowApp

## Overview
A simple service that responds to GET requests

There are two service endpoints that this service exposes:
1. Generate a response based on the user's input on the "Gather" applet of the Exotel workflow
2. Generate and return an audio file based on query parameters passed.

## Build using Maven

    mvn clean package

A .war file will be generated, which can be deployed to the container of your choice. (I use Tomcat 9)



