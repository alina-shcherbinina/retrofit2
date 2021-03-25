package com.example.retrofit2;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public class MainActivity extends AppCompatActivity {

    String API_URL = "https://pixabay.com/";
    String key = "18604856-1deb472d3f26cd97839450987";
    String image_type = "all";
    MainActivity activity = this;

    ImageView imageView;
    EditText editText;
    ListView listView;
    TextView textView;

    PixabayAPI apiEmpty;
    int counter = 0;

    Hashtable<Integer,String> map = new Hashtable<Integer,String>();


    // TODO: добавить возможность выбора типа картинки (image_type)
    //  Filter results by image type.
    //Accepted values: "all", "photo", "illustration", "vector"
    //Default: "all"

    interface PixabayAPI {
        @GET("/api") // метод запроса (POST/GET) и путь к API
                    // пример содержимого веб-формы q=dogs+and+people&key=MYKEY&image_type=photo
        Call<Response> search(
                @Query("q") String q,
                @Query("key") String key,
                @Query("image_type") String image_type); // Тип ответа, действие, содержание запроса

        @GET()
        Call<ResponseBody> getImage (@Url String pictureURL);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image);
        editText = findViewById(R.id.text);
        listView = findViewById(R.id.list_view);
        textView = findViewById(R.id.textView);
        textView.setText(image_type);

        map.put(1,"photo");
        map.put(2,"illustration");
        map.put(3,"vector");
        map.put(4,"all");

    }

    public void onPressChange(View v) {
        counter++;
        if (counter == 4) {
            counter = 0;
            image_type = "all";
        } else {
            image_type = map.get(counter);
        }
        textView.setText(image_type);
    }

    public void startSearch(String text) {
        Log.d("mytag", "image type " + image_type);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL) // адрес API сервера
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // создаём обработчик, определённый интерфейсом PixabayAPI выше
        PixabayAPI api = retrofit.create(PixabayAPI.class);

        Retrofit retrofitEmpty = new Retrofit.Builder().baseUrl(API_URL).build();
         apiEmpty = retrofitEmpty.create(PixabayAPI.class);

//         указываем, какую функцию API будем использовать
        Call<Response> call = api.search(text, key, image_type);

        Callback<Response> callback = new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                // класс Response содердит поля, в которые будут записаны
                // результаты поиска по картинкам
                Response r = response.body(); // получили ответ в виде объекта
                // TODO: отобразить, сколько картинок найдено

                displayResults(r.hits);

                Log.d("mytag", "hits:" + r.hits.length); // сколько картинок нашлось

            }
            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                // обрабатываем ошибку, если она возникла
                // TODO: при возникновении ошибки вывести Toast
                Log.d("mytag", "Error: " + t.getLocalizedMessage());
            }
        };
        call.enqueue(callback); // ставим запрос в очередь

    }

    public void displayResults(Hit[] hits) {
        ListAdapter adapter = new ListAdapter(activity, hits, apiEmpty);
        listView.setAdapter(adapter);
    }

    public void onSearchClick(View v) {
        startSearch(editText.getText().toString());
    }
}