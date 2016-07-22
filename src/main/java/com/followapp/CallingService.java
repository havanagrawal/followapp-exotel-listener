package com.followapp;

import java.awt.BufferCapabilities;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

public class CallingService {

    private static final Properties exotelProperties = new Properties();

    static {
	try {
	    exotelProperties.load(UserInputListenerService.class.getResourceAsStream("/exotel.properties"));
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public static CallStatus callUser(String phoneNumber) {

	if (phoneNumber.length() != 11) {
	    throw new IllegalArgumentException(
		    "Phone number is not in the required format. " + "It should be 11 digits long. "
			    + "Try appending a leading 0 to the phone number, " + "in case it is only 10 digits long.");
	}

	HttpClient client = HttpClients.createDefault();	

	try {
	    HttpPost callRequest = makePostRequest(phoneNumber);
	    HttpResponse httpResponse = client.execute(callRequest);

	    System.out.println(readResponse(httpResponse));

	} catch (ClientProtocolException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return null;
    }

    private static HttpPost makePostRequest(String phoneNumber) throws UnsupportedEncodingException {
	HttpPost callRequest = new HttpPost(getExotelPath());
	List<NameValuePair> postParams = getPostParams(phoneNumber);
	callRequest.setEntity(new UrlEncodedFormEntity(postParams));
	return callRequest;
    }
    
    private static String readResponse(HttpResponse httpResponse) throws UnsupportedOperationException, IOException {
	BufferedReader br = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));

	StringBuilder result = new StringBuilder();
	String line = "";

	while ((line = br.readLine()) != null) {
	    result.append(line);
	}

	return result.toString();
    }

    private static List<NameValuePair> getPostParams(String phoneNumber) {
	String callType = exotelProperties.getProperty("exotel.call.type");
	String flow = exotelProperties.getProperty("exotel.flow");
	String callerId = exotelProperties.getProperty("exotel.caller.id");

	List<NameValuePair> params = new ArrayList<>();
	params.add(new BasicNameValuePair("From", phoneNumber));
	params.add(new BasicNameValuePair("CallType", callType));
	params.add(new BasicNameValuePair("CallerId", callerId));
	params.add(new BasicNameValuePair("Url", flow));
	
	return params;	
    }
    
    private static String getExotelPath() {
	String exotelSid = exotelProperties.getProperty("exotel.sid");
	String exotelToken = exotelProperties.getProperty("exotel.token");

	return new StringBuilder().append("https://").append(exotelSid).append(":").append(exotelToken)
		.append("@twilix.exotel.in/v1/Accounts/").append(exotelSid).append("/Calls/connect").toString();
    }
}
