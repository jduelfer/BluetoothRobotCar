package com.duelfer.bluetoothrobotcar;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    // bluetooth stuff
    private BluetoothAdapter bluetoothAdapter;
    private Set pariedDevices;
    String address = "98:D3:31:40:1C:D2";
    private ProgressDialog progress;
    BluetoothSocket btSocket;
    private boolean isBtConnected = false;
    static final UUID deviceUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    static final int REQUEST_ENABLE_BT = 1;

    // screen size for handling where touch is made
    Point displaySize;
    Rect windowSize;
    int horizontalCenter;
    int verticalCenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // display size
        Display display = getWindowManager().getDefaultDisplay();
        displaySize = new Point();
        display.getSize(displaySize);
        horizontalCenter = displaySize.x / 2;
        verticalCenter = displaySize.y / 2;
        Log.i("horizontalCenter", String.valueOf(horizontalCenter));
        Log.i("verticalCenter", String.valueOf(verticalCenter));

        // setup bluetooth
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // show message saying that there is no bluetooth adapter
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available",
                    Toast.LENGTH_LONG).show();
        } else {
            if (bluetoothAdapter.isEnabled()) {
                // @todo: do something
                Toast.makeText(getApplicationContext(), "Nice, you are connected to something.",
                        Toast.LENGTH_LONG).show();
                ConnectBT bt = new ConnectBT();
                bt.execute();
            } else {
                // ask user to turn bluetooth on
                Toast.makeText(getApplicationContext(), "Hey! Turn yor bluetooth on!",
                        Toast.LENGTH_LONG).show();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // hold a value for the current output stream so we are not sending too much info
        String currentOuput = "";

        int pointerCount = e.getPointerCount();
        List<Float> xList = new ArrayList<>();
        List<Float> yList = new ArrayList<>();

        for (int i = 0; i < pointerCount; i++) {
            xList.add(i, e.getX(i));
            yList.add(i, e.getY(i));
        }

        switch(e.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.i("Touch down", "Action Pointer Down");
                try {
                    Log.i("xList.get(0)", String.valueOf(xList.get(0)));
                    Log.i("xList.get(1)", String.valueOf(xList.get(1)));
                    if (xList.get(0) <= horizontalCenter && xList.get(1) <= horizontalCenter) {
                        Log.i("Double touch down", "left");
                        btSocket.getOutputStream().write("TO-left".toString().getBytes());
                    } else if (xList.get(0) <= horizontalCenter && xList.get(1) > horizontalCenter) {
                        Log.i("Double touch down", "left & right");
                        btSocket.getOutputStream().write("TO-both".toString().getBytes());
                    } else if (xList.get(0) > horizontalCenter && xList.get(1) <= horizontalCenter) {
                        Log.i("Double touch down", "left & right");
                        btSocket.getOutputStream().write("TO-both".toString().getBytes());
                    } else {
                        Log.i("Double touch down", "right");
                        btSocket.getOutputStream().write("TO-right".toString().getBytes());
                    }
                } catch (IOException exception) {
                    Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                Log.i("Touch up", "Action pointer up");
                try {
                    if (xList.get(0) <= horizontalCenter && xList.get(1) <= horizontalCenter) {
                        Log.i("Double touch down", "left");
                        btSocket.getOutputStream().write("TF-left".toString().getBytes());
                    } else if (xList.get(0) <= horizontalCenter && xList.get(1) > horizontalCenter) {
                        Log.i("Double touch down", "left & right");
                        btSocket.getOutputStream().write("TF-both".toString().getBytes());
                    } else if (xList.get(0) > horizontalCenter && xList.get(1) <= horizontalCenter) {
                        Log.i("Double touch down", "left & right");
                        btSocket.getOutputStream().write("TF-both".toString().getBytes());
                    } else {
                        Log.i("Double touch down", "right");
                        btSocket.getOutputStream().write("TF-right".toString().getBytes());
                    }
                } catch (IOException exception) {
                    Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
            case MotionEvent.ACTION_DOWN:
                Log.i("Touch down", "Action Down");
                try {
                    if (xList.get(0) <= horizontalCenter) {
                        Log.i("Touch down", "left");
                        btSocket.getOutputStream().write("TO-left".toString().getBytes());
                    } else {
                        Log.i("Touch down", "right");
                        btSocket.getOutputStream().write("TO-right".toString().getBytes());
                    }
                } catch (IOException exception) {
                    Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.i("Touch up", "Action UP");
                try {
                    if (xList.get(0) <= horizontalCenter) {
                        Log.i("Touch up", "left");
                        btSocket.getOutputStream().write("TF-left".toString().getBytes());
                    } else {
                        Log.i("Touch up", "right");
                        btSocket.getOutputStream().write("TF-right".toString().getBytes());
                    }
                } catch (IOException exception) {
                    Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
        }

        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (btSocket == null || !isBtConnected) {
                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
                    Log.i("Device : ", device.getName());
                    Log.i("Device address : ", device.getAddress());
                    btSocket = device.createInsecureRfcommSocketToServiceRecord(deviceUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            } catch (IOException e) {
                Log.e("Exception: ", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
}
