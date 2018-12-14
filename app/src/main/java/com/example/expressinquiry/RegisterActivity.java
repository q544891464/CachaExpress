package com.example.expressinquiry;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.util.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity{

    private Date registerDate;

    private int questionID1;
    private int questionID2;

    private TextView text1;

    private EditText et_register_username;
    private EditText et_register_pass;
    private EditText et_register_passConfirm;
    private EditText et_register_phonenumber;

    private Button btn_Submit;

    private Spinner sp_question1;
    private EditText et_answer1;
    private ArrayAdapter adapter;
    private int status;
    private JSONObject json = new JSONObject();
    private Handler handler;
    private String url = "http://39.107.245.98/register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        text1=(TextView)findViewById(R.id.text1);

        et_register_username= (EditText)findViewById(R.id.et_registerUsername);
        et_register_pass = (EditText)findViewById(R.id.et_registerpass);
        et_register_passConfirm = (EditText)findViewById(R.id.et_registerpassConfirm);
        et_register_phonenumber = (EditText)findViewById(R.id.et_registerphonenumber);
        sp_question1=(Spinner) findViewById(R.id.sp_question1);
        et_answer1=(EditText)findViewById(R.id.et_answer1);

        adapter = ArrayAdapter.createFromResource(this, R.array.spingarr, android.R.layout.simple_spinner_item);
        //设置下拉菜单的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //将adapter添加到sp_question1当中
        sp_question1.setAdapter(adapter);
        //添加spinner监听器
        sp_question1.setOnItemSelectedListener(new SpinnerXMLSelectedListener());
        //设置默认值
        //sp_question1.setVisibility(View.VISIBLE);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);
                if (msg.what==123)
                {
                    Toast.makeText(RegisterActivity.this, "注册成功！",Toast.LENGTH_SHORT).show();
                    //跳转到登录成功的界面
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                else if (msg.what == 234)
                {
                    Toast.makeText(RegisterActivity.this, "您注册失败，可能是网络问题",Toast.LENGTH_SHORT).show();
                }

            }
        };
        btn_Submit = (Button)findViewById(R.id.btn_submit);






        /**
         * 发送注册数据给服务器
         */
        btn_Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                register();

                /*
                String register_pass=et_register_pass.getText().toString();
                String register_passConfirm=et_register_passConfirm.getText().toString();

                if(!register_pass.equals(register_passConfirm)){
                    Toast.makeText(RegisterActivity.this,"两次密码输入不一致！",Toast.LENGTH_SHORT).show();
                    return;
                }


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //向json添加用户名、密码、电话
                        try
                        {
                            json.put("username", et_register_username.getText().toString());
                            json.put("password", et_register_pass.getText().toString());
                            json.put("phonenumber", et_register_phonenumber.getText().toString());
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        String jsonStr = json.toString();
                        HttpUtils.postJson(url, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.e("TAG", "NetConnect error!");
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
                }).start();*/
            }
        });
    }

    public Date getRegisterDate() {
        registerDate=new Date(System.currentTimeMillis());
        return registerDate;
    }

    class SpinnerXMLSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
            text1.setText("你选择的id是："+arg2);
            setQuestionID1(arg2);

        }

        public void onNothingSelected(AdapterView<?> arg0) {

        }
    }

    public String getAnswer() {
        return et_answer1.getText().toString();
    }

    public void setQuestionID1(int questionID1) {
        this.questionID1 = questionID1;
    }

    public int getQuestionID1() {
        return questionID1;
    }

    public String getPass(){
        return et_register_pass.getText().toString();
    }

    public String getPassConfirm(){
        return et_register_passConfirm.getText().toString();
    }

    public String getUsername(){
        return et_register_username.getText().toString();
    }

    public String getPhonenumber(){
        return et_register_phonenumber.getText().toString();
    }

    public void register(){
        String register_pass=getPass();
        String register_passConfirm=getPassConfirm();

        if(!register_pass.equals(register_passConfirm)){
            Toast.makeText(RegisterActivity.this,"两次密码输入不一致！",Toast.LENGTH_SHORT).show();
            return;
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                //向json添加用户名、密码、电话
                try
                {
                    json.put("username", getUsername());
                    json.put("password", getPass());
                    json.put("phonenumber", getPhonenumber());
                    json.put("question",getQuestionID1());
                    json.put("answer",getAnswer());
                    json.put("registerDate",getRegisterDate());

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                String jsonStr = json.toString();
                HttpUtils.postJson(url, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("TAG", "NetConnect error!");
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
    }


}