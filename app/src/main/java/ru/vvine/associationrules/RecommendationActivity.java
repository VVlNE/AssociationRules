package ru.vvine.associationrules;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecommendationActivity extends AppCompatActivity {

    private Spinner selectElementSpinner;
    private TextView commentTextView;
    private ListView recommendElementsListView;

    private Bundle bundle;
    private AssociationRulesManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);

        bundle = getIntent().getExtras();
        manager = (AssociationRulesManager) bundle.getSerializable(AssociationRulesManager.class.getSimpleName());

        selectElementSpinner = (Spinner) findViewById(R.id.selectElementSpinner);
        commentTextView = (TextView) findViewById(R.id.commentTextView);
        recommendElementsListView = (ListView) findViewById(R.id.recommendElementsListView);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, manager.getElements());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectElementSpinner.setAdapter(spinnerAdapter);

        fillListView(0);

        selectElementSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (manager.hasRecommendElements(position)) {
                    ViewGroup.LayoutParams params = commentTextView.getLayoutParams();
                    params.height = 0;
                    commentTextView.setLayoutParams(params);

                    commentTextView.setHint("");

                    fillListView(position);
                }
                else {
                    ViewGroup.LayoutParams params = commentTextView.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    commentTextView.setLayoutParams(params);

                    commentTextView.setHint("Нечего порекоммендовать, выберите что-нибудь другое!");

                    recommendElementsListView.setAdapter(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ViewGroup.LayoutParams params = commentTextView.getLayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                commentTextView.setLayoutParams(params);

                commentTextView.setHint("Выберите что-нибудь!");
            }
        });

        recommendElementsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(RecommendationActivity.this, TwoElementsSignificanceMeasuresActivity.class);
                intent.putExtra("ACTIVITY_NAME",
                        manager.getElement(selectElementSpinner.getSelectedItemPosition()) + " → " + manager.getElement(manager.findElementNumber(((TextView) view).getText().toString())));
                intent.putExtra("ACTIVITY_DATA", manager.getSignificanceMeasures(selectElementSpinner.getSelectedItemPosition(), manager.findElementNumber(((TextView) view).getText().toString())));
                startActivity(intent);
            }
        });
    }

    private void fillListView(int num) {
        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<>(this, R.layout.recommend_elements_list_view_item, manager.getRecommendElements(num));

        recommendElementsListView.setAdapter(listViewAdapter);
    }
}
