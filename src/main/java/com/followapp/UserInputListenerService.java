package com.followapp;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Root resource (exposed at "exotel" path)
 */
@Path("exotel")
public class UserInputListenerService {

    /**
     * Handle user input from the "Gather" flow of the Exotel API
     *
     * @return A 200-OK Response if the input was processed sucessfully
     */
	
    @GET
    @Path("userinput")
    public Response getResponseOfUser(@QueryParam("digits") String input) {
    	// TODO: Persist the response to the DB
    	// TODO: Send a 302 Response in case the user presses 2
    	System.out.println("We got query parameter: " + input);
    	return Response.status(Response.Status.OK).build();
    }
    
    @GET
    @Path("audiomessage")
    public Response getAudio(@QueryParam("From") String user) {
    	
    }
}
