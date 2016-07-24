<html>
<body>
    <h2>FollowApp RESTful Web Application!</h2>
    <p>
    	There are three services that this application exposes.
    	<table border="1" style="border-collapse:collapse">
    		<tr>
    			<th> Request </th>
    			<th> Description </th>
    			<th> Parameters </th>
    			<th> Sample Query </th>
    		</tr>
    		<tr>
    			<td> GET: Audio Response </td>
    			<td> Returns a link to an audio message in WAV format </td>
    			<td> CallSid, From, To, DialWhomNumber </td>
    			<td> /webapi/exotel/audioresponse?CallSid=abcd&From=1234&To=4321&DialWhomNumber=987654321 </td>
    		</tr>
    		<tr>
    			<td> GET: Audio Message </td>
    			<td> Returns an audio message in WAV format. Uses CallSid to identify the audio to be played. </td>
    			<td> CallSid, From, To, DialWhomNumber </td>
    			<td> /webapi/exotel/audiomessage?CallSid=abcd&From=1234&To=4321&DialWhomNumber=987654321 </td>
    		</tr>
    		<tr>
    			<td> GET: User Input </td>
    			<td> Returns 200-OK if user pressed 1. Else it returns 302-Found </td>
    			<td> digits, From, To, Direction </td>
    			<td> /webapi/exotel/audioresponse?digits="1"&From=1234&To=4321&Direction=outbound </td>
    		</tr>
    	</table>
    </p>
</body>
</html>
