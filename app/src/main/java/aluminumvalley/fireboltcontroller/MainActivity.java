package aluminumvalley.fireboltcontroller;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    //The REQUEST_ENABLE_BT constant passed to startActivityForResult()
    // that the system passes back to you in your onActivityResult()
    // implementation as the requestCode parameter.
    private final int REQUEST_ENABLE_BT = 1;

    TextView timerTest;
    TextView message;
    TextView deviceName;
    ProgressDialog mProgress;
    ListView deviceList;

    BluetoothDevice selectedDevice;
    String selectedDeviceName;

    Set<BluetoothDevice> pairedDevices;
    Set<BluetoothDevice> discoveredDevices;
    List<String> listAllDevices;

    Button selectorButton;
    Button searchBTButton;

    BluetoothAdapter mBluetoothAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerTest = (TextView) findViewById(R.id.testTimer);
        message = (TextView) findViewById(R.id.message);

        Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/LobsterTwo-Regular.ttf");
        message.setTypeface(customFont);

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
        searchBTButton = (Button) findViewById(R.id.start_bt_button);
        searchBTButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgress = new ProgressDialog(MainActivity.this);
                mProgress.setMessage("Searching for Bluetooth Devices...");
                mProgress.show();

                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    // Device does not support Bluetooth
                } else if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBT, REQUEST_ENABLE_BT);

                    searchForConnections();
                } else if (mBluetoothAdapter.isEnabled()){
                    searchForConnections();
                }
            }
        });

        deviceList = (ListView) findViewById(R.id.device_list);

        deviceName = (TextView) findViewById(R.id.device_name);
        //For highlight and keeping track of the device when device selected in the list
        //HIGHLIGHT DOESNT REMAIN: FIX
        deviceList.setSelector(R.drawable.device_selector);
        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                                                      long arg3) {

                view.setSelected(true);
                selectedDeviceName = (String) parent.getItemAtPosition(position);
                deviceName.setText("Device: " + selectedDeviceName);

                //IS THERE A BETTER WAY??
                for (BluetoothDevice device : pairedDevices) {
                    if(device.getName().equals(selectedDeviceName)) {
                        selectedDevice = device;
                        break;
                    }
                }

                for (BluetoothDevice device : discoveredDevices) {
                    if(device.getName().equals(selectedDeviceName)) {
                        selectedDevice = device;
                        break;
                    }
                }
            }
        }

        );

    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private void searchForConnections() {

        //Wait until state is 12 (Turned On)
        while(mBluetoothAdapter.getState() != 12){
            Log.e("Current State: ", String.valueOf(mBluetoothAdapter.getState()));
        }

        //Get paired Bluetooth Devices
        listAllDevices.add("Paired Devices"); //TODO Remove
        pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                listAllDevices.add(device.getName());
            }
        }

        listAllDevices.add("Discovered Devices"); //TODO Remove
        //Get all available Bluetooth Devices that the phone can discover
        BroadcastReceiver mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    discoveredDevices.add(device);
                    listAllDevices.add(device.getName());
                }
            }
        };
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter); // TODO Don't forget to unregister during onDestroy

        message.setText("Available Connections");
        message.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.secondary_title));
        searchBTButton.setVisibility(View.INVISIBLE);
        selectorButton.setVisibility(View.VISIBLE);


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                R.layout.list_text,
                R.id.list_content,
                listAllDevices);

        deviceList.setAdapter(arrayAdapter);

        mProgress.dismiss();
        deviceList.setVisibility(View.VISIBLE);
    }

}
