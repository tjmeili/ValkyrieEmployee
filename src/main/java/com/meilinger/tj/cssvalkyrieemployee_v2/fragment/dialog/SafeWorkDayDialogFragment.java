package com.meilinger.tj.cssvalkyrieemployee_v2.fragment.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.meilinger.tj.cssvalkyrieemployee_v2.R;

public class SafeWorkDayDialogFragment extends DialogFragment {

    private Button buttonYes, buttonNo;
    private SafeWorkDayDialogListener delegate;

    public SafeWorkDayDialogFragment() {
    }

    public static SafeWorkDayDialogFragment newInstance(){
        return new SafeWorkDayDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog_injury, container);
        buttonYes =  v.findViewById(R.id.buttonInjuryYest);
        buttonNo = v.findViewById(R.id.buttonInjuryNo);
        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                delegate.onSafeWorkDay();
            }
        });
        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delegate.onUnsafeWorkDay();
            }
        });
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof SafeWorkDayDialogListener){
            delegate = (SafeWorkDayDialogListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement InjuryDialogLister");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(delegate != null){
            delegate = null;
        }
    }

    public interface SafeWorkDayDialogListener {
        void onUnsafeWorkDay();
        void onSafeWorkDay();
    }
}
