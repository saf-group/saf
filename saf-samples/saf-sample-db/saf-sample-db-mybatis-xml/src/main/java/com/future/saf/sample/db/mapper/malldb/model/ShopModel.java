package com.future.saf.sample.db.mapper.malldb.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ShopModel {
	private Long id;
	private String name;
	private Long ownerId;
	private String address;
}
