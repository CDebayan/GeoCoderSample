package com.example.dc.geocodersample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView latLongText;
    private TextView addressText;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button);
        latLongText = findViewById(R.id.latLongText);
        addressText = findViewById(R.id.addressText);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkProvider();
            }
        });
    }

    private void checkProvider() {
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        boolean isGpsEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isGpsEnable) {
            getCurrentLocation();
        } else if (isNetworkEnable) {
            getCurrentLocation();
        } else {
            Toast.makeText(this, "You must switch on GPS or Network to get your location", Toast.LENGTH_SHORT).show();
        }
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                latLongText.setText(String.format("%s %s", String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude())));
                                getAddress(new LatLng(location.getLatitude(), location.getLongitude()));
                            }
                        }
                    });
        }
    }

    private void getAddress(LatLng latLng) {
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            String address = addresses.get(0).getAddressLine(0);
            String pincode = addresses.get(0).getPostalCode();
            String countryCode = addresses.get(0).getCountryCode();
            String countryName = addresses.get(0).getCountryName();

            addressText.setText(
                    String.format(
                            "Address : %s\n\nPincode : %s\n\nCountry Code : %s\n\nCountry Name : %s\n\n",
                            address, pincode, countryCode, countryName
                    )
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
