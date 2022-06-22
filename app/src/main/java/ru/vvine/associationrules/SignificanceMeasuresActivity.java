package ru.vvine.associationrules;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SignificanceMeasuresActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView supportMeasureTextView, confidenceMeasureTextView, liftMeasureTextView, leverageMeasureTextView, convictionMeasureTextView;
    private Button moreSupportMeasureDetailsButton, moreConfidenceMeasureDetailsButton, moreLiftMeasureDetailsButton, moreLeverageMeasureDetailsButton, moreConvictionMeasureDetailsButton;

    private Bundle bundle;
    private AssociationRulesManager manager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measures_significance);

        bundle = getIntent().getExtras();
        manager = (AssociationRulesManager) bundle.getSerializable(AssociationRulesManager.class.getSimpleName());

        supportMeasureTextView = (TextView) findViewById(R.id.supportMeasureTextView);
        confidenceMeasureTextView = (TextView) findViewById(R.id.confidenceMeasureTextView);
        liftMeasureTextView = (TextView) findViewById(R.id.liftMeasureTextView);
        leverageMeasureTextView = (TextView) findViewById(R.id.leverageMeasureTextView);
        convictionMeasureTextView = (TextView) findViewById(R.id.convictionMeasureTextView);

        moreSupportMeasureDetailsButton = (Button) findViewById(R.id.moreSupportMeasureDetailsButton);
        moreConfidenceMeasureDetailsButton = (Button) findViewById(R.id.moreConfidenceMeasureDetailsButton);
        moreLiftMeasureDetailsButton = (Button) findViewById(R.id.moreLiftMeasureDetailsButton);
        moreLeverageMeasureDetailsButton = (Button) findViewById(R.id.moreLeverageMeasureDetailsButton);
        moreConvictionMeasureDetailsButton = (Button) findViewById(R.id.moreConvictionMeasureDetailsButton);

        supportMeasureTextView.setText(manager.getTheBestSupportMeasure(5));
        confidenceMeasureTextView.setText(manager.getTheBestConfidenceMeasure(10));
        liftMeasureTextView.setText(manager.getTheBestLiftMeasure(10));
        leverageMeasureTextView.setText(manager.getTheBestLeverageMeasure(5));
        convictionMeasureTextView.setText(manager.getTheBestConvictionMeasure(5));

        moreSupportMeasureDetailsButton.setOnClickListener(this);
        moreConfidenceMeasureDetailsButton.setOnClickListener(this);
        moreLiftMeasureDetailsButton.setOnClickListener(this);
        moreLeverageMeasureDetailsButton.setOnClickListener(this);
        moreConvictionMeasureDetailsButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(SignificanceMeasuresActivity.this, MoreDetailsActivity.class);

        switch (v.getId()) {
            case R.id.moreSupportMeasureDetailsButton:
                intent.putExtra("MEASURE_NAME", "Поддержка");
                break;
            case R.id.moreConfidenceMeasureDetailsButton:
                intent.putExtra("MEASURE_NAME", "Достоверность");
                break;
            case R.id.moreLiftMeasureDetailsButton:
                intent.putExtra("MEASURE_NAME", "Лифт");
                break;
            case R.id.moreLeverageMeasureDetailsButton:
                intent.putExtra("MEASURE_NAME", "Левередж");
                break;
            case R.id.moreConvictionMeasureDetailsButton:
                intent.putExtra("MEASURE_NAME", "Улучшение");
                break;
            default:
                break;
        }

        intent.putExtra(AssociationRulesManager.class.getSimpleName(), manager);
        startActivity(intent);
    }
}