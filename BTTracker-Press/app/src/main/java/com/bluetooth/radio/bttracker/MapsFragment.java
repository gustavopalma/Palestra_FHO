package com.bluetooth.radio.bttracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.bluetooth.radio.bttracker.ble.GPSTrackerBLECallblack;
import com.bluetooth.radio.bttracker.databinding.ActivityFindDeviceBinding;
import com.bluetooth.radio.bttracker.databinding.FragmentMapsBinding;
import com.bluetooth.radio.bttracker.gps.GPSTracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment {

    private FragmentMapsBinding binding;
    private List<LatLng> coords;
    private List<Float> speed;
    private GoogleMap mMap;

    private FloatingActionButton simulateRoute;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            googleMap.setMyLocationEnabled(true);
            mMap = googleMap;
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        this.coords = new ArrayList<>();
        this.speed = new ArrayList<>();

        this.loadCoords();
        this.loadSpeed();

        this.simulateRoute = view.findViewById(R.id.startRoute);
        this.simulateRoute.setOnClickListener(view1 -> {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    if( mMap != null) {
                        getActivity().runOnUiThread(()->{
                            mMap.clear();
                        });
                        int index = 0;
                        for ( LatLng item : coords) {
                            int finalIndex = index;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    LatLng marker = new LatLng(item.latitude, item.longitude);
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 20));
                                    mMap.addMarker(new MarkerOptions().title("Velocidade(M/S): " + speed.get(finalIndex)).position(marker));
                                }
                            });

                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            sendMoveIntent(item, speed.get(index));
                            index++;
                        }
                    }
                }
            });
            t.start();
        });

        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    private void sendMoveIntent(LatLng location, Float speed){
        Intent i = new Intent(GPSTracker.MOVE_INTENT);
        i.putExtra(GPSTracker.COORDS_EXTRA, location);
        i.putExtra(GPSTracker.SPEED_EXTRA, speed);
        getActivity().sendBroadcast(i);
    }

    private void loadCoords(){
       this.coords.add(new LatLng(-23.182511305836403, -49.3982634969817));
        this.coords.add(new LatLng(-23.1824435008443, -49.39817699573809));
        this.coords.add(new LatLng(-23.182412680381972, -49.39814279757201));
        this.coords.add(new LatLng(-23.18232761586912, -49.39802813313279));
        this.coords.add(new LatLng(-23.182370148131955, -49.39797515952094));
        this.coords.add(new LatLng(-23.18246939002679, -49.39789402270323));
        this.coords.add(new LatLng(-23.182586507697792, -49.39779343986378));
        this.coords.add(new LatLng(-23.182660476701134, -49.39772973739612));
        this.coords.add(new LatLng(-23.182791771578152, -49.397609037997874));
        this.coords.add(new LatLng(-23.182971762473382, -49.39743804716606));
        this.coords.add(new LatLng(-23.183087646890627, -49.397343499304114));
        this.coords.add(new LatLng(-23.18315545155891, -49.397423965570695));
        this.coords.add(new LatLng(-23.183234351490242, -49.397523207298136));
        this.coords.add(new LatLng(-23.183263938954013, -49.39755271159968));
        this.coords.add(new LatLng(-23.183305238109146, -49.397609708537736));
        this.coords.add(new LatLng(-23.183382905144423, -49.3976955392281));
        this.coords.add(new LatLng(-23.183485844716138, -49.39781623863353));
        this.coords.add(new LatLng(-23.183588510308752, -49.397947611258424));
        this.coords.add(new LatLng(-23.183689600510746, -49.39807099286782));
        this.coords.add(new LatLng(-23.183552116283213, -49.39820808075165));
        this.coords.add(new LatLng(-23.183395549558384, -49.398344202850765));
        this.coords.add(new LatLng(-23.18328853706656, -49.39843536762018));
        this.coords.add(new LatLng(-23.183093136446587, -49.398593617947796));
        this.coords.add(new LatLng(-23.182946866963068, -49.39871496307409));
        this.coords.add(new LatLng(-23.182904334883226, -49.39874580847586));
        this.coords.add(new LatLng(-23.182805093310993, -49.398615050794426));
        this.coords.add(new LatLng(-23.182698916677882, -49.398490931642606));
        this.coords.add(new LatLng(-23.182539298203295, -49.3982994728218));
        this.coords.add(new LatLng(-23.18246717836196, -49.39821364213861));
    }
    private void loadSpeed(){
        this.speed.add(6.023f);
        this.speed.add(6.4309f);
        this.speed.add(6.49568f);
        this.speed.add(7.09458f);
        this.speed.add(7.93485f);
        this.speed.add(7.34958f);
        this.speed.add(7.895f);
        this.speed.add(8.580348f);
        this.speed.add(8.39f);
        this.speed.add(9.13598f);
        this.speed.add(5.5f);
        this.speed.add(4.1230956f);
        this.speed.add(4.3012f);
        this.speed.add(5.123947f);
        this.speed.add(5.56901478f);
        this.speed.add(7.294856f);
        this.speed.add(9.23901f);
        this.speed.add(9.513490f);
        this.speed.add(9.23490857f);
        this.speed.add(9.0923454f);
        this.speed.add(5.238901235f);
        this.speed.add(5.554f);
        this.speed.add(6.5454f);
        this.speed.add(6.684f);
        this.speed.add(6.48905f);
        this.speed.add(6.985f);
        this.speed.add(3.987f);
        this.speed.add(1.648f);
        this.speed.add(1.354f);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().stopService(new Intent(getActivity(), GPSTracker.class));
    }

    public void startBLEConnect(BluetoothDevice device){
        Intent i = new Intent(getActivity(), GPSTracker.class);
        i.putExtra("bt_selected", device);
        getActivity().startService(i);
    }
}