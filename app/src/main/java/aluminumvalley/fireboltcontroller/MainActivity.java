package aluminumvalley.fireboltcontroller;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    //The REQUEST_ENABLE_BT constant passed to startActivityForResult()
    // that the system passes back to you in your onActivityResult()
    // implementation as the requestCode parameter.
    private final int REQUEST_ENABLE_BT = 1;

    private final String NO_DEVICE = "No device selected";
    private final String PAIRED_DEVICES = "Paired Devices";
    private final String DISCOVERED_DEVICES = "Discovered Devices";

    private TextView mainTitle;
    private TextView deviceName;
    private ProgressDialog mProgress;
    private ListView deviceList;

    private BluetoothDevice selectedDevice;
    private String selectedDeviceName;

    private Set<BluetoothDevice> pairedDevices = new HashSet<>();
    private Set<BluetoothDevice> discoveredDevices = new HashSet<>();
    private List<String> listAllDevices = new ArrayList<>();

    private Button selectorButton;
    private Button searchBTButton;

    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainTitle = (TextView) findViewById(R.id.main_title);
        Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/LobsterTwo-Regular.ttf");
        mainTitle.setTypeface(customFont);

        deviceList = (ListView) findViewById(R.id.device_list);

        deviceName = (TextView) findViewById(R.id.device_name);
        deviceName.setText(NO_DEVICE);

        //For highlight and keeping track of the device when device selected in the list
        //HIGHLIGHT DOESNT REMAIN: FIX
        deviceList.setSelector(R.drawable.device_selector);
        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                                                      long arg3) {
                    view.setSelected(true);
                    selectedDeviceName = (String) parent.getItemAtPosition(position);

                    if(selectedDeviceName.equals(PAIRED_DEVICES)||selectedDeviceName.equals(DISCOVERED_DEVICES)){
                        deviceName.setText(NO_DEVICE);
                    } else {
                        deviceName.setText("Device: " + selectedDeviceName);
                        selectedDevice = findSelectedDevice();
                    }
                }
            }
        );

        //Takes selected device and goes to the next activity
        selectorButton = (Button) findViewById(R.id.selector_button);
        selectorButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(selectedDevice != null) {
                        Intent i = new Intent(MainActivity.this, ControllerActivity.class);
                        i.putExtra("BTDevice", selectedDevice);
                        startActivity(i);
                    }
                }
            }
        );

        //Start the bluetooth search
        searchBTButton = (Button) findViewById(R.id.search_bt_button);
        searchBTButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgress = new ProgressDialog(MainActivity.this);
                mProgress.setMessage("Searching for Bluetooth Devices...");
                mProgress.show();

                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    Toast.makeText(MainActivity.this, "This device does not support Bluetooth", Toast.LENGTH_LONG).show();
                } else if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBT, REQUEST_ENABLE_BT);
                    searchForConnections();
                } else if (mBluetoothAdapter.isEnabled()){
                    searchForConnections();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    private void searchForConnections() {
        //Wait until state is 12 (Turned On)
        while(mBluetoothAdapter.getState() != 12){
            Log.e("Current State: ", String.valueOf(mBluetoothAdapter.getState()));
        }
        getPairedBTDevices();
        getDiscoveredBTDevices();
    }

    private void finishedSearchingForConnections() {
        mainTitle.setText("Available Connections");
        mainTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.secondary_title));
        searchBTButton.setVisibility(View.INVISIBLE);
        selectorButton.setVisibility(View.VISIBLE);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                R.layout.list_text,
                R.id.list_content,
                listAllDevices);

        deviceList.setAdapter(arrayAdapter);
        mProgress.dismiss();
        deviceList.setVisibility(View.VISIBLE);
    }

    private void getPairedBTDevices(){
        listAllDevices.add(PAIRED_DEVICES);
        pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                listAllDevices.add(device.getName());
            }
        }
    }

    private void getDiscoveredBTDevices(){
        listAllDevices.add(DISCOVERED_DEVICES);
        //Get all available Bluetooth Devices that the phone can discover
        int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);
        mBluetoothAdapter.startDiscovery();
    }

    private BluetoothDevice findSelectedDevice(){
        //IS THERE A BETTER WAY??
        BluetoothDevice btDevice = null;
        if(pairedDevices != null) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals(selectedDeviceName)) {
                    btDevice = device;
                    break;
                }
            }
        }

        if(discoveredDevices != null) {
            for (BluetoothDevice device : discoveredDevices) {
                if (device.getName().equals(selectedDeviceName)) {
                    btDevice = device;
                    break;
                }
            }
        }
        return btDevice;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                discoveredDevices.add(device);
                listAllDevices.add(device.getName());
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Toast.makeText(MainActivity.this, "Finished Discovery", Toast.LENGTH_SHORT).show();
                finishedSearchingForConnections();
            }
        }
    };
}
