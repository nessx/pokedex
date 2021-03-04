package com.example.pokedex;


import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.ahmadrosid.svgloader.SvgLoader;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

public class fetchData extends AsyncTask<Void, Void, Void> {

    protected String data = "";
    protected String results = "";
    protected ArrayList<String> strTypes; // Create an ArrayList object
    protected String pokSearch;

    public fetchData(String pokSearch) {
        this.pokSearch = pokSearch;
        strTypes = new ArrayList<String>();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            //Make API connection
            URL url = new URL("https://pokeapi.co/api/v2/pokemon/" + pokSearch.toLowerCase().trim());
            Log.i("logtest", "https://pokeapi.co/api/v2/pokemon/" + pokSearch);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            // Read API results
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sBuilder = new StringBuilder();

            // Build JSON String
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                sBuilder.append(line + "\n");
            }

            inputStream.close();
            data = sBuilder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid){
        JSONObject jObject = null;
        String img = "";
        String typeName = "";
        String typeObj="";

        try {
            jObject = new JSONObject(data);

            // Get JSON name, height, weight
            results += "Nombre: " + jObject.getString("name").toUpperCase() + "\n" +
                    "Altura: " + jObject.getString("height") + "\n" +
                    "Anchura: " + jObject.getString("weight") + "\n" +
                    "Exp. Base: " + jObject.getString("base_experience") + "\n" +
                    "ID: " + jObject.getString("id");

            Log.i("logtest", results);
            // Get type/types 
            JSONArray types = new JSONArray(jObject.getString("types"));
            for(int i=0; i<types.length(); i++){
                JSONObject type = new JSONObject(types.getString(i));
                JSONObject type2  = new JSONObject(type.getString("type"));
                strTypes.add(type2.getString("name"));
            }
            img = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/dream-world/" + jObject.getString("id") + ".svg";

            String id = jObject.getString("id");
            MainActivity.btnLeft.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    pokSearch=String.valueOf(Integer.parseInt(id)-1);
                    fetchData process = new fetchData(pokSearch);
                    process.execute();
                }
            });

            MainActivity.btnRight.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    pokSearch=String.valueOf(Integer.parseInt(id)+1);
                    fetchData process = new fetchData(pokSearch);
                    process.execute();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }


        // Set info
        MainActivity.txtDisplay.setText(this.results);

        // Set main img
        SvgLoader.pluck()
                .with(MainActivity.act)
                .load(img, MainActivity.imgPok);

        Picasso.get().load(img).into(MainActivity.imgPok);

        // Set img types
        for(int i=0; i<strTypes.size(); i++){
            if (strTypes.size() == 1){
                MainActivity.imgType[1].setVisibility(View.INVISIBLE);
            }else {
                MainActivity.imgType[1].setVisibility(View.VISIBLE);
            }
            MainActivity.imgType[i].setImageResource(MainActivity.act.getResources().getIdentifier(strTypes.get(i), "drawable", MainActivity.act.getPackageName()));
        }

    }
}
