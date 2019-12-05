package com.future.saf.sample.allinone.remote.dto;

import lombok.*;

import java.io.Serializable;

@ToString
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShopDTO implements Serializable {

	private static final long serialVersionUID = 6913503345260980615L;
	private Long id;
	private String name;
	private Long ownerId;
	private String address;

}
