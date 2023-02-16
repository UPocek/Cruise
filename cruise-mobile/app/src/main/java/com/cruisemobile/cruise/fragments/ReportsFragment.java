package com.cruisemobile.cruise.fragments;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.models.DayDTO;
import com.cruisemobile.cruise.models.ReportDTO;
import com.cruisemobile.cruise.services.ServiceUtils;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;


public class ReportsFragment extends Fragment {

    private static final int MAX_POINTS = 100;
    private SharedPreferences sharedPreferences;
    private ExecutorService executorService;
    private GraphView graphRides;
    private GraphView graphKm;
    private GraphView graphMoney;
    private TextView rideSum;
    private TextView rideAvg;
    private TextView kmSum;
    private TextView kmAvg;
    private TextView moneySum;
    private TextView moneyAvg;
    private ReportDTO rideReportDTO;
    private ReportDTO kmReportDTO;
    private ReportDTO moneyReportDTO;
    private TextView fromDateField;
    private TextView toDateField;
    private DatePickerDialog datePickerDialogFrom;
    private DatePickerDialog datePickerDialogTo;
    private Button generateReportBtn;
    private String fromDate = "";
    private String toDate = "";

    public ReportsFragment() {
        // Required empty public constructor
    }

    public static ReportsFragment newInstance() {
        return new ReportsFragment();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reports, container, false);
        graphRides = view.findViewById(R.id.graphRides);
        graphKm = view.findViewById(R.id.graphKm);
        graphMoney = view.findViewById(R.id.graphMoney);

        rideSum = view.findViewById(R.id.sum_ride_graph);
        kmSum = view.findViewById(R.id.sum_km_graph);
        moneySum = view.findViewById(R.id.sum_money_graph);

        rideAvg = view.findViewById(R.id.avg_ride_graph);
        kmAvg = view.findViewById(R.id.avg_km_graph);
        moneyAvg = view.findViewById(R.id.avg_money_graph);
        fromDateField = view.findViewById(R.id.from_report);
        datePickerDialogFrom = new DatePickerDialog(getContext());
        datePickerDialogFrom.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                fromDate = LocalDate.of(year, month + 1, dayOfMonth).atStartOfDay().toString();
            }
        });
        fromDateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialogFrom.show();
            }
        });

        toDateField = view.findViewById(R.id.till_report);
        datePickerDialogTo = new DatePickerDialog(getContext());
        datePickerDialogTo.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                toDate = LocalDate.of(year, month + 1, dayOfMonth).plusDays(1).atStartOfDay().toString();
            }
        });
        toDateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialogTo.show();
            }
        });
        generateReportBtn = view.findViewById(R.id.generate_report_btn);
        generateReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!fromDate.equals("") && !toDate.equals("")) {
                    processingRideReports();
                } else {
                    Toast.makeText(getContext(), "You need to input dates", Toast.LENGTH_LONG).show();
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void processingRideReports() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    rideReportDTO = getReport("ride");
                    kmReportDTO = getReport("km");
                    moneyReportDTO = getReport("money");
                    if (rideReportDTO == null || kmReportDTO == null || moneyReportDTO == null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "Wrong input dates", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                prepareReports();
                            }
                        });
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private ReportDTO getReport(String type) throws IOException {
        Call<ReportDTO> call = ServiceUtils.reportsEndpoints.getUserReportByType(sharedPreferences.getLong("id", -1), fromDate, toDate, sharedPreferences.getString("role", ""), type);
        return call.execute().body();
    }

    private void prepareReports() {
        rideSum.setText(String.valueOf(rideReportDTO.getSum()));
        rideAvg.setText(new DecimalFormat("##.##").format(rideReportDTO.getAvg()));
        graphRides.removeAllSeries();
        graphRides.addSeries(generateGraphDataPoints(rideReportDTO.getDays()));

        kmSum.setText(new DecimalFormat("##.##").format(kmReportDTO.getSum()));
        kmAvg.setText(new DecimalFormat("##.##").format(kmReportDTO.getAvg()));
        graphKm.removeAllSeries();
        graphKm.addSeries(generateGraphDataPoints(kmReportDTO.getDays()));

        moneySum.setText(new DecimalFormat("##.##").format(moneyReportDTO.getSum()));
        moneyAvg.setText(new DecimalFormat("##.##").format(moneyReportDTO.getAvg()));
        graphMoney.removeAllSeries();
        graphMoney.addSeries(generateGraphDataPoints(moneyReportDTO.getDays()));
    }

    private LineGraphSeries<DataPoint> generateGraphDataPoints(List<DayDTO> days) {
        int orderNumberOfCorrespondingDate = 0;
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        for (DayDTO day : days) {
            series.appendData(new DataPoint(orderNumberOfCorrespondingDate, day.getValue()), true, MAX_POINTS);
            orderNumberOfCorrespondingDate += 1;
        }
        return series;
    }

}

