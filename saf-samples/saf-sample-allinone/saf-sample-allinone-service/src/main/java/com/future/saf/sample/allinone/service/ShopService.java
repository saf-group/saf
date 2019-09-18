package com.future.saf.sample.allinone.service;

import com.future.saf.sample.allinone.remote.dto.ShopDTO;
import com.future.saf.sample.allinone.remote.dto.ShopDetailDTO;

public interface ShopService {

	public ShopDTO getShop(Long shopId);

	public ShopDetailDTO getShopDetail(Long shopId);
}
