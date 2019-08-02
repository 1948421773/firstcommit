package com.musicdo.musicshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
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

public class ShopcartExpandableListViewAdapter extends BaseExpandableListAdapter implements OnClickListener {
	private List<CartBean> groups=new ArrayList<>();
	private SpecBean specBeans=new SpecBean();
	private List<CartItemBean> children=new ArrayList<>();
	private Context context;
	//HashMap<Integer, View> groupMap = new HashMap<Integer, View>();
	//HashMap<Integer, View> childrenMap = new HashMap<Integer, View>();
	private CheckInterface checkInterface;
	private CheckInterface itemcheckInterface;
	private ModifyCountInterface modifyCountInterface;
	private DeleteInterface deleteInterface;
	ArrayList<SpecItemBean> specItemBeans=new ArrayList<>();
	private long mLastClickTime = 0;
	public static final int TIME_INTERVAL = 1000;
	/**
	 * 构造函数
	 * 
	 * @param groups
	 *            组元素列表
	 * @param children
	 *            子元素列表
	 * @param context
	 */
	public ShopcartExpandableListViewAdapter(List<CartBean> groups, List<CartItemBean> children, Context context, ArrayList<SpecItemBean> specItemBeens)
	{
		super();
		this.groups = groups;
		this.children = children;
		this.context = context;
		this.specItemBeans = specItemBeens;
	}

	public void setCheckInterface(CheckInterface checkInterface)
	{
		this.checkInterface = checkInterface;
	}
	public void setDeteInterface(DeleteInterface deleteInterface)
	{
		this.deleteInterface = deleteInterface;
	}

	public void setModifyCountInterface(ModifyCountInterface modifyCountInterface)
	{
		this.modifyCountInterface = modifyCountInterface;
	}

	@Override
	public int getGroupCount()
	{
		return groups.size();
	}

	@Override
	public int getChildrenCount(int groupPosition)
	{
		int size=groups.get(groupPosition).getProductDetail().size();
		return size;
	}

	@Override
	public Object getGroup(int groupPosition)
	{
		return groups.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		if (groups.size()==0){
			return null;
		}else{
			return groups.get(groupPosition).getProductDetail().get(childPosition);
		}

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
			convertView = View.inflate(context, R.layout.item_shopcart_group, null);
			gholder.cb_check = (CheckBox) convertView.findViewById(R.id.determine_chekbox);
			gholder.tv_group_name = (TextView) convertView.findViewById(R.id.tv_shop_name);
			gholder.tv_cart_item_edit = (TextView) convertView.findViewById(R.id.tv_cart_item_edit);
			//groupMap.put(groupPosition, convertView);
			 convertView.setTag(gholder);
		} else
		{
			// convertView = groupMap.get(groupPosition);
			gholder = (GroupHolder) convertView.getTag();
		}
		if (groups.size()!=0&&groups.get(groupPosition).getProductDetail().size()!=0) {
			final CartBean group = groups.get(groupPosition);
			if (group != null) {
				gholder.tv_group_name.setText(group.getShopName());
				gholder.cb_check.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						group.setChoosed(((CheckBox) v).isChecked());
						checkInterface.checkGroup(groupPosition, ((CheckBox) v).isChecked());// 暴露组选接口
					}
				});
				gholder.tv_cart_item_edit.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (group.isEdited() == true) {
							group.setEdited(false);
//						gholder.tv_cart_item_edit.setText(R.string.shoppingcart_complete_edit);
							checkInterface.editGroup(groupPosition, false);// 暴露组编辑接口
						} else {
							group.setEdited(true);
//						gholder.tv_cart_item_edit.setText(R.string.shoppingcart_edit);
							checkInterface.editGroup(groupPosition, true);// 暴露组编辑接口
						}
					}
				});
				gholder.cb_check.setChecked(group.isChoosed());

				if (group.isAllEdited() == true) {
					gholder.tv_cart_item_edit.setVisibility(View.GONE);
				} else {
					if (group.isEdited() == true) {
						gholder.tv_cart_item_edit.setText(R.string.shoppingcart_complete_edit);
					} else {
						gholder.tv_cart_item_edit.setText(R.string.shoppingcart_edit);
					}
					gholder.tv_cart_item_edit.setVisibility(View.VISIBLE);
				}
			}
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
			convertView = View.inflate(context, R.layout.item_shopcart_product, null);
			cholder.iv_adapter_list_pic = (ImageView) convertView.findViewById(R.id.iv_adapter_list_pic);
			cholder.cb_check = (CheckBox) convertView.findViewById(R.id.check_box);
			cholder.iv_cart_parameter_arrow= (ImageView) convertView.findViewById(R.id.iv_cart_parameter_arrow);
			cholder.ll_cart_item_delete= (LinearLayout) convertView.findViewById(R.id.ll_cart_item_delete);
			cholder.ll_reduce_add= (LinearLayout) convertView.findViewById(R.id.ll_reduce_add);
			cholder.ll_choose_parameter= (LinearLayout) convertView.findViewById(R.id.ll_choose_parameter);//修改商品参数
			cholder.tv_choose_parameter = (TextView) convertView.findViewById(R.id.tv_choose_parameter);
			cholder.tv_number = (TextView) convertView.findViewById(R.id.tv_number);
			cholder.tv_intro = (TextView) convertView.findViewById(R.id.tv_intro);
			cholder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
			cholder.iv_increase = (TextView) convertView.findViewById(R.id.tv_add);
			cholder.iv_decrease = (TextView) convertView.findViewById(R.id.tv_reduce);
			cholder.tv_count = (TextView) convertView.findViewById(R.id.tv_num);
			// childrenMap.put(groupPosition, convertView);
			convertView.setTag(cholder);
		} else
		{
			// convertView = childrenMap.get(groupPosition);
			cholder = (ChildHolder) convertView.getTag();
		}
