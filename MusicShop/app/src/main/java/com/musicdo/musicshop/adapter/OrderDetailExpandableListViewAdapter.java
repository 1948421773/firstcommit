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
import com.musicdo.musicshop.activity.GoodsInfoSpecActivity;
import com.musicdo.musicshop.activity.PaymenyActivity;
import com.musicdo.musicshop.activity.ProductDetailActivity;
import com.musicdo.musicshop.bean.AllOrderBean;
import com.musicdo.musicshop.bean.OrderDetailBean;
import com.musicdo.musicshop.bean.OrderDetailItemBean;
import com.musicdo.musicshop.bean.CartBean;
import com.musicdo.musicshop.bean.CartItemBean;
import com.musicdo.musicshop.bean.OrderProObjBean;
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
 * 订单详情Adapter
 * Created by Administrator on 2017/9/4.
 */

public class OrderDetailExpandableListViewAdapter extends BaseExpandableListAdapter implements View.OnClickListener {
    private List<CartBean> groups=new ArrayList<>();
    private SpecBean specBeans=new SpecBean();
    private List<CartItemBean> children=new ArrayList<>();
    AllOrderBean OrderDetailItemBeans=new AllOrderBean();
    private Context context;
    private int stateID;
    DeleteInterface deleteInterface;
    DeliverGoodsWarning deliverGoodsWarning;
    ApplyforRefund applyforRefund;
    TackingLogistics tackingLogistics;
    ConfirmReceipt confirmReceipt;
    AdditionalComments additionalComments;
    GotoEvaluate gotoEvaluate;
    DeleteOrder deleteOrder;
    TackingLogistics_tow tackingLogistics_tow;

    /**
     * 构造函数
     *
     * @param groups
     *            组元素列表
     * @param context
     */
    public OrderDetailExpandableListViewAdapter(int allSize,AllOrderBean groups,int stateID,Context context){
        super();
        this.OrderDetailItemBeans = groups;
        this.stateID = stateID;
        this.context = context;
    }

    public void setDeteInterface(DeleteInterface deleteInterface){
        this.deleteInterface = deleteInterface;
    }

    public void setWarningInterface(DeliverGoodsWarning deliverGoodsWarning){
        this.deliverGoodsWarning = deliverGoodsWarning;
    }

    public void setApplyforRefund(ApplyforRefund applyforRefund) {
        this.applyforRefund = applyforRefund;
    }

    public void setTackingLogistics(TackingLogistics tackingLogistics){
        this.tackingLogistics = tackingLogistics;
    }

    public void setConfirmReceipt(ConfirmReceipt confirmReceipt) {
        this.confirmReceipt = confirmReceipt;
    }

    public void setAdditionalComments(AdditionalComments additionalComments){
        this.additionalComments = additionalComments;
    }

    public void setGotoEvaluate(GotoEvaluate gotoEvaluate){
        this.gotoEvaluate = gotoEvaluate;
    }

    public void seTackingLogistics_tow(TackingLogistics_tow tackingLogistics_tow){
        this.tackingLogistics_tow = tackingLogistics_tow;
    }

    public void setDeleteOrder(DeleteOrder deleteOrder){
        this.deleteOrder = deleteOrder;
    }

