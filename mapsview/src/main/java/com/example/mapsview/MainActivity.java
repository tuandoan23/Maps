package com.example.mapsview;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
    private Unbinder unbinder;
    private MapView mapView;
    private GoogleMap gmap;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private LocationManager locationManager;
    public static final int MY_PERMISSION_ACCESS_COURSE_LOCATION = 123;
    public static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 321;
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    private static final int MIN_TIME_BW_UPDATES = 5000;
    private static final int MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private Marker marker;
    private double longitude;
    private double latitude;
    private Location _location;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        if (gmap != null) {
            ArrayList<MarkerData> listMarkerData = createMarkerData();
            for (int i = 0; i < listMarkerData.size(); i++){
                createMarker(listMarkerData.get(i).getLatitude(), listMarkerData.get(i).getLongitude(), listMarkerData.get(i).getTitle(), listMarkerData.get(i).getResID());
            }
            LogUtils.d("Created marker");
        }
        getMyLocation();
    }

    private void getMyLocation() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCurrentLocation();
            }
        });
    }

    private void requestCurrentLocation() {
        LogUtils.d("requestCurrentLocation");
        checkLocationPermission();
        if (!checkLocationPermission()){
            LogUtils.d("Check Permission");
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_ACCESS_COURSE_LOCATION);

        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//        Location location = getLocation();

        if (null != location){
            LogUtils.d("Location not null");
            gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(15)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            gmap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            createMarker(location.getLatitude(), location.getLongitude(), "Your Location", R.drawable.icon_appota);
//            gmap.setMinZoomPreference(15);
//            gmap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
            LogUtils.d("Longitude: " + location.getLatitude() + " | Latitude: " + location.getLongitude());
        } else {
            LogUtils.d("Location null");
        }
    }

    public boolean checkLocationPermission()
    {
        String fine_location_permission = "android.permission.ACCESS_FINE_LOCATION";
        int res_1 = this.checkCallingOrSelfPermission(fine_location_permission);

        String coarse_location_permission = "android.permission.ACCESS_COARSE_LOCATION";
        int res_2 = this.checkCallingOrSelfPermission(coarse_location_permission);
        return (res_1 == PackageManager.PERMISSION_GRANTED && res_2 == PackageManager.PERMISSION_GRANTED);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case MY_PERMISSION_ACCESS_COURSE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    protected Marker createMarker(final double latitude, final double longitude, final String title, final int resID) {
//        Marker marker = gmap.addMarker(new MarkerOptions()
//                .position(new LatLng(latitude, longitude))
//                .anchor(0.5f, 0.5f)
//                .title(title)
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));;
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                marker = gmap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                        .title(title)
                );
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        Picasso.get()
                .load(resID)
                .resize(250,250)
                .centerCrop()
                .transform(new BubbleTransformation(20))
                .into(target);

        return marker;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private ArrayList<MarkerData> createMarkerData(){
        ArrayList<MarkerData> list = new ArrayList<>();
        list.add(new MarkerData(20.964465, 105.854679, "Yen So Park", R.drawable.ic_park));
        list.add(new MarkerData(20.995775, 105.808006, "Hanoi University of Science", R.drawable.ic_university));
        list.add(new MarkerData(21.001351, 105.816458, "Royal City", R.drawable.ic_university));
        list.add(new MarkerData(21.007071, 105.793412, "Big C", R.drawable.ic_market));
        return list;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) this
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
//                this.canGetLocation = true;
                checkLocationPermission();
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    LogUtils.d("Network :Network Enabled");
                    if (locationManager != null) {
                        _location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (_location != null) {
                            latitude = _location.getLatitude();
                            longitude = _location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (_location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        LogUtils.d("GPS: GPS Enabled");
                        if (locationManager != null) {
                            _location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (_location != null) {
                                latitude = _location.getLatitude();
                                longitude = _location.getLongitude();
                                LogUtils.d(latitude + " | " + longitude);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return _location;
    }
}
