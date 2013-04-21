package edu.umbc.teamawesome.assignment3;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.umbc.teamawesome.assignment3.TACreateUserFragment.TACreateUserDelegate;
import edu.umbc.teamawesome.assignment3.TALoginFragment.TALoginDelegate;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;

public class TAMainActivity extends Activity implements TALoginDelegate, TACreateUserDelegate
{
	private static int DEFAULT_ZOOM_LEVEL = 19;
	private static int REFRESH_RATE = 10000;
	//rough estimation, will vary depending on current lat/long
	private static double KM_TO_DEGREE = .008;
	
	private ProgressDialog progress;
	private GoogleMap map;
	private MapFragment mapFragment;
	private LocationManager locationManager;
	private Location currentLocation;
	private ArrayList<TAUser> userList;
	private boolean shouldUpdate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		registerLocationListener();
		createMap();
		
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				shouldUpdate = true;
				
			}
		};
		
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(task, new Date(), REFRESH_RATE);

		
	}

	@Override
	public void onResume()
	{
		Log.i("WEBSERVICE", "onResume");

		if(mapFragment.isVisible())
		{
			updateMap();
		}
		
		showLogin();
		
	    super.onResume();
	}	
	
	public void showLogin()
	{
		if((TAUserPreferences.getUserId(this) == null || TAUserPreferences.getUserId(this).isEmpty()) && getFragmentManager().getBackStackEntryCount() == 0)
		{
			TALoginFragment loginFragment = new TALoginFragment();
			FragmentTransaction fragmentTransaction =
					getFragmentManager().beginTransaction();
			fragmentTransaction.add(R.id.activity_layout, loginFragment);
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.commit();
		}
	}
	
	public void showMap()
	{
		if(mapFragment != null)
		{			
			
			FragmentTransaction fragmentTransaction =
					getFragmentManager().beginTransaction();
			fragmentTransaction.replace(R.id.activity_layout, mapFragment);
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.commit();
		}		
	}
	
	
	@Override
	public void onBackPressed() {
		if(getFragmentManager().getBackStackEntryCount() == 1)	
			finish();
		super.onBackPressed();
	}
	
	private void registerLocationListener() 
	{
		Log.i("WEBSERVICE", "location listener");
		
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
		LocationListener locationListener = new LocationListener() {
			
			@Override
			public void onLocationChanged(Location location) {
				Log.i("WEBSERVICE", "location changed by " + location.distanceTo(currentLocation));

				if(mapFragment.isVisible() && shouldUpdate)
				{
					shouldUpdate = false;
					Log.i("WEBSERVICE", "location updated");

					currentLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), true));
					
					TAWebService.setLocation(TAUserPreferences.getUserId(TAMainActivity.this), location.getLongitude(), location.getLatitude(), TAMainActivity.this, new TAWebServiceDelegate() {
						
						@Override
						public void webServiceDidFinishWithResult(Object result) 
						{
							Log.i("WEBSERVICE", "set location success for " + TAUserPreferences.getUserId(TAMainActivity.this));
							updateMap();
						}
						
						@Override
						public void webServiceDidFailWithError(String errorString) {
				        	AlertDialog.Builder builder = new AlertDialog.Builder(TAMainActivity.this); 
				        	builder.setMessage(R.string.login_failed).setTitle("Error");
				        	builder.create().show();
						}
					});
					
				}
			}
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {}
			
			@Override
			public void onProviderEnabled(String provider) {}
			
			@Override
			public void onProviderDisabled(String provider) {}
		};
		
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
	}
	
	private void createMap() {		
		GoogleMapOptions options = new GoogleMapOptions();
		
		if(locationManager != null)
		{
			currentLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), true));

			if(currentLocation != null)
			{
				options.mapType(GoogleMap.MAP_TYPE_NORMAL).compassEnabled(true).rotateGesturesEnabled(true).tiltGesturesEnabled(true).camera(new CameraPosition(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM_LEVEL, 0, 0));
			}
			if(mapFragment == null)
			{
				mapFragment = MapFragment.newInstance(options);
			}
		}
	}
	
	public void updateMap()
	{
		updatePins();
		Log.i("WEBSERVICE", "updateMap");


		if(map == null && mapFragment != null)
		{
			map = mapFragment.getMap();
			map.setMyLocationEnabled(true);
			
//			map.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
			
//			updatePins();
		}
		
//		if(locationManager != null)
//		{
//			currentLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), true));
//			
//			if(currentLocation != null && map != null)
//			{
//				map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM_LEVEL));
//			}
//		}
	}
	
	public void updatePins()
	{
		TAWebService.getLocations(this, new TAWebServiceDelegate() 
		{
			
			@Override
			public void webServiceDidFinishWithResult(Object result) 
			{
				userList = (ArrayList<TAUser>) result;
				
				map.clear();
				
				for(TAUser user: userList)
				{

					if(Double.valueOf(user.getLatitude()) - currentLocation.getLatitude() <= KM_TO_DEGREE && 
							Double.valueOf(user.getLongitude()) - currentLocation.getLongitude() <= KM_TO_DEGREE && 
							!user.getUserId().equalsIgnoreCase(TAUserPreferences.getUserId(TAMainActivity.this)))
					{
						Log.i("WEBSERVICE", "marker created for " + user.getUsername());

						MarkerOptions marker = new MarkerOptions();
						marker.draggable(false);
						marker.position(new LatLng(Double.valueOf(user.getLatitude()), Double.valueOf(user.getLongitude())));
						marker.title(user.getUsername().length() > 0 ? user.getUsername() : "Marker");
						marker.snippet(user.getTime());

						map.addMarker(marker);
					}
				}
			}
			
			@Override
			public void webServiceDidFailWithError(String errorString) 
			{
				
//				isLoading(false);
	        	AlertDialog.Builder builder = new AlertDialog.Builder(TAMainActivity.this); 
	        	builder.setMessage(R.string.login_failed).setTitle("Error");
	        	builder.create().show();

			}
		});
	}
	
	@Override
	public void userDidLogin() 
	{
		showMap();
	}

	@Override
	public void userWasCreated() 
	{
		getFragmentManager().popBackStack();
		
		showMap();
	}
	
    protected void isLoading(boolean isLoading)
    {
    	if(isLoading)
    	{
    		if(progress == null)
    		{
    			progress = new ProgressDialog(this);
    			progress.setCancelable(false);
    			progress.setMessage("Loading ...");
    			progress.setIndeterminate(true);
    		}
			progress.show();
    	}
    	else
    	{
    		if(progress != null)
    			progress.dismiss();
    	}
    }
    
    @Override
    protected void onPause() 
    {
    	
		if(mapFragment.isVisible())
		{
			for(int i = getFragmentManager().getBackStackEntryCount(); i > 0; i--)
			{
				Log.i("WEBSERVICE", "pop back");

				getFragmentManager().popBackStack();
			}
		}

		Log.i("WEBSERVICE", "onPause");
    	logout();
    	super.onPause();
    }
    
    @Override
    protected void onDestroy() 
    {
		Log.i("WEBSERVICE", "onDestroy");
    	logout();
    	super.onDestroy();
    }
    
    public void logout()
    {
    	TAUserPreferences.setUserId(this, "");
    	TAUserPreferences.setUserName(this, "");
    }
    
