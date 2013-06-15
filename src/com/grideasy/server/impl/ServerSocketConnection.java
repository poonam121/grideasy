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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Handles the socket connection
 * 
 * @author Felipe Santos
 * @email felchs@gmail.com
 */
public class ServerSocketConnection
{	
	/**
	 * The client socket used on local client intances 
	 */
	private Socket clientSocket;
	
	/**
	 * The client DataOuputStream 
	 */
	private DataOutputStream dataOutputStream;
	
	/**
	 * The client DataInputStream
	 */
	private DataInputStream dataInputStream;

	/**
	 * Initializes the {@link ServerSocket} connection
	 */
	public void accept()
	{
		try
		{
			ServerSocket serverSocket = ServerSocketInstance.getServerSocket();
			clientSocket = serverSocket.accept();
			ServerHostProperties.getInstance().onClientConnected();
		} 
		catch (IOException e)
		{		
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets whether the client socket is connected or not
	 * @return
	 */
	public boolean isConnected()
	{
		return clientSocket != null;
	}

	/**
	 * Gets the client's DataOuputStream
	 * @return the client DataOuputStream
	 * @throws IOException
	 */
	public DataOutputStream getClientOuputStream() throws IOException
	{
		if (dataOutputStream != null)
		{
			return dataOutputStream;
		}
		dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
		
		return dataOutputStream;
	}
	
	/**
	 * Gets the client's DataInputStream
	 * @return the client's DataInputStream
	 * @throws IOException
	 */
	public InputStream getClientInputStream() throws IOException
	{
		if (dataInputStream != null)
		{
			return dataInputStream;
		}
		
		dataInputStream = new DataInputStream(clientSocket.getInputStream());
		
		return dataInputStream;
	}

	/**
	 * Close all connections to this socket 
	 * @throws IOException
	 */
	public void closeAllConnections() throws IOException
	{
		getClientInputStream().close();
		getClientOuputStream().close();
		clientSocket.close();
	}
}
