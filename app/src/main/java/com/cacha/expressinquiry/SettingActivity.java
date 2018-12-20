package com.cacha.expressinquiry;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.cacha.expressinquiry.adapter.SettingRecyclerAdapter;
import com.cacha.expressinquiry.base.PinnedHeaderItemDecoration;
import com.cacha.expressinquiry.entities.TypeEntity;
import com.cacha.expressinquiry.model.SettingItem;
import com.cacha.util.HttpUtils;
import com.mylhyl.circledialog.CircleDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class SettingActivity extends BaseActivity
    implements SettingRecyclerAdapter.OnItemClickListener {

    private static String url_updateSex="http://39.107.245.98/update.php";
    private Handler handler;
    private DialogFragment dialogFragment;

 //   final MyApplication application = (MyApplication) this.getApplication();
/*
    private int userId = application.getId();
    private String sex = application.getSex();
    private String username = application.getUsername();
    private String phonenumber = application.getPhonenumber();*/

  RecyclerView recyclerView;
  SettingRecyclerAdapter settingRecyclerAdapter;


  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_setting);
    init();

      handler = new Handler(){
          @Override
          public void handleMessage(Message msg)
          {
              super.handleMessage(msg);
              if (msg.what==123)
              {
                  Toast.makeText(SettingActivity.this, "修改成功！",Toast.LENGTH_SHORT).show();

              }
              else if (msg.what == 234)
              {
                  Toast.makeText(SettingActivity.this, "修改失败！",Toast.LENGTH_SHORT).show();
              }

          }
      };
  }

  private void init() {
      final MyApplication application = (MyApplication) this.getApplication();
    recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    findViewById(R.id.back_img).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        SettingActivity.this.finish();
      }
    });
    recyclerView.setLayoutManager(
        new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    settingRecyclerAdapter = new SettingRecyclerAdapter();
    recyclerView.setAdapter(settingRecyclerAdapter);
    recyclerView.addItemDecoration(new PinnedHeaderItemDecoration());

    settingRecyclerAdapter.setOnItemClickListener(this);

    List<SettingItem> settingItemList = Util.parseSettings(getResources(), R.xml.settings);
    settingRecyclerAdapter.addAll(settingItemList);

    settingRecyclerAdapter.updateSecondaryText(R.id.id, application.getId()+"");
    settingRecyclerAdapter.updateSecondaryText(R.id.name, application.getUsername()+"");
    settingRecyclerAdapter.updateSecondaryText(R.id.phone, application.getPhonenumber()+"");
    settingRecyclerAdapter.updateSecondaryText(R.id.sex,application.getSex()+"");
    settingRecyclerAdapter.updateSecondaryText(R.id.registerDate,application.getRegisterDate()+"");
  }

  @Override public void onItemClick(int id, SettingItem item) {
    Util.toast(this, item);
   switch (id) {
        case R.id.name:
            new CircleDialog.Builder()
                    .setTitle("提示")
                    .setWidth(0.5f)
                    .setText("用户名无法更改")
                    .setPositive("确定", null)
                    .show(getSupportFragmentManager());
            break;
        case R.id.sex:
            final MyApplication application = (MyApplication) this.getApplication();
            final List<TypeEntity> items = new ArrayList<>();
            items.add(new TypeEntity(1, "男"));
            items.add(new TypeEntity(2, "女"));
//                final String[] items = {"拍照", "从相册选择", "小视频"};
            new CircleDialog.Builder()
                    .configDialog(params -> {
                        params.backgroundColorPress = Color.CYAN;
                        //增加弹出动画
                        params.animStyle = R.style.dialogWindowAnim;
                    })
                    .setTitle("性别")
//                        .setTitleColor(Color.BLUE)
                    .configTitle(params -> {
//                                params.backgroundColor = Color.RED;
                    })
                    .setSubTitle("请选择您的性别")
                    .configSubTitle(params -> {
//                                params.backgroundColor = Color.YELLOW;
                    })
                    .setItems(items, (parent, view1, position1, id1) -> {

                                if(Update(application.getId(),items.get(position1)+"","sex")){
                                    //Toast.makeText(SettingActivity.this, "点击了：" + items.get(position1)+" 修改成功！"
                                     //       , Toast.LENGTH_SHORT).show();
                                    application.setSex(items.get(position1)+"");
                                    settingRecyclerAdapter.updateSecondaryText(R.id.sex,application.getSex());
                                }else {
                                  //  Toast.makeText(SettingActivity.this, "点击了：" + items.get(position1)+" 修改失败！"
                                  //          , Toast.LENGTH_SHORT).show();
                                }
                                return true;
                            }
                    )
                    .setNegative("取消", null)
//                        .setNeutral("中间", null)
//                        .setPositive("确定", null)
//                        .configNegative(new ConfigButton() {
//                            @Override
//                            public void onConfig(ButtonParams params) {
//                                //取消按钮字体颜色
//                                params.textColor = Color.RED;
//                                params.backgroundColorPress = Color.BLUE;
//                            }
//                        })
                    .show(getSupportFragmentManager());
            break;

       case R.id.phone:
           final MyApplication application1 = (MyApplication) this.getApplication();
           Resources res = getResources();
           String question[] = res.getStringArray(R.array.spingarr);
           dialogFragment = new CircleDialog.Builder()
                   .setCanceledOnTouchOutside(false)
                   .setCancelable(true)
                   .setTitle("验证密保问题")
                   .setSubTitle(question[application1.getQuestion()])
                   .setInputHint("请输入密保问题答案")
                   .setInputText("")
                   .setInputHeight(80)
                   .setInputShowKeyboard(true)
                   .setInputEmoji(true)
                   .setInputCounter(20)
//                        .setInputCounter(20, (maxLen, currentLen) -> maxLen - currentLen + "/" + maxLen)
                   .configInput(params -> {
                            params.padding = new int[]{30, 30, 30, 30};
                            params.inputBackgroundResourceId = R.drawable.bg_input;
                            params.gravity = Gravity.CENTER;
                       //密码
//                                params.inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
//                                        | InputType.TYPE_TEXT_FLAG_MULTI_LINE;
                       //文字加粗
                       //params.styleText = Typeface.BOLD;
                   })
                   .setNegative("取消", null)
                   .setPositiveInput("确定", (text, v) -> {
                       if (TextUtils.isEmpty(text)) {
                           Toast.makeText(SettingActivity.this, "请输入内容", Toast.LENGTH_SHORT).show();
                           return false;
                       } else {
                           //答案正确的情况
                           Log.i("anwser", application1.getAnswer());
                           if (text.equals(application1.getAnswer())){
                               Toast.makeText(SettingActivity.this, text, Toast.LENGTH_SHORT).show();
                               RegisterPage page = new RegisterPage();
                               //如果使用我们的ui，没有申请模板编号的情况下需传null
                               page.setTempCode(null);
                               page.setRegisterCallback(new EventHandler() {
                                   public void afterEvent(int event, int result, Object data) {
                                       if (result == SMSSDK.RESULT_COMPLETE) {
                                           // 处理成功的结果
                                           HashMap<String,Object> phoneMap = (HashMap<String, Object>) data;
                                           String country = (String) phoneMap.get("country"); // 国家代码，如“86”
                                           String phone = (String) phoneMap.get("phone"); // 手机号码，如“13800138000”
                                           // TODO 利用国家代码和手机号码进行后续的操作
                                           if(Update(application1.getId(),phone+"","user_phonenumber")){
                                               //Toast.makeText(SettingActivity.this, "点击了：" + items.get(position1)+" 修改成功！"
                                               //       , Toast.LENGTH_SHORT).show();
                                               application1.setPhonenumber(phone);
                                               settingRecyclerAdapter.updateSecondaryText(R.id.phone,application1.getPhonenumber());
                                           }else {
                                               //  Toast.makeText(SettingActivity.this, "点击了：" + items.get(position1)+" 修改失败！"
                                               //          , Toast.LENGTH_SHORT).show();
                                           }
                                       } else{
                                           // TODO 处理错误的结果
                                           Toast.makeText(SettingActivity.this, " 修改失败！", Toast.LENGTH_SHORT).show();
                                       }
                                   }
                               });
                               page.show(this);
                           }
                           //答案错误的情况
                           else{
                               Toast.makeText(SettingActivity.this, "答案错误", Toast.LENGTH_SHORT).show();
                           }

                           return true;
                       }
                   })
                   .show(getSupportFragmentManager());
           break;

       case R.id.feedback:
           //系统邮件系统的动作为android.content.Intent.ACTION_SEND
           Intent email = new Intent(android.content.Intent.ACTION_SEND);
           email.setType("text/plain");

//设置邮件默认地址
           email.putExtra(android.content.Intent.EXTRA_EMAIL, "544891464@qq.com");
//设置邮件默认标题
           email.putExtra(android.content.Intent.EXTRA_SUBJECT, "意见反馈");
//设置要默认发送的内容
           email.putExtra(android.content.Intent.EXTRA_TEXT, "请写下您宝贵的意见");
//调用系统的邮件系统
           startActivity(Intent.createChooser(email, "请选择邮件发送软件"));
           break;


       case R.id.exit:

           removeALLActivity();

           Intent intent = new Intent(SettingActivity.this,LoginActivity.class);

           startActivity(intent);


   }
  }

    public boolean Update(int id1,String data,String updateType){
        JSONObject json = new JSONObject();
        final Object[] objs = new Object[1];


        new Thread(new Runnable() {
            @Override
            public void run() {


                //POST信息中加入用户名和密码
                try
                {
                    json.put("updateType",updateType);
                    json.put("id", id1);
                    json.put("data", data);


                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                String jsonStr = json.toString();
                Log.i("jsonData", jsonStr);
                //HttpUtils.httpPostMethod(url, json, handler);
                HttpUtils.postJson(url_updateSex, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseStr = response.toString();
                        String responseBodyStr = response.body().string();
                        try
                        {
                            //获取返回的json数据，为{"success":"success"}形式.
                            //JSONArray jsonArray = new JSONArray(responseBodyStr);
                            JSONObject jsonData = new JSONObject(responseBodyStr);
                            String resultStr = jsonData.getString("success");

                            Log.i("responseData", resultStr);

                            if (resultStr.equals("success")) //注册成功，发送消息
                            {
                                Message msg = handler.obtainMessage();
                                msg.what = 123;
                                handler.sendMessage(msg);
                            }
                            else //注册失败
                            {
                                Message msg = handler.obtainMessage();
                                msg.what = 234;
                                handler.sendMessage(msg);
                            }
                        }
                        catch(JSONException e)
                        {
                            e.printStackTrace();
                        }

                    }
                }, jsonStr);


            }
        }).start();

        return true;

    }



}

