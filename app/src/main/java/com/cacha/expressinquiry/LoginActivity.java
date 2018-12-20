package com.cacha.expressinquiry;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cacha.expressinquiry.bean.HistoryData;
import com.cacha.expressinquiry.entities.TypeEntity;
import com.cacha.util.Base64Utils;
import com.cacha.util.Database;
import com.cacha.util.HttpUtils;
import com.cacha.util.ShareUtils;
import com.cacha.util.SharedPreferencesUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mylhyl.circledialog.CircleDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener {

    private Handler handler;
    private EditText username;
    private EditText password;
    private Button btn_login;
    private Button btn_register;
    private TextView txtResult;
    private CheckBox checkBox_password;
    private CheckBox checkBox_login;
    private Button btn_login_by_phone;
    private Button btn_forget_password;
    private ImageView iv_see_password;
    private String url = "http://39.107.245.98/login.php";
    private String url1 = "http://39.107.245.98/json_array.php";
    private String url_login_by_phone = "http://39.107.245.98/login_by_phone.php";
    private String result;
    private Database mDatabase;
    LoginResult m_result;

    Map<String, String> map = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mDatabase = Database.getInstance(this);

        //处理登录成功消息
        handler  = new Handler(){
            @Override
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);
                switch (msg.what)
                {
                    case 123:
                        try
                        {
                            //获取用户登录的结果
                            LoginResult result = (LoginResult)msg.obj;
                            String userName = result.getUsername();
                            txtResult.setText(userName+" 成功登录!");

                            if (!mDatabase.checkIsHaveUserName(userName)) //数据库中没有该用户名的数据
                                mDatabase.addUser(userName, result.getPassword()); //保存用户名和密码到数据库中

                            Toast.makeText(LoginActivity.this, "您成功登录",Toast.LENGTH_SHORT).show();

                            //跳转到登录成功的界面
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                            startActivity(intent);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        break;
                    case 456:
                        try
                        {
                            Toast.makeText(LoginActivity.this, "密码错误",Toast.LENGTH_SHORT).show();


                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        break;
                    case 789:
                        try
                        {
                            Toast.makeText(LoginActivity.this,"用户不存在",Toast.LENGTH_SHORT).show();

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        break;
                    case 333:
                        try
                        {
                            //获取用户登录的结果
                            LoginResult result = (LoginResult)msg.obj;
                            String userName = result.getUsername();
                            txtResult.setText(userName+" 成功登录!");

                            if (!mDatabase.checkIsHaveUserName(userName)) //数据库中没有该用户名的数据
                                mDatabase.addUser(userName, result.getPassword()); //保存用户名和密码到数据库中

                            Toast.makeText(LoginActivity.this, "该号码已经注册，您已成功登录",Toast.LENGTH_SHORT).show();

                            //跳转到登录成功的界面
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                            startActivity(intent);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        break;
                    case 555:
                        try
                        {
                            Toast.makeText(LoginActivity.this,"用户尚未注册!",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);

                            intent.putExtra("phone",msg.obj.toString());

                            startActivity(intent);

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        break;
                    case 666:
                        try
                        {
                            LoginResult result = (LoginResult)msg.obj;
                            int userId = result.getId();
                            Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);

                            intent.putExtra("userId",userId+"");

                            startActivity(intent);

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        break;
                    case 888:
                        try
                        {
                            Toast.makeText(LoginActivity.this,"用户不存在",Toast.LENGTH_SHORT).show();

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
            }
        };

        Bundle bundle = this.getIntent().getExtras();

        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);

        btn_login = (Button)findViewById(R.id.btn_login);
        btn_register = (Button)findViewById(R.id.btn_register);
        btn_login_by_phone = (Button)findViewById(R.id.btn_login_by_phone);
        btn_forget_password = (Button)findViewById(R.id.btn_forget_password);

        txtResult = (TextView)findViewById(R.id.txtResult);

        checkBox_password = (CheckBox) findViewById(R.id.checkBox_password);
        checkBox_login = (CheckBox) findViewById(R.id.checkBox_login);

        iv_see_password = (ImageView) findViewById(R.id.iv_see_password);

        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        btn_login_by_phone.setOnClickListener(this);
        btn_forget_password.setOnClickListener(this);
        checkBox_password.setOnCheckedChangeListener(this);
        checkBox_login.setOnCheckedChangeListener(this);
        iv_see_password.setOnClickListener(this);
        if (bundle != null)
        {
            username.setText(bundle.getString("empNo"));
            password.setText(bundle.getString("pass"));
        }

        initData();
    }

    private LoginResult parseJSONWithGson(String jsonData)
    {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        return gson.fromJson(jsonData, LoginResult.class);
    }

    /**
     * 非自动登录的情况下向服务器以Post方式发送登录信息
     */
    public void LoginPost(){

        final MyApplication application = (MyApplication)this.getApplication();

        if (getAccount().isEmpty()){
            Toast.makeText(LoginActivity.this,"你输入的账号为空!",Toast.LENGTH_SHORT).show();
            return;
        }

        if (getPassword().isEmpty()){
            Toast.makeText(LoginActivity.this,"你输入的密码为空!",Toast.LENGTH_SHORT).show();
            return;
        }

        String str_username = username.getText().toString().trim();

        ShareUtils.addCurrentLoginUser(this, str_username);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    //POST信息中加入用户名和密码
                    map.put("uid", username.getText().toString().trim());
                    map.put("pwd", password.getText().toString().trim());
                    //HttpUtils.httpPostMethod(url, json, handler);
                    Log.i("username", username.getText().toString());
                    Log.i("password", password.getText().toString());
                    Log.i("uid", map.get("uid"));
                    Log.i("pwd", map.get("pwd"));
                    HttpUtils.post(url, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("DaiDai", "OnFaile:",e);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseBody = response.body().string();
                            m_result = parseJSONWithGson(responseBody);
                            //发送登录成功的消息
                            Message msg = handler.obtainMessage();



                            switch (m_result.getStatus()){
                                case "1":msg.what = 123;loadCheckBoxState();
                                    Log.i("result", m_result.getStatus());
                                    Log.i("id",m_result.getId()+"");
                                    Log.i("question", m_result.getQuestion()+"");
                                    Log.i("sex", m_result.getSex()+"");
                                    Log.i("anwser", m_result.getAnswer());

                                    //将用户数据存在application中
                                    application.setId(m_result.getId());
                                    application.setUsername(m_result.getUsername());
                                    application.setQuestion(m_result.getQuestion());
                                    application.setAnswer(m_result.getAnswer());
                                    application.setPassword(m_result.getPassword());
                                    application.setPhonenumber(m_result.getPhonenumber());
                                    application.setSex(m_result.getSex());
                                    application.setRegisterDate(m_result.getRegisterDate());
                                    application.setIcon(m_result.getIcon());break;//登录成功
                                case "-2":msg.what = 456;//密码错误
                                    break;
                                case "-1":msg.what = 789;//用户不存在
                                    break;
                            }

                            msg.obj = m_result; //把登录结果也发送过去
                            handler.sendMessage(msg);

                        }
                    }, map);
                }
                catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();



    }

    /**
     * 将用户名与快递单号联系起来
     * @param username 用户名
     */
    private void connectUserAndHistoryList(String username) {
        //获取未绑定用户的查询历史列表
        List<HistoryData> historyList = mDatabase.getHistoryNotBindUser();
        int listSize = historyList.size(); //获取列表的大小

        HistoryData data; //查询历史临时变量
        for (int i = 0; i < listSize; i++) {
            data = historyList.get(i); //获取该位置的的查询历史
            mDatabase.bindUserAndHistory(username, data.getNumber()); //绑定用户与快递单号
        }
    }

    private void initData() {


        //判断用户第一次登陆
        if (firstLogin()) {
            checkBox_password.setChecked(false);//取消记住密码的复选框
            checkBox_login.setChecked(false);//取消自动登录的复选框
        }
        //判断是否记住密码
        if (remenberPassword()) {
            checkBox_password.setChecked(true);//勾选记住密码
            setTextNameAndPassword();//把密码和账号输入到输入框中
        } else {
            setTextName();//把用户账号放到输入账号的输入框中
        }

        //判断是否自动登录
        if (autoLogin()) {
            checkBox_login.setChecked(true);
            setTextNameAndPassword();
            LoginPost();//去登录就可以

        }
    }

    /**
     * 把本地保存的数据设置数据到输入框中
     */
    public void setTextNameAndPassword() {
        username.setText("" + getLocalName());
        password.setText("" + getLocalPassword());
    }

    /**
     * 设置数据到输入框中
     */
    public void setTextName() {
        username.setText("" + getLocalName());
    }


    /**
     * 获得保存在本地的用户名
     */
    public String getLocalName() {
        //获取SharedPreferences对象，使用自定义类的方法来获取对象
        SharedPreferencesUtils helper = new SharedPreferencesUtils(this, "setting");
        String name = helper.getString("name");
        return name;
    }


    /**
     * 获得保存在本地的密码
     */
    public String getLocalPassword() {
        //获取SharedPreferences对象，使用自定义类的方法来获取对象
        SharedPreferencesUtils helper = new SharedPreferencesUtils(this, "setting");
        String password = helper.getString("password");
        return Base64Utils.decryptBASE64(password);   //解码一下
//       return password;   //解码一下

    }

    /**
     * 判断是否自动登录
     */
    private boolean autoLogin() {
        //获取SharedPreferences对象，使用自定义类的方法来获取对象
        SharedPreferencesUtils helper = new SharedPreferencesUtils(this, "setting");
        boolean autoLogin = helper.getBoolean("autoLogin", false);
        return autoLogin;
    }

    /**
     * 判断是否记住密码
     */
    private boolean remenberPassword() {
        //获取SharedPreferences对象，使用自定义类的方法来获取对象
        SharedPreferencesUtils helper = new SharedPreferencesUtils(this, "setting");
        boolean remenberPassword = helper.getBoolean("remenberPassword", false);
        return remenberPassword;
    } 

    /**
     * 判断是否是第一次登陆
     */
    private boolean firstLogin() {
        //获取SharedPreferences对象，使用自定义类的方法来获取对象
        SharedPreferencesUtils helper = new SharedPreferencesUtils(this, "setting");
        boolean first = helper.getBoolean("first", true);
        if (first) {
            //创建一个ContentVa对象（自定义的）设置不是第一次登录，,并创建记住密码和自动登录是默认不选，创建账号和密码为空
            helper.putValues(new SharedPreferencesUtils.ContentValue("first", false),
                    new SharedPreferencesUtils.ContentValue("remenberPassword", false),
                    new SharedPreferencesUtils.ContentValue("autoLogin", false),
                    new SharedPreferencesUtils.ContentValue("name", ""),
                    new SharedPreferencesUtils.ContentValue("password", ""));
            return true;
        }
        return false;
    }

    /**
     * CheckBox点击时的回调方法 ,不管是勾选还是取消勾选都会得到回调
     *
     * @param buttonView 按钮对象
     * @param isChecked  按钮的状态
     */
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == checkBox_password) {  //记住密码选框发生改变时
            if (!isChecked) {   //如果取消“记住密码”，那么同样取消自动登陆
                checkBox_login.setChecked(false);
            }
        } else if (buttonView == checkBox_login) {   //自动登陆选框发生改变时
            if (isChecked) {   //如果选择“自动登录”，那么同样选中“记住密码”
                checkBox_password.setChecked(true);
            }
        }
    }


    /**
     * 保存用户账号
     */
    public void loadUserName() {
        if (!getAccount().equals("") || !getAccount().equals("请输入登录账号")) {
            SharedPreferencesUtils helper = new SharedPreferencesUtils(this, "setting");
            helper.putValues(new SharedPreferencesUtils.ContentValue("name", getAccount()));
        }

    }

    /**
     * 设置密码可见和不可见的相互转换
     */
    private void setPasswordVisibility() {
        if (iv_see_password.isSelected()) {
            iv_see_password.setSelected(false);
            //密码不可见
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        } else {
            iv_see_password.setSelected(true);
            //密码可见
            password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }

    }

    /**
     * 获取账号
     */
    public String getAccount() {
        return username.getText().toString().trim();//去掉空格
    }

    /**
     * 获取密码
     */
    public String getPassword() {
        return password.getText().toString().trim();//去掉空格
    }


    /**
     * 保存用户选择“记住密码”和“自动登陆”的状态
     */
    private void loadCheckBoxState() {
        loadCheckBoxState(checkBox_password, checkBox_login);
    }

    /**
     * 保存按钮的状态值
     */
    public void loadCheckBoxState(CheckBox checkBox_password, CheckBox checkBox_login) {

        //获取SharedPreferences对象，使用自定义类的方法来获取对象
        SharedPreferencesUtils helper = new SharedPreferencesUtils(this, "setting");

        //如果设置自动登录
        if (checkBox_login.isChecked()) {
            //创建记住密码和自动登录是都选择,保存密码数据
            helper.putValues(
                    new SharedPreferencesUtils.ContentValue("remenberPassword", true),
                    new SharedPreferencesUtils.ContentValue("autoLogin", true),
                    new SharedPreferencesUtils.ContentValue("password", Base64Utils.encryptBASE64(getPassword())));

        } else if (!checkBox_password.isChecked()) { //如果没有保存密码，那么自动登录也是不选的
            //创建记住密码和自动登录是默认不选,密码为空
            helper.putValues(
                    new SharedPreferencesUtils.ContentValue("remenberPassword", false),
                    new SharedPreferencesUtils.ContentValue("autoLogin", false),
                    new SharedPreferencesUtils.ContentValue("password", ""));
        } else if (checkBox_password.isChecked()) {   //如果保存密码，没有自动登录
            //创建记住密码为选中和自动登录是默认不选,保存密码数据
            helper.putValues(
                    new SharedPreferencesUtils.ContentValue("remenberPassword", true),
                    new SharedPreferencesUtils.ContentValue("autoLogin", false),
                    new SharedPreferencesUtils.ContentValue("password", Base64Utils.encryptBASE64(getPassword())));
        }
    }

    /**
     * 是否可以点击登录按钮
     *
     * @param clickable
     */
    public void setLoginBtnClickable(boolean clickable) {
        btn_login.setClickable(clickable);
    }


    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        switch (id)
        {

            case R.id.btn_login: //点击登录按钮
                loadUserName();
                LoginPost();

                break;
            case R.id.btn_register: //点击注册按钮
                sendCodeRegister(this);
                break;
            case R.id.iv_see_password:
                setPasswordVisibility();    //改变图片并设置输入框的文本可见或不可见
                break;
            case R.id.btn_login_by_phone:
                sendCodeLogin(this);
                break;
            case R.id.btn_forget_password:
                sendCodeForgetPassword(this);
                break;
        }
    }


    public void loginByPhone(String phone){
        final MyApplication application = (MyApplication)this.getApplication();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    //POST信息中加入用户名和密码
                    map.put("phonenumber", phone);
                    //HttpUtils.httpPostMethod(url, json, handler);
                    Log.i("phonenumber", phone);


                    HttpUtils.post(url_login_by_phone, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("DaiDai", "OnFaile:",e);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseBody = response.body().string();
                            m_result = parseJSONWithGson(responseBody);
                            //发送登录成功的消息
                            Message msg = handler.obtainMessage();



                            switch (m_result.getStatus()){
                                case "1":msg.what = 123;loadCheckBoxState();
                                    Log.i("result", m_result.getStatus());
                                    Log.i("id",m_result.getId()+"");
                                    Log.i("question", m_result.getQuestion()+"");
                                    Log.i("sex", m_result.getSex()+"");
                                    Log.i("anwser", m_result.getAnswer());

                                    //将用户数据存在application中
                                    application.setId(m_result.getId());
                                    application.setUsername(m_result.getUsername());
                                    application.setQuestion(m_result.getQuestion());
                                    application.setAnswer(m_result.getAnswer());
                                    application.setPassword(m_result.getPassword());
                                    application.setPhonenumber(m_result.getPhonenumber());
                                    application.setSex(m_result.getSex());
                                    application.setRegisterDate(m_result.getRegisterDate());break;//登录成功
                                case "-2":msg.what = 456;//密码错误
                                    break;
                                case "-1":msg.what = 789;//用户不存在
                                    break;
                            }

                            msg.obj = m_result; //把登录结果也发送过去
                            handler.sendMessage(msg);

                        }
                    }, map);
                }
                catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void getUserByPhone(String phone){
        final MyApplication application = (MyApplication)this.getApplication();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    //POST信息中加入用户名和密码
                    map.put("phonenumber", phone);
                    //HttpUtils.httpPostMethod(url, json, handler);
                    Log.i("phonenumber", phone);


                    HttpUtils.post(url_login_by_phone, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("DaiDai", "OnFaile:",e);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseBody = response.body().string();
                            m_result = parseJSONWithGson(responseBody);
                            //发送登录成功的消息
                            Message msg = handler.obtainMessage();



                            switch (m_result.getStatus()){
                                case "1":msg.what = 666;loadCheckBoxState();
                                    Log.i("result", m_result.getStatus());
                                    Log.i("id",m_result.getId()+"");
                                    Log.i("question", m_result.getQuestion()+"");
                                    Log.i("sex", m_result.getSex()+"");
                                    Log.i("anwser", m_result.getAnswer());

                                    //将用户数据存在application中
                                    application.setId(m_result.getId());
                                    application.setUsername(m_result.getUsername());
                                    application.setQuestion(m_result.getQuestion());
                                    application.setAnswer(m_result.getAnswer());
                                    application.setPassword(m_result.getPassword());
                                    application.setPhonenumber(m_result.getPhonenumber());
                                    application.setSex(m_result.getSex());
                                    application.setRegisterDate(m_result.getRegisterDate());break;//登录成功
                                case "-2":msg.what = 777;//密码错误
                                    break;
                                case "-1":msg.what = 888;//用户不存在
                                    break;
                            }

                            msg.obj = m_result; //把登录结果也发送过去
                            handler.sendMessage(msg);

                        }
                    }, map);
                }
                catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void registerByPhone(String phone){
        final MyApplication application = (MyApplication)this.getApplication();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    //POST信息中加入用户名和密码
                    map.put("phonenumber", phone);
                    //HttpUtils.httpPostMethod(url, json, handler);
                    Log.i("phonenumber", phone);


                    HttpUtils.post(url_login_by_phone, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("DaiDai", "OnFaile:",e);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseBody = response.body().string();
                            m_result = parseJSONWithGson(responseBody);
                            //发送登录成功的消息
                            Message msg = handler.obtainMessage();



                            switch (m_result.getStatus()){
                                case "1":msg.what = 333;loadCheckBoxState();
                                    Log.i("result", m_result.getStatus());
                                    Log.i("id",m_result.getId()+"");
                                    Log.i("question", m_result.getQuestion()+"");
                                    Log.i("sex", m_result.getSex()+"");
                                    Log.i("anwser", m_result.getAnswer());

                                    //将用户数据存在application中
                                    application.setId(m_result.getId());
                                    application.setUsername(m_result.getUsername());
                                    application.setQuestion(m_result.getQuestion());
                                    application.setAnswer(m_result.getAnswer());
                                    application.setPassword(m_result.getPassword());
                                    application.setPhonenumber(m_result.getPhonenumber());
                                    application.setSex(m_result.getSex());
                                    application.setRegisterDate(m_result.getRegisterDate());msg.obj = m_result; //把登录结果也发送过去break;//登录成功
                                case "-2":msg.what = 444;//密码错误
                                    break;
                                case "-1":msg.what = 555;//用户不存在
                                    msg.obj = phone;
                                    break;
                            }


                            handler.sendMessage(msg);

                        }
                    }, map);
                }
                catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
    }




    public void sendCodeLogin(Context context) {
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
                    loginByPhone(phone);
                } else{
                    // TODO 处理错误的结果
                    Toast.makeText(LoginActivity.this, "登录失败！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        page.show(context);
    }

    public void sendCodeRegister(Context context) {
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
                    registerByPhone(phone);
                } else{
                    // TODO 处理错误的结果
                }
            }
        });
        page.show(context);
    }

    public void sendCodeForgetPassword(Context context) {
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
                    getUserByPhone(phone);
                    //Intent intent = new Intent(LoginActivity.this,ForgetPasswordActivity.class);
                    //intent.putExtra("phone",phone);
                } else{
                    // TODO 处理错误的结果
                }
            }
        });
        page.show(context);
    }




}

