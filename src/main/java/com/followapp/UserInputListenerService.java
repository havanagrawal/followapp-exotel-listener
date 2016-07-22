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
import com.fasterxml.jackson.databind.JsonMappingException;
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
	return Response.ok("hello\nDoes this work?", MediaType.TEXT_PLAIN_TYPE).build();
    }

    @POST
    @Path("call")
    @Consumes(MediaType.APPLICATION_JSON)
    public void callUser(String input) throws JsonParseException, JsonMappingException, IOException {
	System.out.println("[call] Got JSON input: " + input);

	ObjectMapper mapper = new ObjectMapper();
	CallDetails callDetails = mapper.readValue(input, CallDetails.class);
	callDetailMap.put(callDetails.getPhoneNumber(), callDetails);

	generateAudioMessage(callDetails.getPhoneNumber());

	System.out.println("[call] Calling " + callDetails.getPhoneNumber());
	CallingService.callUser(callDetails.getPhoneNumber());
    }

    public void generateAudioMessage(String phoneNumber) throws IOException {
	if (!audioMap.containsKey(phoneNumber)) {
	    CallDetails callDetails = callDetailMap.get(phoneNumber);

	    URL guardianNameUrl = createAudioUrl(callDetails.getGuardianName());
	    URL childNameUrl = createAudioUrl(callDetails.getChildName());
	    URL vaccineUrl = createAudioUrl(callDetails.getVaccineName());
	    // TODO: URL dateUrl =
	    // createAudioUrl(callDetails.getDateForVaccine());

	    audioMap.put(callDetails.getPhoneNumber(), Arrays.asList(guardianNameUrl, childNameUrl, vaccineUrl));
	}
    }

    public URL createAudioUrl(String word) {
	URL wordUrl = null;
	try {
	    wordUrl = new URL(audioBasePath + word + ".wav");
	} catch (MalformedURLException mue) {
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
     * @throws UnsupportedAudioFileException
     */
    @GET
    @Path("audioresponse")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getAudioResponse(@QueryParam("CallSid") String callSid, @QueryParam("From") String from,
	    @QueryParam("To") String exotelNumber, @QueryParam("DialWhomNumber") String beingCalled,
	    @Context UriInfo uriInfo) throws UnsupportedAudioFileException, IOException {

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
    public <P> String join(List<P> items, String separator) {
	StringBuilder sb = new StringBuilder();
	for (P item : items) {
	    sb.append(item.toString()).append(separator);
	}
	return sb.toString();
    }
}
