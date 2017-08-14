package com.parsin.bletool.View;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.parsin.bletool.Model.DaoSession;
import com.parsin.bletool.Model.LocationValidation;
import com.parsin.bletool.Model.LocationValidationDao;
import com.parsin.bletool.R;
import com.parsin.bletool.Utils.MyApp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LogDbActivity extends AppCompatActivity {

    private static final String TAG = LogDbActivity.class.getSimpleName();
    private DaoSession mDaoSession;
    private static final String SQL_DISTINCT_ENAME = "SELECT DISTINCT " +
            LocationValidationDao.Properties.ManLocation.columnName + " FROM " + LocationValidationDao.TABLENAME;

    private ArrayList<String> manListDistinct = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_db);
        mDaoSession = ((MyApp) getApplication()).getDaoSession();
        addToTable("manual", "estimate", "time");

        manListDistinct = getDistinctManLoc(mDaoSession);
        for (String s : manListDistinct){
            List<LocationValidation> list = mDaoSession.getLocationValidationDao().queryBuilder()
                    .where(LocationValidationDao.Properties.ManLocation.eq(s))
                    .orderAsc(LocationValidationDao.Properties.EstLocation).list();
            for (LocationValidation l : list){
                addToTable(l.getManLocation(), l.getEstLocation(), l.getTime());
            }
            addToTable("     ", "     ", "     ");
            addToTable("     ", "     ", "     ");
        }
    }

    private void addToTable(String man, String est, String date){
        TableLayout table = (TableLayout) findViewById(R.id.table_layout);

        TableRow row = new TableRow(this);



        TextView manTextView = new TextView(this);
        TextView estTextView = new TextView(this);
        TextView dateTextView = new TextView(this);
        manTextView.setText(man);
        estTextView.setText(est);
        dateTextView.setText(date);

        manTextView.setGravity(Gravity.CENTER);
        estTextView.setGravity(Gravity.CENTER);
        dateTextView.setGravity(Gravity.CENTER);

        manTextView.setPadding(10,10,10,10);
        estTextView.setPadding(10,10,10,10);
        dateTextView.setPadding(10,10,10,10);



        row.addView(manTextView);
        row.addView(estTextView);
        row.addView(dateTextView);


        // add the TableRow to the TableLayout
        table.addView(row,TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);

    }


    public static ArrayList<String> getDistinctManLoc(DaoSession session) {
        ArrayList<String> result = new ArrayList<>();
        Cursor c = session.getDatabase().rawQuery(SQL_DISTINCT_ENAME, null);
        try{
            if (c.moveToFirst()) {
                do {
                    result.add(c.getString(0));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return result;
    }
}
