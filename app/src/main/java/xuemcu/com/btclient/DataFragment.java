package xuemcu.com.btclient;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

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

/**
 * Created by Administrator on 2018/5/8 0008.
 */

public class DataFragment extends Fragment {


    private int timeType=3;     //1是以天为单位 2是以周为单位  3 以月

    static Myapplication myapplication;
    private static final String TAG = "WeightActivity";
    private LineChart lineChart;
    private LineChart lineChart1;
    private LineChart lineChart2;
    private LineData data;
    private LineDataSet lineDataSet;
    private ArrayList<String> xValues;
    private ArrayList<Entry> yValues;
    private ArrayList<String> xValues_current = new ArrayList<String>();
    private ArrayList<Entry> yValues_current = new ArrayList<Entry>();
    private int index = 1;
    private Connection conn;
    private ProgersssDialog progersssDialog;
    private DecimalFormat sdfdate;
    private List<WeightsBean> weights;
    private List<WeightsBean> weights1;
    private List<WeightsBean> weights2;
    private SimpleDateFormat sdfdatetime;
    private ImageButton headerBackBtn;//顶部返回图像按钮

    private DataBases myDataBase ;
    private SQLiteDatabase db ;

    private List weeklist;
    private List xweekList;
    private List daylist;
    private List xdaylist;

    private LinearLayout weigth_layout;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_weignt_day, container, false);



        initView(root);
//        initData();
        chartSetting();


        xValues = new ArrayList<String>();
        yValues = new ArrayList<Entry>();

        weights = new ArrayList<WeightsBean>();
        weights1=new ArrayList<WeightsBean>();
        weights2=new ArrayList<WeightsBean>();
        sdfdate  = new DecimalFormat("######0.00");
        sdfdatetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        progersssDialog = new ProgersssDialog(getActivity());

        myDataBase = new DataBases(getActivity());
        db = myDataBase.getReadableDatabase();

//        DayTask dayTask = new DayTask(progersssDialog);
//        dayTask.execute();



        weeklist = new ArrayList<Double>();
        xweekList= new ArrayList<String>();

        daylist = new ArrayList<Double>();
        xdaylist = new ArrayList<String>();
        DayTask task = new DayTask(progersssDialog);
        task.execute();






        return root;
    }



    private void initView(View root) {
        lineChart = root.findViewById(R.id.lineChart);

        weigth_layout =(LinearLayout)root.findViewById(R.id.weight_layout);
    }

//    private  void  initData(){
//        TextView tv_title=(TextView)weigth_layout.findViewById(R.id.headerTitle);
//        tv_title.setText("体重曲线图");
//    }





    //画图
    private void getData(List<WeightsBean> weights) {
        int sizes = weights.size();
//        xValues = new ArrayList<String>();

        for (int i = 0; i < sizes; i++) {
            // x轴显示的数据，这里默认使用数字下标显示
            xValues.add(weights.get(i).getTime().substring(5,10));
        }
        // y轴的数据parseFloat
//        yValues = new ArrayList<Entry>();
        for (int i = 0; i < sizes; i++) {
            BigDecimal d = new BigDecimal(weights.get(i).getWeight());
            yValues.add(new Entry((float) d.multiply(new BigDecimal("1")).doubleValue(), i));
        }
    }


    /*
    * 图表设置
    * */
    private void chartSetting() {

//        LimitLine limitLine= new LimitLine(6.5f,"初始体重");        //设置初始体重
        YAxis y = lineChart.getAxisRight();
        y.setEnabled(false);
//        y.addLimitLine(limitLine);

        XAxis x = lineChart.getXAxis();
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.setDescription("");

    }

    private void setData() {

        lineDataSet = new LineDataSet(yValues, "体重变化曲线" /* 显示在比例图上 */);
        // mLineDataSet.setFillAlpha(110);
        // mLineDataSet.setFillColor(Color.RED);
        // 用y轴的集合来设置参数
        lineDataSet.setLineWidth(1.75f); // 线宽
        lineDataSet.setCircleSize(3f);// 显示的圆形大小
        lineDataSet.setColor(getResources().getColor(R.color.light_orange));// 显示颜色
        lineDataSet.setCircleColor(getResources().getColor(R.color.light_red));// 圆形的颜色
        lineDataSet.setHighLightColor(getResources()
                .getColor(R.color.light_red)); // 高亮的线的颜色

        data = new LineData(xValues, lineDataSet);

        lineChart.setData(data);
        lineChart.animateX(2000);

    }
    private void styleOfCubicAndFill() {
        lineDataSet.setDrawCubic(true);
        lineDataSet.setDrawFilled(true);
        reset();
    }


    private void reset() {
        lineChart.removeAllViews();
        lineChart.setData(data);
        // 以动画形式展现
        lineChart.animateX(2000);
    }

    public class DayTask extends AsyncTask<Void, Void, Void> {
        ProgersssDialog progersssDialog;

        public DayTask(ProgersssDialog progersssDialog) {
            super();
            this.progersssDialog = progersssDialog;
        }

        @Override
        protected Void doInBackground(Void... voids) {


            //查询获得游标
            Cursor cursor = db.query("Users", null, null, null, null, null, null);
//                Cursor cursor = db.query(myapplication.getUser_name(),null,null,null,null,null,null);
            int j = 0;    //周期
            int i=1;
            double sum = 0;

            //判断游标是否为空
            if (cursor.moveToFirst()) {
                do {
                    //遍历Cursor对象，取出数据并打印
                    String Time = cursor.getString(cursor.getColumnIndex("Time"));
                    String Weight = cursor.getString(cursor.getColumnIndex("Weight"));
                    Log.e(TAG, "doInBackground: " + Time + Weight);
                    Double d = Double.parseDouble(Weight);


                    weights.add(new WeightsBean(d, Time));

                    //获取周数、体重平均值
                    sum +=Double.parseDouble(Weight) ;
                    if (i % 7 == 0) {
                        weeklist.add(sum / 7);
                        j++;
                        sum = 0;
                        xweekList.add("第" +j+ "周");
                    }
                    i++;

//                        daylist.add(Double.parseDouble(Weight));

                } while (cursor.moveToNext());

//                    //获取最近七天
//                    int n=weights.size();
//                    int h =n-7;
//                    for (int m=n;m>h;m--){
//                        xdaylist.add(weights.get(m-1).getTime());
//                    }


            }
            lineChart.notifyDataSetChanged();
            lineChart.invalidate();
            getData(weights);
            progersssDialog.cancel();

            return null;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progersssDialog.show();
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            lineChart.notifyDataSetChanged();
            lineChart.invalidate();
            setData();
            styleOfCubicAndFill();
        }
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            progersssDialog.cancel();
        }
        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
            progersssDialog.cancel();
        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
            progersssDialog.cancel();
        }
    }
    private void initWeek() {
        int i = 1;       //天数
        for (int m = 0; m < i-1; m++) {
            BigDecimal d = new BigDecimal(weights.get(m).getWeight());
            weeklist.add(new Entry((float) d.multiply(new BigDecimal("1")).doubleValue(), m));
        }
    }

    private void initDay(){
        int i=1;
        for (int m = 0; m < i-1; m++) {
            BigDecimal d = new BigDecimal(weights.get(m).getWeight());
            daylist.add(new Entry((float) d.multiply(new BigDecimal("1")).doubleValue(), m));
        }
    }
}
