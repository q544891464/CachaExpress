package com.example.expressinquiry;

import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.heima.tabview.library.TabView;
import com.heima.tabview.library.TabViewChild;
import java.util.ArrayList;
import java.util.List;



public class MainActivity extends FragmentActivity implements MineFragment.OnFragmentInteractionListener,MainFragment.OnFragmentInteractionListener,SearchFragment.OnFragmentInteractionListener {

    TabView tabView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabView= (TabView) findViewById(R.id.tabView);
        //start add data
        List<TabViewChild> tabViewChildList=new ArrayList<>();
        TabViewChild tabViewChild01=new TabViewChild(R.drawable.tab01_sel,R.drawable.tab01_unsel,"首页",  MainFragment.newInstance("首页","首页"));
        TabViewChild tabViewChild02=new TabViewChild(R.drawable.tab02_sel,R.drawable.tab02_unsel,"查询",  SearchFragment.newInstance("查询",""));
        TabViewChild tabViewChild03=new TabViewChild(R.drawable.tab03_sel,R.drawable.tab03_unsel,"个人",  MineFragment.newInstance("个人","个人"));

        tabViewChildList.add(tabViewChild01);
        tabViewChildList.add(tabViewChild02);
        tabViewChildList.add(tabViewChild03);

        //end add data
        tabView.setTabViewDefaultPosition(2);
        tabView.setTabViewChild(tabViewChildList,getSupportFragmentManager());
        tabView.setOnTabChildClickListener(new TabView.OnTabChildClickListener() {
            @Override
            public void onTabChildClick(int  position, ImageView currentImageIcon, TextView currentTextView) {
                // Toast.makeText(getApplicationContext(),"position:"+position,Toast.LENGTH_SHORT).show();
            }
        });




    }

    /**
     * fragment需要绑定一个Activity用来传输数据 onFragmentInteraction就是传输数据的工具 不可缺少
     * @param uri
     */
    @Override
    public void onFragmentInteraction(Uri uri) {
        Toast.makeText(this,"success",Toast.LENGTH_LONG).show();
    }
}
