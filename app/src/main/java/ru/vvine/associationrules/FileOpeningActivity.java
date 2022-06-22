package ru.vvine.associationrules;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FileOpeningActivity extends AppCompatActivity implements View.OnClickListener {
    
    private ListView fileDataListView;
    private Button significanceMeasuresButton, recommendationButton;

    private AssociationRulesManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening_file);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            Toast.makeText(getApplicationContext(),
                    "Ошибка при передаче файла!",
                    Toast.LENGTH_LONG).show();
            finish();
        }

        TextFile textFile = (TextFile) bundle.getSerializable(TextFile.class.getSimpleName());
        if (textFile == null) {
            Toast.makeText(getApplicationContext(),
                    "Ошибка при записи файла!",
                    Toast.LENGTH_LONG).show();
            finish();
        }

        ((AppCompatActivity) FileOpeningActivity.this).getSupportActionBar().setTitle(textFile.getName());
        manager = new AssociationRulesManager(textFile.getData());

        fileDataListView = (ListView) findViewById(R.id.fileDataListView);

        ArrayList<Map<String, String>> fileData = new ArrayList<Map<String, String>>(manager.getTransactionNumber());
        Map<String, String> map;
        final String transactionNumber = "TRANSACTION_NUMBER", transactionData = "TRANSACTION_DATA";

        for (int i = 0; i < manager.getTransactionNumber(); i++) {
            map = new HashMap<String, String>();
            map.put(transactionNumber, manager.getTransactionNumber(i));
            map.put(transactionData, manager.getTransactionData(i));
            fileData.add(map);
        }

        String[] from = new String[]{transactionNumber, transactionData};
        int[] to = {R.id.transactionNumberTextView, R.id.transactionDataTextView};

        SimpleAdapter adapter = new SimpleAdapter(this, fileData, R.layout.file_data_list_view_item, from, to);

        fileDataListView.addFooterView(createFooter());

        fileDataListView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.significanceMeasuresButton:
                intent = new Intent(FileOpeningActivity.this, SignificanceMeasuresActivity.class);
                intent.putExtra(AssociationRulesManager.class.getSimpleName(), manager);
                startActivity(intent);
                break;
            case R.id.recommendationButton:
                intent = new Intent(FileOpeningActivity.this, RecommendationActivity.class);
                intent.putExtra(AssociationRulesManager.class.getSimpleName(), manager);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    View createFooter() {
        View view = getLayoutInflater().inflate(R.layout.file_data_list_view_footer, null);

        significanceMeasuresButton = (Button) view.findViewById(R.id.significanceMeasuresButton);
        recommendationButton = (Button) view.findViewById(R.id.recommendationButton);

        significanceMeasuresButton.setOnClickListener(this);
        recommendationButton.setOnClickListener(this);

        return view;
    }
}