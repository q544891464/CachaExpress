package com.example.expressinquiry;

import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expressinquiry.adapter.DeliveryAdapter;
import com.example.expressinquiry.bean.DeliveryResult;
import com.example.util.Database;
import com.example.expressinquiry.bean.DeliveryData;
import com.example.expressinquiry.bean.HistoryData;
import com.example.expressinquiry.adapter.CompanyAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.expressinquiry.R;
import com.example.expressinquiry.bean.HistoryData;
import com.example.expressinquiry.bean.DeliveryData;
import com.example.util.Database;
import com.example.util.CompanyInfo;
import com.example.util.ShareUtils;

import net.sf.json.JSON;


public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity";


    private ImageView ivDeliveryPic; //快递公司图片
    private TextView tvDeliveryName; //快递公司名
    private TextView tvDeliveryNumber; //快递单号
    private Button btnRefresh; //刷新按钮

    private ListView lvLogisticsInfoView; //物流信息展示列表
    private TextView tvNoFindInfo; //未能查找到相应的物流信息文字
    private ProgressBar pbLoading; //正在加载进度条

    private List<DeliveryData> logisticsList = new ArrayList<DeliveryData>(); //物流信息列表
    private Database mDatabase; //数据库对象
    private String remark;
    private String companyName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initViews(); //初始化界面
        initDatas(); //初始化数据
        initEvents(); //初始化事件
    }

    /**
     * 初始化事件
     */
    private void initEvents() {
        //给刷新按钮设置点击事件
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initDatas(); //重新初始化数据
                Toast.makeText(DetailActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initDatas() {
        MyApplication application = (MyApplication)getApplication();
        Intent intent = getIntent(); //获取主界面传来的数据
        String deliveryNumber = intent.getStringExtra("deliveryNumber"); //获取快递单号
        String deliveryName = intent.getStringExtra("deliveryCode"); //获取快递公司编号
        companyName = intent.getStringExtra("companyName");//获取快递公司名字
        remark = intent.getStringExtra("remark");//获取备注
        int pictureId = CompanyInfo.getCompanyPicture(deliveryName);

        Log.e(TAG, "pictureId=" + pictureId);

        ivDeliveryPic.setImageResource(pictureId); //设置快递公司图片到控件上
        tvDeliveryName.setText(companyName); //设置快递公司名到控件上
        tvDeliveryNumber.setText(deliveryNumber); //设置快递单号到控件上
        pbLoading.setVisibility(View.VISIBLE); //设置正在加载进度条可见

        mDatabase = Database.getInstance(this); //初始化数据库对象

        initLogisticsInfoView(deliveryName, deliveryNumber); //初始化物流信息界面




    }




    /**
     * 初始化物流信息界面
     *
     * @param deliveryName   快递公司名
     * @param deliveryNumber 快递单号
     */
    private void initLogisticsInfoView(String deliveryName, String deliveryNumber) {
        //拼接URL请求网址
        /*String url = "http://v.juhe.cn/exp/index?key=f0a463f42885094d2c2050db357d9880" +
                "&com=" + deliveryName + "&no=" + deliveryNumber;

        Log.e(TAG, "initLogisticsInfoView: url=" + url);
        //2.判断快递公司名和快递单号不为空
        if (!TextUtils.isEmpty(deliveryName) && !TextUtils.isEmpty(deliveryNumber)) { //必填内容不为空
            //3.拿到数据去请求Json
            RxVolley.get(url, new HttpCallback() {
                @Override
                public void onSuccess(String t) { //请求成功后执行的方法
                    Log.i(TAG, "onClick:Json:" + t);
                    //清空物流信息列表
                    logisticsList.clear();

                    //4.解析Json
                    parsingJson(t);
                }
            });
        }*/
        MyApplication application = (MyApplication)getApplication();
        String result = application.getSearchResult();

        parsingJson(result);
    }

    private DeliveryResult parseJSONWithGson(String jsonData)
    {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        return gson.fromJson(jsonData, DeliveryResult.class);
    }

    /**
     * 解析传入的Json数据
     *
     * @param t json数据
     */
    private void parsingJson(String t) {
        try {
            Log.e(TAG, "parsingJson: "+t);
            JSONObject jsonObject = new JSONObject(t); //获取Json根对象





            DeliveryResult deliveryResult = parseJSONWithGson(t);
            Boolean resultIsSuccess = deliveryResult.isSuccess();//获取响应码


            if (!resultIsSuccess) { //响应码不为success，即请求失败
                tvNoFindInfo.setVisibility(View.VISIBLE); //显示未找到订单文字
                pbLoading.setVisibility(View.GONE); //设置正在加载进度条消失
                return;
            }


            //JSONObject jsonResult = jsonObject.getJSONObject(); //获取根对象中的result节点
            // JSONArray jsonArray = jsonResult.getJSONArray("list"); //获取result节点中的list数组

            for (int i = 0; i < deliveryResult.getTraces().size(); i++) { //遍历list数组
                //JSONObject json = jsonArray.getJSONObject(i); //从list数组中获取单个Json对象
                DeliveryResult.Traces traces =deliveryResult.getTraces().get(i);

                DeliveryData data = new DeliveryData(); //新建快递实体类
                data.setRemark(traces.getAcceptStation()); //设置快递状态
                data.setDatetime(traces.getAcceptTime()+""); //设置快递状态改变时间

                logisticsList.add(data); //将实体类添加进实体类列表
            }

            Collections.reverse(logisticsList); //将实体类列表中的数据逆序排列

            writeDeliveryToDB(deliveryResult); //将快递信息写入数据库

            DeliveryAdapter adapter = new DeliveryAdapter(this, logisticsList); //新建快递类适配器
            lvLogisticsInfoView.setAdapter(adapter); //为实体类展示列表界面设置适配器

            pbLoading.setVisibility(View.GONE); //设置正在加载进度条消失
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将快递信息写入数据库
     *
     * @param deliveryResult
     */
    private void writeDeliveryToDB(DeliveryResult deliveryResult) {
        try {
            MyApplication application = (MyApplication)getApplication();
            String company = deliveryResult.getShipperCode(); //获取快递公司编号
            String number = deliveryResult.getLogisticCode(); //获取快递单号
            String status = deliveryResult.getState(); //获取快递状态


            DeliveryData data = logisticsList.get(0); //获取快递列表中第一个数据
            String datetime = data.getDatetime(); //获取快递更新时间

            HistoryData historyData = new HistoryData(); //搜索历史数据
            historyData.setCompanyName(companyName);//设置快递公司名字
            historyData.setCompany(company); //设置快递公司编号
            historyData.setNumber(number); //设置快递单号
            historyData.setStatus(status); //设置快递状态
            historyData.setRemark(remark); //设置快递备注
            historyData.setDatetime(datetime); //设置快递更新时间

            Log.e(TAG, "!mDatabase.checkIfHasDelivery(number)=" + !mDatabase.checkIfHasDelivery(number));
            if (!mDatabase.checkIfHasDelivery(number)) { //数据库中未有该快递信息
                Log.e(TAG, "mDatabase.addHistory(historyData)");
                mDatabase.addHistory(historyData); //插入该搜索历史

                String loginUserName = ShareUtils.getLoginUserName(this); //获取当前登陆的用户名

                Log.e(TAG, "writeDeliveryToDB: loginUserName=" + loginUserName);
                Log.e(TAG, "writeDeliveryToDB: number=" + number);

                if (!"".equals(loginUserName)) { //当前登录的用户名不为空
                    mDatabase.bindUserAndHistory(loginUserName, number); //绑定用户名与快递单号
                }
            } else { //数据库中已有该快递信息
                Log.e(TAG, "mDatabase.updateHistory(historyData)");
                mDatabase.updateHistory(historyData); //更新该条搜索历史
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化界面
     */
    private void initViews() {
        ivDeliveryPic = (ImageView) findViewById(R.id.iv_deliveryPic);
        tvDeliveryName = (TextView) findViewById(R.id.tv_deliveryName);
        tvDeliveryNumber = (TextView) findViewById(R.id.tv_deliveryNumber);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        btnRefresh = (Button) findViewById(R.id.btn_refresh);
        lvLogisticsInfoView = (ListView) findViewById(R.id.mListView);
        tvNoFindInfo = (TextView) findViewById(R.id.tv_noFindInfo);
    }
}