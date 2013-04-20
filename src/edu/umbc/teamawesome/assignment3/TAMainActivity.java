package edu.umbc.teamawesome.assignment3;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import edu.umbc.teamawesome.assignment3.TALoginFragment.TALoginDelegate;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;

public class TAMainActivity extends Activity implements TALoginDelegate
{
	private static int DEFAULT_ZOOM_LEVEL = 18;

	private GoogleMap map;
	private MapFragment mapFragment;
	private LocationManager locationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		registerLocationListener();
		createMap();
		
		TALoginFragment loginFragment = new TALoginFragment();
		FragmentTransaction fragmentTransaction =
				getFragmentManager().beginTransaction();
		fragmentTransaction.add(R.id.activity_layout, loginFragment);
		fragmentTransaction.commit();

	}

	@Override
	public void onResume()
	{
		Log.i("WEBSERVICE", "onResume");

		if(mapFragment.isVisible())
		{
			updateMap();
		}
		
	    super.onResume();
	}	

	public void updateMap()
	{
//		updatePins();
		Log.i("WEBSERVICE", "updateMap");


		if(map == null && mapFragment != null)
		{
			map = mapFragment.getMap();
			map.setMyLocationEnabled(true);
			
//			map.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
			
//			updatePins();
		}
		
		Location currentLocation = null;
		if(locationManager != null)
		{
			currentLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), true));
			
			if(currentLocation != null && map != null)
			{
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM_LEVEL));
			}
		}
	}
	
	private void registerLocationListener() {
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
		LocationListener locationListener = new LocationListener() {
			
			@Override
			public void onLocationChanged(Location location) {
//				newLocation(location);
				updateMap();
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
		
		Location currentLocation = null;
		if(locationManager != null)
		{
			currentLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), true));

			if(currentLocation != null)

				options.mapType(GoogleMap.MAP_TYPE_NORMAL).compassEnabled(true).rotateGesturesEnabled(true).tiltGesturesEnabled(true).camera(new CameraPosition(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM_LEVEL, 0, 0));

			if(mapFragment == null)
			{
				mapFragment = MapFragment.newInstance(options);
			}
		}
	}

	@Override
	public void userDidLogin() 
	{
		if(mapFragment != null)
		{					
			FragmentTransaction fragmentTransaction =
					getFragmentManager().beginTransaction();
			fragmentTransaction.replace(R.id.activity_layout, mapFragment);
			fragmentTransaction.commit();
			
		}	
		
	}
}
