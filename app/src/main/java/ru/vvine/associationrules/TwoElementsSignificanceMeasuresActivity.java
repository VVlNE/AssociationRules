package ru.vvine.associationrules;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TwoElementsSignificanceMeasuresActivity extends AppCompatActivity {

    private TextView twoElementsSignificanceMeasuresTextView, twoElementsAssociationNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measures_significance_elements_two);

        Bundle bundle = getIntent().getExtras();

        twoElementsSignificanceMeasuresTextView = (TextView) findViewById(R.id.twoElementsSignificanceMeasuresTextView);
        twoElementsAssociationNameTextView = (TextView) findViewById(R.id.twoElementsAssociationNameTextView);

        twoElementsSignificanceMeasuresTextView.setText((String) bundle.getSerializable("ACTIVITY_DATA"));
        twoElementsAssociationNameTextView.setText((String) bundle.getSerializable("ACTIVITY_NAME"));
    }
}
