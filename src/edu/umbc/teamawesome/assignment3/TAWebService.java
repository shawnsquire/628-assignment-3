package edu.umbc.teamawesome.assignment3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;


public class TAWebService {

	//Request Params
	protected static final String REQUEST_KEY = "request";
	protected static final String DELEGATE_KEY = "delegate";
	protected static final String METHOD_KEY = "method";
	protected static final String CONTEXT_KEY = "context";
	
	//Method constants
	protected static final String kAuthenticateMethod = "authenticate";
	protected static final String kGetLocationMethod = "get_location";
	protected static final String kGetLocationsMethod = "get_locations";
	protected static final String kSetLocationsMethod = "set_location";
	protected static final String kCreateUserMethod = "create_user";
	protected static final String kGetChatsMethod = "get_chats";
	protected static final String kSendChatsMethod = "send_chat";

	
	public static String convertStreamToString(InputStream is) 
	{
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(is),8*1024);
		StringBuilder sb = new StringBuilder();
		String line = null;
		
		try
		{
			while((line=reader.readLine()) != null) 
			{
				sb.append(line);
			}
		}
		catch(IOException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			try 
			{
				is.close();
			}
			catch(IOException e) 
			{
				e.printStackTrace();
			}
		}
		
		return sb.toString();
	}
	
	protected static class HttpAsyncTask extends AsyncTask<Object, Void, Object>
	{
		private TAWebRequestDelegate delegate;
		private String error;
		
		@SuppressWarnings("unchecked")
		@Override
		protected Object doInBackground(Object... params)
		{
			if(params == null || params.length == 0)
			{
				error = "Invalid request parameters";
				return null;
			}
			
			Object object = params[0];
			HashMap<String,Object>map = null;
			
			if(object.getClass() == HashMap.class)
			{
				map = (HashMap<String,Object>)object;
			}
			
			if(map == null)
			{
				error = "Invalid request parameters";
				return null;
			}
			
			DefaultHttpClient client = new DefaultHttpClient();
			HttpUriRequest request = (HttpUriRequest)map.get(REQUEST_KEY);
			
			Context ctx = (Context)map.get(CONTEXT_KEY);
			
			
			delegate = (TAWebRequestDelegate)map.get(DELEGATE_KEY);
			
			String method = (String)map.get(METHOD_KEY);
			
			if(method == null)
				method = "";
			
			HttpResponse response = null;
			
			try 
			{
				response = client.execute(request);
				
				int responseCode = response.getStatusLine().getStatusCode();
				
				if(responseCode != 200 && responseCode != 201)
				{
					error = response.getStatusLine().getReasonPhrase();

					return null;
				}
				
//				if(method.equals(kLoginMethod) && response.containsHeader(SET_COOKIE_KEY))
//				{	
//					CookieSyncManager manager = CookieSyncManager.getInstance();
//					CookieManager cookieManager = CookieManager.getInstance();
//					cookieManager.setCookie(ctx.getString(R.string.school_base_url), response.getFirstHeader(SET_COOKIE_KEY).getValue());
//					SPUserPreferences.setCookie(response.getFirstHeader(SET_COOKIE_KEY).getValue(), ctx);
//					manager.sync();
//				}
				
				HttpEntity httpEntity = response.getEntity();
				
				return httpEntity.getContent();
			}
			catch (ClientProtocolException e) 
			{
				error = e.getLocalizedMessage();
				return null;
			} 
			catch (IOException e) 
			{
				error = e.getLocalizedMessage();
				return null;
			}
			catch (Exception e)
			{
				error = e.getLocalizedMessage();
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(Object returnVal)
		{
			if(returnVal == null)//an error occurred
			{
				if(delegate != null)
				{
					if(error != null)
					{
						delegate.webRequestDidFailWithError(error);
					}
					else
					{
						delegate.webRequestDidFailWithError("An error occurred");
					}
				}
			}
			else
			{
				if(delegate != null)
				{
					delegate.webRequestDidFinishWithResult((InputStream)returnVal);
				}
			}
		}
		
	}
	
	public static void getLocations(final Context ctx, final TAWebServiceDelegate delegate)
	{
		TAWebRequestDelegate requestDelegate = new TAWebRequestDelegate()
		{	
			@Override
			public void webRequestDidFailWithError(String errorString)
			{
				delegate.webServiceDidFailWithError(errorString);
			}

			@Override
			public void webRequestDidFinishWithResult(InputStream result) 
			{
				try
				{
					String jsonString = convertStreamToString(result);
					JSONObject object = new JSONObject(jsonString);
					
					if(object.has("success"))
					{
						Log.i("WEBSERVICE", object.get("success").toString());

						
						delegate.webServiceDidFinishWithResult(object.get("success"));
					}
					else
					{
						delegate.webServiceDidFailWithError("Get Locations Failed");
					}
				} 
				catch (JSONException e)
				{
					delegate.webServiceDidFailWithError(e.getLocalizedMessage());
				}
				catch(Exception e)
				{
					delegate.webServiceDidFailWithError(e.getLocalizedMessage());
				}
			}
		};
		
		HttpGet get = new HttpGet(ctx.getString(R.string.api_base_url) + ctx.getString(R.string.get_locations_url));
		
		get.setHeader("Content-Type", "application/json");
				
		HashMap<String,Object>map = new HashMap<String,Object>();
		map.put(REQUEST_KEY, get);
		map.put(DELEGATE_KEY, requestDelegate);
		map.put(METHOD_KEY, kAuthenticateMethod);
		map.put(CONTEXT_KEY, ctx);
		
		new HttpAsyncTask().execute(map);
	}
	
	public static void getLocation(final String userId, final Context ctx, final TAWebServiceDelegate delegate)
	{
		TAWebRequestDelegate requestDelegate = new TAWebRequestDelegate()
		{	
			@Override
			public void webRequestDidFailWithError(String errorString)
			{
				delegate.webServiceDidFailWithError(errorString);
			}

			@Override
			public void webRequestDidFinishWithResult(InputStream result) 
			{
				try
				{
					String jsonString = convertStreamToString(result);
					JSONObject object = new JSONObject(jsonString);
					
					if(object.has("success"))
					{
						delegate.webServiceDidFinishWithResult(object.get("success"));
					}
					else
					{
						delegate.webServiceDidFailWithError("Get Location Failed");
					}
				} 
				catch (JSONException e)
				{
					delegate.webServiceDidFailWithError(e.getLocalizedMessage());
				}
				catch(Exception e)
				{
					delegate.webServiceDidFailWithError(e.getLocalizedMessage());
				}
			}
		};
		
		HttpGet get = new HttpGet(String.format(ctx.getString(R.string.api_base_url) + ctx.getString(R.string.get_location_url), userId));
		
		get.setHeader("Content-Type", "application/json");
				
		HashMap<String,Object>map = new HashMap<String,Object>();
		map.put(REQUEST_KEY, get);
		map.put(DELEGATE_KEY, requestDelegate);
		map.put(METHOD_KEY, kAuthenticateMethod);
		map.put(CONTEXT_KEY, ctx);
		
		new HttpAsyncTask().execute(map);
	}

	public static void setLocation(final String userId, final double longitude, final double latitude, final Context ctx, final TAWebServiceDelegate delegate)
	{
		TAWebRequestDelegate requestDelegate = new TAWebRequestDelegate()
		{	
			@Override
			public void webRequestDidFailWithError(String errorString)
			{
				delegate.webServiceDidFailWithError(errorString);
			}

			@Override
			public void webRequestDidFinishWithResult(InputStream result) 
			{
				try
				{
					String jsonString = convertStreamToString(result);
					JSONObject object = new JSONObject(jsonString);
					
					if(object.has("success"))
					{
						delegate.webServiceDidFinishWithResult(object.get("success"));
					}
					else
					{
						delegate.webServiceDidFailWithError("Login Failed");
					}
				} 
				catch (JSONException e)
				{
					delegate.webServiceDidFailWithError(e.getLocalizedMessage());
				}
				catch(Exception e)
				{
					delegate.webServiceDidFailWithError(e.getLocalizedMessage());
				}
			}
		};
		
		HttpGet get = new HttpGet(String.format(ctx.getString(R.string.api_base_url) + ctx.getString(R.string.set_location), userId, "" + longitude, "" + latitude));
		
		get.setHeader("Content-Type", "application/json");
				
		HashMap<String,Object>map = new HashMap<String,Object>();
		map.put(REQUEST_KEY, get);
		map.put(DELEGATE_KEY, requestDelegate);
		map.put(METHOD_KEY, kAuthenticateMethod);
		map.put(CONTEXT_KEY, ctx);
		
		new HttpAsyncTask().execute(map);
	}

	public static void authenticate(final String userName, final String password, final Context ctx, final TAWebServiceDelegate delegate)
	{
		TAWebRequestDelegate requestDelegate = new TAWebRequestDelegate()
		{	
			@Override
			public void webRequestDidFailWithError(String errorString)
			{
				delegate.webServiceDidFailWithError(errorString);
			}

			@Override
			public void webRequestDidFinishWithResult(InputStream result) 
			{
				try
				{
					String jsonString = convertStreamToString(result);
					JSONObject object = new JSONObject(jsonString);
					
					if(object.has("success"))
					{
						TAUser user = new TAUser();
						user.setUsername(userName);
						user.setUserId(object.get("success").toString());
						Log.i("WEBSERVICE", object.get("success").toString());

						TAUserPreferences.setCurrentUser(ctx, user);
						
						delegate.webServiceDidFinishWithResult(user);
					}
					else
					{
						delegate.webServiceDidFailWithError("Login Failed");
					}
				} 
				catch (JSONException e)
				{
					delegate.webServiceDidFailWithError(e.getLocalizedMessage());
				}
				catch(Exception e)
				{
					delegate.webServiceDidFailWithError(e.getLocalizedMessage());
				}
			}
		};
		
		HttpGet get = new HttpGet(String.format(ctx.getString(R.string.api_base_url) + ctx.getString(R.string.authenticate), userName, password));
		
		get.setHeader("Content-Type", "application/json");
				
		HashMap<String,Object>map = new HashMap<String,Object>();
		map.put(REQUEST_KEY, get);
		map.put(DELEGATE_KEY, requestDelegate);
		map.put(METHOD_KEY, kAuthenticateMethod);
		map.put(CONTEXT_KEY, ctx);
		
		new HttpAsyncTask().execute(map);
	}

	public static void createUser(final String userName, final String password, final String firstName, final String lastName, final Context ctx, final TAWebServiceDelegate delegate)
	{
		TAWebRequestDelegate requestDelegate = new TAWebRequestDelegate()
		{	
			@Override
			public void webRequestDidFailWithError(String errorString)
			{
				delegate.webServiceDidFailWithError(errorString);
			}

			@Override
			public void webRequestDidFinishWithResult(InputStream result) 
			{
				try
				{
					String jsonString = convertStreamToString(result);
					JSONObject object = new JSONObject(jsonString);
					
					if(object.has("success"))
					{
						delegate.webServiceDidFinishWithResult(object.get("success"));
					}
					else
					{
						delegate.webServiceDidFailWithError("Login Failed");
					}
				} 
				catch (JSONException e)
				{
					delegate.webServiceDidFailWithError(e.getLocalizedMessage());
				}
				catch(Exception e)
				{
					delegate.webServiceDidFailWithError(e.getLocalizedMessage());
				}
			}
		};
		
		HttpGet get = new HttpGet(String.format(ctx.getString(R.string.api_base_url) + ctx.getString(R.string.create_user), userName, password, firstName, lastName));
		
		get.setHeader("Content-Type", "application/json");
				
		HashMap<String,Object>map = new HashMap<String,Object>();
		map.put(REQUEST_KEY, get);
		map.put(DELEGATE_KEY, requestDelegate);
		map.put(METHOD_KEY, kAuthenticateMethod);
		map.put(CONTEXT_KEY, ctx);
		
		new HttpAsyncTask().execute(map);
	}

	public static void getChats(final String userId, final Context ctx, final TAWebServiceDelegate delegate)
	{
		TAWebRequestDelegate requestDelegate = new TAWebRequestDelegate()
		{	
			@Override
			public void webRequestDidFailWithError(String errorString)
			{
				delegate.webServiceDidFailWithError(errorString);
			}

			@Override
			public void webRequestDidFinishWithResult(InputStream result) 
			{
				try
				{
					String jsonString = convertStreamToString(result);
					JSONObject object = new JSONObject(jsonString);
					
					if(object.has("success"))
					{
						delegate.webServiceDidFinishWithResult(object.get("success"));
					}
					else
					{
						delegate.webServiceDidFailWithError("Login Failed");
					}
				} 
				catch (JSONException e)
				{
					delegate.webServiceDidFailWithError(e.getLocalizedMessage());
				}
				catch(Exception e)
				{
					delegate.webServiceDidFailWithError(e.getLocalizedMessage());
				}
			}
		};
		
		HttpGet get = new HttpGet(String.format(ctx.getString(R.string.api_base_url) + ctx.getString(R.string.get_chats), userId));
		
		get.setHeader("Content-Type", "application/json");
				
		HashMap<String,Object>map = new HashMap<String,Object>();
		map.put(REQUEST_KEY, get);
		map.put(DELEGATE_KEY, requestDelegate);
		map.put(METHOD_KEY, kAuthenticateMethod);
		map.put(CONTEXT_KEY, ctx);
		
		new HttpAsyncTask().execute(map);
	}

	public static void sendChat(final String senderUserId, final String recipientUserId, final String message, final Context ctx, final TAWebServiceDelegate delegate)
	{
		TAWebRequestDelegate requestDelegate = new TAWebRequestDelegate()
		{	
			@Override
			public void webRequestDidFailWithError(String errorString)
			{
				delegate.webServiceDidFailWithError(errorString);
			}

			@Override
			public void webRequestDidFinishWithResult(InputStream result) 
			{
				try
				{
					String jsonString = convertStreamToString(result);
					JSONObject object = new JSONObject(jsonString);
					
					if(object.has("success"))
					{						
						delegate.webServiceDidFinishWithResult(null);
					}
					else
					{
						delegate.webServiceDidFailWithError("Login Failed");
					}
				} 
				catch (JSONException e)
				{
					delegate.webServiceDidFailWithError(e.getLocalizedMessage());
				}
				catch(Exception e)
				{
					delegate.webServiceDidFailWithError(e.getLocalizedMessage());
				}
			}
		};
		
		HttpGet get = new HttpGet(String.format(ctx.getString(R.string.api_base_url) + ctx.getString(R.string.send_chat), senderUserId, recipientUserId, message));
		
		get.setHeader("Content-Type", "application/json");
				
		HashMap<String,Object>map = new HashMap<String,Object>();
		map.put(REQUEST_KEY, get);
		map.put(DELEGATE_KEY, requestDelegate);
		map.put(METHOD_KEY, kAuthenticateMethod);
		map.put(CONTEXT_KEY, ctx);
		
		new HttpAsyncTask().execute(map);
	}

	
}
