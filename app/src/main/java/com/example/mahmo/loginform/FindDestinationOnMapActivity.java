package com.example.mahmo.loginform;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
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
import android.widget.EditText;
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

public class FindDestinationOnMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button navigate;
    private TextView distanceBar;
    private TextView durationBar;
    private Button stopNavigation;
    private EditText searchBar;
    private Button searchButton;
    private List<Address> addressList;
    private static final int num = 177;
    private FirebaseAuth auth;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private LocationListener listener;
    private FirebaseUser user;
    private LatLng userLocation;
    private Marker myMarker;
    private String objectName = "";
    private String userName = "";
    private Polyline navigationLine, oldNavigationLine = null;
    private String tripDistance ="";
    private String tripDuration ="";
    private String wholeText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_destination_on_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        searchBar = (EditText)findViewById(R.id.SearchBar);
        searchButton = (Button)findViewById(R.id.SearchButtton);
        stopNavigation = (Button)findViewById(R.id.stopNavigationFindDest);
        navigate = (Button)findViewById(R.id.findDestNavigate);
        stopNavigation.setVisibility(View.INVISIBLE);
        navigate.setVisibility(View.INVISIBLE);
        distanceBar = (TextView)findViewById(R.id.findDistinationDistance);
        durationBar = (TextView)findViewById(R.id.findDistinationDuration);
        distanceBar.setVisibility(View.INVISIBLE);
        durationBar.setVisibility(View.INVISIBLE);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        //myRef = database.getReference().child("Active Students");
        user = auth.getCurrentUser();

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


        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (navigationLine != null) {
                    oldNavigationLine = navigationLine;
                    String url;
                    url = getUrl(new LatLng(location.getLatitude(), location.getLongitude()), myMarker.getPosition());
                    FetchUrl FetchUrl = new FetchUrl();
                    FetchUrl.execute(url);
                    oldNavigationLine.remove();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17));
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

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!searchBar.getText().toString().matches(""))
                {
                    searchAddress();
                }
                distanceBar.setVisibility(View.INVISIBLE);
                durationBar.setVisibility(View.INVISIBLE);
                navigate.setVisibility(View.VISIBLE);
            }
        });

        stopNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationLine.remove();
                stopNavigation.setVisibility(View.INVISIBLE);
                searchBar.setVisibility(View.VISIBLE);
                searchButton.setVisibility(View.VISIBLE);
            }
        });

        navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url;
                url = getUrl(userLocation, myMarker.getPosition());
                FetchUrl FetchUrl = new FetchUrl();
                FetchUrl.execute(url);
                stopNavigation.setVisibility(View.VISIBLE);
                searchBar.setVisibility(View.INVISIBLE);
                searchButton.setVisibility(View.INVISIBLE);
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
            if(loc != null)
            {
                userLocation = new LatLng(loc.getLatitude(),loc.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLocation.latitude, userLocation.longitude), 17));
            }
            //Toast.makeText(FindDestinationOnMapActivity.this,""+loc.getLatitude()+"  "+loc.getLongitude(),Toast.LENGTH_LONG).show();

            //myRef.child(userName).child("Latitude").setValue(""+loc.getLatitude());
            //myRef.child(userName).child("Longitude").setValue(""+loc.getLongitude());

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    myMarker = marker;
                    String text = myMarker.getTitle();
                    calculateDistance(text, myMarker);
                    navigate.setVisibility(View.VISIBLE);
                    return false;
                }
            });
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    navigate.setVisibility(View.INVISIBLE);
                }
            });
            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    mMap.clear();
                    myMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Destination"));
                    calculateDistance(myMarker.getTitle(),myMarker);
                    navigate.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    public void calculateDistance(String text, Marker myMarker)
    {
        float results[] = new float[5];
        //DecimalFormat DF = new DecimalFormat(".#");
        myMarker.setTitle(text);
        //Location.distanceBetween(userLocation.latitude, userLocation.longitude, myMarker.getPosition().latitude, myMarker.getPosition().longitude, results);
        searchBar.setText(null);
    }

    public void searchAddress()
    {
        String addressEntered = searchBar.getText().toString();
        mMap.clear();
        addressList = null;
        Geocoder geocoder = new Geocoder(this);
        try {
            addressList = geocoder.getFromLocationName(addressEntered,3);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(addressList.size() > 0)
        {
            for(int i=0;i<addressList.size();i++)
            {
                Address address = addressList.get(i);
                LatLng addressLocation = new LatLng(address.getLatitude(),address.getLongitude());
                myMarker = mMap.addMarker(new MarkerOptions().position(addressLocation));
                calculateDistance(addressEntered,myMarker);
            }
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(addressList.get(0).getLatitude(), addressList.get(0).getLongitude()), 17));

        }
        else
        {
            Toast.makeText(FindDestinationOnMapActivity.this,"No results found",Toast.LENGTH_SHORT).show();
        }
        searchBar.setText("");
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
        }
        else
        {
            durationBar.setText("Duration: "+tripDuration+" mins");
        }
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
                wholeText = data.toString();
                //Log.d("Background Task data", data.toString());
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
            Log.d("downloadUrl", data.toString());
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
                //if(isGoingBack == true)
                //  lineOptions.color(Color.RED);
                //else
                lineOptions.color(Color.BLUE);

                Log.d("onPostExecute","onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                navigationLine = mMap.addPolyline(lineOptions);
                getDistance(wholeText);
                getDuration(wholeText);
                myMarker.setTitle(myMarker.getTitle()/* + "\nDistance to this stop: "+tripDistance + " km"
                        + "\nTime to this stop: "+tripDuration + " mins"*/);
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

}
