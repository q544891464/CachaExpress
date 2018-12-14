package com.example.expressinquiry;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expressinquiry.adapter.HistoryAdapter;
import com.example.expressinquiry.bean.HistoryData;
import com.example.expressinquiry.entities.TypeEntity;
import com.example.expressinquiry.widget.ClearEditText;
import com.example.util.CompanyInfo;
import com.example.util.Database;
import com.example.util.HttpUtils;
import com.example.util.ItemStatus;
import com.example.util.ListViewUtil;
import com.example.util.ShareUtils;
import com.example.view.CircleImageView;
import com.mylhyl.circledialog.CircleDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MineFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends BaseFragment implements View.OnClickListener,CompoundButton.OnCheckedChangeListener ,HistoryAdapter.SubClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ClearEditText et_search;
    private ArrayAdapter<String> mAdapter1;


    private List<HistoryData> mDatas = new ArrayList<>();
    private HistoryAdapter mHistoryAdapter;

    private Button btnClearList; //清空查询历史列表
    private ListView lvHistory; //查询历史展示控件

    private HistoryAdapter mAdapter; //历史记录适配器

    private LinkedList<ItemStatus> mItemsExtendData;


    private Database mDatabase; //数据库

    private List<HistoryData> historyList = new ArrayList<HistoryData>(); //搜索历史列表

    //编辑，显示选择的checkbox
    private boolean mbStatueShow = false;

    private boolean isEdit = false;
    private View mView;
    private Context context;
    private boolean isPrepared;
    private boolean mHasLoadedOnce;
    private CircleImageView cImageView;
    private RelativeLayout pLayout;
    private RelativeLayout dLayout;
    private RelativeLayout sLayout;
    private RelativeLayout aLayout;
    private CheckBox checkBox_all;
    private TextView text_username;
    private TextView mtextviewShow;
    private TextView tv_manage;
    private TextView tv_delete;
    private TabLayout tabLayout;
    private Handler handler;

    private List<String> tabTitleList;
    private String[] tabTitles = {"全部","已揽收","在途中","已签收","问题件"};

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private DialogFragment dialogFragment;

    private int num;//已选的项数

    private OnFragmentInteractionListener mListener;

    public MainFragment() {
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
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
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
            mView = inflater.inflate(R.layout.fragment_main, null, true);
            findViews();
            initDatas(); //初始化数据


            initEvents(); //初始化事件
            initTabLayout();

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

        btnClearList = (Button) mView.findViewById(R.id.btn_clear_list);
        lvHistory = (ListView) mView.findViewById(R.id.lv_history);
        et_search = (ClearEditText) mView.findViewById(R.id.et_search);
        tabLayout = (TabLayout) mView.findViewById(R.id.tl_top);
        tv_manage = (TextView)mView.findViewById((R.id.manage));
        checkBox_all = (CheckBox)mView.findViewById(R.id.checkBox_all);
        tv_delete = (TextView) mView.findViewById(R.id.textviewDetele);



        checkBox_all.setOnCheckedChangeListener(this);

        tv_manage.setOnClickListener(this);
        tv_delete.setOnClickListener(this);

        TextView textviewDetele = (TextView)mView.findViewById(R.id.textviewDetele);
        textviewDetele.setOnClickListener(this);

        mtextviewShow = (TextView)mView.findViewById(R.id.textviewShow);
        mtextviewShow.setText("已选" +  String.valueOf(0) +  "项");

        mView.findViewById(R.id.layoutBottom).setVisibility(View.GONE);
        mView.findViewById(R.id.layoutBottomNew).setVisibility(View.GONE);


        initListView();
        intiEditView();
    }

    public void initTabLayout(){
        tabTitleList = Arrays.asList(tabTitles);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        for (int i = 0; i < tabTitleList.size(); i++) {
            tabLayout.addTab(tabLayout.newTab().setText(tabTitleList.get(i)),i);
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        mAdapter.getFilterByStatus().filter("");
                        break;
                    case 1:
                        mAdapter.getFilterByStatus().filter("已揽收");
                        break;
                    case 2:
                        mAdapter.getFilterByStatus().filter("在途中");
                        break;
                    case 3:
                        mAdapter.getFilterByStatus().filter("已签收");
                        break;
                    case 4:
                        mAdapter.getFilterByStatus().filter("问题件");
                        break;
                    default:
                        mAdapter.getFilterByStatus().filter("");
                        break;
                }

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    private void intiEditView() {
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                mAdapter.getFilter().filter(s);

                mAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initListView() {
//        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data);
//        user_list.setAdapter(mAdapter);


    }


    @Override
    protected void lazyLoad() {
        // TODO Auto-generated method stub
        if (!isPrepared || !isVisible || mHasLoadedOnce) {

            return;
        }
    }
    //接口回调，显示已选择的人数
    @Override
    public void setSelectedNum(int num) {
        mtextviewShow.setText("共选了"+num+"项");
    }


    private void initEvents() {
        //隐藏清空列表按钮
        btnClearList.setVisibility(View.GONE);
        //给清空列表按钮设置点击事件
        btnClearList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.deleteAllHistory(); //清空数据库中快递历史表所有数据
                historyList.clear(); //清空搜索历史列表

                mAdapter.notifyDataSetChanged(); //用适配器通知数据集合改变

                Toast.makeText(getActivity(), "已清空查询历史", Toast.LENGTH_SHORT).show();
            }
        });



        //为搜索历史列表设置子项点击事件
        lvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



                KdniaoTrackQueryAPI api = new KdniaoTrackQueryAPI();

                try {

                    HistoryData data = historyList.get(position); //获取当前位置的搜索历史

                    String deliveryName = data.getCompany();
                    String deliveryNumber = data.getNumber(); //获取快递单号
                    String deliveryRemark = data.getRemark();
                    String companyName = data.getCompanyName();


                    searchPost(api.getReqURL(),api.getParams(deliveryName,deliveryNumber));
                    handler  = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            switch (msg.what){
                                case 999:
                                    Log.i("msg.obj", "msg.obj:"+msg.obj);
                                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                                    intent.putExtra("deliveryCode", deliveryName); //存放快递公司代号
                                    intent.putExtra("deliveryNumber", deliveryNumber); //存放快递单号
                                    intent.putExtra("companyName",companyName);
                                    intent.putExtra("remark",data.getRemark());//存放快递备注

                                    if (!deliveryRemark.isEmpty()){
                                        intent.putExtra("remark",deliveryRemark);
                                    }else{
                                        intent.putExtra("remark","");
                                    }

                                    startActivity(intent); //跳转到详情页
                                    break;
                            }
                        }
                    };





                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        //为搜索历史列表设置子项长按事件
        lvHistory.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final List<TypeEntity> items = new ArrayList<>();
                items.add(new TypeEntity(1, "备注"));
                items.add(new TypeEntity(2, "分享"));
                items.add(new TypeEntity(3, "删除"));
                new CircleDialog.Builder()
                        .configDialog(params -> {
                            params.backgroundColorPress = Color.CYAN;
                            //增加弹出动画
                            params.animStyle = R.style.dialogWindowAnim;
                        })
                        .setTitle("单号"+historyList.get(position).getNumber())
