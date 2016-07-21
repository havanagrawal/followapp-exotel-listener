package com.followapp;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.followapp.appender.WavAppender;

// TODO: Replace all sysouts with logging

/**
 * Root resource (exposed at "exotel" path)
 */
@Path("exotel")
public class UserInputListenerService {

	public static final MediaType WAV = new MediaType("audio", "wav");

	public static final Map<String, File> audioMap = new HashMap<>();
	public static final Map<String, CallDetails> callDetailMap = new HashMap<>();

	// 52.42.109.161 is the AWS host
	public static final String audioBasePath = "52.42.109.161/audioresources/";

	@POST
	@Path("call")
	@Consumes(MediaType.APPLICATION_JSON)
	public void callUser(String input) throws JsonParseException, JsonMappingException, IOException, UnsupportedAudioFileException {
		System.out.println("[call] Got JSON input: " + input);
		
		ObjectMapper mapper = new ObjectMapper();
		CallDetails callDetails = mapper.readValue(input, CallDetails.class);
		callDetailMap.put(callDetails.getPhoneNumber(), callDetails);

		generateAudioMessageIfNotGenerated(callDetails.getPhoneNumber());
		
		System.out.println("[call] Calling " + callDetails.getPhoneNumber());
		// TODO: Send the GET request to Exotel link
		// We need to store the api key in a properties file
		// And read it from there
	}

	public void generateAudioMessageIfNotGenerated(String phoneNumber) throws UnsupportedAudioFileException, IOException {
		if (!audioMap.containsKey(phoneNumber)) {
			CallDetails callDetails = callDetailMap.get(phoneNumber);
			
			URL guardianNameUrl = createAudioUrl(callDetails.getGuardianName());
			URL childNameUrl = createAudioUrl(callDetails.getChildName());
			URL vaccineUrl = createAudioUrl(callDetails.getVaccineName());
			//TODO: URL dateUrl = createAudioUrl(callDetails.getDateForVaccine()); 

			File audioMessageFile = WavAppender.fromUrls(guardianNameUrl, childNameUrl, vaccineUrl);
			audioMap.put(callDetails.getPhoneNumber(), audioMessageFile);			
		}
	}
	
	public URL createAudioUrl(String word) {
		URL wordUrl = null;
		try {
			wordUrl = new URL(audioBasePath + word);
		}
		catch (MalformedURLException mue) {
			// TODO: Try to create a URL using the voicerss API
			mue.printStackTrace();
		}
		return wordUrl;		
	}
	
	/**
	 * Handle user input from the "Gather" flow of the Exotel API
	 *
	 * @return A 200-OK Response if user pressed 1, else return 302-Found
	 */
	@GET
	@Path("userinput")
	public Response getResponseOfUser(
			@QueryParam("digits") String input, 
			@QueryParam("From") String from,
			@QueryParam("To") String to, 
			@QueryParam("Direction") String direction) {
		// TODO: Persist the response to the DB
		System.out.println("[userinput] We got query parameter: " + input);

		int response = Integer.parseInt(input.replace("\"", ""));
		if (response == 1) {
			return Response.status(Response.Status.OK).build();
		} else {
			return Response.status(Response.Status.FOUND).build();
		}
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
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 */
	@GET
	@Path("audioresponse")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getAudioResponse(
			@QueryParam("CallSid") String callSid, 
			@QueryParam("From") String from,
			@QueryParam("To") String exotelNumber, 
			@QueryParam("DialWhomNumber") String beingCalled)
			throws UnsupportedAudioFileException, IOException {

		System.out.println("[audioresponse] CallSid: " + callSid);
		System.out.println("[audioresponse] From: " + from);
		System.out.println("[audioresponse] exotelNumber: " + exotelNumber);
		System.out.println("[audioresponse] DialWhomNumber: " + beingCalled);
		
		Response response = Response
				.ok("https://followapp-havana.herokuapp.com/webapi/exotel/audiomessage", MediaType.TEXT_PLAIN_TYPE)
				.build();

		System.out.println("Sending response with headers: " + response.getHeaders().toString());

		return response;
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
	public Response getAudio(
			@QueryParam("CallSid") String callSid, 
			@QueryParam("From") String from,
			@QueryParam("To") String exotelNumber, 
			@QueryParam("DialWhomNumber") String beingCalled) {
		try {
			return Response.ok(audioMap.get(from), WAV).build();
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return Response.status(Response.Status.NO_CONTENT).build();
		}
	}
}
