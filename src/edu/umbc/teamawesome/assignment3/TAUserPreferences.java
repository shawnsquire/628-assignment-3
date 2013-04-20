package edu.umbc.teamawesome.assignment3;

import android.content.Context;
import android.content.SharedPreferences;

public class TAUserPreferences 
{
	private final static String kUserNameKey = "user name key";
	private final static String kUserIdKey = "user id key";

	private final static String PREFS_NAME = "app prefs";	
		
	public static void setCurrentUser(Context ctx, TAUser user)
	{
		setUserName(ctx, user.getUsername());
		setUserId(ctx, user.getUserId());
	}
	
	public static void setUserName(Context ctx, String userName)
	{
		SharedPreferences prefs = ctx.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

		if(userName != null)
			prefs.edit().putString(kUserNameKey, userName).commit();
		else
			prefs.edit().remove(kUserNameKey).commit();	
	}

	public static String getUserName(Context ctx)
	{
		SharedPreferences prefs = ctx.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		return prefs.getString(kUserNameKey, null);
	}
	
	public static void setUserId(Context ctx, String userId)
	{
		SharedPreferences prefs = ctx.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

		if(userId != null)
			prefs.edit().putString(kUserIdKey, userId).commit();
		else
			prefs.edit().remove(kUserIdKey).commit();	
	}

	public static String getUserId(Context ctx)
	{
		SharedPreferences prefs = ctx.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		return prefs.getString(kUserIdKey, null);
	}
}
