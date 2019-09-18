package com.future.saf.sample.allinone.remote.api;

import com.future.saf.sample.allinone.remote.dto.ShopDTO;
import com.future.saf.sample.allinone.remote.dto.ShopDetailDTO;

public interface ShopRPC {

	/**
	 * 获取店铺基本信息
	 * 
	 * @param shopId
	 * @return
	 */
	public ShopDTO getShop(Long shopId);

	/**
	 * 获取店铺详细信息
	 * 
	 * @param shopId
	 * @return
	 */
	public ShopDetailDTO getShopDetail(Long shopId);
}
