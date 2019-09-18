package com.future.saf.sample.allinone.mapper.malldb;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.future.saf.sample.allinone.model.ShopModel;

import java.util.List;

public interface ShopMapper {

	static final String COLUMNS = "id, name, owner_id, address";
	
	@Select("select "+ COLUMNS +" from shop where id=#{shopId}")
	ShopModel findById(Long shopId);

	// 实际这里必须有数量限制
	@Select("select " + COLUMNS + " from shop where owner_id=#{ownerUserId}")
	List<ShopModel> queryShopModelListByOwnerId(Long ownerUserId);

	@Select("<script>" 
			+ "SELECT " + COLUMNS + " FROM shop WHERE id IN "
				+ "<foreach item='item' index='index' collection='shopIdList' open='(' separator=',' close=')'>" + "#{item}"
				+ "</foreach>" 
			+ "</script>")
	List<ShopModel> queryShopModelListByShopIdList(@Param("shopIdList") List<Long> shopIdList);
}
