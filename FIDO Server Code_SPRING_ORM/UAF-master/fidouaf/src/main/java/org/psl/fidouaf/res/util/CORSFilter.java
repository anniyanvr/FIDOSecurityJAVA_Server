package org.psl.fidouaf.res.util;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

public class CORSFilter implements ContainerResponseFilter {

	@Override
	public ContainerResponse filter(ContainerRequest creq,
			ContainerResponse cresp) {
		ResponseBuilder responseFromAPI = Response.fromResponse(cresp
				.getResponse());
		// cresp.getHttpHeaders().putSingle("Access-Control-Allow-Origin", "*");
		// cresp.getHttpHeaders().putSingle("Access-Control-Allow-Credentials",
		// "true");
		// cresp.getHttpHeaders().putSingle("Access-Control-Allow-Methods",
		// "GET, POST, DELETE, PUT, OPTIONS, HEAD");
		// cresp.getHttpHeaders().putSingle("Access-Control-Allow-Headers",
		// "Content-Type, Accept, X-Requested-With, Authorization, Origin");

		responseFromAPI
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS, HEAD")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With, Authorization, Origin");

		
		String responseRequestHeader = creq.getHeaderValue("Access-Control-Request-Headers");
		 
        if (null != responseRequestHeader && !responseRequestHeader.equals(null)) {
        	responseFromAPI.header("Access-Control-Allow-Headers", responseRequestHeader);
        }
 
        cresp.setResponse(responseFromAPI.build());
        return cresp;
	}
}
