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

    private boolean deviceConnected;
    private String deviceName;
    private BluetoothDevice device;

    private BluetoothSocket mmSocket;
    private OutputStream mmOutputStream;
    private InputStream mmInputStream;

    private Button forwardButton;
    private Button backwardButton;

    private TextView deviceDisplay;
    private SeekBar powerControl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);

        deviceConnected = false;

        device =  getIntent().getExtras().getParcelable("BTDevice");

        deviceDisplay = (TextView) findViewById(R.id.currentSelectedDevice);
        forwardButton = (Button) findViewById(R.id.forward_button);
        forwardButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(deviceConnected){
                    byte msg = 1;
                    try {
                        mmOutputStream.write(msg);
                    }catch(Exception e){
                        Log.e("ERROR IS: ", e.toString());
                    }
                }
            }
        });
        backwardButton = (Button) findViewById(R.id.backwards_button);

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
                Toast.makeText(ControllerActivity.this,"seek bar progress:"+progressChanged,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();

        deviceConnected = connectWithBTDevice();
        deviceDisplay.setText(deviceName);

    }

    private boolean connectWithBTDevice(){
        //TODO CHECK THIS ID
        try {
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); // Standard SerialPortService ID;
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
