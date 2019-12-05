package com.future.saf.sample.allinone.service.impl;

import com.future.saf.sample.allinone.manager.ShopManager;
import com.future.saf.sample.allinone.manager.UserManager;
import com.future.saf.sample.allinone.model.ShopModel;
import com.future.saf.sample.allinone.model.UserModel;
import com.future.saf.sample.allinone.remote.dto.ShopDTO;
import com.future.saf.sample.allinone.remote.dto.ShopDetailDTO;
import com.future.saf.sample.allinone.remote.dto.UserDTO;
import com.future.saf.sample.allinone.service.ShopService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShopServiceImpl implements ShopService {

	@Autowired
	private ShopManager shopManager;

	@Autowired
	private UserManager userManager;

	@Override
	public ShopDTO getShop(Long shopId) {

		if (shopId == null) {
			return null;
		}

		ShopModel shopModel = shopManager.findById(shopId);

		ShopDTO shopDTO = null;
		if (shopModel != null) {
			shopDTO = new ShopDTO();
			shopDTO.setId(shopModel.getId());
			shopDTO.setName(shopModel.getName());
			shopDTO.setAddress(shopModel.getAddress());
			shopDTO.setOwnerId(shopModel.getOwnerId());
		}

		return shopDTO;
	}

	@Override
	public ShopDetailDTO getShopDetail(Long shopId) {

		if (shopId == null) {
			return null;
		}

		ShopModel shopModel = shopManager.findById(shopId);

		ShopDetailDTO shopDTO = null;
		if (shopModel != null) {
			shopDTO = new ShopDetailDTO();
			shopDTO.setId(shopModel.getId());
			shopDTO.setName(shopModel.getName());
			shopDTO.setOwnerId(shopModel.getOwnerId());
			shopDTO.setAddress(shopModel.getAddress());

			if (shopModel.getOwnerId() != null) {
				UserModel userModel = userManager.findById(shopModel.getOwnerId());
				if (userModel != null) {
					UserDTO userDTO = new UserDTO();
					userDTO.setId(userModel.getId());
					userDTO.setName(userModel.getName());

					shopDTO.setOwner(userDTO);
				}
			}
		}

		return shopDTO;

	}

}
