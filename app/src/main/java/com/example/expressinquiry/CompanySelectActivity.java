package com.example.expressinquiry;

import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.expressinquiry.adapter.JAdapter;
import com.example.expressinquiry.adapter.CompanyAdapter;
import com.example.expressinquiry.adapter.SettingRecyclerAdapter;
import com.example.expressinquiry.base.PinnedHeaderItemDecoration;
import com.example.expressinquiry.model.SettingItem;
import com.soubw.jcontactlib.JListView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class CompanySelectActivity extends AppCompatActivity implements SettingRecyclerAdapter.OnItemClickListener{



    List<MainBean> beans = new ArrayList<>();
    RecyclerView recyclerView;
    SettingRecyclerAdapter settingRecyclerAdapter;

    InputStream inputStream = null;//输入流
    FileOutputStream outputStream = null;//输出流


    private List<MainBean> companyList;

    private JAdapter mAdaptor;

    private ProgressBar mLoadingView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_select);


        companyList = new ArrayList<>();
        initView();
        loadData();
    }


    private void initView(){
        MyApplication application = (MyApplication)this.getApplication();
        recyclerView = (RecyclerView) findViewById(R.id.company_recycler_view);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        settingRecyclerAdapter = new SettingRecyclerAdapter();
        recyclerView.setAdapter(settingRecyclerAdapter);
        recyclerView.addItemDecoration(new PinnedHeaderItemDecoration());

        settingRecyclerAdapter.setOnItemClickListener(this);

        List<SettingItem> settingItemList = Util.parseSettings(getResources(), R.xml.company);
        settingRecyclerAdapter.addAll(settingItemList);


        mLoadingView = (ProgressBar) findViewById(R.id.loading_view);
        mAdaptor = new CompanyAdapter(this,
                companyList,//联系人列表
                (JListView) findViewById(R.id.lvList),//JListView对象
                R.layout.jcontact_index_bar_view,//导航条视图
                R.layout.jcontact_preview_view,//预览字母背景图
                R.layout.jcontact_row_view,//列表内容view
                R.layout.jcontact_section_row_view,//列表字母view
                mLoadingView//加载LoadingView
        );
        JListView jListView = (JListView) findViewById(R.id.lvList);
        jListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainBean bean = (MainBean)parent.getAdapter().getItem(position);
                Toast.makeText(CompanySelectActivity.this,bean.getjName(),Toast.LENGTH_SHORT).show();
                application.setCompanyName(bean.getjName().substring(1));
                application.setCompanyCode(bean.getCode());
                application.setCompanySelected(true);
                finish();
            }
        });


    }


    public void loadData(){
        AssetManager assetManager = getAssets();

        int i;
        int index = 0;
        Workbook book;

        Cell name,code;
        try{
            book = Workbook.getWorkbook(assetManager.open("info.xls"));
            int sheetNum = book.getNumberOfSheets();
            Sheet sheet = book.getSheet(0);
            name = sheet.getCell(0, 0);
            i = 1;

            while (i < 120) {
                name = sheet.getCell(0,i);
                code = sheet.getCell(1,i);

                MainBean bean = new MainBean();
                bean.setjName(name.getContents());
                bean.setCode(code.getContents());
                bean.setjPhoneNumber("00000");

                beans.add(bean);

                i++;
            }

            book.close();

        }catch (Exception e) {
            Log.e("IO", "read error=" + e, e);
        }

        companyList.clear();
        companyList = beans;
        mAdaptor.setjContactsList(companyList);
    }

    @Override public void onItemClick(int id, SettingItem item) {
        MyApplication application = (MyApplication)this.getApplication();
        Util.toast(this, item);
        switch (id) {
            //TODO:填写id对应的返回值
            case R.id.company_sf:
                application.setCompanyName("顺丰速运");
                application.setCompanyCode("SF");
                application.setCompanySelected(true);
                finish();
                break;
            case R.id.company_hkty:
                application.setCompanyName("百世快递");
                application.setCompanyCode("HKTY");
                application.setCompanySelected(true);
                finish();
                break;
            case R.id.company_sto:
                application.setCompanyName("申通速运");
                application.setCompanyCode("STO");
                application.setCompanySelected(true);
                finish();
                break;
            case R.id.company_yd:
                application.setCompanyName("韵达速运");
                application.setCompanyCode("YD");
                application.setCompanySelected(true);
                finish();
                break;
            case R.id.company_yto:
                application.setCompanyName("圆通速运");
                application.setCompanyCode("YTO");
                application.setCompanySelected(true);
                finish();
                break;
            case R.id.company_zto:
                application.setCompanyName("中通速运");
                application.setCompanyCode("ZTO");
                application.setCompanySelected(true);
                finish();
                break;
                default:break;

        }

    }
}


