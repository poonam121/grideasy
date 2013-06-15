/*
 * This source file is part of Grideasy
 * For the latest info, see https://code.google.com/p/grideasy/
 * 
 * Grideasy is free software: you can redistribute it
 * and/or modify it under the terms of the MIT License.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.grideasy.client;

/**
 * This class serves as a net discovering of the
 * hosts and ports from client to server
 * 
 * @author Felipe Santos
 * @email felch@gmail.com
 */
public class NetDiscover
{
	/**
	 * The server ip discovered
	 */
	private String ip;
	
	/**
	 * The server port discovered
	 */
	private int port;
	
	/**
	 * The 'port from' to search on the net
	 */
	private int portFrom;
	
	/**
	 * The 'port to' to search on the net
	 */
	@SuppressWarnings("unused")
	private int portTo;
	
	public NetDiscover(String ip, int portFrom, int portTo) throws NetDiscoverException
	{
		this.ip = ip;
		this.portFrom = portFrom;
		this.portTo = portTo;
		
		discover();
	}

	/**
	 * Not implemented.
	 * TODO: to implement the discovery
	 * 
	 * @throws NetDiscoverException
	 */
	public void discover() throws NetDiscoverException
	{
		this.port = portFrom;
	}
	
	/**
	 * Gets the server ip
	 * @return the server ip
	 */
	public String getIp()
	{
		return ip;
	}
	
	/**
	 * Gets the server port ip
	 * @return the server port ip
	 */
	public int getPort()
	{
		return port;
	}
}