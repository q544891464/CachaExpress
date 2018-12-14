package com.example.expressinquiry.adapter;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.soubw.jcontactlib.CharacterParser;
import com.soubw.jcontactlib.JContacts;
import com.soubw.jcontactlib.JIndexBarView;
import com.soubw.jcontactlib.JListView;
import com.soubw.jcontactlib.JSortComparator;
import com.soubw.jcontactlib.JViewHolder;

/**
 * Created by WX_JIN on 2016/3/10.
 */
public abstract class JAdapter<T extends JContacts> extends BaseAdapter{

    public static final int TYPE_ITEM = 0;
    public static final int TYPE_SECTION = 1;

    private static final int TYPE_MAX_COUNT = 2;
    /**
     * all Contacts list
     */
    private List<T> jContactsList;
    /**
     * contain words list
     */
    ArrayList<Integer> mListSectionPos = new ArrayList<>();

    ArrayList<T> mListItems = new ArrayList<>();

    Context mContext;


    int	itemLayoutId;
    int sectionLayoutId;

    JListView lvList;
    View mloadingView;

    public JAdapter(Context context,
                    List<T> jContactsList,
                    JListView lvList,
                    int indexBarViewId,
                    int previewViewId,
                    int itemLayoutId,
                    int sectionLayoutId,
                    View loadingView) {
        this.mContext = context;
        this.jContactsList = jContactsList;
        this.lvList = lvList;
        this.itemLayoutId = itemLayoutId;
        this.sectionLayoutId = sectionLayoutId;
        this.mloadingView = loadingView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        JIndexBarView jIndexBarView = (JIndexBarView) inflater.inflate(indexBarViewId, lvList, false);
        jIndexBarView.setData(lvList, mListItems, mListSectionPos);
        lvList.setJIndexBarView(jIndexBarView,mListSectionPos);
        lvList.setPreviewView(inflater.inflate(previewViewId, lvList, false));
        lvList.setAdapter(this);
        lvList.setJClearEditTextListener(new JListView.JClearEditTextListener() {
            @Override
            public void requestRefreshAdapter(CharSequence s) {
                String str = s.toString();
                if (this != null && str != null){
                    filterData(str);
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getCount() {
        return mListItems.size();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return !mListSectionPos.contains(position);
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        return mListSectionPos.contains(position) ? TYPE_SECTION : TYPE_ITEM;
    }

    @Override
    public T getItem(int position) {
        return mListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mListItems.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        JViewHolder holder = null;
        int type = getItemViewType(position);
        switch (type) {
            case TYPE_ITEM:
                holder = JViewHolder.getViewHolder(mContext, convertView, parent, itemLayoutId, position);
                break;
            case TYPE_SECTION:
                holder = JViewHolder.getViewHolder(mContext, convertView, parent, sectionLayoutId, position);
                break;
        }
        convert(holder, getItem(position), type);
        return holder.getConvertView();
    }


    public abstract void convert(JViewHolder holder, T bean,int type);

    private void setUpdateDate(ArrayList<T> listItems, ArrayList<Integer> listSectionPos){
        this.mListItems = listItems;
        this.mListSectionPos = listSectionPos;
        notifyDataSetChanged();
    }


    public void setjContactsList(List<T> jContactsList){
        this.jContactsList = jContactsList;
        filterData(null);
    }

    public void filterData(String s){
        setIndexBarViewVisibility(s);
        List<T> jFilterList = new ArrayList<>();
        notifyDataSetChanged();
        if (TextUtils.isEmpty(s)) {
            jFilterList.clear();
            notifyDataSetChanged();
            jFilterList.addAll(jContactsList);
            notifyDataSetChanged();
        } else {
            jFilterList.clear();
            notifyDataSetChanged();
            for (T contact : jContactsList) {
                String name = contact.getjName();
                String num = contact.getjPhoneNumber();
                if (name.indexOf(s.toString()) != -1 || CharacterParser.getInstance().getSelling(name).contains(s.toString())
                        || CharacterParser.getInstance().getSelling(num).contains(s.toString())) {
                    jFilterList.add(contact);
                    notifyDataSetChanged();
                }
                notifyDataSetChanged();
            }
        }
        notifyDataSetChanged();
        new LoadFilter().execute(jFilterList);
        notifyDataSetChanged();
    }

    private class LoadFilter extends AsyncTask<List<T>, Void, Void> {
        private void showLoading(View contentView, View loadingView) {
            contentView.setVisibility(View.GONE);
            loadingView.setVisibility(View.VISIBLE);
        }

        private void showContent(View contentView, View loadingView) {
            contentView.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.GONE);
        }
        @Override
        protected void onPreExecute() {
            showLoading(lvList, mloadingView);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(List<T>... params) {
            mListItems.clear();
            mListSectionPos.clear();
            List<T> itemsList = params[0];
            boolean isReFilter = true;
            if (itemsList.size() > 0) {
                Collections.sort(itemsList, new JSortComparator());
                String prev_section = "";
                for (T current_item : itemsList) {
                    String current_section = CharacterParser.getInstance().getSelling(current_item.getjName()).substring(0, 1).toUpperCase(Locale.getDefault());
                    if(JIndexBarView.INDEX_WORD.contains(current_section)){
                        if (!prev_section.equals(current_section)) {
                            mListItems.add(current_item);//Word
                            mListSectionPos.add(mListItems.indexOf(current_item));
                            mListItems.add(current_item);

                            prev_section = current_section;
                        } else {
                            mListItems.add(current_item);
                        }
                    }else{
                        if(isReFilter){
                            mListItems.add(current_item);//Word
                            mListSectionPos.add(mListItems.indexOf(current_item));
                            mListItems.add(current_item);
                            isReFilter = false;
                        }else {
                            mListItems.add(current_item);
                        }
                    }

                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (!isCancelled()) {
                setUpdateDate(mListItems,mListSectionPos);
                lvList.setJClearEditTextFoucus();
                showContent(lvList, mloadingView);
            }
            super.onPostExecute(result);
        }
    }



    private void setIndexBarViewVisibility(String constraint) {
        if (constraint != null && constraint.length() > 0) {
            lvList.setIndexBarVisibility(false);
        } else {
            lvList.setIndexBarVisibility(true);
        }
    }


}
