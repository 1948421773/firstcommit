package com.musicdo.musicshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.activity.GoodsInfoSpecActivity;
import com.musicdo.musicshop.activity.ProductDetailActivity;
import com.musicdo.musicshop.bean.CartBean;
import com.musicdo.musicshop.bean.CartItemBean;
import com.musicdo.musicshop.bean.SpecBean;
import com.musicdo.musicshop.bean.SpecItemBean;
import com.musicdo.musicshop.constant.AppConstants;
import com.musicdo.musicshop.util.SpUtils;
import com.musicdo.musicshop.util.ToastUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 描述:
 * 作者：haiming on 2017/8/18 16:25
 * 邮箱：1948421773@qq.com
 * 版 本 ：1.0
 * 备 注 ：
 */

public class SubmitOrderExpandableListViewAdapter extends BaseExpandableListAdapter implements View.OnClickListener {
    private List<CartBean> groups=new ArrayList<>();
    private SpecBean specBeans=new SpecBean();
    private List<CartItemBean> children=new ArrayList<>();
    private Context context;
    /**
     * 构造函数
     *
     * @param groups
     *            组元素列表
     * @param context
     */
    public SubmitOrderExpandableListViewAdapter(List<CartBean> groups,Context context)
    {
        super();
        this.groups = groups;
        this.children = children;
        this.context = context;
    }


