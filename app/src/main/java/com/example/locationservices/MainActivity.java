package com.example.locationservices;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Main Activity";
    public static final int REQUEST_LOCATION_PERMISSION = 333;
    private TextView textView1;
    private TextView lastLocation;
    private ImageView imageViewAndroid;
    private Button getLocationButton;
    private FusedLocationProviderClient fusedLocationProviderClient;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageViewAndroid = findViewById(R.id.imageview_android);
        getLocationButton = findViewById(R.id.getlocation_mainactivity);
        lastLocation = findViewById(R.id.getlocation_mainactivity);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        getLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PermissionChecker permissionChecker = new PermissionChecker();
                permissionChecker.check(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
                //get location services
                //line 34 shows error because we are calling getLastLocation from a different class, outside of this method.
                final FusedLocationProviderClient flpClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
                flpClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Log.d(TAG, "Latitude " + location.getLatitude() + "longitude" + location.getLongitude());

                        new FetchAddressTask(MainActivity.this).execute(location);
                    }
                });

                flpClient.getLastLocation().addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                        }
                );
            }
        });


    }


    /**
     * @param requestCode  : The code you specified on line 37:
     * @param permissions  : the permission you passed in on line 23(What you wanted granted)
     * @param grantResults : What the user answered for the permission dialog for EACH permission you requested
     * @see
     */

// uses an array to ask multiple permissions
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                    // permission was granted
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();

                } else {
// will log you out of app
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;

            }
        }
    }

    /**
     * Assyntask parameter: <input, In-progress(action it will perform), Final Output>
     * doInBackground: What you want your ASyncTask to do in the background thread.
     * in our case, we want i t to do a network request to get our address using our Lat & Long.
     * We want to "transform" our location input paramter into a String output via
     * Location(lat/long) ---> geocorder.getFromLocation() --> String (address);
     * method onPostExectue() gets called then doInBrackground is done.
     * Type String gets passed in from doInBackground's return
     * <p>
     * Location(Lat/Long) --> geocorder.getFromLocation() --> String(address) --> onPosExectue --> show in UI.
     * <p>
     * <p>
     * Geocorder requires a Context and Locale for Creation.
     *
     * @see Locale;
     * @see AsyncTask;
     */

    private class FetchAddressTask extends AsyncTask<Location, Void, String> {
        private final String TAG = FetchAddressTask.class.getSimpleName();
        private final Geocoder geocoder;

        public FetchAddressTask(Context context) {

            geocoder = new Geocoder(context, Locale.getDefault());
        }

        @Override
        protected String doInBackground(Location... locations) {
            String returnAddress = "";

            try {
                final List<Address> addressList = geocoder.getFromLocation(
                        locations[0].getLatitude(),
                        locations[0].getLongitude(),
                        1);
                if (addressList == null || addressList.size() == 0) {
                    Log.e(TAG, "Geocorder reesponse error ");
                    return "no address";
                }// if not null, assign return address to first line of our address line.
                Log.d(TAG, "Geocorder success:" + addressList.get(0).getAddressLine(0));
                returnAddress = addressList.get(0).getAddressLine(0);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
            return returnAddress;
        }

        protected void onPostExecute(String output) {
            super.onPostExecute(output);
            ((TextView) findViewById(R.id.textview1_activity_main)).setText(output);
            Toast.makeText(getApplicationContext(), output, Toast.LENGTH_SHORT).show();
        }
    }
}
