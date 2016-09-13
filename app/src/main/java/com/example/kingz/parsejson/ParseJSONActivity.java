package com.example.kingz.parsejson;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kingz.parsejson.adapter.MovieAdapter;
import com.example.kingz.parsejson.models.MovieModel;
import com.example.kingz.parsejson.util.IOUtils;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ParseJSONActivity extends AppCompatActivity {
    private ListView lvMovie;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parse_json);

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loading. Please wait...");

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);

        lvMovie = (ListView) findViewById(R.id.lvMovie);

    }


    private boolean checkInternetConnection() {
        ConnectivityManager connManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();

        if (networkInfo == null) {
            Toast.makeText(this, "No default network is currently active", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!networkInfo.isConnected()) {
            Toast.makeText(this, "Network is not connected", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!networkInfo.isAvailable()) {
            Toast.makeText(this, "Network not available", Toast.LENGTH_LONG).show();
            return false;
        }
        Toast.makeText(this, "Network OK", Toast.LENGTH_LONG).show();
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh){
            String jsonUrl = "http://jsonparsing.parseapp.com/jsonData/moviesData.txt";
            JsonTask task = new JsonTask();
            task.execute(jsonUrl);
        }
        return super.onOptionsItemSelected(item);
    }

    public class JsonTask extends AsyncTask<String, Void, List<MovieModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected List<MovieModel> doInBackground(String... params) {
            String textUrl = params[0];

            InputStream in = null;
            BufferedReader br= null;
            try {
                URL url = new URL(textUrl);
                HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

                httpConn.setAllowUserInteraction(false);
                httpConn.setInstanceFollowRedirects(true);
                httpConn.setRequestMethod("GET");
                httpConn.connect();
                int resCode = httpConn.getResponseCode();

                if (resCode == HttpURLConnection.HTTP_OK) {
                    in = httpConn.getInputStream();
                    br= new BufferedReader(new InputStreamReader(in));

                    StringBuilder sb= new StringBuilder();
                    String s= null;
                    while((s= br.readLine())!= null) {
                        sb.append(s);
                        sb.append("\n");
                    }
                    String finalJson = sb.toString();
                    JSONObject parentObject = new JSONObject(finalJson);
                    JSONArray parentArray = parentObject.getJSONArray("movies");

                    List<MovieModel> movieModelList = new ArrayList<>();

                    Gson gson = new Gson();
                    for (int i = 0; i < parentArray.length(); i++) {
                        JSONObject finalObject = parentArray.getJSONObject(i);
                        MovieModel movieModel = gson.fromJson(finalObject.toString(), MovieModel.class);
//                        MovieModel movieModel = new MovieModel();
//                        JSONObject finalObject = parentArray.getJSONObject(i);
//                        movieModel.setMovie(finalObject.getString("movie"));
//                        movieModel.setYear(finalObject.getInt("year"));
//                        movieModel.setRating((float) finalObject.getDouble("rating"));
//                        movieModel.setDuration(finalObject.getString("duration"));
//                        movieModel.setDirector(finalObject.getString("director"));
//                        movieModel.setTagline(finalObject.getString("tagline"));
//                        movieModel.setImage(finalObject.getString("image"));
//                        movieModel.setStory(finalObject.getString("story"));
//
//                        List<MovieModel.Cast> castList = new ArrayList<>();
//                        for (int j = 0; j < finalObject.getJSONArray("cast").length(); j++){
//                            MovieModel.Cast cast = new MovieModel.Cast();
//                            cast.setName(finalObject.getJSONArray("cast").getJSONObject(j).getString("name"));
//                            castList.add(cast);
//                        }
//                        movieModel.setCastList(castList);
                        movieModelList.add(movieModel);
                    }

                    return movieModelList;
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(br);
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<MovieModel> result) {
            if(result  != null){
//            this.textView.setText(result);
                dialog.dismiss();
                MovieAdapter adapter = new MovieAdapter(getApplicationContext(), R.layout.row, result);
                lvMovie.setAdapter(adapter);
            } else{
                Log.e("MyMessage", "Failed to fetch data!");
            }
        }
    }
}
