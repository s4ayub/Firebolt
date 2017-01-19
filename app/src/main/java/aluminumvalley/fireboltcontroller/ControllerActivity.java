package aluminumvalley.fireboltcontroller;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


public class ControllerActivity extends AppCompatActivity {

    private final String STANDARD_SERIAL_PORT_SERVICE_ID = "00001101-0000-1000-8000-00805f9b34fb";

    private boolean deviceConnected;
    private BluetoothDevice device;

    private BluetoothSocket mmSocket;
    private OutputStream mmOutputStream;
    private InputStream mmInputStream;

    private Button mainSwitch;
    private Button forwardButton;
    private Button backwardButton;

    private TextView deviceDisplay;
    private TextView currentPower;

    private SeekBar powerControl;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);

        deviceConnected = false;

        device =  getIntent().getExtras().getParcelable("BTDevice");

        deviceDisplay = (TextView) findViewById(R.id.currentSelectedDevice);
        deviceDisplay.setText(device.getName());

        //IF Main switch is turned off make sure no buttons work
        mainSwitch = (Button) findViewById(R.id.main_switch);

        forwardButton = (Button) findViewById(R.id.forward_button);
        forwardButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(deviceConnected){
                    //byte msg = (byte) powerControl.getProgress();
                    Log.e("PROGRESS VALUE: ", String.valueOf(powerControl.getProgress()));
                    //byte msg = (byte) 1;
                    try {
                        mmOutputStream.write(String.valueOf(powerControl.getProgress()).getBytes());
                    }catch(Exception e){
                        Log.e("ERROR IS: ", e.toString());
                    }
                }
            }
        });
        backwardButton = (Button) findViewById(R.id.backwards_button);
        backwardButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(deviceConnected){
                    //byte msg = (byte) -(powerControl.getProgress()) ;
                    Log.e("PROGRESS VALUE: ", String.valueOf(powerControl.getProgress()));
                    byte msg = (byte) 2;
                    try {
                        mmOutputStream.write(msg);
                    }catch(Exception e){
                        Log.e("ERROR IS: ", e.toString());
                    }
                }
            }
        });

        currentPower = (TextView) findViewById(R.id.current_power);
        powerControl = (SeekBar) findViewById(R.id.power_control_bar);
        powerControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                progressChanged = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                currentPower.setText(String.valueOf(progressChanged));
                Toast.makeText(ControllerActivity.this,"seek bar progress:" + progressChanged,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(!deviceConnected) {
            deviceConnected = connectWithBTDevice();
        }

        currentPower.setText(String.valueOf(powerControl.getProgress()));
    }

    private boolean connectWithBTDevice(){
        try {
            UUID uuid = UUID.fromString(STANDARD_SERIAL_PORT_SERVICE_ID);
            mmSocket = device.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();
            return true;
        }catch(Exception e){
            Log.e("ERROR IS: ", e.toString());
        }
        return false;
    }
}