//	private void createTabBar() {
//
//		final ActionBar bar = getActionBar();
//        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//        bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
//		
//        bar.addTab(bar.newTab()
//                .setText("Map")
//                .setTabListener(new TabListener() {
//					
//					@Override
//					public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
//
//						if(mapFragment != null && mapFragment.isVisible())
//						{
//							FragmentTransaction fragmentTransaction =
//									getFragmentManager().beginTransaction();
//							fragmentTransaction.hide(mapFragment);
//							fragmentTransaction.commit();
//						}
//					}
//
//					@Override
//					public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
//						if(mapFragment != null && !mapFragment.isVisible())
//						{
//							FragmentTransaction fragmentTransaction =
//									getFragmentManager().beginTransaction();
//							fragmentTransaction.show(mapFragment);
//							fragmentTransaction.commit();
//						}
//					}
//					
//					@Override
//					public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
//						
//					}
//				}));
//        bar.addTab(bar.newTab()
//                .setText("Pins")
//                .setTabListener(new TabListener() {
//					
//					@Override
//					public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
//						findViewById(R.id.pinList).setVisibility(View.GONE);
//						findViewById(R.id.buttonClear).setVisibility(View.VISIBLE);
//						findViewById(R.id.textActivity).setVisibility(View.VISIBLE);
//					}
//					
//					@Override
//					public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
//						findViewById(R.id.pinList).setVisibility(View.VISIBLE);
//						findViewById(R.id.buttonClear).setVisibility(View.GONE);
//						findViewById(R.id.textActivity).setVisibility(View.GONE);
//					}
//					
//					@Override
//					public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
//					}
//				}));
//	}

}
