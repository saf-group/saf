package com.future.saf.sample.allinone.mapper.userdb;

import org.apache.ibatis.annotations.Select;

import com.future.saf.sample.allinone.model.UserModel;

public interface UserMapper {

	@Select("select id, name from user where id=#{userId}")
	UserModel findById(long userId);
}