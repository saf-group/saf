package com.future.saf.sample.allinone.remote.dto;

import lombok.*;

@ToString
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SMSConfigDTO {

	private String aliyunSMSUrl;

	private String mobileSMSUrl;

	private String unicomSMSUrl;

}
