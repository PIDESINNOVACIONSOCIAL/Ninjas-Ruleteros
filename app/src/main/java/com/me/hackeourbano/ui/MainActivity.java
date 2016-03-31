package com.me.hackeourbano.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.me.hackeourbano.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMapClickListener {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    /**
     * Request code for the autocomplete activity. This will be used to identify results from the
     * autocomplete activity in onActivityResult.
     */
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;

    private final float[] colorPalette = {
            BitmapDescriptorFactory.HUE_ORANGE,
            BitmapDescriptorFactory.HUE_YELLOW,
            BitmapDescriptorFactory.HUE_GREEN,
            BitmapDescriptorFactory.HUE_CYAN,
            BitmapDescriptorFactory.HUE_AZURE,
            BitmapDescriptorFactory.HUE_BLUE,
            BitmapDescriptorFactory.HUE_VIOLET,
            BitmapDescriptorFactory.HUE_MAGENTA,
            BitmapDescriptorFactory.HUE_ROSE};

    private ArrayList<MarkerOptions> makerList = new ArrayList<>();
    private MarkerOptions myPoint;

    private GoogleMap mMap;
    //--Play services
    private GoogleApiClient mGoogleApiClient;

    private Location mLastLocation;
    //BottomSheet
    private BottomSheetBehavior behavior;

    @Bind(R.id.button_floating_button) FloatingActionButton floatingActionButton;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setupBottomBehavior();
        setupMap();
    }

    @OnClick(R.id.button_floating_button) void showBottomSheet() {
        //drawRoutes();
        makerList.add(myPoint);
        EventBus.getDefault().post(makerList);
        FragmentManager fragmentManager = getSupportFragmentManager();
        ShowResultsDialog showResultsDialog = new ShowResultsDialog();
        showResultsDialog.show(fragmentManager, null);
    }

    @OnClick(R.id.destination_edit_text) void goToAutocompleteMaps() {
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        Intent intent = null;
        try {
            intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(this);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
        startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
    }

    /**
     * Initialize map Fragment
     */
    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        buildGoogleApiClient();

        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
        else
            Toast.makeText(this, "Not connected...", Toast.LENGTH_SHORT).show();
    }


    private void setupBottomBehavior() {
        behavior = BottomSheetBehavior.from(findViewById(R.id.bottomSheet));
    }

    @Override public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        //Enable a botton which set and focus my current location
        //mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(this);
    }

    /**
     * Initialize google APIClient and add places and location api.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * Overriden method from google playservices in ordes to get the latitude and longitude
     */
    @Override public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            // Add a marker in Sydney and move the camera
            Log.d(LOG_TAG, "Latitude -> " + String.valueOf(mLastLocation.getLatitude()));
            Log.d(LOG_TAG, "Longitude -> " + String.valueOf(mLastLocation.getLongitude()));
            LatLng currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            setMarkerLocation(currentLocation, true);
            //Focus my current location.
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17f));
        }
    }

    private void setMarkerLocation(LatLng currentLocation, boolean current) {
        MarkerOptions maker = new MarkerOptions();
        maker.position(currentLocation)
                .icon(current ? BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                        : generateRandomColoredBitmap())
                .title(current ? "My location" : "Friend location");
        makerList.add(maker);
        mMap.addMarker(maker);
    }

    private BitmapDescriptor generateRandomColoredBitmap() {
        Random rn = new Random();
        int range = colorPalette.length;
        int randomNum = rn.nextInt(range);
        return BitmapDescriptorFactory.defaultMarker(colorPalette[randomNum]);
    }

    /**
     * When a place have been selected.
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(LOG_TAG, "Place Selected: " + place.getName());
                Log.i(LOG_TAG, "Longitude: " + place.getLatLng().latitude);
                Log.i(LOG_TAG, "Latitude: " + place.getLatLng().longitude);

                // Format the place's details and display them in the TextView.
                Log.i(LOG_TAG, String.valueOf((formatPlaceDetails(getResources(), place.getName(),
                        place.getId(), place.getAddress(), place.getPhoneNumber(),
                        place.getWebsiteUri()))));

                //Add point
                myPoint = new MarkerOptions();
                myPoint.position(new LatLng(place.getLatLng().latitude, place.getLatLng().longitude))
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_point)).title("Destination");
                mMap.addMarker(myPoint);

                // Display attributions if required.
                CharSequence attributions = place.getAttributions();

                drawRoutes();
                if (!TextUtils.isEmpty(attributions)) {
                    Log.i(LOG_TAG, String.valueOf((Html.fromHtml(attributions.toString()))));
                } else {
                    Log.i(LOG_TAG, (""));
                }

                floatingActionButton.setVisibility(View.VISIBLE);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.e(LOG_TAG, "Error: Status = " + status.toString());
            } else if (resultCode == RESULT_CANCELED) {
                // Indicates that the activity closed before a selection was made. For example if
                // the user pressed the back button.
            }
        }
    }

    private void drawRoutes() {
        Log.e(LOG_TAG, makerList.toString());

        //Trace my route
        mMap.addPolyline(new PolylineOptions()
                .geodesic(true).
                        add(myPoint.getPosition(),
                                makerList.get(0).getPosition()).color(Color.RED));

        for (MarkerOptions a : makerList) {
            mMap.addPolyline(new PolylineOptions()
                    .geodesic(true).
                            add(myPoint.getPosition(), a.getPosition()));

        }
    }


    /**
     * Helper method to format information about a place nicely.
     */
    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        Log.e(LOG_TAG, res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));

    }

    /**
     * When you click on the map it will give you the location
     *
     * @param latLng
     */
    @Override public void onMapClick(LatLng latLng) {
        //Toast.makeText(this, "lat -> " + latLng.latitude + " lon -> " + latLng.longitude, Toast.LENGTH_SHORT).show();

        setMarkerLocation(latLng, false);
    }

    @Override public void onConnectionSuspended(int i) {
    }

    @Override public void onConnectionFailed(ConnectionResult connectionResult) {
    }
}

