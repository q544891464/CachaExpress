package com.cacha.expressinquiry;

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

import com.cacha.expressinquiry.adapter.JAdapter;
import com.cacha.expressinquiry.adapter.CompanyAdapter;
import com.cacha.expressinquiry.adapter.SettingRecyclerAdapter;
import com.cacha.expressinquiry.base.PinnedHeaderItemDecoration;
import com.cacha.expressinquiry.model.SettingItem;
import com.soubw.jcontactlib.JListView;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class CompanySelectActivity extends BaseActivity implements SettingRecyclerAdapter.OnItemClickListener{



    List<MainBean> beans = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerView recyclerView_hide;
    SettingRecyclerAdapter settingRecyclerAdapter;
    SettingRecyclerAdapter settingRecyclerAdapter_hide;


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

        recyclerView_hide = (RecyclerView) findViewById(R.id.company_hide);
        recyclerView_hide.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        settingRecyclerAdapter_hide = new SettingRecyclerAdapter();
        recyclerView_hide.setAdapter(settingRecyclerAdapter_hide);
        recyclerView_hide.addItemDecoration(new PinnedHeaderItemDecoration());

        settingRecyclerAdapter_hide.setOnItemClickListener(this);

        List<SettingItem> settingItemList_hide = Util.parseSettings(getResources(), R.xml.company_hide);
        settingRecyclerAdapter_hide.addAll(settingItemList_hide);


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
        //Util.toast(this, item);
        switch (id) {
            //TODO:填写id对应的返回值

            case R.id.normal_company_hide:
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView_hide.setVisibility(View.GONE);break;

            case R.id.normal_company:

                recyclerView_hide.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);break;
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
            case R.id.company_yzpy:
                application.setCompanyName("邮政快递包裹");
                application.setCompanyCode("YZPY");
                application.setCompanySelected(true);
                finish();
                break;
            case R.id.company_ems:
                application.setCompanyName("EMS");
                application.setCompanyCode("EMS");
                application.setCompanySelected(true);
                finish();
                break;
            case R.id.company_hhtt:
                application.setCompanyName("天天快递");
                application.setCompanyCode("HHTT");
                application.setCompanySelected(true);
                finish();
                break;
            case R.id.company_jd:
                application.setCompanyName("京东快递");
                application.setCompanyCode("JD");
                application.setCompanySelected(true);
                finish();
                break;
            case R.id.company_zjs:
                application.setCompanyName("宅急送");
                application.setCompanyCode("ZJS");
                application.setCompanySelected(true);
                finish();
                break;
                default:break;

        }

    }
}


