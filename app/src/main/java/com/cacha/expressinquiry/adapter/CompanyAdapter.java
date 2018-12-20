package com.cacha.expressinquiry.adapter;

import android.content.Context;
import android.view.View;

import com.cacha.expressinquiry.MainBean;
import com.cacha.expressinquiry.R;
import com.soubw.jcontactlib.JContacts;
import com.soubw.jcontactlib.JListView;
import com.soubw.jcontactlib.JViewHolder;

import java.util.List;

public class CompanyAdapter extends JAdapter {

    public CompanyAdapter(Context context, List<MainBean> jContactsList, JListView lvList, int indexBarViewId, int previewViewId, int itemLayoutId, int sectionLayoutId, View loadingView) {

        super(context, jContactsList, lvList, indexBarViewId, previewViewId, itemLayoutId, sectionLayoutId, loadingView);
    }

    @Override
    public void convert(JViewHolder holder, JContacts bean, int type) {
        MainBean b = (MainBean) bean;
        switch (type) {
            case TYPE_ITEM:
                holder.setText(R.id.row_title,bean.getjName());
                break;
            case TYPE_SECTION:
                holder.setText(R.id.row_title,bean.getjFirstWord());
                break;
        }
    }


}
