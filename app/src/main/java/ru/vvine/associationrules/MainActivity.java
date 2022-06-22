package ru.vvine.associationrules;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button openFileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        openFileButton = (Button) findViewById(R.id.openFileButton);
        openFileButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.openFileButton:
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("text/plain");
                startActivityForResult(intent, 1);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Intent intent;
        switch (requestCode) {
            case 1:
                try {
                    if (resultCode == RESULT_OK) {
                        TextFile textFile = new TextFile(getFileData(data.getData()), getFileName(data.getData()));
                        if (!textFile.isRightFile())
                            throw new Exception("Некорректные данные! Выберите другой файл..");

                        intent = new Intent(MainActivity.this, FileOpeningActivity.class);
                        intent.putExtra(TextFile.class.getSimpleName(), textFile);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    private String getFileData(Uri uri) {
        String data = "";

        try {
            InputStream stream = getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder builder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }

            data = builder.toString();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "Ошибка при чтении файла!",
                    Toast.LENGTH_LONG).show();
        }
        return data;
    }

    private String getFileName(Uri uri) {
        String name = "";
        try {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);

            cursor.moveToFirst();
            name = cursor.getString(nameIndex);

            if (name.contains(".txt"))
                name = name.substring(0, name.indexOf(".txt"));
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "Ошибка при получении имени файла!",
                    Toast.LENGTH_LONG).show();
        }
        return name;
    }
}