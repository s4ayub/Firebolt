package aluminumvalley.fireboltcontroller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
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

public class MainActivity extends AppCompatActivity {

    //The REQUEST_ENABLE_BT constant passed to startActivityForResult()
    // that the system passes back to you in your onActivityResult()
    // implementation as the requestCode parameter.
    private final int REQUEST_ENABLE_BT = 1;

    TextView timerTest;
    TextView message;
    TextView deviceName;

    String selectedDeviceName;

    ProgressDialog mProgress;

    ListView deviceList;
    List<String> foundDevices;

    Button selectorButton;
    Button startBTSearchButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerTest = (TextView)findViewById(R.id.testTimer);
        message = (TextView)findViewById(R.id.message);

        Typeface customFont = Typeface.createFromAsset(getAssets(),  "fonts/LobsterTwo-Regular.ttf");
        message.setTypeface(customFont);

        //Takes selected device and goes to the next activity
        selectorButton = (Button)findViewById(R.id.selector_button);
        selectorButton.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  Intent i = new Intent(MainActivity.this, ControllerActivity.class);
                                                  i.putExtra("BTID", selectedDeviceName);
                                                  startActivity(i);
                                              }

                                          }
        );

        //Start the bluetooth search
        startBTSearchButton = (Button)findViewById(R.id.start_bt_button);
        startBTSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgress = new ProgressDialog(MainActivity.this);
                mProgress.setMessage("Searching for Bluetooth Devices...");
                mProgress.show();

                //Temp timer to simulate BT search
                newTimer();

            }
        });

            deviceList=(ListView)findViewById(R.id.device_list);

            deviceName=(TextView) findViewById(R.id.device_name);
            //For highlight and keeping track of the device when device selected in the list
            //HIGHLIGHT DOESNT REMAIN: FIX
            deviceList.setSelector(R.drawable.device_selector);
            deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {

                @Override
                public void onItemClick (AdapterView < ? > parent, View view,int position,
                long arg3){

                view.setSelected(true);
                selectedDeviceName = (String) parent.getItemAtPosition(position);
                deviceName.setText("Device: " + selectedDeviceName);

                }
            }

            );

        /*  IGNORE THIS FOR NOW
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        }else if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        */

    }

        @Override
    public void onResume(){
        super.onResume();
    }

    /**
     * After BT connections are found, display all the found connections
     */
    private void finishedSearching(){
        mProgress.dismiss();
        message.setText("Availible Connections");
        message.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.secondary_title));
        startBTSearchButton.setVisibility(View.INVISIBLE);
        selectorButton.setVisibility(View.VISIBLE);

        foundDevices = new ArrayList<String>();

        //------------TEMP--------------------//
        foundDevices.add("Device 1: 00000000");
        foundDevices.add("Device 2: 00000000");
        foundDevices.add("Device 3: 00000000");
        foundDevices.add("Device 4: 00000000");
        //------------------------------------//

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                R.layout.list_text,
                R.id.list_content,
                foundDevices);

        deviceList.setAdapter(arrayAdapter);
        deviceList.setVisibility(View.VISIBLE);
    }

    private void newTimer(){

        new CountDownTimer(4000, 1000) {

            public void onTick(long millisUntilFinished) {
                timerTest.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                timerTest.setText("Done!");
                finishedSearching();
            }
        }.start();

    }
}
