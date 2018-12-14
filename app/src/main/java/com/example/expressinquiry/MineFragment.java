package com.example.expressinquiry;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.view.CircleImageView;

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

        text_username = (TextView)mView.findViewById(R.id.textview_mine_nickNmae);
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

           /* case R.id.layout_mine_personal:

                Intent pIntent = new Intent(context, PersonalMessageActivity.class);
                startActivity(pIntent);
                break;
            case R.id.layout_mine_download:

                Intent dIntent = new Intent(context, MyDownLoadActivity.class);
                startActivity(dIntent);
                break;*/
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
}