    @Override
    public int getGroupCount()
    {
        return 1;
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {

        return OrderDetailItemBeans.getOrderProObj().size();
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return OrderDetailItemBeans;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        return OrderDetailItemBeans.getOrderProObj().get(childPosition);
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
            convertView = View.inflate(context, R.layout.item_orderdetail_group, null);
            gholder.tv_group_name = (TextView) convertView.findViewById(R.id.tv_shop_name);
            convertView.setTag(gholder);
        } else
        {
            gholder = (GroupHolder) convertView.getTag();
        }

        if (OrderDetailItemBeans != null)
        {
            gholder.tv_group_name.setText(OrderDetailItemBeans.getShopName());
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
            convertView = View.inflate(context, R.layout.item_orderdetail_child, null);
            cholder.ll_shop_seal_info = (LinearLayout) convertView.findViewById(R.id.ll_shop_seal_info);
            cholder.rl_cost = (RelativeLayout) convertView.findViewById(R.id.rl_cost);//实付费用
            cholder.iv_adapter_list_pic = (ImageView) convertView.findViewById(R.id.iv_adapter_list_pic);
            cholder.tv_choose_parameter = (TextView) convertView.findViewById(R.id.tv_choose_parameter);
            cholder.tv_child_total_price = (TextView) convertView.findViewById(R.id.tv_child_total_price);
            cholder.tv_child_number = (TextView) convertView.findViewById(R.id.tv_child_number);
            cholder.tv_number = (TextView) convertView.findViewById(R.id.tv_number);
            cholder.tv_intro = (TextView) convertView.findViewById(R.id.tv_intro);
            cholder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);

            cholder.ll_unpaid = (LinearLayout) convertView.findViewById(R.id.ll_unpaid);
            cholder.tv_payfor = (TextView) convertView.findViewById(R.id.tv_payfor);//找人代付
            cholder.tv_payfor.setOnClickListener(this);
            cholder.tv_cancel_order = (TextView) convertView.findViewById(R.id.tv_cancel_order);//取消订单

            cholder.tv_payment = (TextView) convertView.findViewById(R.id.tv_payment);//订单立即支付

            cholder.tv_remind_delivery = (TextView) convertView.findViewById(R.id.tv_remind_delivery);//提醒发货

            cholder.ll_unReceipt = (LinearLayout) convertView.findViewById(R.id.ll_unReceipt);
            cholder.tv_longtime_deliver = (TextView) convertView.findViewById(R.id.tv_longtime_deliver);//延长收货
            cholder.tv_applyfor_refund = (TextView) convertView.findViewById(R.id.tv_applyfor_refund);//延长收货
            cholder.tv_tracking_Logistics = (TextView) convertView.findViewById(R.id.tv_tracking_Logistics);//物流追踪
            cholder.tv_confirm_receipt = (TextView) convertView.findViewById(R.id.tv_confirm_receipt);

            cholder.ll_unEvaluation = (LinearLayout) convertView.findViewById(R.id.ll_unEvaluation);
            cholder.tv_delete_order = (TextView) convertView.findViewById(R.id.tv_delete_order);
            cholder.tv_tracking_Logistics_tow = (TextView) convertView.findViewById(R.id.tv_tracking_Logistics_tow);//查看物流
            cholder.tv_goto_evaluate = (TextView) convertView.findViewById(R.id.tv_goto_evaluate);//我要评价
            cholder.tv_additional_comments = (TextView) convertView.findViewById(R.id.tv_additional_comments);//我要评价

            convertView.setTag(cholder);
        } else
        {
            // convertView = childrenMap.get(groupPosition);
            cholder = (ChildHolder) convertView.getTag();
        }
//		final CartItemBean product =groups.get(groupPosition).getProductDetail().get(childPosition);
        final OrderProObjBean product = (OrderProObjBean) getChild(groupPosition, childPosition);
        double totalPrice=0;
        for (OrderProObjBean allOrderBeans:OrderDetailItemBeans.getOrderProObj()){
            totalPrice=totalPrice+allOrderBeans.getMemberPrice()*allOrderBeans.getCount();
        }
        if(OrderDetailItemBeans.getStatusID()==2){//退款订单特殊处理，每个item单独退款退货
            cholder.ll_shop_seal_info.setVisibility(View.VISIBLE);
            cholder.tv_confirm_receipt.setVisibility(View.GONE);
            cholder.tv_tracking_Logistics.setVisibility(View.GONE);

            if (childPosition==OrderDetailItemBeans.getOrderProObj().size()-1){

                cholder.rl_cost.setVisibility(View.VISIBLE);
                cholder.tv_child_total_price.setText(SpUtils.doubleToString(totalPrice));
//            cholder.tv_child_number.setText(String.valueOf(totalNumber));

            }else{
//                cholder.ll_shop_seal_info.setVisibility(View.GONE);
                cholder.rl_cost.setVisibility(View.GONE);
            }

        }else{

            if (childPosition==OrderDetailItemBeans.getOrderProObj().size()-1){
                cholder.ll_shop_seal_info.setVisibility(View.VISIBLE);
                cholder.rl_cost.setVisibility(View.VISIBLE);
                cholder.tv_child_total_price.setText(SpUtils.doubleToString(totalPrice));
//            cholder.tv_child_number.setText(String.valueOf(totalNumber));

            }else{
                cholder.ll_shop_seal_info.setVisibility(View.GONE);
            }
        }


