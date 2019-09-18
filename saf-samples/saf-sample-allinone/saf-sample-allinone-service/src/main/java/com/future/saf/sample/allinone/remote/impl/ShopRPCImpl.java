package com.future.saf.sample.allinone.remote.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import com.future.saf.sample.allinone.remote.api.ShopRPC;
import com.future.saf.sample.allinone.remote.dto.ShopDTO;
import com.future.saf.sample.allinone.remote.dto.ShopDetailDTO;
import com.future.saf.sample.allinone.service.ShopService;
import com.weibo.api.motan.config.springsupport.annotation.MotanService;

@MotanService(basicService = "mallBasicServiceConfigBean")
@Slf4j
public class ShopRPCImpl implements ShopRPC {

	@Autowired
	private ShopService shopService;

	@Override
	public ShopDTO getShop(Long shopId) {
		// TODO 这里还有可能调用其他的RPC完成一些逻辑，同时也有可能添加user关联信息，然后组装DTO。
		ShopDTO shop = shopService.getShop(shopId);
		log.info("{} - {} - {}", this.getClass(), "getShop()", shop);
		return shop;
	}

	@Override
	public ShopDetailDTO getShopDetail(Long shopId) {
		// TODO 这里还有可能调用其他的RPC完成一些逻辑
		ShopDetailDTO shopDetail = shopService.getShopDetail(shopId);
		log.info("{} - {} - {}", this.getClass(), "getShopDetail()", shopDetail);
		return shopDetail;
	}

}
