package com.example.android.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.R.id.list;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> celebURLs = new ArrayList<String>();
    ArrayList<String> celebNames = new ArrayList<String>();
    int chosenCeleb;
    int locationOfCorrectAnswer=0;
    String[] answers = new String[4];
    Button btn1,btn2,btn3,btn4;
    ImageView imageView;
    ArrayList<String> celebNames1;



    public void buttonClicked(View view){
        Log.i(view.getTag().toString(),Integer.toString(locationOfCorrectAnswer));
        Log.i("boolean", String.valueOf(view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))));


    if(view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))){
        Toast.makeText(getApplicationContext(),"Correct Answer!",Toast.LENGTH_SHORT).show();;
        Log.i("answer","correct");
    }else
    {
        Toast.makeText(getApplicationContext(),"Wrong Answer!",Toast.LENGTH_SHORT).show();;
       Toast.makeText(getApplicationContext(),"It's"+celebNames1.get(chosenCeleb),Toast.LENGTH_SHORT).show();
        Log.i("answer","wrong");

    }
        generatequestionandanswers();

    }
    public void generatequestionandanswers(){

        Random random = new Random();
        chosenCeleb = random.nextInt(celebURLs.size());

        ImageDownloader imgTask = new ImageDownloader();
        Bitmap bmp = null;
        try {
            bmp=imgTask.execute(celebURLs.get(chosenCeleb)).get() ;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        imageView.setImageBitmap(bmp);

        locationOfCorrectAnswer =random.nextInt(4);

        int incorrectAnswerLocation;
        for(int i=0;i<4;i++){
            if(i==locationOfCorrectAnswer) {
                answers[i]= celebNames1.get(chosenCeleb);
            }else {

                incorrectAnswerLocation =random.nextInt(celebURLs.size());
                while(incorrectAnswerLocation == chosenCeleb){
                    incorrectAnswerLocation =random.nextInt(celebURLs.size());
                }
                answers[i]=celebNames1.get(incorrectAnswerLocation);
            }
        }

        btn1.setText(answers[0]);
        btn2.setText(answers[1]);
        btn3.setText(answers[2]);
        btn4.setText(answers[3]);


    }

    public class ImageDownloader extends AsyncTask<String,Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            URL url=null;
            HttpURLConnection connection=null;
            try {
                url=new URL(urls[0]);
                connection= (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();
                Bitmap bmp = BitmapFactory.decodeStream(in);
                return bmp;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {

                e.printStackTrace();
                return null;
            }

        }
    }

    public class DownloadTask extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... urls) {
            //Log.i("URL",params[0]);
            String result="";
            URL url;
            HttpURLConnection urlConnection=null;

            try
            {
                url = new URL(urls[0]);
                urlConnection= (HttpURLConnection) url.openConnection();
                //stream to hold the data
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data=reader.read();
                while(data!=-1){
                    char current =(char)data;
                    result+=current;
                    data=reader.read();
                }
                return result;
            }
            catch(Exception e)
            {
                e.printStackTrace();
                return "Something Went Wrong";
            }

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView)findViewById(R.id.imageView);
        btn1=(Button)findViewById(R.id.button1);
        btn2=(Button)findViewById(R.id.button2);
        btn3=(Button)findViewById(R.id.button3);
        btn4=(Button)findViewById(R.id.button4);
        DownloadTask downloadTask = new DownloadTask();
        String result=null;
        try {
            result = downloadTask.execute("http://www.imdb.com/list/ls066521486/").get();
            String[] splitResult = result.split("<div class=\"list detail\">");

            Pattern p = Pattern.compile("src=\"https(.*?)\"");
            Matcher m =p.matcher(splitResult[1]);
            while(m.find()){
               celebURLs.add(m.group(1));
                 Log.i("link",m.group(1));
            }

            p = Pattern.compile("alt=\"Image of (.*?)\"");
            m =p.matcher(splitResult[1]);
            while(m.find()){
               celebNames.add(m.group(1));
                Log.i("name",m.group(1));
            }

            Set<String> array = new LinkedHashSet<String>();
            array.addAll(celebNames);


            celebNames.clear();
            celebNames1 = new ArrayList<String>(array);

            for(String elem : celebNames1){
                Log.i("Value : ",elem);
            }
            celebURLs.remove(celebURLs.size() - 1);
            celebURLs.remove(celebURLs.size() - 1);
            for(String elem : celebURLs){
                Log.i("URL Value : ",elem);
            }

            for(int i = 0; i < celebURLs.size();i++){
                celebURLs.set(i,"https"+celebURLs.get(i));
            }


            generatequestionandanswers();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
       // Log.i("result : ",result);

    }
}
