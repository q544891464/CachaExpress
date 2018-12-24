package com.cacha.expressinquiry;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends AppCompatActivity {

    private RelativeLayout relativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        findViewById(R.id.back_img).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                AboutActivity.this.finish();
            }
        });

        initViews();

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.logo108x108)//图片
                .setDescription("Cacha快递查询APP")//介绍
                .addItem(new Element().setTitle("Version 1.0"))
                .addGroup("与我联系")
                .addEmail("544891464@qq.com")//邮箱
                .addWebsite("http://www.chinm.top")//网站
                .addPlayStore("com.cacha.expressinquiry")//应用商店
                .addGitHub("q544891464")//github
                .create();

        relativeLayout.addView(aboutPage);

    }

    private void initViews(){
        relativeLayout= (RelativeLayout) findViewById(R.id.relativeLayout);

    }

}
