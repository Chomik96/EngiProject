package com.michalfladzinski.engiproject.ui.charts;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.michalfladzinski.engiproject.DatabaseManager;
import com.michalfladzinski.engiproject.MainActivity;
import com.michalfladzinski.engiproject.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ChartsFragment extends Fragment {

    private ChartsViewModel chartsViewModel;
    private String mainURL = "http://18.193.216.152/index.php";
    private TextView avgHumidity, avgPressure, avgTemperature;
    private TextView minHumidity, minPressure, minTemperature;
    private TextView maxHumidity, maxPressure, maxTemperature;
    private EditText startDate, endDate;

    private SimpleDateFormat appFormat = new SimpleDateFormat ("dd/MM/yyyy", Locale.getDefault());
    private SimpleDateFormat databaseFormat = new SimpleDateFormat ("yyy-MM-dd", Locale.getDefault());

    private void getData() throws ParseException {
        String dateStart = startDate.getText().toString();
        final Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse(dateStart);
        String dateEnd = endDate.getText().toString();
        final Date date2 = new SimpleDateFormat("dd/MM/yyyy").parse(dateEnd);

        if(date1.compareTo(date2) <= 0) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        String url = mainURL + "?datestart=" + databaseFormat.format(date1) + "&dateend=" + databaseFormat.format(date2);
                        String response = DatabaseManager.getResult(url);
                        JSONArray resultArray = new JSONArray(response);
                        JSONObject obj = resultArray.getJSONObject(0);

                        avgHumidity.setText(obj.getString("AVG(humidity)"));
                        minHumidity.setText(obj.getString("MIN(humidity)"));
                        maxHumidity.setText(obj.getString("MAX(humidity)"));
                        avgPressure.setText(obj.getString("AVG(pressure)"));
                        minPressure.setText(obj.getString("MIN(pressure)"));
                        maxPressure.setText(obj.getString("MAX(pressure)"));
                        avgTemperature.setText(obj.getString("AVG(temperature)"));
                        minTemperature.setText(obj.getString("MIN(temperature)"));
                        maxTemperature.setText(obj.getString("MAX(temperature)"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        else
        {
            Toast.makeText(getActivity(), "The end date cannot be earlier than the start date.", Toast.LENGTH_SHORT).show();
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        chartsViewModel = ViewModelProviders.of(this).get(ChartsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_charts, container, false);

        avgHumidity = root.findViewById(R.id.humidity_avg);
        minHumidity = root.findViewById(R.id.humidity_min);
        maxHumidity = root.findViewById(R.id.humidity_max);
        avgPressure = root.findViewById(R.id.pressure_avg);
        minPressure = root.findViewById(R.id.pressure_min);
        maxPressure = root.findViewById(R.id.pressure_max);
        avgTemperature = root.findViewById(R.id.temperature_avg);
        minTemperature = root.findViewById(R.id.temperature_min);
        maxTemperature = root.findViewById(R.id.temperature_max);

        startDate = root.findViewById(R.id.start_date);
        endDate = root.findViewById(R.id.end_date);

        startDate.setInputType(InputType.TYPE_NULL);
        endDate.setInputType(InputType.TYPE_NULL);

        Date date = new Date();
        startDate.setText(appFormat.format(date));
        endDate.setText(appFormat.format(date));

        try {
            getData();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                DatePickerDialog picker = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                startDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                try {
                                    getData();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                DatePickerDialog picker = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                endDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                try {
                                    getData();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        return root;
    }
}