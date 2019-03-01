package nms.rgbed;

import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import nms.rgbed.audio_analyzer.SamplingLoop;

public class FFTEffects {
    private static final String TAG = "nms";

    private double[] FFTValues;
    private int[] values = new int[30];
    private int mode = 1;
    private SamplingLoop samplingThread = null;
    private ScheduledExecutorService scheduleTaskExecutor;
    private ScheduledFuture handler;
    private RGBed rgbed;

    public FFTEffects(RGBed rgbed) {
        this.rgbed = rgbed;
        this.scheduleTaskExecutor = Executors.newScheduledThreadPool(1);
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void updateFFTvalues(final double[] spectrumDB) {
        FFTValues = spectrumDB;
    }

    public void startFFTTasks() {
        if (samplingThread != null && handler != null) return;


        samplingThread = new SamplingLoop(this);
        samplingThread.start();

        handler = scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                try {

                    switch(mode) {
                        case 1:
                            values[0] = convertToLampRange(FFTValues[1]);
                            values[1] = convertToLampRange(FFTValues[2]);
                            values[2] = convertToLampRange(FFTValues[3]);
                            values[3] = convertToLampRange(FFTValues[4]);
                            values[4] = convertToLampRange(FFTValues[5]);
                            values[5] = convertToLampRange(FFTValues[6]);
                            values[6] = convertToLampRange(FFTValues[8]);
                            values[7] = convertToLampRange(FFTValues[9]);
                            values[8] = convertToLampRange(FFTValues[10]);
                            values[9] = convertToLampRange(getRangeMax(12, 1));
                            values[10] = convertToLampRange(getRangeMax(18,2));
                            values[11] = convertToLampRange(getRangeMax(22, 2));
                            values[12] = convertToLampRange(getRangeMax(28, 4));
                            values[13] = convertToLampRange(getRangeMax(37, 5));
                            values[14] = convertToLampRange(getRangeMax(46, 6));
                            values[15] = convertToLampRange(getRangeMax(59, 6));
                            values[16] = convertToLampRange(getRangeMax(74, 8));
                            values[17] = convertToLampRange(getRangeMax(92, 8));
                            values[18] = convertToLampRange(getRangeMax(116, 10));
                            values[19] = convertToLampRange(getRangeMax(148, 20));
                            values[20] = convertToLampRange(getRangeMax(185, 20));
                            values[21] = convertToLampRange(getRangeMax(232, 30));
                            values[22] = convertToLampRange(getRangeMax(297, 30));
                            values[23] = convertToLampRange(getRangeMax(372, 40));
                            values[24] = convertToLampRange(getRangeMax(465, 50));
                            values[25] = convertToLampRange(getRangeMax(585, 60));
                            values[26] = convertToLampRange(getRangeMax(743, 80));
                            values[27] = convertToLampRange(getRangeMax(929, 100));
                            values[28] = convertToLampRange(getRangeMax(1161, 140));
                            values[29] = convertToLampRange(getRangeMax(1489, 180));
                            break;

                        case 2:
                            values[0] = values[29] = convertToLampRange(FFTValues[3]);
                            values[1] = values[28] = convertToLampRange(FFTValues[4]);
                            values[2] = values[27] = convertToLampRange(FFTValues[5]);
                            values[3] = values[26] = convertToLampRange(FFTValues[6]);
                            values[4] = values[25] = convertToLampRange(FFTValues[8]);
                            values[5] = values[24] = convertToLampRange(FFTValues[9]);
                            values[6] = values[23] = convertToLampRange(FFTValues[10]);
                            values[7] = values[22] = convertToLampRange(getRangeMax(12, 1));
                            values[8] = values[21] = convertToLampRange(getRangeMax(18,2));
                            values[9] = values[20] = convertToLampRange(getRangeMax(22, 2));
                            values[10] = values[19] = convertToLampRange(getRangeMax(28, 4));
                            values[11] = values[18] = convertToLampRange(getRangeMax(37, 5));
                            values[12] = values[17] = convertToLampRange(getRangeMax(46, 6));
                            values[13] = values[16] = convertToLampRange(getRangeMax(59, 6));
                            values[14] = values[15] = convertToLampRange(getRangeMax(870, 800));
                            break;
                    }

                    rgbed.sendFFT(values);

                }
                catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }, 100, 120, TimeUnit.MILLISECONDS);
    }

    public void stopFFTSendTask() {
        if (handler != null) {
            handler.cancel(false);
            handler = null;
        }

        if (samplingThread != null) {
            samplingThread.finish();
            samplingThread = null;
        }
    }

    private int convertToLampRange(double d) {
        if (d < -72) return 0;
        if (d >= -30) return 23;

        return (int) ((d + 72) * 23 / 42);
    }


    private double getRangeMax(int center, int radius) {
        double max = -120;
        for (int i = center-radius; i < center+radius; ++i) {
            if (FFTValues[i] > max) max = FFTValues[i];
        }
        return max;
    }

}
