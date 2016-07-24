package com.followapp;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

// TODO: Replace all sysouts with logging

/**
 * Root resource (exposed at "exotel" path)
 */
@Path("exotel")
public class UserInputListenerService {

    public static final MediaType WAV = new MediaType("audio", "wav");

    public static final Map<String, List<URL>> audioMap = new HashMap<>();
    public static final Map<String, CallDetails> callDetailMap = new HashMap<>();

    private static final Properties audioProperties = new Properties();

    static {
	try {
	    audioProperties.load(UserInputListenerService.class.getResourceAsStream("/audio.properties"));
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public static final String audioBasePath = audioProperties.getProperty("audioresource.basepath");

    @GET
    @Path("test")
    public Response testSomething() {
	return Response.ok("Hello\nService is up!", MediaType.TEXT_PLAIN_TYPE).build();
    }

    /**
     * Makes a call to the specified user, and initialises the audio to be
     * played when the user picks up
     * 
     * @param input
     *            A String containing the JSON representation of
     *            {@link com.followapp.CallDetails}
     * @throws JsonParseException
     *             when the input string is not valid JSON
     * @throws IOException
     */
    @POST
    @Path("call")
    @Consumes(MediaType.APPLICATION_JSON)
    public void callUser(String input) throws JsonParseException, IOException {
	System.out.println("[call] Got JSON input: " + input);

	ObjectMapper mapper = new ObjectMapper();
	CallDetails callDetails = mapper.readValue(input, CallDetails.class);
	callDetailMap.put(callDetails.getPhoneNumber(), callDetails);

	generateAudioMessage(callDetails.getPhoneNumber());

	System.out.println("[call] Calling " + callDetails.getPhoneNumber());
	CallingService.callUser(callDetails.getPhoneNumber());
    }

    /**
     * Given a phone number, get call details from the callDetailMap, which
     * contains the guardian name, child name, vaccine name, etc and generate
     * audio URL's from them *
     * 
     * @param phoneNumber
     *            The phone number which has been called
     */
    private void generateAudioMessage(String phoneNumber) {
	CallDetails callDetails = callDetailMap.get(phoneNumber);

	URL guardianNameUrl = createAudioUrl(callDetails.getGuardianName());
	URL childNameUrl = createAudioUrl(callDetails.getChildName());
	URL vaccineUrl = createAudioUrl(callDetails.getVaccineName());
	URL dayUrl = createAudioUrl(callDetails.getVaccineDay());
	URL monthUrl = createAudioUrl(callDetails.getVaccineMonth());
	URL yearUrl = createAudioUrl(callDetails.getVaccineYear());
	URL promptUrl = createAudioUrl("prompt");
	
	audioMap.put(callDetails.getPhoneNumber(),
		Arrays.asList(guardianNameUrl, childNameUrl, vaccineUrl, dayUrl, monthUrl, yearUrl, promptUrl));
    }

    /**
     * Create an audio URL from the given word, by prepending the storage
     * solution path to it, and appending the .wav file format to it
     * 
     * @param word
     *            The word for which the audio is required
     * @return A URL pointing to the path that MAY contain the audio resource
     */
    private URL createAudioUrl(String word) {
	URL wordUrl = null;
	try {
	    wordUrl = new URL(audioBasePath + word + ".wav");
	} catch (MalformedURLException mue) {
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
    public Response getResponseOfUser(@QueryParam("digits") String input, @QueryParam("From") String from,
	    @QueryParam("To") String to, @QueryParam("Direction") String direction) {

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
     */
    @GET
    @Path("audioresponse")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getAudioResponse(@QueryParam("CallSid") String callSid, @QueryParam("From") String from,
	    @QueryParam("To") String exotelNumber, @QueryParam("DialWhomNumber") String beingCalled,
	    @Context UriInfo uriInfo) throws IOException {

	System.out.println("[audioresponse] CallSid: " + callSid);
	System.out.println("[audioresponse] From: " + from);
	System.out.println("[audioresponse] exotelNumber: " + exotelNumber);
	System.out.println("[audioresponse] DialWhomNumber: " + beingCalled);
	System.out.println("[audioresponse] Unparsed query parameters: " + uriInfo.getRequestUri().getQuery());

	Response response = Response.ok(join(audioMap.get(from), "\n"), MediaType.TEXT_PLAIN_TYPE).build();

	System.out.println("[audioresponse] Sending response with headers: " + response.getHeaders().toString());
	System.out.println("[audioresponse] Sending response: " + response.toString());
	return response;
    }

    /**
     * This is only for the sake of testing the Exotel flow in case the actual
     * service is not up Or the relevant audio files have not yet been uploaded
     * to the storage solution
     */
    @GET
    @Path("mockaudioresponse")
    @Produces(MediaType.TEXT_PLAIN)
    public Response mockGetAudioResponse(@QueryParam("CallSid") String callSid, @QueryParam("From") String from,
	    @QueryParam("To") String exotelNumber, @QueryParam("DialWhomNumber") String beingCalled,
	    @Context UriInfo uriInfo) throws UnsupportedAudioFileException, IOException {

	System.out.println("[mockaudioresponse] CallSid: " + callSid);
	System.out.println("[mockaudioresponse] From: " + from);
	System.out.println("[mockaudioresponse] exotelNumber: " + exotelNumber);
	System.out.println("[mockaudioresponse] DialWhomNumber: " + beingCalled);
	System.out.println("[mockaudioresponse] Unparsed query parameters: " + uriInfo.getRequestUri().getQuery());
	URL mockGuardianNameUrl = createAudioUrl("ramesh");
	URL mockChildNameUrl = createAudioUrl("priya");
	URL mockVaccineUrl = createAudioUrl("polio");

	Response response = Response
		.ok(join(Arrays.asList(mockGuardianNameUrl, mockChildNameUrl, mockVaccineUrl), "\n"),
			MediaType.TEXT_PLAIN_TYPE)
		.build();

	System.out.println("[mockaudioresponse] Sending response with headers: " + response.getHeaders().toString());
	System.out.println("[mockaudioresponse] Sending response: " + response.toString());
	return response;
    }

    /**
     * A small convenience method to convert a collection into a string
     * 
     * @param items
     *            A List of P
     * @param separator
     *            The string that will be used to separate two items
     * @return A string representation of the collection
     */
    private <P> String join(List<P> items, String separator) {
	StringBuilder sb = new StringBuilder();
	for (P item : items) {
	    sb.append(item.toString()).append(separator);
	}
	return sb.toString();
    }
}
