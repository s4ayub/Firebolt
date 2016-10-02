package aluminumvalley.fireboltcontroller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


public class ControllerActivity extends AppCompatActivity {

    String deviceName;

    TextView deviceDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);

        Intent intent = getIntent();
        deviceName = intent.getStringExtra("BTID");

        deviceDisplay = (TextView) findViewById(R.id.currentSelectedDevice);

    }

    @Override
    protected void onResume(){
        super.onResume();

        deviceDisplay.setText(deviceName);


    }
}
