package ru.vvine.associationrules;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MoreDetailsActivity extends AppCompatActivity implements View.OnKeyListener {

    TextView detailedMeasureTextView, valueRangeTextView;
    EditText enterMinValueEditText, enterMaxValueEditText;

    Bundle bundle;
    AssociationRulesManager manager;
    String titleName;
    double absoluteMin,absoluteMax, rangeMin, rangeMax;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_more);

        setInitialValues();
    }

    private void setInitialValues() {
        detailedMeasureTextView = (TextView) findViewById(R.id.detailedMeasureTextView);
        valueRangeTextView = (TextView) findViewById(R.id.valueRangeTextView);
        enterMaxValueEditText = (EditText) findViewById(R.id.enterMaxValueEditText);
        enterMinValueEditText = (EditText) findViewById(R.id.enterMinValueEditText);

        bundle = getIntent().getExtras();
        manager = (AssociationRulesManager) bundle.getSerializable(AssociationRulesManager.class.getSimpleName());

        titleName = bundle.getString("MEASURE_NAME");
        ((AppCompatActivity) MoreDetailsActivity.this).getSupportActionBar().setTitle(titleName);

        if (titleName.equals("Левередж")) {
            enterMaxValueEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            enterMinValueEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }

        switch (titleName) {
            case "Поддержка":
                valueRangeTextView.setText("Введите значения от " + String.format("%.2f", absoluteMin = manager.getMinSupportMeasure()) + " до " + String.format("%.2f", absoluteMax = manager.getMaxSupportMeasure()));
                break;
            case "Достоверность":
                valueRangeTextView.setText("Введите значения от " + String.format("%.2f", absoluteMin = manager.getMinConfidenceMeasure()) + " до " + String.format("%.2f", absoluteMax = manager.getMaxConfidenceMeasure()));
                break;
            case "Лифт":
                valueRangeTextView.setText("Введите значения от " + String.format("%.2f", absoluteMin = manager.getMinLiftMeasure()) + " до " + String.format("%.2f", absoluteMax = manager.getMaxLiftMeasure()));
                break;
            case "Левередж":
                valueRangeTextView.setText("Введите значения от " + String.format("%.2f", absoluteMin = manager.getMinLeverageMeasure()) + " до " + String.format("%.2f", absoluteMax = manager.getMaxLeverageMeasure()) + " в процентах");
                break;
            case "Улучшение":
                valueRangeTextView.setText("Введите значения от " + String.format("%.2f", absoluteMin = manager.getMinConvictionMeasure()) + " до " + String.format("%.2f", absoluteMax = manager.getMaxConvictionMeasure()));
                break;
            default:
                break;
        }

        rangeMax = absoluteMax;
        rangeMin = absoluteMin;
        setDetailedMeasureTextView();

        enterMaxValueEditText.setOnKeyListener(this);
        enterMinValueEditText.setOnKeyListener(this);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if ((v.getId() == R.id.enterMaxValueEditText) || (v.getId() == R.id.enterMinValueEditText)) {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                if (!enterMaxValueEditText.getText().toString().equals("")) {
                    String str = enterMaxValueEditText.getText().toString();
                    Double num;

                    if (str.contains("-"))
                        num = -1 * Double.parseDouble(str.substring(1, str.length()));
                    else
                        num = Double.parseDouble(str);

                    if (num < absoluteMin) {
                        Toast.makeText(getApplicationContext(),
                                "Значение максимума меньше минимально возможного!\n" + String.format("%.2f", num) + " < " + String.format("%.2f", absoluteMin),
                                Toast.LENGTH_LONG).show();
                        enterMaxValueEditText.getText().clear();
                    }

                    if (num > absoluteMax) {
                        enterMaxValueEditText.setText(String.format("%.2f", absoluteMax));
                        num = absoluteMax;
                    }

                    rangeMax = num;

                }
                else
                    rangeMax = absoluteMax;

                if (!enterMinValueEditText.getText().toString().equals("")) {
                    String str = enterMinValueEditText.getText().toString();
                    Double num;

                    if (str.contains("-"))
                        num = -1 * Double.parseDouble(str.substring(1, str.length()));
                    else
                        num = Double.parseDouble(str);

                    if (num < absoluteMin) {
                        enterMinValueEditText.setText(String.format("%.2f", absoluteMin));
                        num = absoluteMin;
                    }

                    if (num > absoluteMax) {
                        Toast.makeText(getApplicationContext(),
                                "Значение минимума больше минимально возможного!\n" + String.format("%.2f", num) + " > " + String.format("%.2f", absoluteMax),
                                Toast.LENGTH_LONG).show();
                        enterMinValueEditText.getText().clear();
                    }

                    rangeMin = num;

                }
                else
                    rangeMin = absoluteMin;


                if (rangeMin <= rangeMax) {
                    if (!setDetailedMeasureTextView()) {
                        Toast.makeText(getApplicationContext(),
                                "В выбраном диапазоне нет результатов, введите другие значения!",
                                Toast.LENGTH_LONG).show();

                        rangeMin = absoluteMin;
                        rangeMax = absoluteMax;
                        setDetailedMeasureTextView();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(),
                            "Максимум не может быть меньше минимума, введите другие значения!",
                            Toast.LENGTH_LONG).show();

                    rangeMin = absoluteMin;
                    rangeMax = absoluteMax;
                    setDetailedMeasureTextView();
                }
            }
        }

        return false;
    }

    private boolean setDetailedMeasureTextView() {
        String data = "";

        switch (titleName) {
            case "Поддержка":
                data = manager.getSupportMeasure(rangeMin, rangeMax);
                break;
            case "Достоверность":
                data = manager.getConfidenceMeasure(rangeMin, rangeMax);
                break;
            case "Лифт":
                data = manager.getLiftMeasure(rangeMin, rangeMax);
                break;
            case "Левередж":
                data = manager.getLeverageMeasure(rangeMin, rangeMax);
                break;
            case "Улучшение":
                data = manager.getConvictionMeasure(rangeMin, rangeMax);
                break;
            default:
                break;
        }

        if (data.length() > 0)
            detailedMeasureTextView.setText(data);
        else
            return false;

        return true;
    }

}