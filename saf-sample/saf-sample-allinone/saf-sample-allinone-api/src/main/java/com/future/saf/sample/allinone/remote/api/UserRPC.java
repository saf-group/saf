package com.future.saf.sample.allinone.remote.api;

import com.future.saf.sample.allinone.remote.dto.UserDTO;
import com.future.saf.sample.allinone.remote.dto.UserDetailDTO;

public interface UserRPC {

	/**
	 * 返回包含用户商铺信息的用户肖像
	 * 
	 * @param userId
	 * @return
	 */
	public UserDetailDTO getUserDetail(Long userId);

	/**
	 * 返回用户的基本肖像信息
	 * 
	 * @param userId
	 * @return
	 */
	public UserDTO getUser(Long userId);
}
