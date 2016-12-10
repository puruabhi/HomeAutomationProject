package com.example.android.bluetooth2;

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
import android.os.Message;
import android.view.View;

import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;
    public TextView pairedDevicesTextView;
    public Button turnOnButton;
    public Button turnOffButton;
    public Button searchDevicesButton;
    public Button pairedDevicesButton;
    private ArrayList listSearch;
    ConnectThread mConnectThread;
    private BluetoothDevice mDevice;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Toast.makeText(getApplicationContext(),action,Toast.LENGTH_SHORT).show();

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //Toast.makeText(getApplicationContext(), "Starting Search",Toast.LENGTH_LONG).show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //Toast.makeText(getApplicationContext(), "Finished Searching",Toast.LENGTH_LONG).show();
                searchDevicesButton.setText("search devices");
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //bluetooth device found
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                listSearch.add(device.getName() + " : " + device.getAddress());
                ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1, listSearch){
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        // Get the Item from ListView
                        View view = super.getView(position, convertView, parent);

                        // Initialize a TextView for ListView each Item
                        TextView tv = (TextView) view.findViewById(android.R.id.text1);

                        // Set the text color of TextView (ListView Item)
                        tv.setTextColor(Color.BLACK);

                        // Generate ListView Item using TextView
                        return view;
                    }
                };
                //Toast.makeText(getApplicationContext(), "Found device " + device.getName(),Toast.LENGTH_LONG).show();
                listView.setAdapter(adapter);
            }
            else if(BluetoothDevice.ACTION_ACL_CONNECTED.equals(action))
            {
                //Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_SHORT);
            }
            else if(BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiverIntent();
        setupUI();
    }

    private void setupUI()
    {
        setContentView(R.layout.activity_main);
        BA = BluetoothAdapter.getDefaultAdapter();
        listView = (ListView) findViewById(R.id.listView);
        pairedDevicesTextView = (TextView)findViewById(R.id.pairedDevicesTextView);
        turnOnButton = (Button)findViewById(R.id.turnOnButton);
        turnOffButton = (Button)findViewById(R.id.turnOffButton);
        searchDevicesButton = (Button)findViewById(R.id.searchDeviceButton);
        pairedDevicesButton = (Button)findViewById(R.id.pairedDevicesButton);
        setInitialLayout();
        if(BA==null)
        {
            Toast.makeText(getApplicationContext(), "Bluetooh is not available",Toast.LENGTH_SHORT).show();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = (String) parent.getItemAtPosition(position);
                String address = "";
                //Toast.makeText(getApplicationContext(),name,Toast.LENGTH_SHORT).show();
                int count = 0;
                for(int i=0;i<name.length();i++)
                {
                    if(count==0&&name.charAt(i)==':')
                    {
                        i++;count++;
                    }
                    else if(count>0)address+=name.charAt(i);
                }
                Toast.makeText(getApplicationContext(), "Address: "+address,Toast.LENGTH_SHORT).show();
                mDevice = BA.getRemoteDevice(address);
                mConnectThread = new ConnectThread(mDevice);
                mConnectThread.start();
            }
        });
    }

    public void setInitialLayout()
    {
        pairedDevicesTextView.setText("Paired Devices:");
        pairedDevicesButton.setVisibility(View.GONE);
        if(BA.isEnabled())
        {
            turnOffButton.setVisibility(View.VISIBLE);
            turnOnButton.setVisibility(View.GONE);
            searchDevicesButton.setVisibility(View.VISIBLE);
            pairedDevicesTextView.setVisibility(View.VISIBLE);
            showPairedDevicesList();
        }
        else
        {
            turnOffButton.setVisibility(View.GONE);
            turnOnButton.setVisibility(View.VISIBLE);
            searchDevicesButton.setVisibility(View.GONE);
            pairedDevicesTextView.setVisibility(View.GONE);
        }
    }


    public void turnOn(View view)
    {
        if (!BA.isEnabled()) {
            Intent turn_On = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turn_On, 0);
            Toast.makeText(getApplicationContext(), "Turned on",Toast.LENGTH_SHORT).show();
            while(!BA.isEnabled());
        }
        else {
            Toast.makeText(getApplicationContext(), "Already on", Toast.LENGTH_SHORT).show();
        }
        turnOffButton.setVisibility(View.VISIBLE);
        turnOnButton.setVisibility(View.GONE);
        searchDevicesButton.setVisibility(View.VISIBLE);
        pairedDevicesTextView.setVisibility(View.VISIBLE);
        pairedDevicesTextView.setText("Paired Devices:");
        showPairedDevicesList();
    }

    public void turnOff(View view)
    {
        BA.disable();
        Toast.makeText(getApplicationContext(), "Turned off", Toast.LENGTH_SHORT).show();
        listView.setAdapter(null);
        turnOffButton.setVisibility(View.GONE);
        turnOnButton.setVisibility(View.VISIBLE);
        searchDevicesButton.setVisibility(View.GONE);
        pairedDevicesTextView.setVisibility(View.GONE);
        pairedDevicesButton.setVisibility(View.GONE);
    }

    public void showPairedDevicesList(){
        pairedDevices = BA.getBondedDevices();

        listSearch = new ArrayList();

        for(BluetoothDevice bt : pairedDevices) listSearch.add(bt.getName()+" : "+bt.getAddress());
        Toast.makeText(getApplicationContext(), "Showing Paired Devices",Toast.LENGTH_SHORT).show();

        final ArrayAdapter adapter = new  ArrayAdapter(this,android.R.layout.simple_list_item_1, listSearch);

        listView.setAdapter(adapter);
    }

    public void listOutPairedDevices(View view)
    {
        listView.setAdapter(null);
        pairedDevicesTextView.setText("Paired Devices:");
        pairedDevicesButton.setVisibility(View.GONE);
        listSearch = new ArrayList();

        for(BluetoothDevice bt : pairedDevices) listSearch.add(bt.getName()+" : "+bt.getAddress());
        Toast.makeText(getApplicationContext(), "Showing Paired Devices",Toast.LENGTH_SHORT).show();

        final ArrayAdapter adapter = new  ArrayAdapter(this,android.R.layout.simple_list_item_1, listSearch);

        listView.setAdapter(adapter);
        BA.cancelDiscovery();
    }

    public void registerReceiverIntent()
    {
        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        registerReceiver(mReceiver, filter);
    }

    public void searchNearbyDevices(View view)
    {
        String text = (String) searchDevicesButton.getText();
        if(text.equals("search devices"))
        {
            pairedDevicesTextView.setText("Available Devices: ");
            listView.setAdapter(null);
            listSearch = new ArrayList();
            BA.startDiscovery();
            searchDevicesButton.setText("Cancel");
            pairedDevicesButton.setVisibility(View.VISIBLE);
        }
        else if(text.equals("Cancel"))
        {
            BA.cancelDiscovery();
            searchDevicesButton.setText("search devices");
        }

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
        }
        public void run() {
            BA.cancelDiscovery();
            try {
                mmSocket.connect();
            } catch (IOException connectException) {
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }
        }
        /*public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }*/
    }
}
