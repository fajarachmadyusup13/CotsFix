package com.example.xxx.tescotsfix;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xxx.tescotsfix.adapter.ListViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    TextView textViewLoading;
    ProgressBar progressBar;
    RelativeLayout relativeLayoutRoot;
    ListViewAdapter listViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializationViewPreExecute();
        initializationJSON();
        initializationListView();

    }

    public void initializationJSON(){
        DataFromJSON dataFromJSON = new DataFromJSON();
        dataFromJSON.execute("http://dif.indraazimi.com/hewan/hewan.json");
    }

    public void initializationListView(){
        try {
            String jsonRespones = readResponesFromInternal();
            JSONArray jsonArray = new JSONArray(jsonRespones);
            listViewAdapter = new ListViewAdapter(jsonArray);
            listView.setAdapter(listViewAdapter);
            askingDialog();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void initializationViewPreExecute(){
        textViewLoading = (TextView) findViewById(R.id.tv_loading);
        progressBar =  (ProgressBar) findViewById(R.id.pb_progress);
        relativeLayoutRoot = (RelativeLayout) findViewById(R.id.root);
        listView = (ListView) findViewById(R.id.lv_listData);
    }



    public void askingDialog(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, int i, long l) {
                try {
                    final int position =i;
                    final String namaHewan = listViewAdapter.getItem(i).getString("nama");
                    final String imageAddressHewan = listViewAdapter.getItem(i).getString("foto");
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(namaHewan)
                            .setMessage("Mau diapakan Hewan ini ?")
                            .setPositiveButton("Ubah", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    editDialog(view, position);
                                }
                            })
                            .setNegativeButton("Hapus", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Snackbar snackbar = Snackbar.make(relativeLayoutRoot, namaHewan+" Telah dihapus", Snackbar.LENGTH_SHORT)
                                            .setAction("Undo", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Toast.makeText(MainActivity.this, "tidak ada undo yang dilakukan", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                    snackbar.show();
                                }
                            })
                            .setNeutralButton("Lihat Detail", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                                    intent.putExtra("nama", namaHewan);
                                    intent.putExtra("image", imageAddressHewan);
                                    startActivity(intent);
                                }
                            });
                    builder.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void editDialog(View view, int position){

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = (LayoutInflater) relativeLayoutRoot.getContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.item_dialog_edit, relativeLayoutRoot, false);
        EditText editTextEdit = (EditText) view.findViewById(R.id.et_itemEdit);
        try {
            editTextEdit.setText(listViewAdapter.getItem(position).getString("nama"));
            builder.setTitle("Ubah Data")
                    .setView(view)
                    .setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void buttonTambahAction(View view){
        LayoutInflater inflater = (LayoutInflater) relativeLayoutRoot.getContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.item_dialog_tambah, relativeLayoutRoot, false);
        EditText editTextTambah = (EditText) view.findViewById(R.id.et_itemTambah);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tambah Data")
                .setView(view)
                .setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.show();
    }



    public String loadRespones (String sURL){
        BufferedReader reader;
        String strUrl = sURL;
        String jsonRespone = null;
        String line;
        HttpURLConnection urlConnection = null;
        InputStream inputStream;
        StringBuffer stringBuffer;

        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            inputStream = urlConnection.getInputStream();
            stringBuffer = new StringBuffer();

            if (inputStream == null){
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            while ((line = reader.readLine()) != null){
                stringBuffer.append(line+"\n");
            }

            if (stringBuffer.length() == 0){
                return null;
            }

            jsonRespone = stringBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }

        return jsonRespone;
    }

    public void saveResponesToInternal(String respones){
        try {
            FileOutputStream fileOutputStream = openFileOutput("respones.txt", Context.MODE_PRIVATE);
            fileOutputStream.write(respones.getBytes());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readResponesFromInternal(){
       StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream inputStream = openFileInput("respones.txt");
            int ch;
            while ((ch = inputStream.read()) != -1){
                stringBuilder.append((char) ch);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

    public class DataFromJSON extends AsyncTask<String, Void, String>{


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            textViewLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            return loadRespones(strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            saveResponesToInternal(s);
            progressBar.setVisibility(View.INVISIBLE);
            textViewLoading.setVisibility(View.INVISIBLE);
        }
    }
}
