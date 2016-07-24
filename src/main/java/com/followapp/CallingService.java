package com.followapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;


/**
 * Class to encapsulate the logic of calling a person
 *  
 * @see <a href="http://support.exotel.in/support/solutions/articles/48278-outbound-call-to-connect-a-customer-to-an-app">Exotel Connect Customer to App</a>
 */
public class CallingService {

    private static final Properties exotelProperties = new Properties();

    // Load exotel properties, which contain sid and token
    static {
	try {
	    exotelProperties.load(UserInputListenerService.class.getResourceAsStream("/exotel.properties"));
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Call the specified phone number via Exotel
     * 
     * @param phoneNumber The number that will be called
     * @return
     */
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

	    System.out.println(readHttpResponse(httpResponse));

	} catch (ClientProtocolException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return null;
    }

    /**
     * Create an HttpPost request that can be sent to Exotel
     * Which results in a call being made to the specified phone number
     * 
     * @param phoneNumber The number that will be called 
     * @return An HttpPost request
     * @throws UnsupportedEncodingException In case the URLEncoding of the parameters fails
     */
    private static HttpPost makePostRequest(String phoneNumber) throws UnsupportedEncodingException {
	HttpPost callRequest = new HttpPost(getExotelPath());
	List<NameValuePair> postParams = getPostParams(phoneNumber);
	callRequest.setEntity(new UrlEncodedFormEntity(postParams));
	return callRequest;
    }
    
    /**
     * Generate the URL to which we send POST request,
     * using the exotel sid and token
     * 
     * @return The URL to which the HttpPost request can be made
     */
    private static String getExotelPath() {
	String exotelSid = exotelProperties.getProperty("exotel.sid");
	String exotelToken = exotelProperties.getProperty("exotel.token");

	return new StringBuilder().append("https://").append(exotelSid).append(":").append(exotelToken)
		.append("@twilix.exotel.in/v1/Accounts/").append(exotelSid).append("/Calls/connect").toString();
    }
    
    /**
     * Generate mandatory POST parameters to the request
     * 
     * @param phoneNumber phoneNumber The number that will be called
     * @return A List<NameValuePair> which can be attached as POST parameters to the Http Request
     */
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
    
    /**
     * Read and return the content of the response sent by Exotel
     * 
     * @param httpResponse The HttpResponse received from Exotel
     * @return Return the string containing content of the HttpResponse
     * @throws IOException when reading the response fails (perhaps due to a broken stream)
     */
    private static String readHttpResponse(HttpResponse httpResponse) throws IOException {
	BufferedReader br = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));

	StringBuilder result = new StringBuilder();
	String line = "";

	while ((line = br.readLine()) != null) {
	    result.append(line);
	}

	return result.toString();
    }
}
