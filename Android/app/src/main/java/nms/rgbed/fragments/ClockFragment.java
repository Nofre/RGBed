package nms.rgbed.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import nms.rgbed.AlarmDialog;
import nms.rgbed.ClockStatus;
import nms.rgbed.MainActivity;
import nms.rgbed.R;

public class ClockFragment extends Fragment implements AlarmDialog.AlarmDialogListener, View.OnClickListener {

    private String[] alarmsArray = new String[7];

    private View rootView;
    private TextView currentTime;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_clock, container, false);

        currentTime = rootView.findViewById(R.id.currentTime);

        rootView.findViewById(R.id.back_button).setOnClickListener(this);
        rootView.findViewById(R.id.sync_time_button).setOnClickListener(this);
        rootView.findViewById(R.id.save_alarms_button).setOnClickListener(this);
        rootView.findViewById(R.id.set_monday).setOnClickListener(this);
        rootView.findViewById(R.id.set_tuesday).setOnClickListener(this);
        rootView.findViewById(R.id.set_wednesday).setOnClickListener(this);
        rootView.findViewById(R.id.set_thursday).setOnClickListener(this);
        rootView.findViewById(R.id.set_friday).setOnClickListener(this);
        rootView.findViewById(R.id.set_saturday).setOnClickListener(this);
        rootView.findViewById(R.id.set_sunday).setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ClockStatus status = ((MainActivity) getActivity()).getRGBed().requestClockStatus();

        if (status != null &&status.timestamp != null) {
            currentTime.setText(status.timestamp);
        }
        else {
            currentTime.setText(R.string.error_current_time);
            return;
        }

        //TODO
    }

    @Override
    public void setAlarm(int dow, @Nullable String value) {
        alarmsArray[dow-1] = value;
        int id = 0;

        switch (dow) {
            case 1:
                id = R.id.value_monday;
                break;
            case 2:
                id = R.id.value_tuesday;
                break;
            case 3:
                id = R.id.value_wednesday;
                break;
            case 4:
                id = R.id.value_thursday;
                break;
            case 5:
                id = R.id.value_friday;
                break;
            case 6:
                id = R.id.value_saturday;
                break;
            case 7:
                id = R.id.value_sunday;
                break;
        }

        ((TextView) rootView.findViewById(id)).setText(value != null ? value : "Disabled");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_button:
                ((MainActivity) getActivity()).showMainFragment();
                break;

            case R.id.sync_time_button:
                ClockStatus status = ((MainActivity) getActivity()).getRGBed().syncTime();
                if (status != null && status.timestamp != null) {
                    currentTime.setText(status.timestamp);
                }
                else {
                    currentTime.setText(R.string.error_current_time);
                }

                break;

            case R.id.save_alarms_button:
                StringBuilder alarms = new StringBuilder();
                for (String alarm : alarmsArray) {
                    if (alarm != null) alarms.append(alarm);
                    alarms.append(",");
                }
                ((MainActivity) getActivity()).getRGBed().setAlarms(alarms.toString());
                break;

            case R.id.set_monday:
                AlarmDialog dialogMonday = AlarmDialog.newInstance(1);
                dialogMonday.setTargetFragment(this, 0);
                dialogMonday.show(getFragmentManager(), "alarmDialog");
                break;

            case R.id.set_tuesday:
                AlarmDialog dialogTuesday = AlarmDialog.newInstance(2);
                dialogTuesday.setTargetFragment(this, 0);
                dialogTuesday.show(getFragmentManager(), "alarmDialog");
                break;

            case R.id.set_wednesday:
                AlarmDialog dialogWednesday = AlarmDialog.newInstance(3);
                dialogWednesday.setTargetFragment(this, 0);
                dialogWednesday.show(getFragmentManager(), "alarmDialog");
                break;

            case R.id.set_thursday:
                AlarmDialog dialogThursday = AlarmDialog.newInstance(4);
                dialogThursday.setTargetFragment(this, 0);
                dialogThursday.show(getFragmentManager(), "alarmDialog");
                break;

            case R.id.set_friday:
                AlarmDialog dialogFriday = AlarmDialog.newInstance(5);
                dialogFriday.setTargetFragment(this, 0);
                dialogFriday.show(getFragmentManager(), "alarmDialog");
                break;

            case R.id.set_saturday:
                AlarmDialog dialogSaturday = AlarmDialog.newInstance(6);
                dialogSaturday.setTargetFragment(this, 0);
                dialogSaturday.show(getFragmentManager(), "alarmDialog");
                break;

            case R.id.set_sunday:
                AlarmDialog dialogSunday = AlarmDialog.newInstance(7);
                dialogSunday.setTargetFragment(this, 0);
                dialogSunday.show(getFragmentManager(), "alarmDialog");
                break;
        }
    }
}