    @Override
    public int getGroupCount()
    {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {

        return groups.get(groupPosition).getProductDetail().size();
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        return groups.get(groupPosition).getProductDetail().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return 0;
    }

    @Override
    public boolean hasStableIds()
    {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {

        final GroupHolder gholder;
        if (convertView == null)
        {
            gholder = new GroupHolder();
            convertView = View.inflate(context, R.layout.item_submitorder_group, null);
            gholder.tv_group_name = (TextView) convertView.findViewById(R.id.tv_shop_name);
            convertView.setTag(gholder);
        } else
        {
            gholder = (GroupHolder) convertView.getTag();
        }
        final CartBean group =groups.get(groupPosition);
        if (group != null)
        {
            gholder.tv_group_name.setText(group.getShopName());
        }
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {

        final ChildHolder cholder;
        if (convertView == null)
        {
            cholder = new ChildHolder();
            convertView = View.inflate(context, R.layout.item_submitorder_child, null);
            cholder.ll_shop_seal_info = (LinearLayout) convertView.findViewById(R.id.ll_shop_seal_info);
            cholder.iv_adapter_list_pic = (ImageView) convertView.findViewById(R.id.iv_adapter_list_pic);
            cholder.tv_choose_parameter = (TextView) convertView.findViewById(R.id.tv_choose_parameter);
            cholder.tv_child_total_price = (TextView) convertView.findViewById(R.id.tv_child_total_price);
            cholder.tv_child_number = (TextView) convertView.findViewById(R.id.tv_child_number);
            cholder.tv_number = (TextView) convertView.findViewById(R.id.tv_number);
            cholder.tv_intro = (TextView) convertView.findViewById(R.id.tv_intro);
            cholder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
            convertView.setTag(cholder);
        } else
        {
            // convertView = childrenMap.get(groupPosition);
            cholder = (ChildHolder) convertView.getTag();
        }
//		final CartItemBean product =groups.get(groupPosition).getProductDetail().get(childPosition);
        final CartItemBean product = (CartItemBean) getChild(groupPosition, childPosition);
        if (childPosition==getChildrenCount(groupPosition)-1){
            cholder.ll_shop_seal_info.setVisibility(View.VISIBLE);
            double totalPrice=0;
            int totalNumber=0;
            for (CartItemBean price:groups.get(groupPosition).getProductDetail()){
                totalPrice=totalPrice+price.getMemberPrice()*price.getCount();
                totalNumber=totalNumber+price.getCount();
            }
            cholder.tv_child_total_price.setText(SpUtils.doubleToString(totalPrice));
            cholder.tv_child_number.setText(String.valueOf(totalNumber));
        }else{
            cholder.ll_shop_seal_info.setVisibility(View.GONE);
        }

        if (product != null){
            Picasso.with(context)
                    .load(AppConstants.PhOTOADDRESS+product.getSrcDetail())
                    .resize(200,200)
                    .centerCrop()
                    .config(Bitmap.Config.RGB_565)
                    .placeholder(R.mipmap.img_start_loading)
                    .error(R.mipmap.img_load_error)
                    .into(cholder.iv_adapter_list_pic);//加载网络图片

            if(!product.getPropertysText().equals("")){
                cholder.tv_choose_parameter.setText(product.getPropertysText());
            }else{
            }
            cholder.tv_intro.setText(product.getName());
            cholder.tv_price.setText("¥" + product.getMemberPrice() + "");
            cholder.tv_number.setText("X"+product.getCount() + "");

            /*cholder.iv_adapter_list_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context,ProductDetailActivity.class);
                    intent.putExtra("prod_id",product.getProductID());
                    context.startActivity(intent);
                }
            });*/

        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
        }
    }

    /**
     * 组元素绑定器
     *
     *
     */
    private class GroupHolder
    {
        TextView tv_group_name;
    }

    /**
     * 子元素绑定器
     *
     *
     */
    private class ChildHolder
    {
        ImageView iv_adapter_list_pic;
        TextView tv_intro;
        TextView tv_child_number;
        TextView tv_child_total_price;
        TextView tv_choose_parameter;
        TextView tv_price;
        TextView tv_number;
        LinearLayout ll_shop_seal_info;
    }

    /**
     * 复选框接口
     *
     *
     */
    public interface CheckInterface
    {
        /**
         * 组选框状态改变触发的事件
         *
         * @param groupPosition
         *            组元素位置
         * @param isChecked
         *            组元素选中与否
         */
        public void checkGroup(int groupPosition, boolean isChecked);

        /**
         * 子选框状态改变时触发的事件
         *
         * @param groupPosition
         *            组元素位置
         * @param childPosition
         *            子元素位置
         * @param isChecked
         *            子元素选中与否
         */
        public void checkChild(int groupPosition, int childPosition, boolean isChecked);

        /**
         * 组编辑状态改变触发的事件
         *
         * @param groupPosition
         *            组元素位置
         * @param isEdited
         *            组编辑与否
         */
        public void editGroup(int groupPosition, boolean isEdited);

        /**
         * 子编辑状态改变触发的事件
         *
         * @param groupPosition
         *            组元素位置
         * @param childPosition
         *            子元素位置
         * @param isChecked
         *            子编辑与否
         */
        public void editChild(int groupPosition, int childPosition, boolean isChecked);
    }

    /**
     * 改变数量的接口
     *
     *
     */
    public interface ModifyCountInterface
    {
        /**
         * 增加操作
         *
         * @param groupPosition
         *            组元素位置
         * @param childPosition
         *            子元素位置
         * @param showCountView
         *            用于展示变化后数量的View
         * @param isChecked
         *            子元素选中与否
         */
        public void doIncrease(int groupPosition, int childPosition, View showCountView, boolean isChecked);

        /**
         * 删减操作
         *
         * @param groupPosition
         *            组元素位置
         * @param childPosition
         *            子元素位置
         * @param showCountView
         *            用于展示变化后数量的View
         * @param isChecked
         *            子元素选中与否
         */
        public void doDecrease(int groupPosition, int childPosition, View showCountView, boolean isChecked);
    }

    public interface DeleteInterface
    {
        /**
         * 删除操作
         *
         * @param groupPosition
         *            组元素位置
         * @param childPosition
         *            子元素位置
         * @param isChecked
         *            子元素选中与否
         */
        public void delete(int groupPosition, int childPosition, boolean isChecked);

    }
    private void getSpecData(final CartItemBean CartItem) {
        if (CartItem.getProductID()==0) {
            ToastUtil.showShort(context,"数据加载超时");
            return;
        }
        OkHttpUtils.get(AppConstants.GetSpec)
                .tag(this)
                .params("ProductID",CartItem.getProductID())//ProductID固定地方便调试，其他ID没数据
//                .params("ID",productId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {
                        JSONObject jsonObject;
                        Gson gson=new Gson();
                        String jsonData=null;
                        String Message=null;
                        try {
                            jsonObject = new JSONObject(s);
                            jsonData = jsonObject.getString("ReturnData");
                            Message = jsonObject.getString("Message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        specBeans= gson.fromJson(s, SpecBean.class);
//						Intent intent = new Intent(context, GoodsInfoSpecActivity.class);
//						intent.putExtra("ProductID", CartItem.getProductID());
//						intent.putExtra("price", CartItem.getMemberPrice());
//						intent.putExtra("Propertys", CartItem.getPropertys());
//						intent.putExtra("spec",specBeans);
//						intent.putExtra("shoppingCart","shoppingCart");
//						context.startActivity(intent);

                        Intent intent = new Intent(context, GoodsInfoSpecActivity.class);
                        intent.putExtra("GUID", CartItem.getGUID());
                        intent.putExtra("Propertys", CartItem.getPropertys());
                        intent.putExtra("PropertysText", CartItem.getPropertysText());
                        intent.putExtra("ProductID", CartItem.getProductID());
                        intent.putExtra("price", CartItem.getMemberPrice());
                        intent.putExtra("Propertys", CartItem.getPropertys());
                        intent.putExtra("Count", CartItem.getCount());
//						intent.putParcelableArrayListExtra("specItemBeans",specItemBeans);
                        intent.putExtra("spec",specBeans);
                        intent.putExtra("shoppingCart","shoppingCart");
                        context.startActivity(intent);
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
//                        ToastUtil.showShort(context,"开始加载数据");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showShort(context,"数据加载超时");

                    }
                });
    }

}
