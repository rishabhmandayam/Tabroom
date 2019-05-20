package rm.tabroomfinal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements Download_data.download_complete {
    public ListView list;
    public ArrayList<Tournament> tournamentList = new ArrayList<Tournament>();
    public ListAdapter adapter;
    private ProgressBar progressBar;
    private int progressStatus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (ListView) findViewById(R.id.list);
        adapter = new ListAdapter(this);
        list.setAdapter(adapter);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_cyclic);

        Download_data download_data = new Download_data((Download_data.download_complete) this);
        String refreshString = getIntent().getStringExtra("refresh");
        if (refreshString != null && refreshString.equalsIgnoreCase("true")) {
            download_data.download_data_from_link("", getApplicationContext(), true);
        } else {
            download_data.download_data_from_link("", getApplicationContext(), false);
        }
        progressBar.setProgress(100);

    }

    public void get_data(String data) {
        try {
            JSONArray data_array = new JSONArray(data);

            for (int i = 0; i < data_array.length(); i++) {
                JSONObject obj = new JSONObject(data_array.get(i).toString());

                Tournament add = new Tournament();
                add.setTornamentDate(obj.getString("Dates"));
                add.setCity(obj.getString("City"));
                add.setLocale(obj.getString("Locale"));
                add.setRegistration(obj.getString("Registration"));
                add.setTorunamentName(obj.getString("Tournament"));
                add.setTournamentLink(obj.getString("tournamentlink"));
                tournamentList.add(add);

            }
            progressBar.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void RefreshFromURL(View view) {
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        intent.putExtra("refresh", "true");
        startActivity(intent);

    }


}
