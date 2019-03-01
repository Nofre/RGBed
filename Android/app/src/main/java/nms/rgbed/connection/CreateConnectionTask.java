package nms.rgbed.connection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class CreateConnectionTask extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "nms";

    private static final String ADDRESS = "20:17:05:08:38:12"; //Address of bluetooth module
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier


    private BluetoothAdapter mBTAdapter;
    private BluetoothSocket mBTSocket;
    private CreateConnectionTaskListener listener;

    public CreateConnectionTask(BluetoothAdapter mBTAdapter, CreateConnectionTaskListener listener) {
        this.mBTAdapter = mBTAdapter;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        BluetoothDevice device = mBTAdapter.getRemoteDevice(ADDRESS);

        try {
            mBTSocket = device.createRfcommSocketToServiceRecord(BTMODULEUUID);
            mBTSocket.connect();
        }
        catch (IOException e) {
            Log.e(TAG, e.getMessage());

            try {
                mBTSocket.close();
            }
            catch (IOException e2) {
                Log.e(TAG, e.getMessage());
            }

            listener.connectionError("Socket creation failed. If the problem persists, try to restart RGBed.");
            return null;
        }

        listener.connectionCreated(mBTSocket);
        return null;
    }


    public interface CreateConnectionTaskListener {
        void connectionCreated(BluetoothSocket mBTSocket);

        void connectionError(String message);
    }
}
