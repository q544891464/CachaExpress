package com.cacha.expressinquiry;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cacha.expressinquiry.entities.TypeEntity;
import com.mylhyl.circledialog.CircleDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

public class AccountActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout pLayout;
    private RelativeLayout qLayout;

    private DialogFragment dialogFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        pLayout = (RelativeLayout)findViewById(R.id.layout_password);
        qLayout = (RelativeLayout)findViewById(R.id.layout_question);
        findViewById(R.id.back_img).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                AccountActivity.this.finish();
            }
        });

        pLayout.setOnClickListener(this);
        qLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.layout_password:
                final MyApplication application = (MyApplication) this.getApplication();
                final List<TypeEntity> items = new ArrayList<>();
                items.add(new TypeEntity(1, "密保问题"));
                items.add(new TypeEntity(2, "手机验证"));
//                final String[] items = {"拍照", "从相册选择", "小视频"};
                new CircleDialog.Builder()
                        .configDialog(params -> {
                            params.backgroundColorPress = Color.CYAN;
                            //增加弹出动画
                            params.animStyle = R.style.dialogWindowAnim;
                        })
                        .setTitle("修改密码")
//                        .setTitleColor(Color.BLUE)
                        .configTitle(params -> {
//                                params.backgroundColor = Color.RED;
                        })
                        .setSubTitle("选择验证方式")
                        .configSubTitle(params -> {
//                                params.backgroundColor = Color.YELLOW;
                        })
                        .setItems(items, (parent, view1, position1, id1) -> {

                                    switch (position1){
                                        case 0://密保问题
                                            showQuestion();
                                            break;
                                        case 1:
                                            sendCodeForgetPassword(this);
                                            break;
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

            case R.id.layout_question:

                Intent intent = new Intent(AccountActivity.this,QuestionActivity.class);
                startActivity(intent);

                break;

        }
    }

    public void showQuestion(){
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
                        Toast.makeText(AccountActivity.this, "请输入内容", Toast.LENGTH_SHORT).show();
                        return false;
                    } else {
                        //答案正确的情况
                        Log.i("anwser", application1.getAnswer());
                        if (text.equals(application1.getAnswer())){
                            Intent intent = new Intent(AccountActivity.this,ForgetPasswordActivity.class);

                            intent.putExtra("userId",application1.getId()+"");
                            startActivity(intent);
                        }
                        //答案错误的情况
                        else{
                            Toast.makeText(AccountActivity.this, "答案错误", Toast.LENGTH_SHORT).show();
                        }

                        return true;
                    }
                })
                .show(getSupportFragmentManager());
    }

    public void sendCodeForgetPassword(Context context) {
        final MyApplication application1 = (MyApplication) this.getApplication();
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
                    if (phone.equals(application1.getPhonenumber())){

                        Toast.makeText(AccountActivity.this,"验证成功",Toast.LENGTH_SHORT);

                        Intent intent = new Intent(AccountActivity.this,ForgetPasswordActivity.class);

                        intent.putExtra("userId",application1.getId()+"");
                        startActivity(intent);
                    }else{
                        Toast.makeText(AccountActivity.this,"验证失败",Toast.LENGTH_SHORT);
                    }

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
