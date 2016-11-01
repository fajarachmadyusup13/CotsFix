package com.example.xxx.tescotsfix;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DetailActivity extends AppCompatActivity {

    TextView textViewNama;
    TextView textViewImage;
    ImageView imageViewHewan;
    String imageAddress;
    String tempimageAddress;
    String nama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        textViewNama = (TextView) findViewById(R.id.tv_namaHewan);
        textViewImage = (TextView) findViewById(R.id.tv_imageAddress);
        imageViewHewan = (ImageView) findViewById(R.id.iv_imageHewan);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Intent intent = getIntent();

        getSupportActionBar().setTitle(nama);

        nama = intent.getStringExtra("nama");
        imageAddress = intent.getStringExtra("image");

        tempimageAddress = imageAddress;
        if (imageAddress.length() > 37){
            tempimageAddress = imageAddress.substring(0,36);
        }

        int kali = sharedPreferences.getInt("NUM_OPEN_"+nama, 0);
        kali++;
        textViewNama.setText("Anda telah melihat "+intent.getStringExtra("nama")+" "+kali+"x");
        editor.putInt("NUM_OPEN_"+nama, kali);
        editor.apply();

        textViewImage.setText("Sumber : "+tempimageAddress+"...");

        DownloadImageTask downloadImageTask = new DownloadImageTask();
        downloadImageTask.execute(imageAddress);
    }

    public void buttonOpenBrowserAction(View view){
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(imageAddress)));
    }

    public Bitmap loadImageFromNetwork(String strUrl){
        Bitmap bitmap = null;
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            urlConnection.disconnect();
        }

        return bitmap;
    }

    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... strings) {
            return loadImageFromNetwork(strings[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            imageViewHewan.setImageBitmap(bitmap);
        }
    }


}
