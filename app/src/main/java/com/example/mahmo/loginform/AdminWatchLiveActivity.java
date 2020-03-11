package com.example.mahmo.loginform;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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

public class AdminWatchLiveActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private TextView infoBar;
    private TextView distanceBar;
    private TextView durationBar;
    private static final int num = 177;
    private FirebaseAuth auth;
    private DatabaseReference myRef, Ref, reference;
    private FirebaseDatabase database;
    private LocationListener listener;
    private FirebaseUser firebaseUser;
    private String objectName;
    private Marker myMarker;
    private LatLng userLocation;
    private Polyline navigationLine;
    private String wholeText ="";
    private String tripDistance = "";
    private String tripDuration = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_watch_live);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        infoBar = (TextView)findViewById(R.id.InfoBar);
        infoBar.setVisibility(View.INVISIBLE);
        distanceBar = (TextView)findViewById(R.id.adminWatchDistance);
        durationBar = (TextView)findViewById(R.id.adminWatchDuration);
        distanceBar.setVisibility(View.INVISIBLE);
        durationBar.setVisibility(View.INVISIBLE);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        //myRef = database.getReference().child("Active Students");
        Ref = database.getReference().child("Active Buses");

       /* myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                objectName = dataSnapshot.getKey();
                LatLng latLng = new LatLng(Double.parseDouble(dataSnapshot.child("Latitude").getValue().toString()),
                        Double.parseDouble(dataSnapshot.child("Longitude").getValue().toString()));
                myMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Student "+objectName));
                myMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.student));

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                objectName = dataSnapshot.getKey();
                LatLng latLng = new LatLng(Double.parseDouble(dataSnapshot.child("Latitude").getValue().toString()),
                        Double.parseDouble(dataSnapshot.child("Longitude").getValue().toString()));
                myMarker.setPosition(latLng);
                calculateDistance(myMarker.getTitle(), myMarker);
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
        });*/

        Ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                objectName = dataSnapshot.getKey();
                LatLng latLng = new LatLng(Double.parseDouble(dataSnapshot.child("Latitude").getValue().toString()),
                        Double.parseDouble(dataSnapshot.child("Longitude").getValue().toString()));

                String busID="";
                for(int i=0;i<objectName.length();i++)
                {
                    if(objectName.charAt(i)!='@')
                    {
                        busID += objectName.charAt(i);
                    }
                    else
                    {
                        break;
                    }
                }
                busID = busID.substring(5);
                myMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Bus no "+busID));
                myMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.buscar));

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                objectName = dataSnapshot.getKey();
                LatLng latLng = new LatLng(Double.parseDouble(dataSnapshot.child("Latitude").getValue().toString()),
                        Double.parseDouble(dataSnapshot.child("Longitude").getValue().toString()));
                myMarker.setPosition(latLng);
                calculateDistance(myMarker.getTitle(), myMarker);
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

            if(loc != null)
            {
                userLocation = new LatLng(loc.getLatitude(),loc.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLocation.latitude, userLocation.longitude), 12));
            }

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    myMarker = marker;
                    String text = myMarker.getTitle();
                    calculateDistance(text, myMarker);
                    String url;
                    LatLng universityLocation = new LatLng(30.167149, 31.492144);
                    url = getUrl(myMarker.getPosition(), universityLocation);
                    FetchUrl FetchUrl = new FetchUrl();
                    FetchUrl.execute(url);
                    infoBar.setVisibility(View.VISIBLE);
                    return false;
                }
            });
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    infoBar.setVisibility(View.INVISIBLE);
                    distanceBar.setVisibility(View.INVISIBLE);
                    durationBar.setVisibility(View.INVISIBLE);
                }
            });

        }

    }

    public void calculateDistance(String text, Marker myMarker)
    {
        float results[] = new float[5];
        DecimalFormat DF = new DecimalFormat(".#");
        //Location.distanceBetween(30.1698239, 31.4904979, myMarker.getPosition().latitude, myMarker.getPosition().longitude, results);
        infoBar.setText(text /*+ "\n Distance from University: " + DF.format(results[0]/1000) + " KM"*/);
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
                lineOptions.color(Color.TRANSPARENT);

                Log.d("onPostExecute","onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                navigationLine = mMap.addPolyline(lineOptions);
                getDistance(wholeText);
                getDuration(wholeText);
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////


}
