package com.future.saf.sample.allinone.constant;

public class KVKeyConstant {

	// **********(1).user KEY**********//

	/**
	 * 根据用户id获取用户对象
	 */
	public static final String KEY_USER = "user:uid:%d";

	// **********(1).shop KEY**********//

	/**
	 * 根据shopId获取商店对象
	 */
	public static final String KEY_SHOP_MODEL = "shop:sid:%d";

	/**
	 * 根据userId获取用户的shopList
	 */
	public static final String KEY_USER_SHOPLIST = "user:shoplist:%d";

	public static String getShopModelKey(Long shopId) {
		return String.format(KVKeyConstant.KEY_SHOP_MODEL, shopId);
	}
}
