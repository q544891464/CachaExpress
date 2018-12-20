package com.cacha.expressinquiry;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cacha.util.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ForgetPasswordActivity extends BaseActivity {

    private static String url_update="http://39.107.245.98/update.php";
    private EditText et_new_password;
    private EditText et_new_password_confirm;

    private Button btn_forget_password_submit;
    private Handler handler;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        Intent intent = getIntent();
        String str_userId = intent.getStringExtra("userId");
        userId = Integer.parseInt(str_userId);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);
                if (msg.what==123)
                {
                    Toast.makeText(ForgetPasswordActivity.this, "修改成功！",Toast.LENGTH_SHORT).show();
                    finish();

                }
                else if (msg.what == 234)
                {
                    Toast.makeText(ForgetPasswordActivity.this, "修改失败！可能是网络出了问题！",Toast.LENGTH_SHORT).show();
                }

            }
        };

        et_new_password = (EditText)findViewById(R.id.et_new_password);
        et_new_password_confirm = (EditText)findViewById(R.id.et_new_password_confirm);

        btn_forget_password_submit = (Button)findViewById(R.id.btn_forget_password_submit);

        btn_forget_password_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String new_pass=getPass();
                String new_passConfirm=getPassConfirm();

                if(!new_pass.equals(new_passConfirm)){
                    Toast.makeText(ForgetPasswordActivity.this,"两次密码输入不一致！",Toast.LENGTH_SHORT).show();
                    return;
                }

                Update(userId,new_pass,"user_password");
            }
        });
    }

    public String getPass(){
        return et_new_password.getText().toString();
    }

    public String getPassConfirm(){
        return et_new_password_confirm.getText().toString();
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
                HttpUtils.postJson(url_update, new Callback() {
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
