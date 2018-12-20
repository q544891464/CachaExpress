package com.cacha.expressinquiry;

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
import android.widget.Toast;

import com.cacha.util.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import scala.util.regexp.Base;

public class QuestionActivity extends BaseActivity {


    private static String url_updateSex="http://39.107.245.98/update.php";
    private Handler handler;

    private int questionID1;

    private String answer1;

    private Button btn_question_submit;

    private Spinner sp_question1_new;

    private EditText et_answer1_new;

    private ArrayAdapter adapter;

    private int result = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        final MyApplication application = (MyApplication) this.getApplication();

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);
                if (msg.what==123)
                {
                    result++;
                    if(result == 2){
                        Toast.makeText(QuestionActivity.this, "修改成功！",Toast.LENGTH_SHORT).show();
                        application.setQuestion(getQuestionID1());
                        application.setAnswer(getAnswer());
                    }

                }
                else if (msg.what == 234)
                {
                    Toast.makeText(QuestionActivity.this, "修改失败！",Toast.LENGTH_SHORT).show();
                }

            }
        };

        btn_question_submit = (Button)findViewById(R.id.btn_question_submit);

        sp_question1_new = (Spinner)findViewById(R.id.sp_question1_new);

        et_answer1_new = (EditText)findViewById(R.id.et_answer1_new);

        adapter = ArrayAdapter.createFromResource(this, R.array.spingarr, android.R.layout.simple_spinner_item);
        //设置下拉菜单的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //将adapter添加到sp_question1当中
        sp_question1_new.setAdapter(adapter);
        //添加spinner监听器
        sp_question1_new.setOnItemSelectedListener(new SpinnerXMLSelectedListener());


        btn_question_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getAnswer().equals("")){
                    Toast.makeText(QuestionActivity.this,"请输入内容",Toast.LENGTH_SHORT);
                }else{
                    Update(application.getId(),getQuestionID1()+"","question1");

                    Update(application.getId(),getAnswer(),"answer1");

                }
            }
        });

    }

    public int getQuestionID1() {
        return questionID1;
    }

    public void setQuestionID1(int questionID1) {
        this.questionID1 = questionID1;
    }
    public String getAnswer() {
        return et_answer1_new.getText().toString();
    }


    class SpinnerXMLSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {

            setQuestionID1(arg2);

        }

        public void onNothingSelected(AdapterView<?> arg0) {

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
