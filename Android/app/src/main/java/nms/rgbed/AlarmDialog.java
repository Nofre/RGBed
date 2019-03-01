package nms.rgbed;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

public class AlarmDialog extends DialogFragment {

    public static AlarmDialog newInstance(int dow) {
        AlarmDialog fragment = new AlarmDialog();
        Bundle args = new Bundle();
        args.putInt("dow", dow);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_set_alarm, container);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final int dow = getArguments().getInt("dow");

        view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        view.findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePicker timePicker = view.findViewById(R.id.time_picker);
                ((AlarmDialogListener) getTargetFragment()).setAlarm(dow, timePicker.getHour() + ":" + timePicker.getMinute());
                dismiss();
            }
        });

        view.findViewById(R.id.disable_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AlarmDialogListener) getTargetFragment()).setAlarm(dow, null);
                dismiss();
            }
        });

    }

    public interface AlarmDialogListener {
        void setAlarm(int dow, @Nullable String value);
    }
}
