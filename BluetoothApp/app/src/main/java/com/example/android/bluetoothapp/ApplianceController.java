package com.example.android.bluetoothapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by abhishek on 20-12-2016.
 */

public class ApplianceController extends Activity {

    TextView bluetoothTextView;
    ImageView bluetoothIcon;
    Button chooseDeviceButton, checkStatusButton;
    ToggleButton led1ToggleButton, led3ToggleButton, led2ToggleButton, led4ToggleButton;
    String btStatus,address;
    Intent intent1;
    private BluetoothDevice mDevice;
    private BluetoothAdapter BA;
    ConnectThread mConnectThread;
    ConnectedThread mConnectedThread;
    Handler mHandler;
    int cStatus;
    String status;
    private final BroadcastReceiver mmReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //Toast.makeText(context,action,Toast.LENGTH_SHORT).show();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //Toast.makeText(getApplicationContext(), "Starting Search",Toast.LENGTH_LONG).show();
            }
            else if(BluetoothDevice.ACTION_ACL_CONNECTED.equals(action))
            {
                //Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_SHORT).show();
            }
            else if(BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action));
            {

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appliance_controller);
        setUI();
        setOnClickListeners();
        intent1 = getIntent();
        setBtImage();
        cStatus = 0;
        status = "0000";
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                byte[] writeBuf = (byte[]) msg.obj;
                int begin = (int)msg.arg1;
                int end = (int)msg.arg2;
                switch(msg.what) {
                    case 1:
                        String writeMessage = new String(writeBuf);
                        writeMessage = writeMessage.substring(begin, end);
                        if(cStatus==0&&writeMessage.charAt(0)=='L')Toast.makeText(getApplicationContext(),writeMessage,Toast.LENGTH_SHORT).show();
                        else if(cStatus==1) {
                            if(writeMessage.charAt(0)=='0'||writeMessage.charAt(0)=='1') {
                                status = writeMessage;
                                setStatusOnCheck();
                            }
                        }
                        break;
                }
            }
        };
    }

    public void setUI(){
        bluetoothTextView = (TextView)findViewById(R.id.bluetoothTextView);
        bluetoothIcon = (ImageView)findViewById(R.id.bluetoothIcon);
        chooseDeviceButton = (Button)findViewById(R.id.chooseDeviceButton);
        checkStatusButton = (Button)findViewById(R.id.checkStatusButton);
        led1ToggleButton = (ToggleButton)findViewById(R.id.led1ToggleButton);
        led2ToggleButton = (ToggleButton)findViewById(R.id.led2ToggleButton);
        led3ToggleButton = (ToggleButton)findViewById(R.id.led3ToggleButton);
        led4ToggleButton = (ToggleButton)findViewById(R.id.led4ToggleButton);
        BA = BluetoothAdapter.getDefaultAdapter();
        registerReceiverIntent();
    }

    public void setStatusOnCheck(){
        if(status.charAt(0)=='0'){
            led1ToggleButton.setChecked(true);
            //Toast.makeText(getApplicationContext(),"Yes",Toast.LENGTH_SHORT).show();
            led1ToggleButton.setBackgroundColor(Color.GREEN);
        }
        else if(status.charAt(0)=='1') {
            led1ToggleButton.setChecked(false);
            led1ToggleButton.setBackgroundColor(Color.RED);
            //Toast.makeText(getApplicationContext(),"No",Toast.LENGTH_SHORT).show();
        }
        if(status.charAt(1)=='0'){
            led2ToggleButton.setChecked(true);
            led2ToggleButton.setBackgroundColor(Color.GREEN);
        }
        else if(status.charAt(1)=='1'){
            led2ToggleButton.setChecked(false);
            led2ToggleButton.setBackgroundColor(Color.RED);
        }
        if(status.charAt(2)=='0'){
            led3ToggleButton.setChecked(true);
            led3ToggleButton.setBackgroundColor(Color.GREEN);
        }
        else if(status.charAt(2)=='1') {
            led3ToggleButton.setChecked(false);
            led3ToggleButton.setBackgroundColor(Color.RED);
        }
        if(status.charAt(3)=='0'){
            led4ToggleButton.setChecked(true);
            led4ToggleButton.setBackgroundColor(Color.GREEN);
        }
        else if(status.charAt(3)=='1'){
            led4ToggleButton.setChecked(false);
            led4ToggleButton.setBackgroundColor(Color.RED);
        }
        cStatus = 0;
    }

    public void registerReceiverIntent()
    {
        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        registerReceiver(mmReceiver, filter);
    }

    public void setBtImage()
    {
        btStatus = intent1.getStringExtra("btStatus");
        if(btStatus.equals("0")){
            bluetoothIcon.setImageResource(R.drawable.bluetooth_disconnected);
            led1ToggleButton.setEnabled(false);
            led2ToggleButton.setEnabled(false);
            led3ToggleButton.setEnabled(false);
            led4ToggleButton.setEnabled(false);
            checkStatusButton.setEnabled(false);
        }
        else if(btStatus.equals("1")){
            bluetoothIcon.setImageResource(R.drawable.bluetooth_connected);
            led1ToggleButton.setEnabled(true);
            led2ToggleButton.setEnabled(true);
            led3ToggleButton.setEnabled(true);
            led4ToggleButton.setEnabled(true);
            checkStatusButton.setEnabled(true);
        }
        else if(btStatus.equals("3")) {
            bluetoothIcon.setImageResource(R.drawable.bluetooth_connected);
            led1ToggleButton.setEnabled(true);
            led2ToggleButton.setEnabled(true);
            led3ToggleButton.setEnabled(true);
            led4ToggleButton.setEnabled(true);
            address = intent1.getStringExtra("address");
            //Toast.makeText(getApplicationContext(),"This address: "+address,Toast.LENGTH_SHORT).show();
            connectBluetooth();
        }
    }

    public void setOnClickListeners() {

        chooseDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),BluetoothConnect.class);
                intent.putExtra("btStatus",btStatus);
                startActivity(intent);
            }
        });

        checkStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"Checking",Toast.LENGTH_SHORT).show();
                String out = "9";
                mConnectedThread.write(out.getBytes());
                cStatus = 1;
            }
        });

        led1ToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String out;
                if(led1ToggleButton.isChecked()){
                    out = "0";
                    mConnectedThread.write(out.getBytes());
                    led1ToggleButton.setBackgroundColor(Color.GREEN);
                    //Toast.makeText(getApplicationContext(),"On",Toast.LENGTH_SHORT).show();
                }
                else{
                    out = "1";
                    mConnectedThread.write(out.getBytes());
                    led1ToggleButton.setBackgroundColor(Color.RED);
                    //Toast.makeText(getApplicationContext(),"Off",Toast.LENGTH_SHORT).show();
                }
            }
        });

        led2ToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String out;
                if(led2ToggleButton.isChecked()){
                    out ="2";
                    mConnectedThread.write(out.getBytes());
                    led2ToggleButton.setBackgroundColor(Color.GREEN);
                }
                else{
                    out = "3";
                    mConnectedThread.write(out.getBytes());
                    led2ToggleButton.setBackgroundColor(Color.RED);
                }
            }
        });

        led3ToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String out;
                if(led3ToggleButton.isChecked()){
                    out = "4";
                    mConnectedThread.write(out.getBytes());
                    led3ToggleButton.setBackgroundColor(Color.GREEN);
                }
                else{
                    out = "5";
                    mConnectedThread.write(out.getBytes());
                    led3ToggleButton.setBackgroundColor(Color.RED);
                }
            }
        });

        led4ToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String out;
                if(led4ToggleButton.isChecked()){
                    out = "6";
                    mConnectedThread.write(out.getBytes());
                    led4ToggleButton.setBackgroundColor(Color.GREEN);
                }
                else{
                    out = "7";
                    mConnectedThread.write(out.getBytes());
                    led4ToggleButton.setBackgroundColor(Color.RED);
                }
            }
        });
    }

    public void connectBluetooth(){
        mDevice = BA.getRemoteDevice(address);
        mConnectThread = new ConnectThread(mDevice);
        mConnectThread.start();
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            mmDevice = device;
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) { }
            mmSocket = tmp;
            //mSocket = mmSocket;
        }
        public void run() {
            BA.cancelDiscovery();
            try {
                mmSocket.connect();
                mConnectedThread = new ConnectedThread(mmSocket);
                mConnectedThread.start();
            } catch (IOException connectException) {
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        public void run() {
            byte[] buffer = new byte[1024];
            int begin = 0;
            int bytes = 0;
            while (true) {
                try {
                    bytes += mmInStream.read(buffer, bytes, buffer.length - bytes);
                    for(int i = begin; i < bytes; i++) {
                        if(buffer[i] == "#".getBytes()[0]) {
                            mHandler.obtainMessage(1, begin, i, buffer).sendToTarget();
                            begin = i + 1;
                            if(i == bytes - 1) {
                                bytes = 0;
                                begin = 0;
                            }
                        }
                    }

                } catch (IOException e) {
                    break;
                }
            }
        }
        public void write(byte[] bytes) {
            try {
                //Toast.makeText(getApplicationContext(),"thread",Toast.LENGTH_SHORT).show();
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
}
