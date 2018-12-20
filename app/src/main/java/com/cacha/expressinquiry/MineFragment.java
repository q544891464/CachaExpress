package com.cacha.expressinquiry;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cacha.expressinquiry.entities.TypeEntity;
import com.cacha.util.FileUtil;
import com.cacha.util.HttpUtils;
import com.cacha.util.ShareUtils;
import com.cacha.view.CircleImageView;
import com.mylhyl.circledialog.CircleDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.cacha.util.StringAndBitmap;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.ContentValues.TAG;
import static com.cacha.util.FileUtil.getRealFilePathFromUri;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MineFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MineFragment extends BaseFragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //请求相机
    private static final int REQUEST_CAPTURE = 100;
    //请求相册
    private static final int REQUEST_PICK = 101;
    //请求截图
    private static final int REQUEST_CROP_PHOTO = 102;
    //请求访问外部存储
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 103;
    //请求写入外部存储
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 104;



    private static final int CAMERA_REQUEST_CODE = 105;

    private static String url_update="http://39.107.245.98/update.php";
    private Handler handler;
    private DialogFragment dialogFragment;

    //调用照相机返回图片文件
    private File tempFile;
    // 1: qq, 2: weixin
    private int type;

    private View mView;
    private Context context;
    private boolean isPrepared;
    private boolean mHasLoadedOnce;
    private CircleImageView cImageView;
    private RelativeLayout pLayout;
    private RelativeLayout dLayout;
    private RelativeLayout sLayout;
    private RelativeLayout aLayout;
    private TextView text_username;



    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MineFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MineFragment
     */
    // TODO: Rename and change types and number of parameters
    public static MineFragment newInstance(String param1, String param2) {
        MineFragment fragment = new MineFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (mView == null) {

            context = getActivity();
            mView = inflater.inflate(R.layout.fragment_mine, null, true);
            findViews();

            isPrepared = true;
            lazyLoad();
        }
        ViewGroup parent = (ViewGroup) mView.getParent();
        if (parent != null) {

            parent.removeView(mView);
        }
        return mView;
    }

    public void findViews() {

        final MyApplication application = (MyApplication) this.getActivity().getApplication();

        StringAndBitmap stringAndBitmap = new StringAndBitmap();

        text_username = (TextView)mView.findViewById(R.id.textview_mine_nickNmae);
        text_username.setText(ShareUtils.getLoginUserName(getActivity()));
        cImageView = (CircleImageView) mView
                .findViewById(R.id.circleimageview_mine_photo);

        if(application.getIcon()!=null){
            cImageView.setImageBitmap(stringAndBitmap.stringToBitmap(application.getIcon()));
        }


        pLayout = (RelativeLayout) mView
                .findViewById(R.id.layout_mine_personal);
        pLayout.setOnClickListener(this);
        dLayout = (RelativeLayout) mView
                .findViewById(R.id.layout_mine_account);
        dLayout.setOnClickListener(this);
        sLayout = (RelativeLayout) mView.findViewById(R.id.layout_mine_setting);
        sLayout.setOnClickListener(this);
        aLayout = (RelativeLayout) mView
                .findViewById(R.id.layout_mine_aboutApp);
        aLayout.setOnClickListener(this);
    }

    @Override
    protected void lazyLoad() {
        // TODO Auto-generated method stub
        if (!isPrepared || !isVisible || mHasLoadedOnce) {

            return;
        }
    }

    @Override
    public void onClick(View view) {


        switch (view.getId()) {

            case R.id.layout_mine_personal:

                type = 1;
                uploadHeadImage();
                break;
           /* case R.id.layout_mine_download:

                Intent dIntent = new Intent(context, MyDownLoadActivity.class);
                startActivity(dIntent);
                break;*/
            case R.id.layout_mine_account:
                Intent pIntent = new Intent(context,AccountActivity.class);
                startActivity(pIntent);
                break;
            case R.id.layout_mine_setting:

                Intent sIntent = new Intent(context, SettingActivity.class);
                startActivity(sIntent);
                break;
           case R.id.layout_mine_aboutApp:

                Intent aIntent = new Intent(context, CompanySelectActivity.class);
                startActivity(aIntent);
                break;
            default:
                break;
        }


    }

    @Override
    public String getTitle() {
        // TODO Auto-generated method stub
        return null;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    private void uploadHeadImage(){
        final List<TypeEntity> items = new ArrayList<>();
        items.add(new TypeEntity(1, "拍照（暂时不可用）"));
        items.add(new TypeEntity(2, "从相册中选取"));
        new CircleDialog.Builder()
                .configDialog(params -> {
                    params.backgroundColorPress = Color.CYAN;
                    //增加弹出动画
                    params.animStyle = R.style.dialogWindowAnim;
                })
                .setTitle("选择投降")
//                        .setTitleColor(Color.BLUE)
                .configTitle(params -> {
//                                params.backgroundColor = Color.RED;
                })
                .setSubTitle("请选择你要进行的方式")
                .configSubTitle(params -> {
//                                params.backgroundColor = Color.YELLOW;
                })
                .setItems(items, (parent1, view1, position1, id1) -> {
                            switch (position1){
                                //选择拍照
                                case 0:
                                    //权限判断
                                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                            != PackageManager.PERMISSION_GRANTED) {
                                        //申请WRITE_EXTERNAL_STORAGE权限
                                        requestPermissions( new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                                        Log.e(TAG, "uploadHeadImage: 申请WRITE_EXTERNAL_STORAGE权限" );


                                    } else {
                                        //跳转到调用系统相机
                                        gotoCamera();
                                    }

                                    /*if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                                            != PackageManager.PERMISSION_GRANTED) {
                                        //申请WRITE_EXTERNAL_STORAGE权限
                                        requestPermissions( new String[]{Manifest.permission.CAMERA},
                                                CAMERA_REQUEST_CODE);
                                        Log.e(TAG, "uploadHeadImage: 申请相机权限" );


                                    } else {
                                        //跳转到调用系统相机
                                        gotoCamera();
                                    }*/
                                    break;
                                case 1:
                                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                                            != PackageManager.PERMISSION_GRANTED) {
                                        //申请READ_EXTERNAL_STORAGE权限
                                        requestPermissions( new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                                READ_EXTERNAL_STORAGE_REQUEST_CODE);

                                    } else {
                                        //跳转到相册
                                        gotoPhoto();
                                    }
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
                .show(getActivity().getSupportFragmentManager());
    }



    /**
     * 外部存储权限申请返回
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                Log.e(TAG, "uploadHeadImage: 申请WRITE_EXTERNAL_STORAGE权限成功" );
                gotoCamera();
            }
        } else if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                Log.e(TAG, "uploadHeadImage: 申请READ_EXTERNAL_STORAGE权限成功" );
                gotoPhoto();
            }
        } else if(requestCode == CAMERA_REQUEST_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                Log.e(TAG, "uploadHeadImage: 申请相机权限成功" );
                gotoCamera();
            }
        }

    }

    /**
     * 跳转到相册
     */
    private void gotoPhoto() {
        Log.d("evan", "*****************打开图库********************");
        //跳转到调用系统图库
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "请选择图片"), REQUEST_PICK);
    }


    /**
     * 跳转到照相机
     */
    private void gotoCamera() {
        Log.d("evan", "*****************打开相机********************");
        //创建拍照存储的图片文件
        tempFile = new File(FileUtil.checkDirPath(Environment.getExternalStorageDirectory().getPath() + "/image/"), System.currentTimeMillis() + ".jpg");

        //跳转到调用系统相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //设置7.0中共享文件，分享路径定义在xml/file_paths.xml
            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".fileProvider", tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
            Log.e(TAG, "intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);" );
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
            Log.e(TAG, "intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));" );
        }
        startActivityForResult(intent, REQUEST_CAPTURE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case REQUEST_CAPTURE: //调用系统相机返回
                if (resultCode == RESULT_OK) {
                    gotoClipActivity(Uri.fromFile(tempFile));
                    Log.e(TAG, "onActivityResult: 调用系统相机返回" );
                }else{
                    Log.e(TAG, "onActivityResult: 调用系统相机返回失败" );
                    Log.e(TAG, "resultCode: " +resultCode);
                }
                break;
            case REQUEST_PICK:  //调用系统相册返回
                if (resultCode == RESULT_OK) {
                    Uri uri = intent.getData();
                    gotoClipActivity(uri);
                    Log.e(TAG, "onActivityResult: 调用系统相册返回" );
                }
                break;
            case REQUEST_CROP_PHOTO:  //剪切图片返回
                if (resultCode == RESULT_OK) {
                    final Uri uri = intent.getData();
                    if (uri == null) {
                        return;
                    }
                    String cropImagePath = getRealFilePathFromUri(getActivity().getApplicationContext(), uri);
                    Bitmap bitMap = BitmapFactory.decodeFile(cropImagePath);
                    if (type == 1) {
                        cImageView.setImageBitmap(bitMap);
                    } else {
                        cImageView.setImageBitmap(bitMap);
                    }
                    //此处后面可以将bitMap转为二进制上传后台网络
                    //......
                    final MyApplication application = (MyApplication)this.getActivity().getApplication();

                   StringAndBitmap stringAndBitmap = new StringAndBitmap();
                    String string = stringAndBitmap.bitmapToString(bitMap);
                    Update(application.getId(),string,"icon");
                    application.setIcon(string);


                }
                break;
        }
    }


    /**
     * 打开截图界面
     */
    public void gotoClipActivity(Uri uri) {
        if (uri == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(getActivity(), ClipImageActivity.class);
        intent.putExtra("type", type);
        intent.setData(uri);
        startActivityForResult(intent, REQUEST_CROP_PHOTO);
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

                            }
                            else //注册失败
                            {


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
