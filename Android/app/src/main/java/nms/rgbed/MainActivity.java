package nms.rgbed;


import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import nms.rgbed.fragments.ClockFragment;
import nms.rgbed.fragments.MainFragment;


public class MainActivity extends Activity implements NotificationsInterface {
    private static final String TAG = "nms";

    private Fragment mainFragment;
    private Fragment clockFragment;
    private Fragment currentFragment;
    private RGBed rgbed;

    private FFTEffects FFTeffects;


    //private Handler mHandler; // Our main handler that will receive callback notifications


    // #defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    // private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    //  private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status


    private BluetoothAdapter mBTAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio

        rgbed = new RGBed(this, mBTAdapter);
        FFTeffects = new FFTEffects(rgbed);

        findViewById(R.id.toggle_bluetooth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleBluetooth();
            }
        });

        findViewById(R.id.connect_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rgbed.connect();
            }
        });

        if (mBTAdapter.isEnabled()) {
            findViewById(R.id.toggle_bluetooth).setVisibility(View.GONE);
            findViewById(R.id.connecting_text).setVisibility(View.VISIBLE);
        }
        else {
            findViewById(R.id.toggle_bluetooth).setVisibility(View.VISIBLE);
            findViewById(R.id.connecting_text).setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        rgbed.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        rgbed.disconnect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent Data){
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                showToast("Bluetooth turned on");
                rgbed.connect();
            }
            else {
                showToast("Error turning on bluetooth");
            }
        }
    }


    public void toggleBluetooth() {
        if (mBTAdapter == null) {
            return;
        }

        if (!mBTAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        else {
            mBTAdapter.disable();
            showToast("Bluetooth turned off");
        }
    }


    public RGBed getRGBed() {
        return rgbed;
    }

    @Override
    public void reconnect() {
        Log.d(TAG, "Reconnect");
        rgbed.disconnect();

        if (mBTAdapter.isEnabled()) {
            rgbed.connect();
        }
        else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    findViewById(R.id.toggle_bluetooth).setVisibility(View.VISIBLE);
                    findViewById(R.id.fragment_container).setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void showConnecting(final boolean show) {
        Log.d(TAG, "Show Connecting: " + show);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.connecting_text).setVisibility(show ? View.VISIBLE : View.GONE);
                findViewById(R.id.toggle_bluetooth).setVisibility(!(mBTAdapter.isEnabled() || show) ? View.VISIBLE : View.GONE);
                findViewById(R.id.fragment_container).setVisibility(!show ? View.VISIBLE : View.GONE);
                findViewById(R.id.connect_button).setVisibility(View.GONE);

                if (!show) {
                    if (currentFragment == null || (currentFragment == mainFragment && mainFragment.isHidden())) {
                        showMainFragment();
                    }
                    else if (currentFragment == clockFragment && clockFragment.isHidden()) {
                        showClockFragment();
                    }
                }
            }
        });

    }

    @Override
    public void showConnectButton() {
        Log.d(TAG, "Show Connect Button");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.connect_button).setVisibility(mBTAdapter.isEnabled() ? View.VISIBLE : View.GONE);
                findViewById(R.id.toggle_bluetooth).setVisibility(!mBTAdapter.isEnabled() ? View.VISIBLE : View.GONE);
                findViewById(R.id.fragment_container).setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showMainFragment() {
        Log.d(TAG, "showMainFragment");

        if (mainFragment == null) {
            mainFragment = new MainFragment();
        }

        currentFragment = mainFragment;

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mainFragment)
                .addToBackStack(null)
                .commit();
    }

    public void showClockFragment() {
        Log.d(TAG, "showClockFragment");

        if (clockFragment == null) {
            clockFragment = new ClockFragment();
        }

        currentFragment = clockFragment;

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, clockFragment)
                .addToBackStack(null)
                .commit();
    }

    public FFTEffects getFFTeffects() {
        return FFTeffects;
    }
}
