package com.followapp;

import java.io.File;

import javax.ws.rs.GET;
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
	 * @return A 200-OK Response if the input was processed successfully
	 */

	@GET
	@Path("userinput")
	public Response getResponseOfUser(@QueryParam("digits") String input) {
		// TODO: Persist the response to the DB
		// TODO: Send a 302 Response in case the user presses 2
		System.out.println("We got query parameter: " + input);
		return Response.status(Response.Status.OK).build();
	}

	/**
	 * Return a Response containing a link to the audio file
	 * 
	 * http://support.exotel.in/support/solutions/articles/48285-greeting-using-
	 * dynamic-text-or-audio-from-url
	 * 
	 * Note: The HEAD request is handled automatically
	 * 
	 * @param user
	 * @return
	 */
	@GET
	@Path("audioresponse")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getAudioResponse(@QueryParam("CallSid") String callSid, 
									 @QueryParam("From") String from,
									 @QueryParam("To") String exotelNumber, 
									 @QueryParam("DialWhomNumber") String beingCalled) {
		return Response.ok("http://localhost:8080/exotelListener/webapi/exotel/audiomessage", MediaType.TEXT_PLAIN_TYPE)
				.build();
	}

	/**
	 * Send a Response containing the audio file in WAV format
	 * 
	 * This may not be required if we find a audio hosting/file sharing service
	 * Which does not require login/authentication to retrieve files
	 * 
	 * @param user
	 * @return
	 */
	@GET
	@Path("audiomessage")
	@Produces("audio/wav")
	public Response getAudio(@QueryParam("CallSid") String callSid, 
							 @QueryParam("From") String from,
							 @QueryParam("To") String exotelNumber, 
							 @QueryParam("DialWhomNumber") String beingCalled) {
		// TODO: Figure out how we store these audio files
		// TODO: Write logic to retrieve file based on params
		// For now, we return a static file
		try {
			File message = new File(this.getClass().getResource("/audio/havan.wav").getFile().replace("%20", " "));
			return Response.ok(message, WAV).build();
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return Response.status(Response.Status.NO_CONTENT).build();
		}
	}
}
