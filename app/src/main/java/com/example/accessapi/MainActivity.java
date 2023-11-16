package com.example.accessapi;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String API_URL = "https://dummy.restapiexample.com/api/v1/employees";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new FetchEmployeeData().execute();
    }

    private class FetchEmployeeData extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String employeeDataJsonStr = null;

            try {
                URL url = new URL(API_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();

                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                employeeDataJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("MainActivity", "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("MainActivity", "Error closing stream", e);
                    }
                }
            }

            return employeeDataJsonStr;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    // Mengonversi JSON string ke objek JSON
                    JSONObject jsonObject = new JSONObject(result);

                    // Mendapatkan array karyawan
                    JSONArray employeesArray = jsonObject.getJSONArray("data");

                    // Iterasi melalui array dan tampilkan nama dan gaji
                    for (int i = 0; i < employeesArray.length(); i++) {
                        JSONObject employeeObject = employeesArray.getJSONObject(i);
                        String employeeName = employeeObject.getString("employee_name");
                        String employeeSalary = employeeObject.getString("employee_salary");

                        Log.d("Employee", "Name: " + employeeName + ", Salary: " + employeeSalary);
                    }
                } catch (JSONException e) {
                    Log.e("MainActivity", "Error parsing JSON", e);
                }
            }
        }
    }
}