        if (product != null){
            Picasso.with(context)
                    .load(AppConstants.PhOTOADDRESS+product.getSrc())
                    .resize(200,200)
                    .centerCrop()
                    .config(Bitmap.Config.RGB_565)
                    .placeholder(R.mipmap.img_start_loading)
                    .error(R.mipmap.img_load_error)
                    .into(cholder.iv_adapter_list_pic);//加载网络图片

            if(!product.getPropertysText().equals("")){
                cholder.tv_choose_parameter.setText(product.getPropertysText());
            }else{
                cholder.tv_choose_parameter.setVisibility(View.GONE);
            }
            cholder.tv_intro.setText(product.getProductName());
            cholder.tv_price.setText("¥" + SpUtils.doubleToString(product.getMemberPrice()));
            cholder.tv_number.setText("X"+product.getCount() + "");

            cholder.iv_adapter_list_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context,ProductDetailActivity.class);
                    intent.putExtra("prod_id",product.getProductID());
                    context.startActivity(intent);
                }
            });

            //判断订单状态
            switch(OrderDetailItemBeans.getStatusID()){
                case 0:
                    cholder.ll_unpaid.setVisibility(View.VISIBLE);
                    cholder.tv_remind_delivery.setVisibility(View.GONE);
                    cholder.ll_unReceipt.setVisibility(View.GONE);
                    cholder.ll_unEvaluation.setVisibility(View.GONE);
                    break;
                case 1:
                    cholder.ll_unpaid.setVisibility(View.GONE);
                    cholder.tv_remind_delivery.setVisibility(View.VISIBLE);
                    cholder.ll_unReceipt.setVisibility(View.VISIBLE);
                    cholder.tv_tracking_Logistics.setVisibility(View.GONE);
                    cholder.tv_confirm_receipt.setVisibility(View.GONE);
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

                    cholder.tv_additional_comments.setVisibility(View.GONE);
                    cholder.tv_goto_evaluate.setVisibility(View.GONE);
                    cholder.tv_tracking_Logistics_tow.setVisibility(View.VISIBLE);
                    cholder.tv_delete_order.setVisibility(View.GONE);
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
                case -3:
                    cholder.ll_unEvaluation.setVisibility(View.VISIBLE);
                    cholder.ll_unpaid.setVisibility(View.GONE);
                    cholder.tv_remind_delivery.setVisibility(View.GONE);
                    cholder.ll_unReceipt.setVisibility(View.GONE);
                    cholder.tv_goto_evaluate.setVisibility(View.GONE);
                    cholder.tv_additional_comments.setVisibility(View.GONE);
                    cholder.tv_tracking_Logistics_tow.setVisibility(View.GONE);
                    break;
            }
            cholder.tv_cancel_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteInterface.delete(groupPosition, childPosition);// 暴露组选接口
                }
            });
            final double finalTotalPrice = totalPrice;
            cholder.tv_payment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context,PaymenyActivity.class);
                    String price=SpUtils.doubleToString(finalTotalPrice);
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

            cholder.tv_applyfor_refund.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String orderNumber = product.getOrderNumber();
                    String username = AppConstants.USERNAME;
                    String icon = product.getSrc();
                    int OrdProID = product.getOrdProID();
                    String title =product.getProductName();
                    String param =product.getPropertysText();
                    Double price =product.getMemberPrice();
                    int ProductId = product.getProductID();
                    applyforRefund.doApplyforRefund(orderNumber, ProductId, username,icon,title,param,SpUtils.doubleToString(price),OrdProID);// 暴露组选接口

                }
            });

            cholder.tv_tracking_Logistics.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String orderNumber=product.getOrderNumber();
                    String url=product.getSrc();
                    int orderShopID=product.getShopID();
                    tackingLogistics.doTackingLogistics(orderNumber, orderShopID,url);// 暴露组选接口

                }
            });

            cholder.tv_confirm_receipt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String orderNumber = product.getOrderNumber();
                    confirmReceipt.doConfirmReceipt(orderNumber, groupPosition, null);
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

            cholder.tv_goto_evaluate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String orderNumber=product.getOrderNumber();
                    int orderShopID=product.getShopID();
                    gotoEvaluate.doGotoEvaluate(orderNumber, groupPosition,null);

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
                    tackingLogistics_tow.doTackingLogistics_tow(orderNumber, orderShopID,orderName,url);// 暴露组选接口

                }
            });

        }else{
            cholder.ll_shop_seal_info.setVisibility(View.GONE);
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
        RelativeLayout rl_cost;

        LinearLayout ll_unpaid;
        TextView tv_payfor;//朋友代付
        TextView tv_cancel_order;//取消订单
        TextView tv_payment;//立即付款
        //代发货
        TextView tv_remind_delivery;//提醒发货
        //待收货
        LinearLayout ll_unReceipt;
        TextView tv_longtime_deliver;//延长发货
        TextView tv_applyfor_refund;//申请退款
        TextView tv_tracking_Logistics;//物流跟踪
        TextView tv_confirm_receipt;//确认收货
        //待评价
        LinearLayout ll_unEvaluation;
        TextView tv_delete_order;//删除订单
        TextView tv_goto_evaluate;//我要评价
        TextView tv_additional_comments;//追加评价
        TextView tv_tracking_Logistics_tow;//查看物流

    }

    public interface TackingLogistics_tow
    {
        /**
         * 删减操作
         *
         * @param OrderNumber 订单号
         * @param ShopID	店铺ID
         * @param UserName	用户名
         */
        public void doTackingLogistics_tow(String OrderNumber, int ShopID,String UserName,String url);
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

    public interface TackingLogistics
    {
        /**
         * 删减操作
         *
         * @param OrderNumber 订单号
         * @param ShopID	店铺ID
         * @param UserName	用户名
         */
        public void doTackingLogistics(String OrderNumber, int ShopID,String UserName);
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

    public interface ConfirmReceipt {
        /**
         * 删减操作
         *
         * @param OrderNumber   订单号
         * @param groupPosition 店铺ID
         * @param UserName      用户名
         */
        public void doConfirmReceipt(String OrderNumber, int groupPosition, String UserName);
    }

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
         */
        public void delete(int groupPosition, int childPosition);

    }

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

    public interface ApplyforRefund {
        /**
         * 删减操作
         *
         * @param OrderNumber 订单号
         * @param UserName    用户名
         */
        public void doApplyforRefund(String OrderNumber, int ProductId, String UserName,String icon,String title,String param,String price,int OrdProID);
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