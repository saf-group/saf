package com.future.saf.sample.allinone.remote.dto;

import java.io.Serializable;

import lombok.*;

@ToString(callSuper = true)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShopDetailDTO extends ShopDTO implements Serializable {

	private static final long serialVersionUID = -3861349179076712826L;

	private UserDTO owner;

}
