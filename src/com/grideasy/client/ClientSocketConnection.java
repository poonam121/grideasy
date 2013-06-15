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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * This class holds a {@link Socket} connection
 * between GridClient and GridServer
 */
public class ClientSocketConnection
{
	/**
	 * The server host which this {@link Socket} connects to
	 */
	private String host;
	
	/**
	 * The server port which this {@link Socket} connects to
	 */
	private int port;
	
	/**
	 * The {@link Socket} to connection
	 */
	private Socket socket;
	
	/**
	 * Responsible by the execution of given classes into a new class loader
	 */
	private ClientProcessExecuter executer;
	
	/**
	 * Constructor passing fields
	 * @param host the host of the server
	 * @param port the port of the server
	 * @param executer the executer of the classes
	 */
	public ClientSocketConnection(String host, int port, ClientProcessExecuter executer)
	{
		this.host = host;
		this.port = port;
		this.executer = executer;
	}
	
	/**
	 * Gets whether this client is connected to the server or not
	 * @return
	 */
	public boolean isConnected()
	{
		return socket != null && socket.isConnected();
	}

	/**
	 * Runs the program via client socket connection and initializes the
	 * program executing the classes into a new {@link ClassLoader}
	 * 
	 * @throws IOException
	 */
	public void run() throws IOException
	{
		try
		{
			socket = new Socket(host, port);

			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream os = new DataOutputStream(socket.getOutputStream());
			
			executer.execute(in, os, socket);

			socket.close();
			
			System.out.println("Client finished.");
		} 
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			System.out.println("Trying to connect on host: " + host + ":" + port);

			try
			{
				Thread.sleep(5000);
			} 
			catch (InterruptedException e1)
			{
				e1.printStackTrace();
			}
		}
	}
}
