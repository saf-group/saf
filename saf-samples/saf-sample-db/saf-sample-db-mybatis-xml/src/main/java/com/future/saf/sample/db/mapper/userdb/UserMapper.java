package com.future.saf.sample.db.mapper.userdb;

import org.apache.ibatis.annotations.Select;

import com.future.saf.sample.db.mapper.userdb.model.UserModel;

public interface UserMapper {

	@Select("select id, name from user where id=#{userId}")
	UserModel findById(long userId);
}