//                        .setTitleColor(Color.BLUE)
                        .configTitle(params -> {
//                                params.backgroundColor = Color.RED;
                        })
                        .setSubTitle("请选择你要进行的操作")
                        .configSubTitle(params -> {
//                                params.backgroundColor = Color.YELLOW;
                        })
                        .setItems(items, (parent1, view1, position1, id1) -> {
                                    switch (position1){
                                        //选择备注
                                        case 0:
                                            showRemarkDialog(position);
                                            break;
                                        case 1:

                                            break;
                                        case 2:
                                            showDeleteDialog(position);
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
                return true;
            }
        });
    }



    //显示添加备注的对话框
    //position 为选择条目的位置
    private void showRemarkDialog(int position){
        dialogFragment = new CircleDialog.Builder()
                .setCanceledOnTouchOutside(false)
                .setCancelable(true)
                .setTitle("备注")
                .setSubTitle("")
                .setInputHint("请输入备注")
                .setInputText(historyList.get(position).getRemark())
                .setInputHeight(300)
                .setInputShowKeyboard(true)
                .setInputEmoji(true)
                .setInputCounter(10)
//                        .setInputCounter(20, (maxLen, currentLen) -> maxLen - currentLen + "/" + maxLen)
                .configInput(params -> {
//                            params.padding = new int[]{30, 30, 30, 30};
//                                params.inputBackgroundResourceId = R.drawable.bg_input;
//                                params.gravity = Gravity.CENTER;
                    //密码
//                                params.inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
//                                        | InputType.TYPE_TEXT_FLAG_MULTI_LINE;
                    //文字加粗
                    params.styleText = Typeface.BOLD;
                })
                .setNegative("取消", null)
                .setPositiveInput("确定", (text, v) -> {
                    if (TextUtils.isEmpty(text)) {
                        Toast.makeText(getActivity(), "请输入内容", Toast.LENGTH_SHORT).show();
                        return false;
                    } else {
                        historyList.get(position).setRemark(text);
                        initDatas();
                        return true;
                    }
                })
                .show(getActivity().getSupportFragmentManager());
    }

    /**
     * 弹出是否删除对话框对话框
     * @param position 当前子项的位置
     */
    private void showDeleteDialog(int position){
        //获取当前位置的历史记录
        HistoryData data = historyList.get(position);
        final String number = data.getNumber(); //获取快递单号

        //新建弹出对话框
        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(getActivity());
        normalDialog.setTitle("提示"); //设置标题
        normalDialog.setMessage("是否删除快递单号\n" + number + "？"); //设置内容
        normalDialog.setPositiveButton("确定", //设置确定按钮相关信息
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDatabase.deleteHistory(number); //删除该快递单号的历史
                        initDatas(); //初始化数据
                    }
                });
        normalDialog.setNegativeButton("取消", //设置取消按钮相关信息
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); //设置对话框消失
                    }
                });
        // 显示
        normalDialog.show(); //显示对话框
    }




    /**
     * 初始化数据
     */
    private void initDatas() {
        mDatabase = Database.getInstance(getActivity()); //初始化数据库
        historyList.clear(); //清空历史记录表

//        historyList = mDatabase.getAllHistory(); //从数据库中获取数据指针

        if (!ShareUtils.checkIsHaveLogin(getActivity())) { //当前未登录
            historyList = mDatabase.getHistoryNotBindUser(); //从数据库中获取未绑定用户的查询记录
        } else { //已登录
            String loginUserName = ShareUtils.getLoginUserName(getActivity()); //获取当前登陆用户名

            Log.e(TAG, "loginUserName=" + loginUserName);
            historyList = mDatabase.getUserHistory(loginUserName); //从数据库中获取该用户名相关的记录
        }

        Log.e(TAG, "historyList.size()=" + historyList.size());
        Log.e(TAG, "historyList=" + historyList);

//        Collections.reverse(historyList); //将搜索历史列表逆序排列

        mAdapter = new HistoryAdapter(getActivity(), historyList); //新建历史消息适配器
        lvHistory.setAdapter(mAdapter); //为历史消息展示列表设置适配器

        mAdapter.setSubOnClickListener(new HistoryAdapter.SubClickListener() {
            @Override
            public void setSelectedNum(int num) {
                mtextviewShow.setText("共选了"+num+"项");
            }
        });
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
        switch (buttonView.getId()){
            case R.id.checkBox_all:
                if(isChecked){
                    num = 0;
                    for(int i = 0;i<historyList.size();i++){
                        historyList.get(i).setChecked(true);
                        if(historyList.get(i).isChecked()){
                            num++;
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                    mtextviewShow.setText("共选了"+num+"项");
                }else{
                    num = 0;
                    for(int i = 0;i<historyList.size();i++){
                        historyList.get(i).setChecked(false);
                    }
                    mAdapter.notifyDataSetChanged();
                    mtextviewShow.setText("共选了"+num+"项");
                }

        }

    }

    @Override
    public void onClick(View view) {


        switch (view.getId()) {

            case R.id.manage:
                //isEdit为false时显示编辑画面
                if(!isEdit){
                    mView.findViewById(R.id.layoutBottom).setVisibility(View.VISIBLE);
                    mView.findViewById(R.id.layoutBottomNew).setVisibility(View.GONE);

                    for(int i = 0; i<historyList.size();i++ ){
                        historyList.get(i).setVisible(View.VISIBLE);
                    }

                    mAdapter.notifyDataSetChanged();
                    isEdit = true;
                }
                //否则返回正常界面
                else{
                    mView.findViewById(R.id.layoutBottom).setVisibility(View.GONE);
                    mView.findViewById(R.id.layoutBottomNew).setVisibility(View.GONE);

                    for(int i = 0; i<historyList.size();i++ ){
                        historyList.get(i).setVisible(View.GONE);
                    }

                    mAdapter.notifyDataSetChanged();
                    isEdit = false;
                }

                break;

            case R.id.textviewDetele:

                //显示确认提示框
                new CircleDialog.Builder()
                        .setCanceledOnTouchOutside(false)
                        .setCancelable(false)
                        .configDialog(params -> {
                              params.backgroundColor = Color.WHITE;
//                            params.backgroundColorPress = Color.BLUE;
                        })
                        .setTitle("删除条目")
                        .setSubTitle("副标题")
                        .setText("确定要删除吗？")
                        .configText(params -> {
//                                params.gravity = Gravity.LEFT | Gravity.TOP;
//                            params.padding = new int[]{100, 0, 100, 50};
                        })
                        .setNegative("取消", null)
                        .setPositive("确定", v ->
                                deleteItems()
                        )
                        .configPositive(params -> params.backgroundColorPress = Color.RED)
                        .show(getActivity().getSupportFragmentManager());
                break;




           /* case R.id.layout_mine_personal:

                Intent pIntent = new Intent(context, PersonalMessageActivity.class);
                startActivity(pIntent);
                break;
            case R.id.layout_mine_download:

                Intent dIntent = new Intent(context, MyDownLoadActivity.class);
                startActivity(dIntent);
                break;
            case R.id.layout_mine_setting:

                Intent sIntent = new Intent(context, SettingActivity.class);
                startActivity(sIntent);
                break;
            case R.id.layout_mine_aboutApp:

                Intent aIntent = new Intent(context, AboutAppActivity.class);
                startActivity(aIntent);
                break;*/
            default:
                break;
        }


    }


    public void deleteItems(){
        for(int i = 0;i<historyList.size();i++ ){
            if (historyList.get(i).isChecked()){
                HistoryData data = historyList.get(i);
                final String number = data.getNumber(); //获取快递单号

                mDatabase.deleteHistory(number); //删除该快递单号的历史
                initDatas(); //初始化数据
                mView.findViewById(R.id.layoutBottom).setVisibility(View.GONE);
            }
        }
    }

    public void searchPost(String url, Map<String, String> params){

        final MyApplication application = (MyApplication)this.getActivity().getApplication();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {

                    HttpUtils.post(url, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseBody = response.body().string();
                            Log.i("resultBody", responseBody);
                            Message message =handler.obtainMessage();
                            message.what=999;
                            message.obj=responseBody;
                            handler.sendMessage(message);
                            application.setSearchResult(responseBody);

                        }
                    }, params);
                }
                catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();

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
    @Override
    public void onResume(){
        super.onResume();
        initDatas();
        if(isEdit){
            mView.findViewById(R.id.layoutBottom).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.layoutBottomNew).setVisibility(View.GONE);

            for(int i = 0; i<historyList.size();i++ ){
                historyList.get(i).setVisible(View.VISIBLE);
            }

            mAdapter.notifyDataSetChanged();
        }
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
}
