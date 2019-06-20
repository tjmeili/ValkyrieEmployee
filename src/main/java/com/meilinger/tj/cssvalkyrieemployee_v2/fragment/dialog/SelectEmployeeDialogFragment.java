package com.meilinger.tj.cssvalkyrieemployee_v2.fragment.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.meilinger.tj.cssvalkyrieemployee_v2.R;

import java.util.ArrayList;

import server.data.Employee;


public class SelectEmployeeDialogFragment extends DialogFragment {

    private int selectedEmployee = -1;

    private EmployeeSelectListener delegate;
    private ListView listView;
    private static ArrayList<Employee> employees;

    public SelectEmployeeDialogFragment() {}


    public static SelectEmployeeDialogFragment newInstance(ArrayList<Employee> emps) {
        SelectEmployeeDialogFragment fragment = new SelectEmployeeDialogFragment();
        employees = emps;
        return fragment;
    }

    public interface EmployeeSelectListener{
        void onEmployeeSelected(Employee e);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.dialog_select_employee, container, false);
        listView = (ListView) v.findViewById(R.id.employeeListView);
        listView.setAdapter(new EmployeeListAdapter(getActivity(), R.layout.list_item_employee));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedEmployee = position;
            }
        });
        Button button = (Button) v.findViewById(R.id.SelectEmployeeOkButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedEmployee != -1){
                    if(delegate != null){
                        delegate.onEmployeeSelected(employees.get(selectedEmployee));
                    }
                } else {
                    Toast.makeText(getActivity(), "Select a user before continuing.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof EmployeeSelectListener) {
            delegate = (EmployeeSelectListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement EmployeeSelectListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        delegate = null;
    }

    private class EmployeeListAdapter extends ArrayAdapter<Employee> {

        private EmployeeListAdapter(@NonNull Context context, int resource) {
            super(context, resource, employees);
        }

        private class ViewHolder{
            TextView tvName;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder holder = null;

            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item_employee, parent, false);
                holder = new ViewHolder();
                holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if(holder != null){
                Employee e = employees.get(position);
                holder.tvName.setText(e.getFirstName() + " " + e.getLastName());
            }
            return convertView;
        }

        @Nullable
        @Override
        public Employee getItem(int position) {
            return super.getItem(position);
        }
    }

}
