package org.aooshi.j.bodyreusable;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;


public class BodyReusableHttpServletRequestWrapper extends HttpServletRequestWrapper {
	
    private final byte[] bodyBytes;
    private final int bodyLength;

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    
    private ServletInputStream inputStream;
    private BufferedReader bufferedReader;
        

	public BodyReusableHttpServletRequestWrapper(HttpServletRequest request, HttpServletResponse response) throws IOException {
		super(request);
		
		this.bodyLength = request.getContentLength();
		
		//
		ServletInputStream inputStream2 = request.getInputStream();
//		int available = inputStream2.available();
//		boolean finished = inputStream2.isFinished();
//		boolean ready = inputStream2.isReady();
		this.bodyBytes = this.readBytes(inputStream2);
		
		//
		if (this.bodyBytes == null || this.bodyBytes.length != this.bodyLength)
		{
			throw new IOException("read content body failure.");
		}
		
		//
		this.request = request;
		this.response = response;
		
		//
		this.resetInputStream();
	}
	
	private byte[] readBytes(final InputStream input) throws IOException
	{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer2 = new byte[1024];
        byte[] buffer = null;
        int len;
        
        try
        {
	        while ((len = input.read(buffer2)) > 0) {
	            baos.write(buffer2, 0, len);
	        }
	        
	        buffer = baos.toByteArray();
        }
        finally
        {
        	try {
				baos.close();
			} catch (IOException e) {
			}
        }
        
        return buffer;
	}
	

	/**
	 * @return the bodyBytes
	 */
	public byte[] getBodyBytes() {
		return bodyBytes;
	}

	/**
	 * @return the request
	 */
	public HttpServletRequest getRequest() {
		return request;
	}

	/**
	 * @return the response
	 */
	public HttpServletResponse getResponse() {
		return response;
	}
	
	/**
	 * reset input stream to available
	 */
	public synchronized void resetInputStream()
	{
		this.inputStream = new BodyReusableServletInputStream(this.bodyBytes);
		this.bufferedReader = new BufferedReader(new InputStreamReader(this.inputStream)); 
	}

	@Override
    public BufferedReader getReader() throws IOException {
        return this.bufferedReader;
    }
	
	@Override
    public ServletInputStream getInputStream() throws IOException {
		return this.inputStream;
    }
	
	private String bodyString = null;

	/**
	 * get body to string
	 * @return
	 */
	public String getBodyString()
	{
		String charsetName = this.request.getCharacterEncoding();
		if (charsetName == null || charsetName == "")
			charsetName = "UTF-8";
		
		return this.getBodyString(charsetName);
	}
	
	/**
	 * get body to string and charset
	 * @param charsetName
	 * @return
	 */
	public synchronized String getBodyString(String charsetName)
	{
		if (this.bodyString == null)
		{
			try {
				this.bodyString = new String(this.bodyBytes, charsetName);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		return this.bodyString;
	}

}
