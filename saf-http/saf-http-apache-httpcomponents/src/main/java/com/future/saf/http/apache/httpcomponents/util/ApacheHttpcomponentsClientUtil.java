package com.future.saf.http.apache.httpcomponents.util;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.util.Args;

import java.net.URI;

public class ApacheHttpcomponentsClientUtil {

	public static String determineTarget(final HttpUriRequest request) {

		Args.notNull(request, "HTTP request");

		// A null target may be acceptable if there is a default target.
		// Otherwise, the null target is detected in the director.
		HttpHost target = null;
		final URI requestURI = request.getURI();

		if (requestURI.isAbsolute()) {
			target = URIUtils.extractHost(requestURI);
			if (target == null) {
				return "unKnown";
			}
		}
		return target.getHostName();
	}
}
