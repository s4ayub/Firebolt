package aluminumvalley.fireboltcontroller;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
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

    BluetoothDevice selectedDevice;
    String selectedDeviceName;

    ProgressDialog mProgress;

    ListView deviceList;
    Set<BluetoothDevice> pairedDevices;
    List<String> foundDevices;

    Button selectorButton;
    Button startBTSearchButton;

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

                                                      //CONNECT TO DEVICE

                                                      Intent i = new Intent(MainActivity.this, ControllerActivity.class);
                                                      i.putExtra("BTDevice", selectedDevice);
                                                      startActivity(i);
                                                  }
                                              }

                                          }
        );

        //Start the bluetooth search
        startBTSearchButton = (Button) findViewById(R.id.start_bt_button);
        startBTSearchButton.setOnClickListener(new View.OnClickListener() {
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

                    //SET A TIMER?? Display on test timer
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
            }
        }

        );

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * After BT connections are found, display all the found connections
     */
    private void searchForConnections() {

        foundDevices = new ArrayList<String>();
        pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                foundDevices.add(device.getName());
            }
        }

        message.setText("Availible Connections");
        message.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.secondary_title));
        startBTSearchButton.setVisibility(View.INVISIBLE);
        selectorButton.setVisibility(View.VISIBLE);


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                R.layout.list_text,
                R.id.list_content,
                foundDevices);

        deviceList.setAdapter(arrayAdapter);

        mProgress.dismiss();
        deviceList.setVisibility(View.VISIBLE);
    }

}
