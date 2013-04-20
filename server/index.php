<!DOCTYPE html>
<html>
<head>
	<title>API - Assignment 3 - Team Awesome - CMSC 628</title>
</head>

<body>
	<h1>API Documentation</h1>

	<h2>Basics</h2>
	<p>The API utilizes HTTP/GET over a connection at "http://<?php echo $_SERVER['SERVER_NAME'].$_SERVER['REQUEST_URI']; ?>api.php".</p>
	<p>Each call must pass a <em>action</em> parameter, and any additional parameters as defined below.</p>
	<p>All responses are returned in JSON format. The top-level is an object with a single key, either <em>success</em> or <em>error</em>.<br />
			If the key is <em>error</em>, the value of that key is a string representing the error that occurred.<br />
			The result of <em>success</em> is defined in the table below.</p>

	<table cellpadding="5" cellspacing="0" border="1" width="100%">
		<thead>
			<tr>
				<th>action</th>
				<th>parameters</th>
				<th width="15%">return</th>
				<th>description</th>
				<th width="20%">example</th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td>get_locations</td>
				<td></td>
				<td>[{<br />
							&nbsp;&nbsp;&nbsp;&nbsp;"id_user": <em>int</em>, <br />
							&nbsp;&nbsp;&nbsp;&nbsp;"time": <em>time</em>, <br />
							&nbsp;&nbsp;&nbsp;&nbsp;"latitude": <em>double</em>, <br />
							&nbsp;&nbsp;&nbsp;&nbsp;"longitude": <em>double</em>, <br />
							&nbsp;&nbsp;&nbsp;&nbsp;"username": <em>string</em>, <br />
							&nbsp;&nbsp;&nbsp;&nbsp;"firstname": <em>string</em>, <br />
							&nbsp;&nbsp;&nbsp;&nbsp;"lastname": <em>string</em>, <br />
						}, ...]</td>
				<td>Returns an array of objects referencing each user, their information, and their location</td>
				<td><p><strong>Request</strong><br />
						?action=get_locations</p>

						<p><strong>Response</strong><br />
						{"success":[{"id_user":"1", "time":"2013-04-03 10:49:03", "latitude":"39.845117", "longitude":"-76.892123", "username":"shawn", "firstname":"Shawn", "lastname":"Squire"}, ... ]}</p>
			</tr>

			<tr>
				<td>get_location</td>
				<td>id_user: <em>int</em> <strong>(required)</strong></td>
				<td>{<br />
							&nbsp;&nbsp;&nbsp;&nbsp;"id_user": <em>int</em>, <br />
							&nbsp;&nbsp;&nbsp;&nbsp;"time": <em>time</em>, <br />
							&nbsp;&nbsp;&nbsp;&nbsp;"latitude": <em>double</em>, <br />
							&nbsp;&nbsp;&nbsp;&nbsp;"longitude": <em>double</em>, <br />
							&nbsp;&nbsp;&nbsp;&nbsp;"username": <em>string</em>, <br />
							&nbsp;&nbsp;&nbsp;&nbsp;"firstname": <em>string</em>, <br />
							&nbsp;&nbsp;&nbsp;&nbsp;"lastname": <em>string</em>, <br />
						}</td>
				<td>Return an object referencing a particular user, their information, and their location</td>
				<td><p><strong>Request</strong><br />
						?action=get_location&amp;id_user=1</p>

						<p><strong>Response</strong><br />
						{"success":{"id_user":"1", "time":"2013-04-03 10:49:03", "latitude":"39.845117", "longitude":"-76.892123", "username":"shawn", "firstname":"Shawn", "lastname":"Squire"}}</p>
			</tr>

			<tr>
				<td>set_location</td>
				<td>id_user: <em>int</em> <strong>(required)</strong><br />
						longitude: <em>double</em> <strong>(required)</strong><br />
						latitude: <em>double</em> <strong>(required)</strong></td>
				<td><em>[same as get_location]</em></td>
				<td>Update a particular user's longitude and latitude, and returns all locations</td>
				<td><p><strong>Request</strong><br />
						?action=set_location&amp;id_user=1&amp;longitude=39.2444&amp;latitude=76.6939</p>

						<p><strong>Response</strong><br />
						{"success":[{"id_user":"1", "time":"2013-04-03 14:08:37", "latitude":"76.6939", "longitude":"39.2444", "username":"shawn", "firstname":"Shawn", "lastname":"Squire"}, ...]}</p></td>
			</tr>

			<tr>
				<td>authenticate</td>
				<td>username: <em>string</em> <strong>(required)</strong><br />
						password: <em>string</em> <strong>(required)</strong></td>
				<td><em>int</em></td>
				<td>Authenticate if a username and password are correct, and return the corresponding user id if correct. Return error if incorrect.</td>
				<td><p><strong>Request</strong><br />
						?action=authenticate&amp;username=shawn&amp;password=shpass</p>

						<p><strong>Response</strong><br />
						{"success":"1"}</p></td>
			</tr>

			<tr>
				<td>create_user</td>
				<td>username: <em>string</em> <strong>(required)</strong><br />
						password: <em>string</em> <strong>(required)</strong><br />
						firstname: <em>string</em> (optional)<br />
						lastname: <em>string</em> (optional)</td>
				<td><em>int</em></td>
				<td>Add a new user to the system and return the newly created user id. If username is invalid or taken, will return an error.</td>
				<td><p><strong>Request</strong><br />
						?action=create_user&amp;username=testuser&amp;password=test&amp;firstname=Test</p>

						<p><strong>Response</strong><br />
						{"success":"11"}</p></td>
			</tr>

			<tr>
				<td>get_chats</td>
				<td>id_user: <em>int</em> <strong>(required)</strong></td>
				<td>[{<br />
							&nbsp;&nbsp;&nbsp;&nbsp;"message": <em>string</em>,<br />
							&nbsp;&nbsp;&nbsp;&nbsp;"sent": <em>time</em>,<br />
							&nbsp;&nbsp;&nbsp;&nbsp;"id_user_from": <em>int</em>, <br />
							&nbsp;&nbsp;&nbsp;&nbsp;"username": <em>string</em>, <br />
							&nbsp;&nbsp;&nbsp;&nbsp;"firstname": <em>string</em>, <br />
							&nbsp;&nbsp;&nbsp;&nbsp;"lastname": <em>string</em>, <br />
						}, ...]</td>
				<td>Retrieves all chats sent <strong>to</strong> the user specified, in descending order. Includes the date sent, as well as information about who sent the message.</td>
				<td><p><strong>Request</strong><br />
						?action=get_chats&amp;id_user=1</p>

						<p><strong>Response</strong><br />
						{"success":[{"message":"xD", "sent":"2013-04-03 13:38:17", "id_user_from":"2", "username":"jermer", "firstname":"Justin", "lastname":"Ermer"}, ... ]}</p></td>
			</tr>

			<tr>
				<td>send_chat</td>
				<td>id_user_from: <em>int</em> <strong>(required)</strong><br />
						id_user_to: <em>int</em> <strong>(required)</strong><br />
						message: <em>string</em> <strong>(reuired)</strong></td>
				<td><em>null</em></td>
				<td>Sends a message to a user, from another user. Returns null success message if valid, and an error otherwise.</td>
				<td><p><strong>Request</strong><br />
						?action=send_chat&amp;id_user_to=2&amp;id_user_from=1&amp;message=Hello</p>

						<p><strong>Response</strong><br />
						{"success":null}</p></td>
			</tr>
		</tbody>
	</table>
</body>
</html>
