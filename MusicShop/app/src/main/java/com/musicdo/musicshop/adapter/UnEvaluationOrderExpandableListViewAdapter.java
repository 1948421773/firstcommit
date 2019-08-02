package com.musicdo.musicshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.activity.AllOrderPayForActivity;
import com.musicdo.musicshop.activity.GoodsInfoSpecActivity;
import com.musicdo.musicshop.activity.OrderDtailActivity;
import com.musicdo.musicshop.activity.PaymenyActivity;
import com.musicdo.musicshop.activity.ProductDetailActivity;
import com.musicdo.musicshop.bean.AllOrderBean;
import com.musicdo.musicshop.bean.CartItemBean;
import com.musicdo.musicshop.bean.OrderDetailBean;
import com.musicdo.musicshop.bean.SpecBean;
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
 * Created by Yuedu on 2017/10/9.
 */

public class UnEvaluationOrderExpandableListViewAdapter extends BaseExpandableListAdapter implements View.OnClickListener {
    private List<AllOrderBean> groups=new ArrayList<>();
    private SpecBean specBeans=new SpecBean();
    //    private List<CartItemBean> children=new ArrayList<>();
    private Context context;
    DeleteInterface deleteInterface;
    DeliverGoodsWarning deliverGoodsWarning;
    LongTimeDeliver longTimeDeliver;
    TackingLogistics tackingLogistics;
    GotoEvaluate gotoEvaluate;
    AdditionalComments additionalComments;
    DeleteOrder deleteOrder;
    /**
     * 构造函数
     *
     * @param groups
     *            组元素列表
     * @param context
     */
    public UnEvaluationOrderExpandableListViewAdapter(List<AllOrderBean> groups,Context context)
    {
        super();
        this.groups = groups;
//        this.children = children;
        this.context = context;
    }

    public void setDeteInterface(DeleteInterface deleteInterface){
        this.deleteInterface = deleteInterface;
    }
    public void setWarningInterface(DeliverGoodsWarning deliverGoodsWarning){
        this.deliverGoodsWarning = deliverGoodsWarning;
    }
    public void setLongTimeDeliver(LongTimeDeliver longTimeDeliver){
        this.longTimeDeliver = longTimeDeliver;
    }
    public void setTackingLogistics(TackingLogistics tackingLogistics){
        this.tackingLogistics = tackingLogistics;
    }
    public void setGotoEvaluate(GotoEvaluate gotoEvaluate){
        this.gotoEvaluate = gotoEvaluate;
    }

    public void setAdditionalComments(AdditionalComments additionalComments){
        this.additionalComments = additionalComments;
    }

    public void setDeleteOrder(DeleteOrder deleteOrder){
        this.deleteOrder = deleteOrder;
    }

