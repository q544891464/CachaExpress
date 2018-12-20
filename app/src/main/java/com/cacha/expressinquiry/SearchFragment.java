package com.cacha.expressinquiry;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cacha.util.HttpUtils;

import java.io.IOException;
import java.util.Map;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MineFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends BaseFragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    private Handler handler;



    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private View mView;
    private Context context;
    private boolean isPrepared;
    private boolean mHasLoadedOnce;
    private RelativeLayout sLayout;
    private TextView txt_company;
    private Button btn_search;
    private EditText et_order;
    private EditText et_remark;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SearchFragment() {
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
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication application = (MyApplication)this.getActivity().getApplication();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
       /* if(!application.company.getjName().isEmpty()){
            txt_company.setText(application.company.getjName());
        }*/

    }

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (mView == null) {

            context = getActivity();
            mView = inflater.inflate(R.layout.fragment_search, null, true);
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

        sLayout = (RelativeLayout)mView.findViewById(R.id.select_company);

        sLayout.setOnClickListener(this);

        txt_company = (TextView)mView.findViewById(R.id.text_company_id);

        btn_search = (Button)mView.findViewById(R.id.btn_search);

        btn_search.setOnClickListener(this);

        et_order = (EditText)mView.findViewById(R.id.et_order_id);

        et_remark=(EditText)mView.findViewById(R.id.et_remark);


        /*text_username = (TextView)mView.findViewById(R.id.textview_mine_nickNmae);
        text_username.setText(application.getUsername());
        cImageView = (CircleImageView) mView
                .findViewById(R.id.circleimageview_mine_photo);

        pLayout = (RelativeLayout) mView
                .findViewById(R.id.layout_mine_personal);
        pLayout.setOnClickListener(this);
        dLayout = (RelativeLayout) mView
                .findViewById(R.id.layout_mine_download);
        dLayout.setOnClickListener(this);
        sLayout = (RelativeLayout) mView.findViewById(R.id.layout_mine_setting);
        sLayout.setOnClickListener(this);
        aLayout = (RelativeLayout) mView
                .findViewById(R.id.layout_mine_aboutApp);
        aLayout.setOnClickListener(this);*/
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

            case R.id.select_company:

                Intent pIntent = new Intent(context, CompanySelectActivity.class);
                startActivity(pIntent);
                break;
           /* case R.id.layout_mine_download:

                Intent dIntent = new Intent(context, MyDownLoadActivity.class);
                startActivity(dIntent);
                break;*/
            case R.id.btn_search:

                KdniaoTrackQueryAPI api = new KdniaoTrackQueryAPI();

                final MyApplication application = (MyApplication)this.getActivity().getApplication();
                String ShipperCode = application.getCompanyCode();
                String LogisticCode = et_order.getText().toString().trim();
                String CompanyName = application.getCompanyName();
                String Remark = et_remark.getText().toString().trim();
                application.setOrderId(LogisticCode);

                Log.i("ShipperCode", ShipperCode);
                Log.i("LogisticCode",LogisticCode);

                try {
                    searchPost(api.getReqURL(),api.getParams(ShipperCode,LogisticCode));

                    handler  = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            switch (msg.what){
                                case 999:
                                    Log.i("msg.obj", "msg.obj:"+msg.obj);
                                    application.setSearchResult(msg.obj+"");
                                    Log.i("application", "application:"+application.getSearchResult());
                                    Intent newIntent = new Intent(context,DetailActivity.class);
                                    newIntent.putExtra("deliveryNumber",LogisticCode);
                                    newIntent.putExtra("deliveryCode",ShipperCode);
                                    newIntent.putExtra("companyName",CompanyName);
                                    if (!Remark.isEmpty()){
                                        newIntent.putExtra("remark",Remark);
                                    }else{
                                        newIntent.putExtra("remark","");
                                    }
                                    startActivity(newIntent);
                                    break;
                            }
                        }
                    };



                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
           /* case R.id.layout_mine_aboutApp:

                Intent aIntent = new Intent(context, AboutAppActivity.class);
                startActivity(aIntent);
                break;*/
            default:
                break;
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
        MyApplication application = (MyApplication)this.getActivity().getApplication();
        if(application.isCompanySelected()){
            txt_company.setText(application.getCompanyName());
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
