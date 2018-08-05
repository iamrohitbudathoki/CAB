package com.geetapoojakaran.cab;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomersMapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener
      {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location lastlocation;
    LocationRequest mLocationRequest;

    private Button LogoutCustomerButton;
    private FirebaseAuth mAuth;
    private FirebaseUser CurrentUser;
    private Button customerscallcabbutton;
    private String customerID;
    private DatabaseReference customerdatabaseref;
    private LatLng customerpickuplocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers_map);

        mAuth=FirebaseAuth.getInstance();
        CurrentUser=mAuth.getCurrentUser();
        customerID=FirebaseAuth.getInstance().getCurrentUser().getUid();
        customerdatabaseref= FirebaseDatabase.getInstance().getReference().child("Customers Request");


        LogoutCustomerButton=findViewById(R.id.customer_logout_btn);
        customerscallcabbutton=findViewById(R.id.customers_call_cab_btn);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        LogoutCustomerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                LogoutCustomer();
            }
        });

        customerscallcabbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeoFire geoFire=new GeoFire(customerdatabaseref);
                geoFire.setLocation(customerID,new GeoLocation(lastlocation.getLatitude(),lastlocation.getLongitude()));

                customerpickuplocation=new LatLng(lastlocation.getLatitude(),lastlocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(customerpickuplocation).title("Pickup Customer From Here!!!"));


            }
        });

    }




          @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        buildGoogleApiClient();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);



    }

          protected synchronized  void buildGoogleApiClient() {

              mGoogleApiClient = new GoogleApiClient.Builder(this)
                      .addConnectionCallbacks(this)
                      .addOnConnectionFailedListener(this)
                      .addApi(LocationServices.API)
                      .build();

              mGoogleApiClient.connect();

          }

          @Override
          public void onConnected(@Nullable Bundle bundle) {
              mLocationRequest = new LocationRequest();
              mLocationRequest.setInterval(1000);
              mLocationRequest.setFastestInterval(1000);
              mLocationRequest.setPriority(mLocationRequest.PRIORITY_HIGH_ACCURACY);

              if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                  // TODO: Consider calling
                  //    ActivityCompat#requestPermissions
                  // here to request the missing permissions, and then overriding
                  //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                  //                                          int[] grantResults)
                  // to handle the case where the user grants the permission. See the documentation
                  // for ActivityCompat#requestPermissions for more details.
                  return;
              }
              LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

          }

          @Override
          public void onConnectionSuspended(int i) {

          }

          @Override
          public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

          }

          @Override
          public void onLocationChanged(Location location) {

              lastlocation=location;

              LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
              mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
              mMap.animateCamera(CameraUpdateFactory.zoomTo(8));


          }
          private void LogoutCustomer() {

              Intent welcomeintent=new Intent(getApplicationContext(),WelcomeActivity.class);
              welcomeintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
              startActivity(welcomeintent);
              finish();
          }
      }
