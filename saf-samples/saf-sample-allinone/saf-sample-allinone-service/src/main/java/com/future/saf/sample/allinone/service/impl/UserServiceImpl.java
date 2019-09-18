package com.future.saf.sample.allinone.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.future.saf.sample.allinone.manager.ShopManager;
import com.future.saf.sample.allinone.manager.UserManager;
import com.future.saf.sample.allinone.model.ShopModel;
import com.future.saf.sample.allinone.model.UserModel;
import com.future.saf.sample.allinone.remote.dto.ShopDTO;
import com.future.saf.sample.allinone.remote.dto.UserDTO;
import com.future.saf.sample.allinone.remote.dto.UserDetailDTO;
import com.future.saf.sample.allinone.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserManager userManager;

	@Autowired
	private ShopManager shopManager;

	@Override
	public UserDTO getUser(Long userId) {
		if (userId == null) {
			return null;
		}

		UserModel userModel = userManager.findById(userId);

		UserDTO userDTO = null;
		if (userModel != null) {
			userDTO = new UserDTO();
			userDTO.setId(userModel.getId());
			userDTO.setName(userModel.getName());
		}

		return userDTO;
	}

	@Override
	public UserDetailDTO getUserDetail(Long userId) {
		if (userId == null) {
			return null;
		}

		UserModel userModel = userManager.findById(userId);

		UserDetailDTO userDTO = null;
		if (userModel != null) {
			userDTO = new UserDetailDTO();
			userDTO.setId(userModel.getId());
			userDTO.setName(userModel.getName());

			List<ShopModel> shopModelList = shopManager.findShopListByOwnerId(userId);
			if (!CollectionUtils.isEmpty(shopModelList)) {
				List<ShopDTO> shopDTOList = new ArrayList<ShopDTO>();
				ShopDTO dto = null;
				for (ShopModel sm : shopModelList) {
					dto = new ShopDTO();
					dto.setId(sm.getId());
					dto.setName(sm.getName());
					dto.setAddress(sm.getAddress());
					dto.setOwnerId(sm.getOwnerId());
					shopDTOList.add(dto);
				}
				userDTO.setShopList(shopDTOList);
			}
		}

		return userDTO;
	}

}
