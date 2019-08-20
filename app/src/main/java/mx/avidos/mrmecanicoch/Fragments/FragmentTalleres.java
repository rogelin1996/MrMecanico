package mx.avidos.mrmecanicoch.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import mx.avidos.mrmecanicoch.Bases.BaseFragment;
import mx.avidos.mrmecanicoch.R;

public class FragmentTalleres extends BaseFragment {

    private ArrayList<Marker> tmpRealTimeMarkers = new ArrayList<>();
    private ArrayList<Marker> realTimeMarkers = new ArrayList<>();

    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private static final int REQUEST_ACCESS_LOCATION = 1;

    public FragmentTalleres() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_talleres, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                googleMap.clear();

                LocationManager mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        if (location != null) {
                            Location locationUser = new Location("user");
                            locationUser.setLatitude(location.getLatitude());
                            locationUser.setLongitude(location.getLongitude());
                            LatLng ubicationUser = new LatLng(location.getLatitude(), location.getLongitude());
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicationUser, 10));

                        } else {
                            verifyLocationPermissions(getActivity());
                            LatLng ubicacionTemporal = new LatLng(20.6736, -103.344);
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionTemporal, 10));
                        }

                } else {
                    verifyLocationPermissions(getActivity());
                    LatLng ubicacionTemporal = new LatLng(20.6736, -103.344);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionTemporal, 10));
                }

                mDatabaseReference.child("talleres").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (Marker marker: realTimeMarkers){
                            marker.remove();
                        }

                        for (final DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            final double latitud = Double.parseDouble(snapshot.child("latitud").getValue().toString());
                            final double longitud = Double.parseDouble(snapshot.child("longitud").getValue().toString());
                            final String nombre = snapshot.child("nombre").getValue(String.class);

                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(new LatLng(latitud, longitud))
                                    .title(nombre)
                                    .snippet(dataSnapshot.getKey());

                            tmpRealTimeMarkers.add(googleMap.addMarker(markerOptions));
                        }

                        realTimeMarkers.clear();
                        realTimeMarkers.addAll(tmpRealTimeMarkers);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    return;
                }

                googleMap.setMyLocationEnabled(true);

            }
        });
        return view;
    }

    public static void verifyLocationPermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_LOCATION,
                    REQUEST_ACCESS_LOCATION
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_ACCESS_LOCATION) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {

            }
        }
    }

}
