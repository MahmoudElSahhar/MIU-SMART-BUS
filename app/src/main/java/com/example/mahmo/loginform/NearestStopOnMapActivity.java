package com.example.mahmo.loginform;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NearestStopOnMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button stopNavigation;
    private Button findClosestStop;
    private TextView distanceBar;
    private TextView durationBar;
    private Button navigate;
    private static final int num = 177;
    private FirebaseAuth auth;
    private TextView infoBar;
    private DatabaseReference myRef, Ref;
    private FirebaseDatabase database;
    private LocationListener listener;
    private LatLng userLocation;
    private FirebaseUser user;
    private String busTime = "";
    private Marker myMarker, closestStop;
    private ArrayList<Marker> allStops = new ArrayList<>();
    private String objectName = "";
    private String userName = "";
    private Polyline navigationLine = null, oldNavigationLine = null;
    private String wholeText = "";
    private String tripDistance = "";
    private String tripDuration = "";
    private String busLine = "";
    private String markerInfo = "";
    private boolean isWorking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearest_stop_on_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        infoBar = (TextView) findViewById(R.id.InfoBar);
        findClosestStop = (Button) findViewById(R.id.startSearching);
        navigate = (Button) findViewById(R.id.nearestStopNavigate);
        stopNavigation = (Button) findViewById(R.id.stopNavigationNearestStop);
        distanceBar = (TextView) findViewById(R.id.nearestStopDistance);
        durationBar = (TextView) findViewById(R.id.nearestStopDuration);
        infoBar.setVisibility(View.INVISIBLE);
        navigate.setVisibility(View.INVISIBLE);
        distanceBar.setVisibility(View.INVISIBLE);
        durationBar.setVisibility(View.INVISIBLE);
        stopNavigation.setVisibility(View.INVISIBLE);

        for (int i = 0; i < user.getEmail().length(); i++) {
            if (user.getEmail().charAt(i) != '@') {
                userName += user.getEmail().charAt(i);
            } else {
                break;
            }
        }

        //Ref = database.getReference().child("Active Students");
        myRef = database.getReference().child("Bus Lines");


        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                objectName = dataSnapshot.getKey();
                myRef = database.getReference().child("Bus Lines").child(objectName);
                myRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        LatLng currentStop = new LatLng(Double.parseDouble(dataSnapshot.child("Latitude").getValue().toString()),
                                Double.parseDouble(dataSnapshot.child("Longitude").getValue().toString()));
                        busTime = dataSnapshot.child("Time").getValue().toString();
                        Marker marker = mMap.addMarker(new MarkerOptions().position(currentStop).title(" " + objectName + " bus line  -  " + dataSnapshot.getKey() + " stop\n" + "Bus arrives at " + busTime));
                        busLine = dataSnapshot.getKey();
                        allStops.add(marker);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(isWorking)
                {
                    if (navigationLine != null) {
                        //oldNavigationLine = navigationLine;
                        navigationLine.remove();
                        String url;
                        url = getUrl(new LatLng(location.getLatitude(), location.getLongitude()), closestStop.getPosition());
                        FetchUrl FetchUrl = new FetchUrl();
                        FetchUrl.execute(url);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17));
                        //oldNavigationLine.remove();
                    }
                }
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
        };

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, listener);

        findClosestStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkNearest(allStops,userLocation);
                navigate.setVisibility(View.VISIBLE);
            }
        });

        navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isWorking = true;
                String url;
                url = getUrl(userLocation, closestStop.getPosition());
                FetchUrl FetchUrl = new FetchUrl();
                FetchUrl.execute(url);
                findClosestStop.setVisibility(View.INVISIBLE);
                stopNavigation.setVisibility(View.VISIBLE);
                navigate.setVisibility(View.INVISIBLE);
            }
        });

        stopNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isWorking = false;

                if(navigationLine!=null)
                    navigationLine.remove();

                stopNavigation.setVisibility(View.INVISIBLE);
                navigate.setVisibility(View.INVISIBLE);
                findClosestStop.setVisibility(View.VISIBLE);
                distanceBar.setVisibility(View.INVISIBLE);
                durationBar.setVisibility(View.INVISIBLE);
                infoBar.setText(markerInfo);
                closestStop.setTitle(markerInfo);
            }
        });


    }

    public void onBackPressed(){
        super.onBackPressed();
        DatabaseReference item = FirebaseDatabase.getInstance().getReference().child("Active Students").child(userName);
        item.removeValue();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, num);
            }

            return;
        }
        else
        {
            mMap.setMyLocationEnabled(true);
            LocationManager service = (LocationManager)getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = service.getBestProvider(criteria, false);
            Location loc = service.getLastKnownLocation(provider);
            //if(loc != null)
            {
                userLocation = new LatLng(loc.getLatitude(),loc.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLocation.latitude, userLocation.longitude), 17));
            }
            //Ref.child(userName).child("Latitude").setValue(""+loc.getLatitude());
            //Ref.child(userName).child("Longitude").setValue(""+loc.getLongitude());

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    myMarker = marker;
                    String text = myMarker.getTitle();
                    calculateDistance(text, myMarker);
                    infoBar.setVisibility(View.VISIBLE);
                    navigate.setVisibility(View.INVISIBLE);
                    //distanceBar.setVisibility(View.INVISIBLE);
                    //durationBar.setVisibility(View.INVISIBLE);
                    return false;
                }
            });
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    infoBar.setVisibility(View.INVISIBLE);
                    navigate.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    public void calculateDistance(String text, Marker myMarker)
    {
        float results[] = new float[5];
        DecimalFormat DF = new DecimalFormat(".#");
        Location.distanceBetween(userLocation.latitude, userLocation.longitude, myMarker.getPosition().latitude, myMarker.getPosition().longitude, results);
        infoBar.setText(text /*+ "\n Distance from stop: " + DF.format(results[0]/1000) + " KM"*/);
    }

    public void checkNearest(ArrayList<Marker> allStops, LatLng userLocation)
    {
        closestStop = allStops.get(0);
        DecimalFormat DF = new DecimalFormat(".#");
        float newDistance[] = new float[5];
        float shortestDistance[] = new float[5];
        for(int i=0;i<allStops.size();i++)
        {
            Location.distanceBetween(allStops.get(i).getPosition().latitude,allStops.get(i).getPosition().longitude,userLocation.latitude,userLocation.longitude,newDistance);
            Location.distanceBetween(closestStop.getPosition().latitude,closestStop.getPosition().longitude,userLocation.latitude,userLocation.longitude,shortestDistance);
            if(newDistance[0] < shortestDistance[0])
            {
                closestStop = allStops.get(i);
            }
        }
        markerInfo = closestStop.getTitle();
        mMap.addMarker(new MarkerOptions().position(closestStop.getPosition()).title(closestStop.getTitle()));
        infoBar.setText(closestStop.getTitle() /*+ "\n Distance to this stop: " + DF.format(shortestDistance[0]/1000) + " KM"*/);
        infoBar.setVisibility(View.VISIBLE);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(closestStop.getPosition().latitude, closestStop.getPosition().longitude), 17));

    }

    public void getDistance(String text)
    {
        String word = "";
        for(int i=0;i<text.length();i++)
        {
            if(text.charAt(i) != ' ')
            {
                word += text.charAt(i);
            }
            else
            {
                if(word.equals("km\","))
                {
                    break;
                }
                else
                {
                    tripDistance = word;
                    word = "";
                }
            }
        }
        tripDistance = tripDistance.substring(1);
        //infoBar.setText(infoBar.getText() + "\nDistance to this stop: "+tripDistance + " km");
        distanceBar.setText("Distance: "+tripDistance+" km");
        distanceBar.setVisibility(View.VISIBLE);
        infoBar.setVisibility(View.VISIBLE);
    }

    public void getDuration(String text)
    {
        String word = "";
        String hrs= "" ;
        for(int i=0;i<text.length();i++)
        {
            if(text.charAt(i) != ' ')
            {
                word += text.charAt(i);
            }
            else
            {
                if(word.equals("mins\","))
                {
                    break;
                }
                else if(word.equals("hour"))
                {
                    hrs = tripDuration;
                    tripDuration = word;
                    word = "";
                }
                else
                {
                    tripDuration = word;
                    word = "";
                }
            }
        }
        tripDuration = tripDuration.substring(1);
        if(hrs != "")
        {
            hrs = hrs.substring(1);
            durationBar.setText("Duration: "+hrs+" hr "+tripDuration+" mins");
            //infoBar.setText(infoBar.getText() + "\nTime to this stop: "+ hrs + " hr " +tripDuration + " mins");
        }
        else
        {
            //infoBar.setText(infoBar.getText() + "\nTime to this stop: "+tripDuration + " mins");
            durationBar.setText("Duration: "+tripDuration+" mins");
        }
        infoBar.setVisibility(View.VISIBLE);
        durationBar.setVisibility(View.VISIBLE);
    }


    /////////////////////////////////////////////////////////////////////////////////////////////

    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                //Log.d("Background Task data", data.toString());
                wholeText = data.toString();

                ////////////////////////////////////////////

                ///////////////////////////////////////////
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            //Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.BLUE);

                Log.d("onPostExecute","onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                navigationLine = mMap.addPolyline(lineOptions);
                getDistance(wholeText);
                getDuration(wholeText);
                closestStop.setTitle(closestStop.getTitle()/* + "\nDistance to this stop: "+tripDistance + " km"
                        + "\nTime to this stop: "+tripDuration + " mins"*/);
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
}
