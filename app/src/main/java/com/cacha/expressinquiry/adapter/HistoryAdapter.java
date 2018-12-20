package com.cacha.expressinquiry.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

import com.cacha.expressinquiry.bean.HistoryData;
import com.cacha.expressinquiry.R;

//快递查询实体类
public class HistoryAdapter extends BaseAdapter implements Filterable{
    private Context mContext; //上下文
    private List<HistoryData> mList; //数据列表
    private LayoutInflater inflater; //布局加载器
    private HistoryData data; //快递历史实体类临时变量
    private int size = 0;
    private int selectedNum;
    private SubClickListener subClickListener;





    public HistoryAdapter(Context mContext, List<HistoryData> mList) {
        this.mContext = mContext;
        this.mList = mList;

        for (int i = 0; i < mList.size(); i++) {
            if(mList.get(i).isChecked()){
                selectedNum++;
            }
        }


        //获取系统服务
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;

        if (convertView == null) { //第一次加载
            viewHolder = new ViewHolder(); //新建ViewHolder
            convertView = inflater.inflate(R.layout.layout_history_item, null); //加载布局

            //为ViewHolder中的组件绑定布局
            viewHolder.tvCompany = (TextView) convertView.findViewById(R.id.tv_deliveryName);
            viewHolder.tvNumber = (TextView) convertView.findViewById(R.id.tv_deliveryNumber);
            viewHolder.tvStatus = (TextView) convertView.findViewById(R.id.tv_status);
            viewHolder.tvRemark = (TextView) convertView.findViewById(R.id.tv_remark);
            viewHolder.tvDatetime = (TextView) convertView.findViewById(R.id.tv_date);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox1);

            convertView.setTag(viewHolder); //把viewHolder设置进convertView标签
        } else { //非第一次加载
            viewHolder = (ViewHolder) convertView.getTag(); //从convertView标签中取出viewHolder
        }

        //从列表中获取当前位置的数据
        data = mList.get(position);

        //为相应控件设置文字
        viewHolder.tvCompany.setText(data.getCompanyName());
        viewHolder.tvNumber.setText(data.getNumber());
        viewHolder.tvStatus.setText(data.getStatus());
        viewHolder.tvRemark.setText(data.getRemark());
        viewHolder.tvDatetime.setText(data.getDatetime());

        viewHolder.checkBox.setChecked(mList.get(position).isChecked());
        viewHolder.checkBox.setVisibility(mList.get(position).getVisible());

        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CheckBox box = (CheckBox)view;
                if(box.isChecked()){
                    Log.i("HistoryAdapter", "选择了:" + position);
                    mList.get(position).setChecked(true);
                    selectedNum ++;
                    Log.i("HistoryAdapter", "共选了:" + selectedNum + "项");
                }else{
                    Log.i("HistoryAdapter", "取消了:" + position);
                    mList.get(position).setChecked(false);
                    selectedNum --;
                    Log.i("HistoryAdapter", "共选了:" + selectedNum + "项");
                }

                subClickListener.setSelectedNum(selectedNum);

