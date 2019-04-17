package org.aooshi.j.bodyreusable;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

public class BodyReusableServletInputStream extends ServletInputStream {
	
	private final ByteArrayInputStream byteArrayInputStream;
	private final byte[] bodyBytes;
	
	public BodyReusableServletInputStream(byte[] data) {
		super();
		
		this.bodyBytes = data;		
		byteArrayInputStream = new ByteArrayInputStream(data);
	}

	/**
	 * @return the bodyBytes
	 */
	public byte[] getBodyBytes() {
		return bodyBytes;
	}

	@Override
	public boolean isFinished() {
    	int available = byteArrayInputStream.available();
        return available == 0;
	}

	@Override
	public boolean isReady() {
		return false;
	}

	@Override
	public void setReadListener(ReadListener listener) {

	}

	@Override
	public int read() throws IOException {
        return byteArrayInputStream.read();
	}
	

}
