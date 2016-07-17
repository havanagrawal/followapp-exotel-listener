package com.followapp;

import java.io.File;

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

	public static final MediaType WAV = new MediaType("audio", "wav");
	
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
    @Produces("audio/wav")
    public Response getAudio(@QueryParam("From") String user) {
    	// TODO: Figure out how we store these audio files
    	// For now, we return a static file
    	try {
    		File message = new File(this.getClass().getResource("/audio/havan.wav").getFile().replace("%20", " "));
    		return Response.ok(message, WAV)
    				.build();
    	}
    	catch (NullPointerException npe) {
    		npe.printStackTrace();
    		return Response.status(Response.Status.NO_CONTENT)
    				.build();
    	}
    	//return Response.status(Response.Status.OK).build();
    }
}
