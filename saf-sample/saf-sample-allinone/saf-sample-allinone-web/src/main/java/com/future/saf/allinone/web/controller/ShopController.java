package com.future.saf.allinone.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.future.saf.core.web.WebResult;
import com.future.saf.sample.allinone.remote.api.ShopRPC;
import com.future.saf.sample.allinone.remote.dto.ShopDTO;
import com.future.saf.sample.allinone.remote.dto.ShopDetailDTO;
import com.weibo.api.motan.config.springsupport.annotation.MotanReferer;

@RestController
@RequestMapping(value = "/shop")
public class ShopController {

	@MotanReferer(basicReferer = "mallBasicRefererConfigBean")
	private ShopRPC shoprpc;

	@RequestMapping("/getShop")
	public WebResult<ShopDTO> getShop(@RequestParam(name = "shopId") Long shopId) {
		ShopDTO shopDTO = shoprpc.getShop(shopId);
		return new WebResult<>(WebResult.CODE_SUCCESS, "getShop success.", shopDTO);
	}

	@RequestMapping("/getShopDetail")
	public WebResult<ShopDetailDTO> getShopDetail(@RequestParam(name = "shopId") Long shopId) {
		ShopDetailDTO shopDetailDTO = shoprpc.getShopDetail(shopId);
		return new WebResult<>(WebResult.CODE_SUCCESS, "getShopDetail success.", shopDetailDTO);
	}
}
