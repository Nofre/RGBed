package nms.rgbed.connection;

import android.bluetooth.BluetoothSocket;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import nms.rgbed.NotificationsInterface;

public class ConnectionThread extends Thread {

    private static final String TAG = "nms";
    private static final int MAX_REQUEST_WAIT_ATTEMPTS = 4;

    private final BluetoothSocket socket;
    private final NotificationsInterface notificationsInterface;
    private InputStream inStream;
    private OutputStream outStream;

    public ConnectionThread(BluetoothSocket socket, NotificationsInterface notificationsInterface) {
        this.socket = socket;
        this.notificationsInterface = notificationsInterface;

        try {
            inStream = socket.getInputStream();
            outStream = socket.getOutputStream();
        }
        catch (IOException e) {
            Log.e(TAG, "Error getting socket streams", e);
            notificationsInterface.reconnect();
        }
    }
    

    /* Call this from the main activity to send data to the remote device */
    public void write(String input) {
        Log.d(TAG, "Send: " + input);

        try {
            outStream.write(input.getBytes());
        }
        catch (IOException e) {
            Log.e(TAG, e.getMessage());
            notificationsInterface.reconnect();
        }
    }

    public String request(String input) {
        Log.d(TAG, "Send: " + input);

        try {
            outStream.write(input.getBytes());
        }
        catch (IOException e) {
            Log.e(TAG, e.getMessage());
            notificationsInterface.reconnect();
            return null;
        }

        int attempts = 0;
        
        try {
            while (inStream.available() == 0 && attempts < MAX_REQUEST_WAIT_ATTEMPTS) {
                SystemClock.sleep(100);
                attempts += 1;
            }
        }
        catch (IOException e) {
            Log.e(TAG, e.getMessage());
            notificationsInterface.reconnect();
            return null;
        }

        if (attempts == MAX_REQUEST_WAIT_ATTEMPTS) {
            return null;
        }

        SystemClock.sleep(100);

        byte[] buffer = new byte[1024];
        
        try {
            int availableBytes = inStream.available();
            int readBytes = inStream.read(buffer, 0, availableBytes);
            
            if (availableBytes != readBytes) {
                return null;
            }
        }
        catch (IOException e) {
            Log.e(TAG, e.getMessage());
            notificationsInterface.reconnect();
            return null;
        }

        return new String(buffer);
    }
    
    public void cancel() {
        try {
            socket.close();
        }
        catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

}