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
package com.grideasy.server.impl;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Handles Singleton instance of {@link ServerSocket}
 * 
 * @author Felipe Santos
 * @email felchs@gmail.com
 * 
 */
public class ServerSocketInstance
{
	/**
	 * Singleton instance of {@link ServerSocket}
	 */
	private static ServerSocket serverSocket;
	
	/**
	 * Gets the singleton instance of {@link ServerSocket}
	 * @return the singleton instance of {@link ServerSocket}
	 */
	public synchronized static ServerSocket getServerSocket()
	{
		if (serverSocket == null)
		{
			int port = ServerHostProperties.getInstance().getPort();
			try
			{
				serverSocket = new ServerSocket(port);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return serverSocket;
	}
}
