package aluminumvalley.fireboltcontroller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


public class ControllerActivity extends AppCompatActivity {

    private String deviceName;
    private TextView deviceDisplay;
    private SeekBar powerControl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);

        Intent intent = getIntent();
        deviceName = intent.getStringExtra("BTID");

        deviceDisplay = (TextView) findViewById(R.id.currentSelectedDevice);

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

        deviceDisplay.setText(deviceName);


    }
}