                notifyDataSetChanged();
            }
        });



        return convertView;
    }

    public void setItemsExtendDataSize (int i){
        size = i;
    }

    public int getItemsExtendDataSize (){
        return size;
    }



    public class ViewHolder { //保存控件状态的内部类
        private TextView tvCompany; //快递公司名称
        private TextView tvNumber; //快递单号
        private TextView tvStatus; //快递状态改变时间
        private TextView tvDatetime; //快递状态改变时间
        private TextView tvRemark; //快递备注
        private  CheckBox checkBox;

    }



    public interface SubClickListener {
        public void setSelectedNum(int num);
    }

    public void setSubOnClickListener(SubClickListener subClickListener) {
        this.subClickListener = subClickListener;
    }



    //过滤相关
    /**
     * This lock is also used by the filter
     * (see {@link #getFilter()} to make a synchronized copy of
     * the original array of data.
     * 过滤器上的锁可以同步复制原始数据。
     *
     */
    private final Object mLock = new Object();

    // A copy of the original mObjects array, initialized from and then used instead as soon as
    // the mFilter ArrayFilter is used. mObjects will then only contain the filtered values.
    //对象数组的备份，当调用ArrayFilter的时候初始化和使用。此时，对象数组只包含已经过滤的数据。
    private ArrayList<HistoryData> mOriginalValues;
    private ArrayFilter mFilter;
    private ArrayFilterByStatus mFilterByStatus;
    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    public Filter getFilterByStatus(){
        if (mFilterByStatus == null) {
            mFilterByStatus = new ArrayFilterByStatus();
        }
        return mFilterByStatus;
    }

    /**
     * 过滤数据的类
     */
    /**
     * <p>An array filter constrains the content of the array adapter with
     * a prefix. Each item that does not start with the supplied prefix
     * is removed from the list.</p>
     * <p/>
     * 一个带有首字母约束的数组过滤器，每一项不是以该首字母开头的都会被移除该list。
     */
    private class ArrayFilter extends Filter {
        //执行刷选
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();//过滤的结果
            //原始数据备份为空时，上锁，同步复制原始数据
            if (mOriginalValues == null) {
                synchronized (mLock) {
                    mOriginalValues = new ArrayList<>(mList);
                }
            }
            //当首字母为空时
            if (prefix == null || prefix.length() == 0) {
                ArrayList<HistoryData> list;
                synchronized (mLock) {//同步复制一个原始备份数据
                    list = new ArrayList<>(mOriginalValues);
                }
                results.values = list;
                results.count = list.size();//此时返回的results就是原始的数据，不进行过滤
            } else {
                String prefixString = prefix.toString().toLowerCase();//转化为小写

                ArrayList<HistoryData> values;
                synchronized (mLock) {//同步复制一个原始备份数据
                    values = new ArrayList<>(mOriginalValues);
                }
                final int count = values.size();
                final ArrayList<HistoryData> newValues = new ArrayList<>();

                for (int i = 0; i < count; i++) {
                    final HistoryData value = values.get(i);//从List<User>中拿到User对象
//          final String valueText = value.toString().toLowerCase();
                    final String valueText = value.getNumber().toString().toLowerCase();//User对象的name属性作为过滤的参数
                    // First match against the whole, non-splitted value
                    if (valueText.startsWith(prefixString) || valueText.indexOf(prefixString.toString()) != -1) {//第一个字符是否匹配
                        newValues.add(value);//将这个item加入到数组对象中
                    } else {//处理首字符是空格
                        final String[] words = valueText.split(" ");
                        final int wordCount = words.length;

                        // Start at index 0, in case valueText starts with space(s)
                        for (int k = 0; k < wordCount; k++) {
                            if (words[k].startsWith(prefixString)) {//一旦找到匹配的就break，跳出for循环
                                newValues.add(value);
                                break;
                            }
                        }
                    }
                }
                results.values = newValues;//此时的results就是过滤后的List<User>数组
                results.count = newValues.size();
            }
            return results;
        }

        //刷选结果
        @Override
        protected void publishResults(CharSequence prefix, FilterResults results) {
            //noinspection unchecked
            mList = (List<HistoryData>) results.values;//此时，Adapter数据源就是过滤后的Results
            if (results.count > 0) {
                notifyDataSetChanged();//这个相当于从mDatas中删除了一些数据，只是数据的变化，故使用notifyDataSetChanged()
            } else {
                /**
                 * 数据容器变化 ----> notifyDataSetInValidated

                 容器中的数据变化 ----> notifyDataSetChanged
                 */
                notifyDataSetInvalidated();//当results.count<=0时，此时数据源就是重新new出来的，说明原始的数据源已经失效了
            }
        }
    }


    private class ArrayFilterByStatus extends Filter {
        //执行刷选
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();//过滤的结果
            //原始数据备份为空时，上锁，同步复制原始数据
            if (mOriginalValues == null) {
                synchronized (mLock) {
                    mOriginalValues = new ArrayList<>(mList);
                }
            }
            //当首字母为空时
            if (prefix == null || prefix.length() == 0) {
                ArrayList<HistoryData> list;
                synchronized (mLock) {//同步复制一个原始备份数据
                    list = new ArrayList<>(mOriginalValues);
                }
                results.values = list;
                results.count = list.size();//此时返回的results就是原始的数据，不进行过滤
            } else {
                String prefixString = prefix.toString().toLowerCase();//转化为小写

                ArrayList<HistoryData> values;
                synchronized (mLock) {//同步复制一个原始备份数据
                    values = new ArrayList<>(mOriginalValues);
                }
                final int count = values.size();
                final ArrayList<HistoryData> newValues = new ArrayList<>();

                for (int i = 0; i < count; i++) {
                    final HistoryData value = values.get(i);//从List<User>中拿到User对象
//          final String valueText = value.toString().toLowerCase();
                    final String valueText = value.getStatus().toString().toLowerCase();//User对象的name属性作为过滤的参数
                    // First match against the whole, non-splitted value
                    if (valueText.startsWith(prefixString) || valueText.indexOf(prefixString.toString()) != -1) {//第一个字符是否匹配
                        newValues.add(value);//将这个item加入到数组对象中
                    } else {//处理首字符是空格
                        final String[] words = valueText.split(" ");
                        final int wordCount = words.length;

                        // Start at index 0, in case valueText starts with space(s)
                        for (int k = 0; k < wordCount; k++) {
                            if (words[k].startsWith(prefixString)) {//一旦找到匹配的就break，跳出for循环
                                newValues.add(value);
                                break;
                            }
                        }
                    }
                }
                results.values = newValues;//此时的results就是过滤后的List<User>数组
                results.count = newValues.size();
            }
            return results;
        }

        //刷选结果
        @Override
        protected void publishResults(CharSequence prefix, FilterResults results) {
            //noinspection unchecked
            mList = (List<HistoryData>) results.values;//此时，Adapter数据源就是过滤后的Results
            if (results.count > 0) {
                notifyDataSetChanged();//这个相当于从mDatas中删除了一些数据，只是数据的变化，故使用notifyDataSetChanged()
            } else {
                /**
                 * 数据容器变化 ----> notifyDataSetInValidated

                 容器中的数据变化 ----> notifyDataSetChanged
                 */
                notifyDataSetInvalidated();//当results.count<=0时，此时数据源就是重新new出来的，说明原始的数据源已经失效了
            }
        }
    }

}
