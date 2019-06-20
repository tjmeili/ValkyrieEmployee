package com.meilinger.tj.cssvalkyrieemployee_v2.fragment.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.meilinger.tj.cssvalkyrieemployee_v2.R;

/**
 * Created by TJ on 3/21/2018.
 */

public class EnterJobDialogFragment extends DialogFragment {

    //TODO: Job number starting with a zero

    public EnterJobDialogFragment() {
    }

    public interface JobDialogListener{
        void onJobEntered(int jobNumber);
    }

    private JobDialogListener jobDialogListener = null;

    public static EnterJobDialogFragment newInstance(){
        EnterJobDialogFragment jobDialog = new EnterJobDialogFragment();
        return jobDialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_job_number, container, false);
        final EditText etJobNumber = (EditText) v.findViewById(R.id.etJobNumber);
        Button okButton = (Button) v.findViewById(R.id.EnterJobNumberOkButton);
        Button cancelButton = (Button) v.findViewById(R.id.cancelButton);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String jobNum = etJobNumber.getText().toString();
                if(jobNum.length() != 0){
                    jobDialogListener.onJobEntered(Integer.parseInt(jobNum));
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "No job number entered.", Toast.LENGTH_SHORT);
                    dismiss();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof JobDialogListener) {
            jobDialogListener = (JobDialogListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement JobDialogListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        jobDialogListener = null;
    }
}
