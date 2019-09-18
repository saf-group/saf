package com.future.saf.sample.allinone.remote.dto;

import java.io.Serializable;
import java.util.List;

import lombok.*;

@ToString(callSuper = true)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailDTO extends UserDTO implements Serializable {

	private static final long serialVersionUID = 5535756618301633282L;

	private List<ShopDTO> shopList;

}
