package com.bluetooth.radio.bttracker.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class GPSTrackerBLECallblack extends BluetoothGattCallback implements Serializable {
    private static final String TAG = GPSTrackerBLECallblack.class.getSimpleName();
    private Context context;

    public static final String SPEED_CHARACTERISTIC_UUID = "00002a67-0000-1000-8000-00805f9b34fb";
    public static final String LONGITUDE_CHARACTERISTIC_UUID = "00002aaf-0000-1000-8000-00805f9b34fb";
    public static final String LATITUDADE_CHARACTERISTIC_UUID = "00002aae-0000-1000-8000-00805f9b34fb";

    private Map<String,BluetoothGattCharacteristic> charMap;

    public GPSTrackerBLECallblack(Context context) {
        this.context = context;
        this.charMap = new HashMap<>();
    }

    public BluetoothGattCharacteristic getCharcteristicByUUID(String UUID){
        return charMap.get(UUID);
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        // TODO Adicionar uma mensagem á UI para sinalizar conectado ou não
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            Log.d(TAG, "Cnnected");
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            gatt.discoverServices();
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            Log.d(TAG, "Disconnected");
        }
    }


    @Override
    public void onCharacteristicRead(
            BluetoothGatt gatt,
            BluetoothGattCharacteristic characteristic,
            int status
    ) {
        //TODO só um metodo de exemplo, precisa ser desenvolvido
        Log.d(TAG, "Leu");
        // For all other profiles, writes the data formatted in HEX.
        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for(byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));
            Log.d(TAG, "dados: " + stringBuilder.toString());
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);

    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        //TODO pode ser ajustado de acordo com as necessidades do projeto
        for (BluetoothGattService service : gatt.getServices()) {
            Log.d(TAG, "Service UUID: " + service.getUuid().toString());
            for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                Log.d(TAG, "characteristic: " + characteristic.getUuid());
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                if (characteristic.getUuid().toString().equals(SPEED_CHARACTERISTIC_UUID)) {
                    charMap.put(SPEED_CHARACTERISTIC_UUID, characteristic);
                } else if (characteristic.getUuid().toString().equals(LONGITUDE_CHARACTERISTIC_UUID)) {
                    charMap.put(LONGITUDE_CHARACTERISTIC_UUID, characteristic);
                } else if (characteristic.getUuid().toString().equals(LATITUDADE_CHARACTERISTIC_UUID)) {
                    charMap.put(LATITUDADE_CHARACTERISTIC_UUID, characteristic);
                }
            }
        }
    }
}