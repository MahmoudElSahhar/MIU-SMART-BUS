package com.example.mahmo.loginform;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Response;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.net.URL;

public class BusOnMapActivity extends FragmentActivity implements OnMapReadyCallback
{

    private GoogleMap mMap;
    private TextView infoBar;
    private Button searchForStudents;
    private Button navigate;
    private TextView studentsCounter;
    private Button increaseStudentsNumber;
    private Button decreaseStudentsNumber;
    private Button stopNavigation;
    private String servingLine ="";
    private int numberOfTakenSeats = 0;
    private int busCapacity;
    private DatabaseReference myRef, Ref, reference, databaseReference;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private LocationListener listener;
    private static final int num = 177;
    private FirebaseUser user;
    private Marker myMarker;
    String busTime ="";
    private LatLng userLocation;
    private String objectName = "";
    private String userName = "";
    private ArrayList<Marker> allStops = new ArrayList<>();
    private ArrayList<Marker> everyStop;
    private ArrayList<Marker> stopsOrder = new ArrayList<>();
    private boolean isGoingBack = false;
    private Polyline navigationLine;
    private int routeCounter = 0;
    private String neededDistance ="";
    private String wholeText ="";
    public static String studentID = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_on_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        infoBar = (TextView)findViewById(R.id.InfoBar);
        navigate = (Button)findViewById(R.id.busNavigate);
        studentsCounter = (TextView)findViewById(R.id.StudentsCounter);
        stopNavigation = (Button)findViewById(R.id.stopNavigationBus);
        searchForStudents = (Button)findViewById(R.id.searchForStudents);
        increaseStudentsNumber = (Button)findViewById(R.id.increaseStudentsNumber);
        decreaseStudentsNumber = (Button)findViewById(R.id.decreaseStudentsNumber);
        infoBar.setVisibility(View.INVISIBLE);
        stopNavigation.setVisibility(View.INVISIBLE);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        searchForStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BusOnMapActivity.this,GetStudentIDActivity.class));
            }
        });

        for(int i=0;i<user.getEmail().length();i++)
        {
            if(user.getEmail().charAt(i)!='@')
            {
                userName += user.getEmail().charAt(i);
            }
            else
            {
                break;
            }
        }

        userName = userName.substring(5);

        databaseReference = database.getReference().child("Active Buses").child("Bus no "+userName).child("Students On Bus");

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                studentID = dataSnapshot.getKey();
                numberOfTakenSeats++;
                studentsCounter.setText(""+numberOfTakenSeats);
                startActivity(new Intent(BusOnMapActivity.this,GetStudentIDActivity.class));
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

        reference = database.getReference().child("Available Buses").child("Bus no "+userName);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getKey().equals("Bus Capacity"))
                {
                    busCapacity = Integer.parseInt(dataSnapshot.getValue().toString());
                    Ref.child("Bus no "+userName).child("Students On Board").setValue(""+numberOfTakenSeats+"/"+busCapacity);
                }
                else if(dataSnapshot.getKey().equals("Serving Line"))
                {
                    servingLine = dataSnapshot.getValue().toString();
                    Ref.child("Bus no "+userName).child("Serving Line").setValue(""+servingLine);
                }
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

        userName = userName.substring(0,1).toUpperCase() + userName.substring(1).toLowerCase();
        Ref = database.getReference().child("Active Buses");
        myRef = database.getReference().child("Bus Lines").child(BusWelcomeActivity.servingline);



        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                objectName = dataSnapshot.getKey();
                busTime = dataSnapshot.child("Time").getValue().toString();
                LatLng latLng = new LatLng(Double.parseDouble(dataSnapshot.child("Latitude").getValue().toString()),
                        Double.parseDouble(dataSnapshot.child("Longitude").getValue().toString()));

                Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(objectName + " stop\n" + "Bus arrives at "+busTime));
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


        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Ref.child("Bus no "+userName).child("Latitude").setValue(""+location.getLatitude());
                Ref.child("Bus no "+userName).child("Longitude").setValue(""+location.getLongitude());
                mMap.clear();
                for(int i=0;i<allStops.size();i++)
                {
                    mMap.addMarker(new MarkerOptions().position(allStops.get(i).getPosition()));
                }
                String url;
                for(int i=0;i<allStops.size();i++)
                {
                    if(i==0)
                    {
                        url = getUrl(userLocation, stopsOrder.get(0).getPosition());
                    }
                    else
                    {
                        url = getUrl(stopsOrder.get(i-1).getPosition(), stopsOrder.get(i).getPosition());
                    }
                    FetchUrl FetchUrl = new FetchUrl();
                    FetchUrl.execute(url);
                }

                isGoingBack = true;
                String URl;
                LatLng universityLocation = new LatLng(30.167149, 31.492144);
                URl = getUrl(stopsOrder.get(allStops.size() - 1).getPosition(), universityLocation);
                FetchUrl FetchUrlSecond = new FetchUrl();
                FetchUrlSecond.execute(URl);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17));
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

        increaseStudentsNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(numberOfTakenSeats < busCapacity)
                {
                    numberOfTakenSeats++;
                    Ref.child("Bus no "+userName).child("Students On Board").setValue("" + numberOfTakenSeats + "/" + busCapacity);
                }
                else
                    Toast.makeText(BusOnMapActivity.this, "Bus is full", Toast.LENGTH_SHORT).show();
                studentsCounter.setText(""+numberOfTakenSeats);
            }
        });

        decreaseStudentsNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(numberOfTakenSeats > 0)
                {
                    numberOfTakenSeats--;
                    Ref.child("Bus no "+userName).child("Students On Board").setValue("" + numberOfTakenSeats + "/" + busCapacity);
                }
                else
                    Toast.makeText(BusOnMapActivity.this, "Bus is empty", Toast.LENGTH_SHORT).show();
                studentsCounter.setText(""+numberOfTakenSeats);
            }
        });

        stopNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                //Toast.makeText(BusOnMapActivity.this, "THIS: "+wholeText.substring(716,725), Toast.LENGTH_LONG).show();
                for(int i=0;i<allStops.size();i++)
                {
                    mMap.addMarker(new MarkerOptions().position(allStops.get(i).getPosition()));
                }
                stopNavigation.setVisibility(View.INVISIBLE);
                navigate.setVisibility(View.VISIBLE);
            }
        });

        navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Getting URL to the Google Directions API

                Ref.child("Bus no "+userName).child("Latitude").setValue(""+userLocation.latitude);
                Ref.child("Bus no "+userName).child("Longitude").setValue(""+userLocation.longitude);
                Ref.child("Bus no "+userName).child("Students On Board").setValue(""+numberOfTakenSeats+"/"+busCapacity);
                Ref.child("Bus no "+userName).child("Serving Line").setValue(""+servingLine);

                arrangeStops(userLocation);

                String url;
                for(int i=0;i<allStops.size();i++)
                {
                    if(i==0)
                    {
                        url = getUrl(userLocation, stopsOrder.get(0).getPosition());
                    }
                    else
                    {
                        url = getUrl(stopsOrder.get(i-1).getPosition(), stopsOrder.get(i).getPosition());
                    }
                    FetchUrl FetchUrl = new FetchUrl();
                    // Start downloading json data from Google Directions API
                    FetchUrl.execute(url);
                }

                isGoingBack = true;
                String URl;
                LatLng universityLocation = new LatLng(30.167149, 31.492144);
                URl = getUrl(stopsOrder.get(allStops.size() - 1).getPosition(), universityLocation);
                FetchUrl FetchUrlSecond = new FetchUrl();
                // Start downloading json data from Google Directions API
                FetchUrlSecond.execute(URl);
                //move map camera
                Toast.makeText(BusOnMapActivity.this,"Navigation is starting... Please wait",Toast.LENGTH_LONG).show();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
                stopNavigation.setVisibility(View.VISIBLE);
                navigate.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void onBackPressed(){
        super.onBackPressed();
        DatabaseReference item = FirebaseDatabase.getInstance().getReference().child("Active Buses").child("Bus no " + userName);
        item.removeValue();
        finish();
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
            //Location location = mMap.getMyLocation();
            LocationManager service = (LocationManager)getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = service.getBestProvider(criteria, false);
            Location loc = service.getLastKnownLocation(provider);
            //myMarker = mMap.addMarker(new MarkerOptions().position(userLocation));
            //myMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.buscar));
            //if(loc != null)
            {
                userLocation = new LatLng(loc.getLatitude(),loc.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLocation.latitude, userLocation.longitude), 17));
                Ref.child("Bus no "+userName).child("Latitude").setValue(""+loc.getLatitude());
                Ref.child("Bus no "+userName).child("Longitude").setValue(""+loc.getLongitude());
            }


            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    myMarker = marker;
                    String text = myMarker.getTitle();
                    calculateDistance(text, myMarker);
                    infoBar.setVisibility(View.VISIBLE);
                    return false;
                }
            });
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    infoBar.setVisibility(View.INVISIBLE);
                }
            });
        }
    }
    public void calculateDistance(String text, Marker myMarker)
    {
        float results[] = new float[5];
        DecimalFormat DF = new DecimalFormat(".#");
        //Location.distanceBetween(30.1698239, 31.4904979, myMarker.getPosition().latitude, myMarker.getPosition().longitude, results);
        infoBar.setText(text/* + "\n Distance from University: " + DF.format(results[0]/1000) + " KM"*/);
    }

    public void arrangeStops(LatLng userLocation)
    {
        everyStop  = new ArrayList<>(allStops);
        float longestDistance[] = new float[5];
        float farStop = 0;
        Marker farMarker = everyStop.get(0);
        for(int i=0;i<allStops.size();i++)
        {
            farStop = 0;
            for(int j=0;j<everyStop.size();j++)
            {
                Location.distanceBetween(everyStop.get(j).getPosition().latitude,everyStop.get(j).getPosition().longitude,30.1698239, 31.4904979, longestDistance);
                if(longestDistance[0] > farStop)
                {
                    farStop = longestDistance[0];
                    farMarker = everyStop.get(j);
                }
            }
            everyStop.remove(farMarker);
            stopsOrder.add(farMarker);
        }
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
                JSONObject root=new JSONObject(data);
                JSONArray array_rows=root.getJSONArray("rows");
                Log.d("JSON","array_rows:"+array_rows);
                JSONObject object_rows=array_rows.getJSONObject(0);
                Log.d("JSON","object_rows:"+object_rows);
                JSONArray array_elements=object_rows.getJSONArray("elements");
                Log.d("JSON","array_elements:"+array_elements);
                JSONObject  object_elements=array_elements.getJSONObject(0);
                Log.d("JSON","object_elements:"+object_elements);
                JSONObject object_duration=object_elements.getJSONObject("duration");
                JSONObject object_distance=object_elements.getJSONObject("distance");

                JSONObject durationtext = object_duration.getJSONObject("text");

                neededDistance = durationtext.toString();

                //infoBar.setText(""+neededDistance+" km");
                infoBar.setVisibility(View.VISIBLE);

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
                routeCounter++;
                navigationLine = mMap.addPolyline(lineOptions);
                if(allStops.size() + 1 == routeCounter)
                {
                    navigationLine.setColor(Color.BLACK);
                }

            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

}
