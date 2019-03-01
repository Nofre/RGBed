package nms.rgbed.audio_analyzer;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import nms.rgbed.FFTEffects;


public class SamplingLoop extends Thread {
    private static final String TAG = "nms";

    private final int audioSourceId = MediaRecorder.AudioSource.UNPROCESSED;
    private final int sampleRate = 44100;
    private final int fftLen = 4096;
    private final int hopLen = 2048;
    private final String wndFuncName = "Hanning";
    private final int nFFTAverage = 1;
    private final boolean isAWeighting = false;

    private short[] audioSamples = new short[hopLen];
    private volatile boolean isRunning = true;
    private FFTEffects FFTeffects;


    public SamplingLoop(FFTEffects FFTeffects) {
        this.FFTeffects = FFTeffects;
    }

    @Override
    public void run() {
        AudioRecord record;

        // tolerate up to about 1 sec.
        int bufferSampleSize = (int)Math.ceil(1.0 * sampleRate / fftLen) * fftLen;

        try {
            record = new AudioRecord(
                    audioSourceId,
                    sampleRate,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSampleSize * 2); //Samples of 2 bytes
        }
        catch (IllegalArgumentException e) {
            Log.e(TAG, "Fail to initialize AudioRecord.", e);
            return;
        }

        STFT stft = new STFT(fftLen, hopLen, sampleRate, nFFTAverage, wndFuncName, isAWeighting);

        // Start recording
        try {
            record.startRecording();
        }
        catch (IllegalStateException e) {
            Log.e(TAG, "Fail to start recording.", e);
            return;
        }

        while (isRunning) {
            int numOfReadShort = record.read(audioSamples, 0, hopLen);
            stft.feedData(audioSamples, numOfReadShort);

            if (stft.nElemSpectrumAmp() >= nFFTAverage) {
                FFTeffects.updateFFTvalues(stft.getSpectrumAmpDB());
            }
        }

        record.stop();
        record.release();
    }

    public void finish() {
        isRunning = false;
        interrupt();
    }
}
