package com.followapp;

import java.io.File;
import java.io.IOException;
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

/**
 * Root resource (exposed at "exotel" path)
 */
@Path("exotel")
public class UserInputListenerService {

	public static final MediaType WAV = new MediaType("audio", "wav");

	// Ideally, in the calling service, we can populate this
	// With phone number -> File mappings
	// And then start making calls
	// That way, our response time is faster
	public static final Map<String, File> audioMap = new HashMap<>();
	
	@POST
	@Path("call")
	@Consumes(MediaType.APPLICATION_JSON)
	public void callUser(String input) throws JsonParseException, JsonMappingException, IOException {
		System.out.println("Got JSON input: " + input);
		ObjectMapper mapper = new ObjectMapper();
		CallDetails callDetails = mapper.readValue(input, CallDetails.class);
		System.out.println("Calling " + callDetails.getPhoneNumber());
	}
	
	/**
	 * Handle user input from the "Gather" flow of the Exotel API
	 *
	 * @return A 200-OK Response if user pressed 1, else return 302-Found
	 */
	@GET
	@Path("userinput")
	public Response getResponseOfUser(@QueryParam("digits") String input,
									  @QueryParam("From") String from,
									  @QueryParam("To") String to,
									  @QueryParam("Direction") String direction) {
		// TODO: Persist the response to the DB
		System.out.println("We got query parameter: " + input);
		
		int response = Integer.parseInt(input.replace("\"", ""));
		if (response == 1) {
			return Response.status(Response.Status.OK).build();
		}
		else {
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
	public Response getAudioResponse(@QueryParam("CallSid") String callSid, 
									 @QueryParam("From") String from,
									 @QueryParam("To") String exotelNumber, 
									 @QueryParam("DialWhomNumber") String beingCalled) throws UnsupportedAudioFileException, IOException {
		
		// TODO: Get the URL's of the files, depending on the phone number (beingCalled)
		// Probably from the database
		// Assume we have greetingUrl, nameUrl, childGreetUrl, childNameUrl, 
		// vaccinePrefixUrl, vaccineUrl, noOfDaysUrl, suffixUrl  
		// Where greetingUrl, childGreetUrl, vaccinePrefixUrl and suffixUrl are constants
		
		String name = "Havan";
		String childName = "Baby";
		String vaccine = "Awesomeness";
		int noOfDays = 20;
		String key = "0dcf43ba6349422c816b2ce91a1ef0cb";
		
		URL greetingUrl = new URL("http://api.voicerss.org/?key=" + key + "&src=hello&hl=en-in&c=WAV&f=8khz_16bit_mono");
		URL nameUrl = new URL("http://api.voicerss.org/?key=" + key + "&src=" + name + "&hl=en-in&c=WAV&f=8khz_16bit_mono");
		URL childGreetUrl = new URL("http://api.voicerss.org/?key=" + key + "&src=your%20child%20&hl=en-in&c=WAV&f=8khz_16bit_mono");
		URL childNameUrl = new URL("http://api.voicerss.org/?key=" + key + "&src=" + childName + "&hl=en-in&c=WAV&f=8khz_16bit_mono");
		URL vaccinePrefixUrl = new URL("http://api.voicerss.org/?key=" + key + "&src=is%20yet%20to%20take%20the%20vaccine%20&hl=en-in&c=WAV&f=8khz_16bit_mono");
		URL vaccineUrl = new URL("http://api.voicerss.org/?key=" + key + "&src=" + vaccine + "&hl=en-in&c=WAV&f=8khz_16bit_mono");
		URL noOfDaysUrl = new URL("http://api.voicerss.org/?key=" + key + "&src=in%20" + noOfDays + "%20days&hl=en-in&c=WAV&f=8khz_16bit_mono");
		URL suffixUrl = new URL("http://api.voicerss.org/?key=" + key + "&src=press%20one%20if%20you%20have%20taken%20vaccine%20%20else%20press%20no&hl=en-in&c=WAV&f=8khz_16bit_mono");
		
		File audioMessageFile = WavAppender.fromUrls(greetingUrl, nameUrl, childGreetUrl, childNameUrl, vaccinePrefixUrl, vaccineUrl, noOfDaysUrl, suffixUrl);
		
		audioMap.put(callSid, audioMessageFile);
		
		return Response.ok("http://followapp-havana.herokuapp.com/webapi/exotel/audiomessage", MediaType.TEXT_PLAIN_TYPE)
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
		try {
			//File message = new File(this.getClass().getResource("/audio/havan.wav").getFile().replace("%20", " "));
			return Response.ok(audioMap.get(callSid), WAV).build();
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return Response.status(Response.Status.NO_CONTENT).build();
		}
	}
}
