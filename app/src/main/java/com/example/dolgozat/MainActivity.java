package com.example.dolgozat;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button buttonAdd, buttonNew, buttonEdit, buttonBack;
    private EditText editTextCountry, editTextPopulation, editTextCity, editTextId;
    private ListView listViewAdatok;
    private ProgressBar progressBar;
    private LinearLayout linearLayoutPersonForm;
    private List<Country> countries = new ArrayList<>();
    private String url = "https://retoolapi.dev/MXtJRc/data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        linearLayoutPersonForm.setVisibility(View.GONE);
        buttonEdit.setVisibility(View.GONE);
        RequestTask task = new RequestTask(url, "GET");
        task.execute();

        buttonNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayoutPersonForm.setVisibility(View.VISIBLE);
                buttonNew.setVisibility(View.GONE);
            }
        });

        buttonBack.setOnClickListener(view -> {
            linearLayoutPersonForm.setVisibility(View.GONE);
            buttonNew.setVisibility(View.VISIBLE);
            urlapAlaphelyzetbe();
        });

        buttonAdd.setOnClickListener(view -> orszagHozzadas());

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emberModositas();
            }
        });
    }

    private void init() {
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonNew = findViewById(R.id.buttonNew);
        buttonEdit = findViewById(R.id.buttonEdit);
        buttonBack = findViewById(R.id.buttonBack);

        editTextId = findViewById(R.id.editTextId);
        editTextCountry = findViewById(R.id.editTextCountry);
        editTextPopulation = findViewById(R.id.editTextPopulation);
        editTextCity = findViewById(R.id.editTextCity);

        progressBar = findViewById(R.id.progressBar);

        linearLayoutPersonForm = findViewById(R.id.linearLayoutPersonForm);

        listViewAdatok = findViewById(R.id.listViewAdatok);
        listViewAdatok.setAdapter(new CountryAdapter());
    }

    private void orszagHozzadas() {
        String country = editTextCountry.getText().toString();
        String pop = editTextPopulation.getText().toString();
        String city = editTextCity.getText().toString();

        boolean valid = validacio();

        if (valid){
            Toast.makeText(this,
                    "Minden mezőt ki kell tölteni", Toast.LENGTH_SHORT).show();
            return;
        }

        int population = Integer.parseInt(pop);
        Country person = new Country(0,country,city,population);
        Gson jsonConverter = new Gson();
        RequestTask task = new RequestTask(url, "POST",
                jsonConverter.toJson(person));
        task.execute();
    }

    private void emberModositas() {
        String counry = editTextCountry.getText().toString();
        String pop = editTextPopulation.getText().toString();
        String city = editTextCity.getText().toString();
        String idText = editTextId.getText().toString();

        int popualtion = Integer.parseInt(pop);
        int id = Integer.parseInt(idText);

        boolean valid = validacio();

        if (valid){
            Toast.makeText(this,
                    "Minden mezőt ki kell tölteni", Toast.LENGTH_SHORT).show();
            return;
        }

        Country country = new Country(id,counry,city,popualtion);
        Gson jsonConverter = new Gson();
        RequestTask task = new RequestTask(url+"/"+id, "PUT",
                jsonConverter.toJson(countries));
        task.execute();
    }

    private boolean validacio() {
        if (editTextCity.getText().toString().isEmpty() ||
                editTextPopulation.getText().toString().isEmpty() || editTextCountry.getText().toString().isEmpty())
            return true;
        else
            return false;
    }

    private class CountryAdapter extends ArrayAdapter<Country> {
        public CountryAdapter() {
            super(MainActivity.this, R.layout.country_list_items, countries);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.country_list_items,null,false);
            Country actualCountry = countries.get(position);
            TextView textViewCountry = view.findViewById(R.id.textViewCountry);
            TextView textViewCity = view.findViewById(R.id.textViewCity);
            TextView textViewPopulation = view.findViewById(R.id.textViewPopulation);
            TextView textViewModosit = view.findViewById(R.id.textViewUpdate);
            TextView textViewTorles = view.findViewById(R.id.textViewDelete);

            textViewCountry.setText(actualCountry.getCountry());
            textViewCity.setText(actualCountry.getCity());
            textViewPopulation.setText("(" +actualCountry.getPopulation() + ")");

            textViewModosit.setOnClickListener(view1 -> {
                linearLayoutPersonForm.setVisibility(View.VISIBLE);
                editTextId.setText(String.valueOf(actualCountry.getId()));
                editTextCity.setText(actualCountry.getCity());
                editTextCountry.setText(actualCountry.getCountry());
                editTextPopulation.setText(String.valueOf(actualCountry.getPopulation()));
                buttonEdit.setVisibility(View.VISIBLE);
                buttonAdd.setVisibility(View.GONE);
                buttonNew.setVisibility(View.GONE);
            });

            textViewTorles.setOnClickListener(view12 -> {
                RequestTask task = new RequestTask(url, "DELETE",
                        String.valueOf(actualCountry.getId()));
                task.execute();
            });

            return view;
        }
    }

    private void urlapAlaphelyzetbe() {
        editTextPopulation.setText("");
        editTextCountry.setText("");
        editTextCity.setText("");
        linearLayoutPersonForm.setVisibility(View.GONE);
        buttonNew.setVisibility(View.VISIBLE);
        buttonAdd.setVisibility(View.VISIBLE);
        buttonEdit.setVisibility(View.GONE);
        RequestTask task = new RequestTask(url, "GET");
        task.execute();
    }

    private class RequestTask extends AsyncTask<Void, Void, Response> {
        String requestUrl;
        String requestType;
        String requestParams;

        public RequestTask(String requestUrl, String requestType, String requestParams) {
            this.requestUrl = requestUrl;
            this.requestType = requestType;
            this.requestParams = requestParams;
        }

        public RequestTask(String requestUrl, String requestType) {
            this.requestUrl = requestUrl;
            this.requestType = requestType;
        }

        @Override
        protected Response doInBackground(Void... voids) {
            Response response = null;
            try {
                switch (requestType) {
                    case "GET":
                        response = RequestHandler.get(requestUrl);
                        break;
                    case "POST":
                        response = RequestHandler.post(requestUrl, requestParams);
                        break;
                    case "PUT":
                        response = RequestHandler.put(requestUrl, requestParams);
                        break;
                    case "DELETE":
                        response = RequestHandler.delete(requestUrl + "/" + requestParams);
                        break;
                }

            } catch (IOException e) {
                Toast.makeText(MainActivity.this,
                        e.toString(), Toast.LENGTH_SHORT).show();
            }
            return response;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            progressBar.setVisibility(View.GONE);
            Gson converter = new Gson();
            if (response.getResponseCode() >= 400) {
                Toast.makeText(MainActivity.this,
                        "Hiba történt a kérés feldolgozása során", Toast.LENGTH_SHORT).show();
                Log.d("onPostExecuteError: ", response.getContent());
            }
            switch (requestType) {
                case "GET":
                    Country[] peopleArray = converter.fromJson(response.getContent(), Country[].class);
                    countries.clear();
                    countries.addAll(Arrays.asList(peopleArray));
                    break;
                case "POST":
                    Country person = converter.fromJson(response.getContent(), Country.class);
                    countries.add(0, person);
                    urlapAlaphelyzetbe();
                    break;
                /*API 21en nem mukodik
                case "PUT":
                    Country updatePerson = converter.fromJson(response.getContent(), Country.class);
                    countries.replaceAll(country1 ->
                            country1.getId() == updatePerson.getId() ? updatePerson : country1);
                    urlapAlaphelyzetbe();
                    break;
                case "DELETE":
                    int id = Integer.parseInt(requestParams);
                    countries.removeIf(person1 -> country1.getId() == id);
                    break;
*/
            }
        }
    }
}