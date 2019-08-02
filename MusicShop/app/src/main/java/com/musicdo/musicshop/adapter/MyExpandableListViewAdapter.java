package com.musicdo.musicshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.musicdo.musicshop.R;
import com.musicdo.musicshop.activity.ProductDetailActivity;
import com.musicdo.musicshop.activity.SearchProuductActivity;
import com.musicdo.musicshop.bean.HomeSecondBean;
import com.musicdo.musicshop.util.ScreenUtil;

import java.util.List;

/**
 * Created by Yuedu on 2017/9/13.
 */

public class MyExpandableListViewAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    /**
     * 每个分组的名字的集合
     */
    private List<HomeSecondBean> groupList;
    HomeSecondItemRecylistAdapter RecylistAdapter;
    /**
     * 所有分组的所有子项的 GridView 数据集合
     */

    private GridView gridView;

    public MyExpandableListViewAdapter(Context context, List<HomeSecondBean> groupList) {
        mContext = context;
        this.groupList = groupList;
    }

    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groupList.get(groupPosition).get_List().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup
            parent) {
        GroupHolder groupHolder;
        if (null == convertView) {
            convertView = View.inflate(mContext, R.layout.item_homesecond_group, null);
            groupHolder=new GroupHolder();
            groupHolder.Group_line = (ImageView) convertView.findViewById(R.id.im_title_line);
            groupHolder.Group_title = (TextView) convertView.findViewById(R.id.tv_title);
            groupHolder.Group_more = (TextView) convertView.findViewById(R.id.tv_homesecond_more);
            convertView.setTag(groupHolder);
        }else{
            groupHolder=(GroupHolder)convertView.getTag();
        }

        // 如果是展开状态，就显示展开的箭头，否则，显示折叠的箭头
//        if (isExpanded) {
//            ivGroup.setImageResource(R.drawable.ic_open);
//        } else {
//            ivGroup.setImageResource(R.drawable.ic_close);
//        }
        // 设置分组组名
        groupHolder.Group_title.setText(groupList.get(groupPosition).getName());
        switch (groupPosition){
            case 0:
                groupHolder.Group_line.setBackgroundColor(mContext.getResources().getColor(R.color.homesecond_lise_one));
                groupHolder.Group_title.setTextColor(mContext.getResources().getColor(R.color.homesecond_lise_one));
                break;
            case 1:
                groupHolder.Group_line.setBackgroundColor(mContext.getResources().getColor(R.color.homesecond_lise_two));
                groupHolder.Group_title.setTextColor(mContext.getResources().getColor(R.color.homesecond_lise_two));
                break;
            case 2:
                groupHolder.Group_line.setBackgroundColor(mContext.getResources().getColor(R.color.homesecond_lise_third));
                groupHolder.Group_title.setTextColor(mContext.getResources().getColor(R.color.homesecond_lise_third));
                break;
            case 3:
                groupHolder.Group_line.setBackgroundColor(mContext.getResources().getColor(R.color.homesecond_lise_four));
                groupHolder.Group_title.setTextColor(mContext.getResources().getColor(R.color.homesecond_lise_four));
                break;
            case 4:
                groupHolder.Group_line.setBackgroundColor(mContext.getResources().getColor(R.color.homesecond_lise_four));
                groupHolder.Group_title.setTextColor(mContext.getResources().getColor(R.color.homesecond_lise_four));
                break;
            case 5:
                groupHolder.Group_line.setBackgroundColor(mContext.getResources().getColor(R.color.homesecond_lise_five));
                groupHolder.Group_title.setTextColor(mContext.getResources().getColor(R.color.homesecond_lise_five));
                break;
            case 6:
                groupHolder.Group_line.setBackgroundColor(mContext.getResources().getColor(R.color.homesecond_lise_six));
                groupHolder.Group_title.setTextColor(mContext.getResources().getColor(R.color.homesecond_lise_six));
                break;
            case 7:
                groupHolder.Group_line.setBackgroundColor(mContext.getResources().getColor(R.color.homesecond_lise_seven));
                groupHolder.Group_title.setTextColor(mContext.getResources().getColor(R.color.homesecond_lise_seven));
                break;
            case 8:
                groupHolder.Group_line.setBackgroundColor(mContext.getResources().getColor(R.color.homesecond_lise_eight));
                groupHolder.Group_title.setTextColor(mContext.getResources().getColor(R.color.homesecond_lise_eight));
                break;
            case 9:
                groupHolder.Group_line.setBackgroundColor(mContext.getResources().getColor(R.color.homesecond_lise_night));
                groupHolder.Group_title.setTextColor(mContext.getResources().getColor(R.color.homesecond_lise_night));
                break;
        }
        groupHolder.Group_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, SearchProuductActivity.class);
                intent.putExtra("keyword",groupList.get(groupPosition).getName());
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View
            convertView, ViewGroup parent) {
        ChildHolder childHolder;
        if (null == convertView) {
            convertView = View.inflate(mContext, R.layout.item_homesecond_child, null);
            childHolder=new ChildHolder();
//            childHolder.gv_homesecond_item=(GridView) convertView.findViewById(R.id.gv_homesecond_item);
            childHolder.rc_searchprod_overall=(RecyclerView)convertView.findViewById(R.id.rc_searchprod_overall);
            convertView.setTag(childHolder);
        }else{
            childHolder=(ChildHolder)convertView.getTag();
        }
        // 因为 convertView 的布局就是一个 GridView，
        // 所以可以向下转型为 GridView
//        gridView = (GridView) convertView;
        // 创建 GridView 适配器

        RecylistAdapter= new HomeSecondItemRecylistAdapter(mContext, groupList.get(groupPosition).get_List(),false);
        childHolder.rc_searchprod_overall.setLayoutManager( new GridLayoutManager(mContext, 2));
        childHolder.rc_searchprod_overall.setAdapter(RecylistAdapter);
//        childHolder.rc_searchprod_overall.setNestedScrollingEnabled(false);
//        LinearLayout.LayoutParams linearParams =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
//        linearParams.width= ScreenUtil.getScreenWidth(mContext);
//        linearParams.height=ScreenUtil.getScreenHeight(mContext);
//        gridView.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
        RecylistAdapter.setOnItemClickListener(new HomeSecondItemRecylistAdapter.OnSearchItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                Toast.makeText(mContext, "点击了第" + (groupPosition + 1) + "组，第" +
//                        (position + 1) + "项", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(mContext,ProductDetailActivity.class);
                int id=groupList.get(groupPosition).get_List().get(position).getID();
                intent.putExtra("prod_id",id);
                mContext.startActivity(intent);
            }
        });


    /*<com.musicdo.musicshop.view.MyGridView
xmlns:android="http://schemas.android.com/apk/res/android"
android:layout_width="match_parent"
android:layout_height="wrap_content"
android:id="@+id/gv_homesecond_item"
android:numColumns="2">
</com.musicdo.musicshop.view.MyGridView>*/
//        gridView = (GridView) convertView;
        // 创建 GridView 适配器
        /*MyGridViewAdapter gridViewAdapter = new MyGridViewAdapter(mContext, groupList.get(groupPosition).get_List());
        childHolder.gv_homesecond_item.setAdapter(gridViewAdapter);
        childHolder.gv_homesecond_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(mContext,ProductDetailActivity.class);
                intent.putExtra("prod_id",groupList.get(groupPosition).get_List().get(position).getID());
                mContext.startActivity(intent);
            }
        });*/
        return convertView;
    }

    private class ChildHolder
    {
        RecyclerView rc_searchprod_overall;
//        GridView gv_homesecond_item;
    }
    private class GroupHolder
    {
        ImageView Group_line;
        TextView Group_title;
        TextView Group_more;
    }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
}
}
