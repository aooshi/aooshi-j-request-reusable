package org.aooshi.j.bodyreusable;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


//@WebFilter(filterName = "bodyReusableFilter", urlPatterns = "/*")
public class BodyReusableFilter implements Filter {
	
	//20M allow
	private int maxRequestLength = 20 * 1024 * 1024;
	private boolean logRequest = false;
	
	private final Logger logger = LogManager.getLogger(BodyReusableFilter.class);
	
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
	        throws IOException, ServletException {
		
	    if (request instanceof HttpServletRequest) {
	    	
	    	HttpServletRequest req = (HttpServletRequest) request;
	    	HttpServletResponse resp = (HttpServletResponse) response;
	    	
	    	BodyReusableHttpServletRequestWrapper wrapper = new BodyReusableHttpServletRequestWrapper(req,resp);	    	
	    	
	    	byte[] data = wrapper.getBodyBytes();
	    	
	    	if (this.logRequest == true)
	    	{
	    		String bodyString = wrapper.getBodyString();
	    		String msg = "";
	    		msg += req.getMethod() + "\t";
	    		msg += req.getRequestURL() + "?" + req.getQueryString();
	    		msg += "\r\n";
	    		msg += bodyString;
	    		msg += "\r\n";
	    		
	    		//
	    		this.logger.info(msg);
	    	}
	    	
	    	if (data != null && data.length >  this.maxRequestLength)
	    	{
	    		resp.sendError(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
	    	}
	    	else
	    	{	    		
	    		chain.doFilter(wrapper, response);
	    	}
	    } else {
	        chain.doFilter(request, response);
	    }

	}

	@Override
	public void destroy() {
	}

	/**
	 * get max Request Length, default 20M
	 * @return
	 */
	public int getMaxRequestLength() {
		return maxRequestLength;
	}

	/**
	 * set max Request Length, default 20M
	 * @param maxRequestLength
	 */
	public void setMaxRequestLength(int maxRequestLength) {
		this.maxRequestLength = maxRequestLength;
	}

	/**
	 * http://logging.apache.org/log4j/2.x/manual/configuration.html
	 * @return
	 */
	public boolean isLogRequest() {
		return logRequest;
	}

	/**
	 * http://logging.apache.org/log4j/2.x/manual/configuration.html
	 * @param logRequest
	 */
	public void setLogRequest(boolean logRequest) {
		this.logRequest = logRequest;
	}
	
}
