package com.example.android.bluetooth2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class connected extends MainActivity {

    private TextView connectedTextView;
    private Button turnOn;
    private Button turnOff;
    private BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mmDevice;
    BluetoothSocket mmSocket;
    InputStream mmInputStream;
    OutputStream mmOutputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.connected);
        setupUI();
    }

    public void setupUI()
    {
        mmDevice = mBluetoothAdapter.getRemoteDevice("98:D3:31:FD:13:30");
        turnOn = (Button) findViewById(R.id.turnOnButton);
        turnOff = (Button)findViewById(R.id.turnOffButton);
        connectedTextView = (TextView)findViewById(R.id.connectedTextView);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
        try {
            BluetoothSocket mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();
        }catch (Exception e){
            Toast.makeText(this,"Exception Encountered",Toast.LENGTH_SHORT).show();}
    }

    /*public void turnLedOn()
    {
        String msg = "1";
        try{
            mmOutputStream.write(msg.getBytes());
        }catch(Exception e){Toast.makeText(getApplicationContext(),"Error During On",Toast.LENGTH_SHORT).show();}
    }

    public void turnLedOff()
    {
        String msg = "0";
        try{
            mmOutputStream.write(msg.getBytes());
        }catch(Exception e){Toast.makeText(getApplicationContext(),"Error During On",Toast.LENGTH_SHORT).show();}
    }*/
}