//		final CartItemBean product =groups.get(groupPosition).getProductDetail().get(childPosition);
		final CartItemBean product = (CartItemBean) getChild(groupPosition, childPosition);
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
				cholder.ll_choose_parameter.setVisibility(View.VISIBLE);
			}else{
				cholder.ll_choose_parameter.setVisibility(View.GONE);
			}
			cholder.tv_intro.setText(product.getName());
			cholder.tv_price.setText("¥" + product.getMemberPrice() + "");
			cholder.tv_count.setText(product.getCount() + "");
			cholder.tv_number.setText("X"+product.getCount() + "");
			cholder.cb_check.setChecked(product.isChoosed());
			if (product.isEdited()){
				cholder.tv_intro.setVisibility(View.GONE);
				cholder.iv_cart_parameter_arrow.setVisibility(View.VISIBLE);
				cholder.ll_cart_item_delete.setVisibility(View.VISIBLE);
				cholder.ll_reduce_add.setVisibility(View.VISIBLE);
				cholder.ll_choose_parameter.setBackgroundColor(context.getResources().getColor(R.color.white));
				cholder.tv_number.setVisibility(View.GONE);
				cholder.tv_price.setVisibility(View.GONE);
			}else{
				cholder.tv_intro.setVisibility(View.VISIBLE);
				cholder.iv_cart_parameter_arrow.setVisibility(View.GONE);
				cholder.ll_cart_item_delete.setVisibility(View.GONE);
				cholder.ll_reduce_add.setVisibility(View.GONE);
				cholder.ll_choose_parameter.setBackgroundColor(context.getResources().getColor(R.color.shoppingcart_list_item_bg));
				cholder.tv_number.setVisibility(View.VISIBLE);
				cholder.tv_price.setVisibility(View.VISIBLE);
			}
			cholder.cb_check.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					product.setChoosed(((CheckBox) v).isChecked());
					cholder.cb_check.setChecked(((CheckBox) v).isChecked());
					checkInterface.checkChild(groupPosition, childPosition, ((CheckBox) v).isChecked());// 暴露子选接口
				}
			});
			cholder.iv_increase.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					modifyCountInterface.doIncrease(groupPosition, childPosition, cholder.tv_count, cholder.cb_check.isChecked());// 暴露增加接口
				}
			});
			cholder.iv_decrease.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					modifyCountInterface.doDecrease(groupPosition, childPosition, cholder.tv_count, cholder.cb_check.isChecked());// 暴露删减接口
				}
			});
			cholder.ll_choose_parameter.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (System.currentTimeMillis() - mLastClickTime >= TIME_INTERVAL) {//避免重复点击
						mLastClickTime = System.currentTimeMillis();
					}else{
						return;
					}
					if (!product.isEdited()){
						return;//不是编辑状态不能点击
					}
					if (specItemBeans!=null){
						if (specItemBeans.size()!=0){
//							specItemBeans.addAll(specBeans.getData());
							Intent intent = new Intent(context, GoodsInfoSpecActivity.class);
							intent.putExtra("GUID", product.getGUID());
							intent.putExtra("Propertys", product.getPropertys());
							intent.putExtra("PropertysText", product.getPropertysText());
							intent.putExtra("ProductID", product.getProductID());
							intent.putExtra("price", product.getMemberPrice());
							intent.putExtra("Propertys", product.getPropertys());
							intent.putExtra("Count", product.getCount());
							intent.putParcelableArrayListExtra("specItemBeans",specItemBeans);
							intent.putExtra("shoppingCart","shoppingCart");
							context.startActivity(intent);
						}else{
							getSpecData(product);
						}
					}else{
						getSpecData(product);
					}

				}
			});
			cholder.iv_adapter_list_pic.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent=new Intent(context,ProductDetailActivity.class);
					intent.putExtra("prod_id",product.getProductID());
					context.startActivity(intent);
				}
			});
			cholder.ll_cart_item_delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					deleteInterface.delete(groupPosition, childPosition,cholder.cb_check.isChecked());// 暴露组选接口
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
		}
	}

	/**
	 * 组元素绑定器
	 * 
	 * 
	 */
	private class GroupHolder
	{
		CheckBox cb_check;
		TextView tv_group_name;
		TextView tv_cart_item_edit;
	}

	/**
	 * 子元素绑定器
	 * 
	 * 
	 */
	private class ChildHolder
	{
		CheckBox cb_check;
		ImageView iv_cart_parameter_arrow,iv_adapter_list_pic;
		LinearLayout ll_cart_item_delete;
		LinearLayout ll_reduce_add;
		LinearLayout ll_choose_parameter;
		TextView tv_product_name;
		TextView tv_choose_parameter;
		TextView tv_price;
		TextView iv_increase;
		TextView tv_count;
		TextView iv_decrease;
		TextView tv_intro;
		TextView tv_number;
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
