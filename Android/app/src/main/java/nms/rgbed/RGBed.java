package nms.rgbed;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

import nms.rgbed.connection.ConnectionThread;
import nms.rgbed.connection.CreateConnectionTask;

public class RGBed implements CreateConnectionTask.CreateConnectionTaskListener {

    private static final String TAG = "nms";


    private BluetoothAdapter mBTAdapter;
    private BluetoothSocket mBTSocket;
    private ConnectionThread mConnectionThread;
    private NotificationsInterface notificationsInterface;
    private boolean isConnecting;
    private int mode;


    public RGBed(NotificationsInterface notificationsInterface, BluetoothAdapter mBTAdapter) {
        this.notificationsInterface = notificationsInterface;
        this.mBTAdapter = mBTAdapter;
        this.isConnecting = false;
        this.mode = 1;
    }


    public void connect() {
        if (!mBTAdapter.isEnabled() || isConnecting) {
            return;
        }

        Log.d(TAG, "Connecting...");
        isConnecting = true;
        notificationsInterface.showConnecting(true);
        new CreateConnectionTask(mBTAdapter, this).execute();
    }


    public void disconnect() {
        if (mConnectionThread != null) mConnectionThread.cancel();
        try {
            if (mBTSocket != null && mBTSocket.isConnected()) mBTSocket.close();
        }
        catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void setColor(int color) {
        if(mConnectionThread != null)
            mConnectionThread.write("#" + Integer.toHexString(color) + ".");
    }

    public void setMode(int mode) {
        this.mode = mode;
        if(mConnectionThread != null)
            mConnectionThread.write("M" + mode + ".");
    }

    public int getMode() {
        return mode;
    }

    @Nullable
    public ClockStatus requestClockStatus() {
        if(mConnectionThread != null) {
            String result =  mConnectionThread.request("G.");
            Log.d(TAG, "Received Clock Status: " + result);

            ClockStatus status = new ClockStatus();
            status.timestamp = result;
            //TODO

            return status;
        }

        return null;
    }

    public ClockStatus syncTime() {
        if(mConnectionThread != null) {
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            Calendar cal = Calendar.getInstance();

            // Get day of week. Calendar class' week starts by sunday, but the device works with
            // weeks that start by monday, so fix it.
            int dow = (cal.get(Calendar.DAY_OF_WEEK)+6)%7;

            mConnectionThread.write("S" + df.format(cal) + dow  +  ".");

            return requestClockStatus();
        }

        return null;
    }

    // Alarms format: 12:30,,08:04,,,,15:32
    public void setAlarms(String alarms) {
        if(mConnectionThread != null) {
            String[] alarmsArray = alarms.split(",");

            if (alarmsArray.length != 7) {
                Log.e(TAG, "Alarms badly formatted");
                return;
            }

            mConnectionThread.write("A" + alarms + ".");
        }
    }

    public void togglePerson(int i) {
        if(mConnectionThread != null)
            mConnectionThread.write("P" + i + ".");
    }

    public void sendFFT(final int values[]) {
        if(mConnectionThread != null) {
            StringBuilder str = new StringBuilder();
            for (Integer i : values) {
                if (i >= 24) str.append("23");
                else {
                    if (i < 10) str.append("0");
                    str.append(i);
                }
            }

            mConnectionThread.write("F" + str.toString() + ".");
        }
    }

    @Override
    public void connectionCreated(BluetoothSocket mBTSocket) {
        this.mBTSocket = mBTSocket;

        mConnectionThread = new ConnectionThread(mBTSocket, notificationsInterface);
        mConnectionThread.start();

        notificationsInterface.showConnecting(false);
        notificationsInterface.showToast("Connected");

        isConnecting = false;
        Log.d(TAG, "RGBed connected");
    }

    @Override
    public void connectionError(String message) {
        notificationsInterface.showToast(message);
    }
}