    @Override
    public int getGroupCount()
    {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {

        return groups.get(groupPosition).getOrderDetail().size();
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        return groups.get(groupPosition).getOrderDetail().get(childPosition);
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

        final UnEvaluationOrderExpandableListViewAdapter.GroupHolder gholder;
        if (convertView == null)
        {
            gholder = new UnEvaluationOrderExpandableListViewAdapter.GroupHolder();
            convertView = View.inflate(context, R.layout.order_unpaid_group, null);
            gholder.tv_group_name = (TextView) convertView.findViewById(R.id.tv_shop_name);
            gholder.tv_all_order_state = (TextView) convertView.findViewById(R.id.tv_all_order_state);
            convertView.setTag(gholder);
        } else
        {
            gholder = (UnEvaluationOrderExpandableListViewAdapter.GroupHolder) convertView.getTag();
        }
        final AllOrderBean group =groups.get(groupPosition);
        if (group != null)
        {
            gholder.tv_group_name.setText(group.getShopName());
            switch(group.getStatus()){//0等待付款，1已付款，2已发货，3已收货，4交易完成，5退款申请中，6退款完成，-1用户已取消，-2管理员已取消
                case 0:
                    gholder.tv_all_order_state.setText("待付款");
                    break;
                case 1:
                    gholder.tv_all_order_state.setText("买家已付款");
                    break;
                case 2:
                    gholder.tv_all_order_state.setText("等待收货");
                    break;
                case 3:
                    gholder.tv_all_order_state.setText("交易完成");
                    break;
                case 4:
                    gholder.tv_all_order_state.setText("交易完成");
                    break;
                case 5:
                    gholder.tv_all_order_state.setText("退款申请中");
                    break;
                case 6:
                    gholder.tv_all_order_state.setText("退款完成");
                    break;
                case -1:
                    gholder.tv_all_order_state.setText("交易关闭");
                    break;
                case -2:
                    gholder.tv_all_order_state.setText("商家已取消");
                    break;
                case -3:
                    gholder.tv_all_order_state.setText("平台管理员已取消");
                    break;
            }
        }
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {

        final UnEvaluationOrderExpandableListViewAdapter.ChildHolder cholder;
        if (convertView == null)
        {
            cholder = new UnEvaluationOrderExpandableListViewAdapter.ChildHolder();
            convertView = View.inflate(context, R.layout.order_unpaid_item, null);
            cholder.tv_intro = (TextView) convertView.findViewById(R.id.tv_intro);
            cholder.ll_allorder_total_info = (LinearLayout) convertView.findViewById(R.id.ll_allorder_total_info);
            cholder.rl_purchaselist_item_title = (RelativeLayout) convertView.findViewById(R.id.rl_purchaselist_item_title);

            cholder.sd_purchase_img = (ImageView) convertView.findViewById(R.id.sd_purchase_img);
            cholder.tv_choose_parameter = (TextView) convertView.findViewById(R.id.tv_choose_parameter);
            cholder.tv_child_total_price = (TextView) convertView.findViewById(R.id.tv_child_total_price);
            cholder.tv_child_number = (TextView) convertView.findViewById(R.id.tv_child_number);
            cholder.tv_number = (TextView) convertView.findViewById(R.id.tv_number);

            cholder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);

            cholder.ll_unpaid = (LinearLayout) convertView.findViewById(R.id.ll_unpaid);
            cholder.tv_payfor = (TextView) convertView.findViewById(R.id.tv_payfor);//找人代付
            cholder.tv_payfor.setOnClickListener(this);
            cholder.tv_cancel_order = (TextView) convertView.findViewById(R.id.tv_cancel_order);//取消订单

            cholder.tv_payment = (TextView) convertView.findViewById(R.id.tv_payment);//订单立即支付

            cholder.tv_remind_delivery = (TextView) convertView.findViewById(R.id.tv_remind_delivery);//提醒发货

            cholder.ll_unReceipt = (LinearLayout) convertView.findViewById(R.id.ll_unReceipt);
            cholder.tv_longtime_deliver = (TextView) convertView.findViewById(R.id.tv_longtime_deliver);//延长收货
            cholder.tv_tracking_Logistics = (TextView) convertView.findViewById(R.id.tv_tracking_Logistics);//物流追踪
            cholder.tv_confirm_receipt = (TextView) convertView.findViewById(R.id.tv_confirm_receipt);

            cholder.ll_unEvaluation = (LinearLayout) convertView.findViewById(R.id.ll_unEvaluation);
            cholder.tv_delete_order = (TextView) convertView.findViewById(R.id.tv_delete_order);
            cholder.tv_tracking_Logistics_tow = (TextView) convertView.findViewById(R.id.tv_tracking_Logistics_tow);//物流追踪
            cholder.tv_goto_evaluate = (TextView) convertView.findViewById(R.id.tv_goto_evaluate);//我要评价
            cholder.tv_additional_comments = (TextView) convertView.findViewById(R.id.tv_additional_comments);//我要评价

            cholder.order_unpaid_prod_total_price = (TextView) convertView.findViewById(R.id.order_unpaid_prod_total_price);
            cholder.tv_order_unpaid_num = (TextView) convertView.findViewById(R.id.tv_order_unpaid_num);

            convertView.setTag(cholder);
        } else
        {
            // convertView = childrenMap.get(groupPosition);
            cholder = (UnEvaluationOrderExpandableListViewAdapter.ChildHolder) convertView.getTag();
        }
//		final CartItemBean product =groups.get(groupPosition).getProductDetail().get(childPosition);
        final OrderDetailBean product = (OrderDetailBean) getChild(groupPosition, childPosition);
        int getchildcount=getChildrenCount(groupPosition)-1;
        if (childPosition==getchildcount){
//            cholder.ll_shop_seal_info.setVisibility(View.VISIBLE);
            double totalPrice=0;
            int totalNumber=0;

//            cholder.tv_child_total_price.setText(SpUtils.doubleToString(groups.get(groupPosition).getTotalPrice()));
//            cholder.tv_child_number.setText(String.valueOf(groups.get(groupPosition).getCount()));
            cholder.ll_allorder_total_info.setVisibility(View.VISIBLE);
        }else{
            cholder.ll_allorder_total_info.setVisibility(View.GONE);
        }

        if (product != null){
            Picasso.with(context)
                    .load(AppConstants.PhOTOADDRESS+product.getSrc())
                    .resize(200,200)
                    .centerCrop()
                    .config(Bitmap.Config.RGB_565)
                    .placeholder(R.mipmap.img_start_loading)
                    .error(R.mipmap.img_load_error)
                    .into(cholder.sd_purchase_img);//加载网络图片

//            if(!product.getOrderDetail().equals("")){
//                cholder.tv_choose_parameter.setText(product.getPropertysText());
//            }else{
//            }
            cholder.tv_intro.setText(product.getName());
            cholder.tv_choose_parameter.setText(product.getPropertysText());
            cholder.tv_price.setText("¥" +SpUtils.doubleToString(product.getMemberPrice()));
            cholder.tv_number.setText("X"+product.getCount() + "");



            //判断订单状态
            switch(groups.get(groupPosition).getStatus()){
                case 0:
                    cholder.ll_unpaid.setVisibility(View.VISIBLE);
                    cholder.tv_remind_delivery.setVisibility(View.GONE);
                    cholder.ll_unReceipt.setVisibility(View.GONE);
                    cholder.ll_unEvaluation.setVisibility(View.GONE);
                    break;
                case 1:
                    cholder.ll_unpaid.setVisibility(View.GONE);
                    cholder.tv_remind_delivery.setVisibility(View.VISIBLE);
                    cholder.ll_unReceipt.setVisibility(View.GONE);
                    cholder.ll_unEvaluation.setVisibility(View.GONE);
                    break;
                case 2:
                    cholder.ll_unpaid.setVisibility(View.GONE);
                    cholder.tv_remind_delivery.setVisibility(View.GONE);
                    cholder.ll_unReceipt.setVisibility(View.VISIBLE);
                    cholder.ll_unEvaluation.setVisibility(View.GONE);
                    break;
                case 3:
                    cholder.ll_unpaid.setVisibility(View.GONE);
                    cholder.tv_remind_delivery.setVisibility(View.GONE);
                    cholder.ll_unReceipt.setVisibility(View.GONE);
                    cholder.ll_unEvaluation.setVisibility(View.VISIBLE);
                    cholder.tv_goto_evaluate.setVisibility(View.VISIBLE);
                    cholder.tv_tracking_Logistics_tow.setVisibility(View.VISIBLE);
                    switch (product.getCommentStatus()){
                        case 0://待评价
                            cholder.tv_goto_evaluate.setVisibility(View.VISIBLE);
                            cholder.tv_additional_comments.setVisibility(View.GONE);
                            break;
                        case 1://已评价
                            cholder.tv_goto_evaluate.setVisibility(View.GONE);
                            cholder.tv_additional_comments.setVisibility(View.VISIBLE);
                            break;
                        case 2://已追评
                            cholder.tv_goto_evaluate.setVisibility(View.GONE);
                            cholder.tv_additional_comments.setVisibility(View.GONE);
                            break;
                        case 3://截止评价
                            cholder.tv_goto_evaluate.setVisibility(View.GONE);
                            cholder.tv_additional_comments.setVisibility(View.GONE);
                            break;
                    }
                    break;
                case 4://已收货未确认收货 15个工作日平台自动修改状态为：交易完成，可评价，和待评价一样
                    cholder.ll_unpaid.setVisibility(View.GONE);
                    cholder.tv_remind_delivery.setVisibility(View.GONE);
                    cholder.ll_unReceipt.setVisibility(View.GONE);
                    cholder.ll_unEvaluation.setVisibility(View.VISIBLE);
                    break;
                case 5:
                    break;
                case 6:
                    break;
                case 7:
                    break;
                case 8:
                    break;
                case -1:
                    cholder.ll_unEvaluation.setVisibility(View.VISIBLE);
                    cholder.ll_unpaid.setVisibility(View.GONE);
                    cholder.tv_remind_delivery.setVisibility(View.GONE);
                    cholder.ll_unReceipt.setVisibility(View.GONE);
                    cholder.tv_goto_evaluate.setVisibility(View.GONE);
                    cholder.tv_additional_comments.setVisibility(View.GONE);
                    cholder.tv_tracking_Logistics_tow.setVisibility(View.GONE);
                    break;
                case -2:
                    break;
            }
            int itemTotalNum=0;
            double itemTotalPrice=0;
            for (OrderDetailBean total:groups.get(groupPosition).getOrderDetail()){
                itemTotalNum=itemTotalNum+total.getCount();
                itemTotalPrice=itemTotalPrice+total.getCount()*total.getMemberPrice();
            }
            cholder.tv_order_unpaid_num.setText(String.valueOf(itemTotalNum));
            cholder.order_unpaid_prod_total_price.setText(SpUtils.doubleToString(itemTotalPrice));

            cholder.sd_purchase_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context,ProductDetailActivity.class);
                    intent.putExtra("prod_id",product.getProductID());
                    context.startActivity(intent);
                }
            });

            cholder.tv_cancel_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteInterface.delete(groupPosition, childPosition);// 暴露组选接口
                }
            });

            final double finalItemTotalPrice = itemTotalPrice;
            cholder.tv_payment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context,PaymenyActivity.class);
                    String price=SpUtils.doubleToString(finalItemTotalPrice);
                    String title=product.getName();
                    String orderNumber=product.getOrderNumber();
                    intent.putExtra("price",price);
                    intent.putExtra("title",title);
                    intent.putExtra("OrderNumber",orderNumber);
                    context.startActivity(intent);
                }
            });
            cholder.tv_remind_delivery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String orderNumber=product.getOrderNumber();
                    int orderShopID=product.getShopID();
                    deliverGoodsWarning.doWarning(orderNumber, orderShopID,null);// 暴露组选接口

                }
            });
            cholder.tv_longtime_deliver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String orderNumber=product.getOrderNumber();
                    int orderShopID=product.getShopID();
                    longTimeDeliver.dolongTimeDeliver(orderNumber, orderShopID,null);// 暴露组选接口

                }
            });
            cholder.tv_delete_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String orderNumber=product.getOrderNumber();
                    int orderShopID=product.getShopID();
                    deleteOrder.doDeleteOrder(orderNumber, orderShopID,null);// 删除订单

                }
            });
            cholder.tv_tracking_Logistics_tow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String orderNumber=product.getOrderNumber();
                    String orderName=product.getName();
                    String url=product.getSrc();
                    int orderShopID=product.getShopID();
                    tackingLogistics.doTackingLogistics(orderNumber, orderShopID,orderName,url);// 暴露组选接口

                }
            });
            cholder.tv_goto_evaluate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String orderNumber=product.getOrderNumber();
                    int orderShopID=product.getShopID();
                    gotoEvaluate.doGotoEvaluate(orderNumber, groupPosition,null);

                }
            });

            cholder.tv_additional_comments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String orderNumber=product.getOrderNumber();
                    int orderShopID=product.getShopID();
                    additionalComments.doAdditionalComments(orderNumber, groupPosition,null);

                }
            });

            cholder.rl_purchaselist_item_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, OrderDtailActivity.class);
                    intent.putExtra("Number",product.getOrderNumber());//订单编号
                    intent.putExtra("orderState",product.getStatusID());//订单编号
                    context.startActivity(intent);
                }
            });
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
            case R.id.tv_payfor:{
                Intent intent=new Intent(context,AllOrderPayForActivity.class);
                context.startActivity(intent);
            }
            break;
            case R.id.tv_cancel_order:{
                Intent intent=new Intent(context,AllOrderPayForActivity.class);
                context.startActivity(intent);
            }
            break;
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
        TextView tv_all_order_state;
    }

    /**
     * 子元素绑定器
     *
     *
     */
    private class ChildHolder
    {
        ImageView sd_purchase_img;
        TextView tv_intro;
        TextView tv_child_number;
        TextView tv_child_total_price;
        TextView tv_choose_parameter;
        TextView tv_price;
        TextView tv_number;
        LinearLayout ll_allorder_total_info;
        RelativeLayout rl_purchaselist_item_title;
        //订单状态
        //待付款
        LinearLayout ll_unpaid;
        TextView tv_payfor;//朋友代付
        TextView tv_cancel_order;//取消订单
        TextView tv_payment;//立即付款
        //代发货
        TextView tv_remind_delivery;//提醒发货
        //待收货
        LinearLayout ll_unReceipt;
        TextView tv_longtime_deliver;//延长发货
        TextView tv_tracking_Logistics;//物流跟踪
        TextView tv_confirm_receipt;//确认收货
        //待评价
        LinearLayout ll_unEvaluation;
        TextView tv_delete_order;//删除订单
        TextView tv_goto_evaluate;//我要评价
        TextView tv_additional_comments;//追加评价
        TextView tv_tracking_Logistics_tow;//我要评价
        TextView tv_order_unpaid_num;//店铺下的所有商品数量
        TextView order_unpaid_prod_total_price;//店铺下的所有商品总价
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
    public interface DeliverGoodsWarning
    {
        /**
         * 删减操作
         *
         * @param OrderNumber 订单号
         * @param ShopID	店铺ID
         * @param UserName	用户名
         */
        public void doWarning(String OrderNumber, int ShopID,String UserName);
    }
    /**
     * 延长发货
     */
    public interface LongTimeDeliver
    {
        /**
         * 删减操作
         *
         * @param OrderNumber 订单号
         * @param ShopID	店铺ID
         * @param UserName	用户名
         */
        public void dolongTimeDeliver(String OrderNumber, int ShopID,String UserName);
    }
    /**
     * 物流追踪
     */
    public interface TackingLogistics
    {
        /**
         * 删减操作
         *
         * @param OrderNumber 订单号
         * @param ShopID	店铺ID
         * @param UserName	用户名
         */
        public void doTackingLogistics(String OrderNumber, int ShopID, String UserName,String url);
    }

    public interface AdditionalComments
    {
        /**
         * 删减操作
         *
         * @param OrderNumber 订单号
         * @param groupPosition	店铺ID
         * @param UserName	用户名
         */
        public void doAdditionalComments(String OrderNumber, int groupPosition,String UserName);
    }


    /**
     * 我要评价
     */
    public interface GotoEvaluate
    {
        /**
         * 删减操作
         *
         * @param OrderNumber 订单号
         * @param groupPosition	店铺ID
         * @param UserName	用户名
         */
        public void doGotoEvaluate(String OrderNumber, int groupPosition,String UserName);
    }
    public interface DeleteOrder
    {
        /**
         * 删减操作
         *
         * @param OrderNumber 订单号
         * @param groupPosition	店铺ID
         * @param UserName	用户名
         */
        public void doDeleteOrder(String OrderNumber, int groupPosition,String UserName);
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

    public interface DeleteInterface
    {
        /**
         * 删除操作
         * @param groupPosition 组元素位置
         * @param childPosition 子元素位置
         */
        public void delete(int groupPosition, int childPosition);

    }
}
