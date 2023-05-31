package com.bluetooth.radio.bttracker.gps;

import android.Manifest;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.bluetooth.radio.bttracker.ble.GPSTrackerBLECallblack;
import com.google.android.gms.maps.model.LatLng;

public class GPSTracker extends Service implements LocationListener {

    private LocationManager locationManager;
    private GPSTrackerBLECallblack bleCallblack;
    private BluetoothDevice device;

    private BluetoothGatt mBluetoothGatt;

    public static final String MOVE_INTENT = "tracker.move";
    public static final String COORDS_EXTRA = "coords";
    public static final String SPEED_EXTRA = "speed";

    private static final String TAG = GPSTracker.class.getSimpleName();

    private Receiver receiver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.locationManager = locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return super.onStartCommand(intent, flags, startId);
        }
        this.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        this.locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,
                1, this);

        this.receiver = new Receiver();
        registerReceiver(receiver, new IntentFilter(MOVE_INTENT));

        this.bleCallblack = new GPSTrackerBLECallblack(this);
        this.device = intent.getParcelableExtra("bt_selected");
        startBLEConnect();


        return super.onStartCommand(intent, flags, startId);
    }

    public void startBLEConnect(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        this.mBluetoothGatt = this.device.connectGatt(this, true, getImuGattCallback());

    }

    public boolean sendData(String characteristic, Float value) {
        if (ActivityCompat.checkSelfPermission(GPSTracker.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        BluetoothGattCharacteristic x = getImuGattCallback().getCharcteristicByUUID(characteristic);
        if( x == null)
            return false;
        Integer intBits = Float.floatToIntBits(value);
        x.setValue(intBits, BluetoothGattCharacteristic.FORMAT_UINT32, 0);
        boolean status = false;
        do {
            status = mBluetoothGatt.writeCharacteristic(x);
            Log.d(TAG,"status: " + status);
        } while(!status);
        return status;
    }

    private GPSTrackerBLECallblack getImuGattCallback(){
        if ( this.bleCallblack == null)
            bleCallblack = new GPSTrackerBLECallblack(this);
        return bleCallblack;
    }

    @Override
    public void onLocationChanged(Location location) {
        sendData(GPSTrackerBLECallblack.SPEED_CHARACTERISTIC_UUID,location.getSpeed());
        sendData(GPSTrackerBLECallblack.LATITUDADE_CHARACTERISTIC_UUID, (float) location.getLatitude());
        sendData(GPSTrackerBLECallblack.LONGITUDE_CHARACTERISTIC_UUID, (float) location.getLongitude());
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.locationManager.removeUpdates(this);
    }

    class Receiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if ( intent.getAction().equals(MOVE_INTENT) ) {
                LatLng latLng = intent.getParcelableExtra(COORDS_EXTRA);
                Float speed = intent.getFloatExtra(SPEED_EXTRA,0);
                sendData(GPSTrackerBLECallblack.LATITUDADE_CHARACTERISTIC_UUID, (float) latLng.latitude);
                sendData(GPSTrackerBLECallblack.LONGITUDE_CHARACTERISTIC_UUID, (float) latLng.longitude);
                sendData(GPSTrackerBLECallblack.SPEED_CHARACTERISTIC_UUID, speed);
            }
        }
    }


}