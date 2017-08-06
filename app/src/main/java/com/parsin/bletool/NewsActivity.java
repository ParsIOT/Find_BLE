package com.parsin.bletool;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class NewsActivity extends AppCompatActivity {
    ArrayAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ArrayAdapter adapter;
        final String[] months = {"بازدید وزیر صنعت از غرفه ایران خودرو در نمایشگاه تهران",
                "گزارش تصویری/ بازدید وزیر صنعت ، معدن و تجارت از غرفه گروه سایپا در نمایشگاه خودرو تهران",
                "گزارش تصویری بازدید معاون وزیر صنعت از غرفه سایپا",
                "بازدید مدیرعامل گروه سایپا از نمایشگاه خودرو تهران",
                "حضور اسنپ و تپسی در نمایشگاه بین المللی خودرو تهران",
                "استقبال گسترده سازمان ها و رسانه های ایرانی و بین المللی از غرفه خبرگزاری آوا در نمایشگاه رسانه ",
                "حضور فعال بانک آینده در نهمین نمایشگاه بین المللی طلا، نقره، جواهر، ساعت و صنایع وابسته",
                "تدوین برنامه راهبردی طلا و جواهرات و مصنوعات مرتبط"};
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, months);
        final ListView listview = (ListView) findViewById(R.id.list2);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                String text = (String) listview.getItemAtPosition(position);
                //startActivity(new Intent(NewsActivity.this, ItemBoothActivity.class));
                //Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });

    }

}
