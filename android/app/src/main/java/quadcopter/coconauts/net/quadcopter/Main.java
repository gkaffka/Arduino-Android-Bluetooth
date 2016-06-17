package quadcopter.coconauts.net.quadcopter;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

public class Main extends AppCompatActivity {

    public final String TAG = "Main";

    private TextView status;
    private Switch mSwitch;
    private Bluetooth bt;
    private ProgressBar mBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        status = (TextView) findViewById(R.id.textStatus);
        mBar = (ProgressBar) findViewById(R.id.progress_bar);

        findViewById(R.id.restart).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                connectService();
            }
        });

        mSwitch = (Switch) findViewById(R.id.bt_switch);

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("Seekbar", "onStopTrackingTouch ");
                bt.sendMessage(isChecked ? "1" : "2");
            }
        });

        bt = new Bluetooth(this, mHandler);
        connectService();

    }

    public void connectService() {
        try {
            mBar.setVisibility(View.VISIBLE);
            status.setText("Connecting...");
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter.isEnabled()) {
                bt.start();
                bt.connectDevice("HC-06");
                Log.d(TAG, "Btservice started - listening");
                status.setText("Connected");
            } else {
                Log.w(TAG, "Btservice started - bluetooth is not enabled");
                status.setText("Bluetooth Not enabled");
            }
        } catch (Exception e) {
            Log.e(TAG, "Unable to start bt ", e);
            status.setText("Unable to connect " + e);
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case Bluetooth.MESSAGE_STATE_CHANGE:
                    if (msg.arg1 == 3) mBar.setVisibility(View.GONE);
                    Log.d(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    break;
                case Bluetooth.MESSAGE_WRITE:
                    Log.d(TAG, "MESSAGE_WRITE ");
                    break;
                case Bluetooth.MESSAGE_READ:
                    Log.d(TAG, "MESSAGE_READ ");
                    break;
                case Bluetooth.MESSAGE_DEVICE_NAME:
                    Log.d(TAG, "MESSAGE_DEVICE_NAME " + msg);
                    break;
                case Bluetooth.MESSAGE_TOAST:
                    Log.d(TAG, "MESSAGE_TOAST " + msg);
                    break;
            }
        }
    };

}
