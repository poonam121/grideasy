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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class has the server info about nodes, host and ports
 * 
 * @author Felipe Santos
 * @email felchs@gmail.com
 */
public class ClientHostProperties
{
	/**
	 * The server port which the client connect to
	 */
	public int port;
	
	/**
	 * The ip server port which the client connect to
	 */
	public String ip;
	
	/**
	 * Constructor that initializes the configurations of
	 * this client. By default the configuration file is
	 * in the root and set on the file config/gridclientmapproperties
	 */
	public ClientHostProperties()
	{
		Properties prop = new Properties();
		try
		{
			InputStream in = new FileInputStream("config/gridclientmapproperties");
			prop.load(in);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		port = Integer.parseInt(prop.getProperty("port"));
		ip = prop.getProperty("server_ip");
	}
}