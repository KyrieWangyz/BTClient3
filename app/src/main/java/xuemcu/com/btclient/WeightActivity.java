package xuemcu.com.btclient;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.math.BigDecimal;
import java.sql.Connection;
import java.text.DecimalFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class WeightActivity extends AppCompatActivity implements View.OnClickListener {

    private DayFragment dayFragment;
    private DataFragment dataFragment;
    private WeekFragment weekFragment;
    private Button day_btn;
    private Button week_btn;
    private Button mon_btn;
    private FrameLayout frameLayout;
    private FragmentManager fm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_weight);

        fm = getFragmentManager();//必须加在这里，ActivityUtils
        dataFragment= (DataFragment) fm.findFragmentById(R.id.fragment_content);

        day_btn=findViewById(R.id.day_btn);
        week_btn=findViewById(R.id.week_btn);
        mon_btn=findViewById(R.id.month_btn);
        day_btn.setOnClickListener(this);
        week_btn.setOnClickListener(this);
        mon_btn.setOnClickListener(this);

        if(dataFragment==null){
            dataFragment=new DataFragment();
            FragmentTransaction fragmentTransaction
                    =fm.beginTransaction();
            fragmentTransaction.add(R.id.fragment_content,dataFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        switch (v.getId()){
            case R.id.day_btn:
                hideFragment(dataFragment,fragmentTransaction);
                hideFragment(weekFragment,fragmentTransaction);

                if(dayFragment==null){
                    dayFragment=new DayFragment();
                    fragmentTransaction.add(R.id.fragment_content,dayFragment);
                }else{
                    fragmentTransaction.show(dayFragment);
                }
                break;

            case R.id.week_btn:
                hideFragment(dataFragment,fragmentTransaction);
                hideFragment(dayFragment,fragmentTransaction);

                if(weekFragment==null){
                    weekFragment=new WeekFragment();
                    fragmentTransaction.add(R.id.fragment_content,weekFragment);
                }else{
                    fragmentTransaction.show(weekFragment);
                }
                break;

            case R.id.month_btn:
                hideFragment(dayFragment,fragmentTransaction);
                hideFragment(weekFragment,fragmentTransaction);

                if(dataFragment==null){
                    dataFragment=new DataFragment();
                    fragmentTransaction.add(R.id.fragment_content,dataFragment);
                }else{
                    fragmentTransaction.show(dataFragment);
                }
                break;
        }
        fragmentTransaction.commit();
    }
    private void hideFragment(Fragment fragment, FragmentTransaction ft){
        if(fragment != null){
            ft.hide(fragment);
        }
    }
}