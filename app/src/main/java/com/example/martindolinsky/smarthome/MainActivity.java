package com.example.martindolinsky.smarthome;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Switch;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private Set<BluetoothDevice> pairedDevices;
    private BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    Handler h;
    final int RECIEVE_MESSAGE = 1;
    public String addressFromDialog;
    AlertDialog bluetoothDialog;
    private ProgressDialog progress;
    private StringBuilder sb = new StringBuilder();


    Button bluetooth_connect_btn, plus_btn, minus_btn, plus5_btn, minus5_btn;
    Switch switch1,switch2,switch3;
    TextView userView;
    OutputStream out;
    MotionEvent me;


    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // deklarácia grafických objektov
        bluetooth_connect_btn = (Button) findViewById(R.id.bluetooth_connect_btn);
        plus_btn = (Button) findViewById(R.id.plus_btn);
        minus_btn = (Button) findViewById(R.id.minus_btn);
        switch1 = (Switch) findViewById(R.id.switch1);
        switch2 = (Switch) findViewById(R.id.switch2);
        switch3 = (Switch) findViewById(R.id.switch3);
        plus5_btn = (Button) findViewById(R.id.plus5_btn);
        minus5_btn = (Button) findViewById(R.id.minus5_btn);
        userView = (TextView) findViewById(R.id.user);

        bluetooth_connect_btn.setBackgroundColor(Color.GREEN);
        plus_btn.setBackgroundColor(Color.WHITE);
        minus_btn.setBackgroundColor(Color.WHITE);
        plus5_btn.setBackgroundColor(Color.WHITE);
        minus5_btn.setBackgroundColor(Color.WHITE);


        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RECIEVE_MESSAGE:
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);
                        sb.append(strIncom);
                        int endOfLineIndex = sb.indexOf("#"); // zistí koniec riadka

                        if (endOfLineIndex > 0) {
                            String input = sb.substring(0, endOfLineIndex);
                            sb.delete(0, sb.length());

                            try {

                                userView.setText(" ");
                                userView.setText(input); // input = správa z arduina

                            } catch (Exception e) {

                            }
                        }
                        break;
                }
            }
        };


        // Tlačidlo na pripojenie aplikácie s Bluetooth zariadenami
        bluetooth_connect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myBluetooth = BluetoothAdapter.getDefaultAdapter();

                if(myBluetooth == null)
                {
                    // Správa, ktorá sa zobrazí pri nedostupnom Bluetooth zariadení
                    Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();

                    // ukončí aplikáciu
                    finish();
                }
                else if(!myBluetooth.isEnabled())
                {
                    // požiada o potvrdenie pre zapnutie bluetooth od používateľa
                    Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(turnBTon,1);
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                View view1 = inflater.inflate(R.layout.bluetooth_dialog, null);

                ArrayList<String> listDevices = pairedDevicesList();

                ListView listView = (ListView) view1.findViewById(R.id.browse);
                listView.setAdapter(new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,listDevices));
                listView.setOnItemClickListener(myListClickListener);
                builder.setView(view1)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.dismiss();
                            }
                        });
                bluetoothDialog = builder.create();
                bluetoothDialog.setCanceledOnTouchOutside(false);
                bluetoothDialog.show();

            }
        });

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if(isChecked){
                    Toast.makeText(getApplicationContext(), "ON", Toast.LENGTH_SHORT).show();
                    if (btSocket!=null)
                    {
                        try
                        {
                            btSocket.getOutputStream().write("0".getBytes());
                        }
                        catch (IOException e)
                        {
                            msg("Error");
                        }
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "OFF", Toast.LENGTH_SHORT).show();
                    try
                    {
                        btSocket.getOutputStream().write("1".getBytes());
                    }
                    catch (IOException e)
                    {
                        msg("Error");
                    }

                }
            }
        });

        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if(isChecked){
                    Toast.makeText(getApplicationContext(), "ON", Toast.LENGTH_SHORT).show();
                    if (btSocket!=null)
                    {
                        try
                        {
                            btSocket.getOutputStream().write("2".getBytes());
                        }
                        catch (IOException e)
                        {
                            msg("Error");
                        }
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "OFF", Toast.LENGTH_SHORT).show();
                    try
                    {
                        btSocket.getOutputStream().write("3".getBytes());
                    }
                    catch (IOException e)
                    {
                        msg("Error");
                    }

                }
            }
        });

        switch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if(isChecked){
                    Toast.makeText(getApplicationContext(), "ON", Toast.LENGTH_SHORT).show();
                    if (btSocket!=null)
                    {
                        try
                        {
                            btSocket.getOutputStream().write("4".getBytes());
                        }
                        catch (IOException e)
                        {
                            msg("Error");
                        }
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "OFF", Toast.LENGTH_SHORT).show();
                    try
                    {
                        btSocket.getOutputStream().write("5".getBytes());
                    }
                    catch (IOException e)
                    {
                        msg("Error");
                    }

                }
            }
        });

        plus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btSocket!=null)
                {
                    try
                    {
                        btSocket.getOutputStream().write("6".getBytes());
                    }
                    catch (IOException e)
                    {
                        msg("Error");
                    }
                }


            }


        });

        minus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btSocket!=null)
                {
                    try
                    {
                        btSocket.getOutputStream().write("7".getBytes());
                    }
                    catch (IOException e)
                    {
                        msg("Error");
                    }
                }

            }
        });

        plus5_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btSocket!=null)
                {
                    try
                    {
                        btSocket.getOutputStream().write("8".getBytes());
                    }
                    catch (IOException e)
                    {
                        msg("Error");
                    }
                }


            }


        });

        minus5_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btSocket!=null)
                {
                    try
                    {
                        btSocket.getOutputStream().write("9".getBytes());
                    }
                    catch (IOException e)
                    {
                        msg("Error");
                    }
                }


            }


        });
    }

    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView<?> av, View v, int arg2, long arg3)
        {
            // zistí MAC adresu pripájaného zariadenia
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            addressFromDialog = address;


            bluetoothDialog.cancel();


            new ConnectBT().execute();
        }
    };

    private ArrayList pairedDevicesList() {
        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice bt : pairedDevices) {
                list.add(bt.getName() + "\n" + bt.getAddress()); // Zistí meno a zariadenia a jeho MAC adresu
            }
        } else {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }

        return list;
    }

    private void msg(String string)
    {
        Toast.makeText(getApplicationContext(),string, Toast.LENGTH_SHORT).show();
    }


    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; // správne pripojené zariadenie

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(MainActivity.this, "Connecting...", "Please wait!!!");  // zobrazí načítavací dialóg
        }

        @Override
        protected Void doInBackground(Void... devices) // keď je dialóg zobrazený v pozadí prebieha pripájanie
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter(); // získa mobilné bluetooth zariadenie
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(addressFromDialog); // pripojí adresu zariadenia a skontroluje či je dostupné
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(PORT_UUID); // vytvorí RFCOMM (SPP) pripojenie
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect(); //spustí pripojenie

                    progress.dismiss();

                    if (!ConnectSuccess)
                    {
                        msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                        finish();
                    }
                    else
                    {
                        msg("Connected.");
                        isBtConnected = true;
                        InputStream tmpOut = null;

                        try {
                            tmpOut = btSocket.getInputStream();
                        } catch (IOException e) { }

                        byte[] buffer = new byte[256];
                        int bytes;

                        while (true) {
                            try {
                                // čítanie z InputStream
                                bytes = tmpOut.read(buffer);
                                h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();     // pošle správu Handleru
                            } catch (IOException e) {
                                break;
                            }
                        }
                    }
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);

        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

}