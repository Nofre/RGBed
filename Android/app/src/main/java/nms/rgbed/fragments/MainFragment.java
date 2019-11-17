package nms.rgbed.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import nms.rgbed.MainActivity;
import nms.rgbed.R;
import top.defaults.colorpicker.ColorObserver;
import top.defaults.colorpicker.ColorPickerView;

public class MainFragment extends Fragment implements View.OnClickListener {

    ColorPickerView colorPicker;
    ImageView micImg;
    View personLayout;
    Button bpall;
    Button bp1;
    Button bp2;
    SeekBar fftMultiplier;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        rootView.findViewById(R.id.show_clock_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).showClockFragment();
            }
        });


        colorPicker = rootView.findViewById(R.id.colorPicker);
        micImg = rootView.findViewById(R.id.mic_img);
        personLayout = rootView.findViewById(R.id.person_layout);
        bpall = rootView.findViewById(R.id.bpall);
        bp1 = rootView.findViewById(R.id.bp1);
        bp2 = rootView.findViewById(R.id.bp2);
        fftMultiplier = rootView.findViewById(R.id.fft_multiplier);

        bpall.setOnClickListener(this);
        bp1.setOnClickListener(this);
        bp2.setOnClickListener(this);
        rootView.findViewById(R.id.bfft1).setOnClickListener(this);
        rootView.findViewById(R.id.bfft2).setOnClickListener(this);
        rootView.findViewById(R.id.bm1).setOnClickListener(this);
        rootView.findViewById(R.id.bm2).setOnClickListener(this);
        rootView.findViewById(R.id.bm3).setOnClickListener(this);
        rootView.findViewById(R.id.bm4).setOnClickListener(this);
        rootView.findViewById(R.id.bm5).setOnClickListener(this);
        rootView.findViewById(R.id.bm6).setOnClickListener(this);

        fftMultiplier.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double multiplier = 1 + progress / 100;
                ((MainActivity) getActivity()).getFFTeffects().setMultiplier(multiplier);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        colorPicker.subscribe(new ColorObserver() {
            @Override
            public void onColor(int color, boolean fromUser) {
                Activity act = getActivity();
                if (act != null) {
                    ((MainActivity) act).getRGBed().setColor(color);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (getActivity() == null) return;

        switch (v.getId()) {
            case R.id.bpall:
                ((MainActivity) getActivity()).getRGBed().togglePerson(0);
                break;
            case R.id.bp1:
                ((MainActivity) getActivity()).getRGBed().togglePerson(1);
                break;

            case R.id.bp2:
                ((MainActivity) getActivity()).getRGBed().togglePerson(2);
                break;

            case R.id.bfft1:
                if (getActivity().checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    ((MainActivity) getActivity()).getRGBed().setMode(0);
                    ((MainActivity) getActivity()).getFFTeffects().setMode(1);
                    ((MainActivity) getActivity()).getFFTeffects().startFFTTasks();
                    colorPicker.setVisibility(View.INVISIBLE);
                    micImg.setVisibility(View.VISIBLE);
                    personLayout.setVisibility(View.INVISIBLE);
                    fftMultiplier.setVisibility(View.VISIBLE);
                }
                else {
                    String[] permissions = new String[1];
                    permissions[0] = Manifest.permission.RECORD_AUDIO;
                    getActivity().requestPermissions(permissions, 0);
                }
                break;

            case R.id.bfft2:
                if (getActivity().checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    ((MainActivity) getActivity()).getRGBed().setMode(0);
                    ((MainActivity) getActivity()).getFFTeffects().setMode(2);
                    ((MainActivity) getActivity()).getFFTeffects().startFFTTasks();
                    colorPicker.setVisibility(View.INVISIBLE);
                    micImg.setVisibility(View.VISIBLE);
                    personLayout.setVisibility(View.INVISIBLE);
                    fftMultiplier.setVisibility(View.VISIBLE);
                }
                else {
                    String[] permissions = new String[1];
                    permissions[0] = Manifest.permission.RECORD_AUDIO;
                    getActivity().requestPermissions(permissions, 0);
                }
                break;

            case R.id.bm1:
                ((MainActivity) getActivity()).getFFTeffects().stopFFTSendTask();
                ((MainActivity) getActivity()).getRGBed().setMode(1);
                colorPicker.setVisibility(View.VISIBLE);
                micImg.setVisibility(View.INVISIBLE);
                personLayout.setVisibility(View.VISIBLE);
                fftMultiplier.setVisibility(View.INVISIBLE);
                break;

            case R.id.bm2:
                ((MainActivity) getActivity()).getFFTeffects().stopFFTSendTask();
                ((MainActivity) getActivity()).getRGBed().setMode(2);
                colorPicker.setVisibility(View.VISIBLE);
                micImg.setVisibility(View.INVISIBLE);
                personLayout.setVisibility(View.INVISIBLE);
                fftMultiplier.setVisibility(View.INVISIBLE);
                break;

            case R.id.bm3:
                ((MainActivity) getActivity()).getFFTeffects().stopFFTSendTask();
                ((MainActivity) getActivity()).getRGBed().setMode(3);
                colorPicker.setVisibility(View.VISIBLE);
                micImg.setVisibility(View.INVISIBLE);
                personLayout.setVisibility(View.INVISIBLE);
                fftMultiplier.setVisibility(View.INVISIBLE);
                break;

            case R.id.bm4:
                ((MainActivity) getActivity()).getFFTeffects().stopFFTSendTask();
                ((MainActivity) getActivity()).getRGBed().setMode(4);
                colorPicker.setVisibility(View.VISIBLE);
                micImg.setVisibility(View.INVISIBLE);
                bp1.setVisibility(View.INVISIBLE);
                bp2.setVisibility(View.INVISIBLE);
                fftMultiplier.setVisibility(View.INVISIBLE);
                break;

            case R.id.bm5:
                ((MainActivity) getActivity()).getFFTeffects().stopFFTSendTask();
                ((MainActivity) getActivity()).getRGBed().setMode(5);
                colorPicker.setVisibility(View.INVISIBLE);
                micImg.setVisibility(View.INVISIBLE);
                personLayout.setVisibility(View.INVISIBLE);
                fftMultiplier.setVisibility(View.INVISIBLE);
                break;

            case R.id.bm6:
                ((MainActivity) getActivity()).getFFTeffects().stopFFTSendTask();
                ((MainActivity) getActivity()).getRGBed().setMode(6);
                colorPicker.setVisibility(View.INVISIBLE);
                micImg.setVisibility(View.INVISIBLE);
                personLayout.setVisibility(View.INVISIBLE);
                fftMultiplier.setVisibility(View.INVISIBLE);
                break;
        }
    }
}
