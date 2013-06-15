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
 * When the discover of the net were not possible
 * it throws an {@link NetDiscoverException}
 * 
 * @author Felipe Santos
 * @email felchs@gmail.com
 */
public class NetDiscoverException extends Exception
{
	/**
	 * Default static serial UID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The ip where the exception occurred
	 */
	private final String ip;
	
	/**
	 * The port from where the exception occurred
	 */
	private final int portFrom;
	
	/**
	 * The port to where the exception occurred
	 */
	private final int portTo;
	
	/**
	 * Constructor passing parameters
	 * 
	 * @param ip the ip where the exception occurred
	 * @param portFrom the port from where the exception occurred
	 * @param portTo the port to where the exception occurred
	 */
	public NetDiscoverException(String ip, int portFrom, int portTo)
	{
		this.ip = ip;
		this.portFrom = portFrom;
		this.portTo = portTo;
	}

	/**
	 * Gets the {@link NetDiscoverException} string message
	 * return the {@link NetDiscoverException} string message
	 */
	@Override
	public String getMessage()
	{
		return "The disover was unble to stablish connection from ip: " + ip + " from port: " + portFrom + ", to: " + portTo + "."; 
	}
}
