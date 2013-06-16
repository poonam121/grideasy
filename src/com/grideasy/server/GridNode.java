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
package com.grideasy.server;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.rmi.server.ServerCloneException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import com.grideasy.client.GridJob;
import com.grideasy.client.JobResult;
import com.grideasy.server.loader.JarBuilder;

/**
 * GridNode represents a instance or processing
 * Instances of GridNode receive one or more {@link GridJob} that do some processing
 * returning the results to the {@link GridServer}.
 * The nodes can be located locally or can be put in different machines allowing you
 * to distribute processing power.
 * The communications between {@link GridNode} and {@link GridServer} is
 * done using a socket connection.
 * The host and port configuration is found by default in 
 * the file /config/gridservermapproperties
 * The GridNode get all the the java code located in the {@link GridServer} and distribute
 * it to the {@link GridNode} initializing a new instance of {@link ClassLoader} executing
 * the java code externally to the {@link GridServer} instance. This means you can spread
 * your code between nodes/machines without the necessity of manually putting the code on
 * each instance node. For that you must add the package names which you want to distribute
 * using the method {@link GridNode}{@link #addPackageName(String)}
 *  
 * @author Felipe Santos
 * @email felchs@gmail.com
 */
public class GridNode
{
	/**
	 * The packages which the {@link GridServer} will pass trhough the {@link GridNode}
	 */
	private Set<String> packageNames = new HashSet<String>();
	
	/**
	 * Whether this {@link GridNode} instance is available for a new {@link GridJob} or not
	 */
	private boolean available = true;
	
	/**
	 * The {@link ServerSocketConnection} maintained by this instance 
	 */
	private ServerSocketConnection serverSocketConnection = new ServerSocketConnection();
	
	/**
	 * {@link ExecutorService} used to spread jobs 
	 */
	private ExecutorService executor;
	
	/**
	 * Whether this {@link GridNode} instance is local node to the {@link GridServer} instance or not
	 */
	private boolean localNode;
	
	/**
	 * Whether this {@link GridNode} instance of closed or not
	 */
	private boolean closed;

	/**
	 * Constructor passing params
	 *  
	 * @param executor {@link ExecutorService} for jobs processing
	 * @param localNode whether this node is local or not  
	 */
	public GridNode(ExecutorService executor, boolean localNode)
	{
		this.executor = executor;
		this.localNode = localNode;
	}
	
	/**
	 * The package name is used to distribute the code between nodes.
	 * In other words, the classes of a given package are delivered among
	 * nodes (which can be in located in different machines) making the work
	 * of code delivering easier once you program in just one machine, the
	 * {@link GridServer} machine, just spreading the code among {@link GridNode}
	 * 
	 * @param packageName the package name for code spreading
	 */
	public void addPackageName(String packageName)
	{
		packageNames.add(packageName);
	}
	
	/**
	 * Returns whether this {@link GridNode} is available for a new {@link GridJob} or not
	 * @param whether this {@link GridNode} is available for a new {@link GridJob} or not
	 */
	public synchronized void setAvailable(boolean available)
	{
		this.available = available;
	}
	
	/**
	 * Whether this {@link GridJob} is local to the {@link GridServer} instance or not
	 * @return
	 */
	public boolean isLocalNode()
	{
		return localNode;
	}
	
	/**
	 * Check if this {@link GridNode} is available or not.
	 * If the {@link GridNode} is available it sets to not available
	 * because a new {@link GridJob} is going to be processed.
	 * 
	 * @return whether this {@link GridNode} is available or not
	 */
	public synchronized boolean checkAndSetIsAvailable()
	{
		boolean availableRet = available;
		
		if (available)
		{
			setAvailable(false);
		}
		
		return availableRet;
	}

	/**
	 * Whether this {@link GridNode} is closed or not
	 * @return whether this {@link GridNode} is closed or not
	 */
	public boolean isClosed()
	{
		return closed;
	}

	/**
	 * Sets this {@link GridNode} to be closed 
	 * @param closed whether this {@link GridNode} is to be sed closed or not
	 */
	public void setClosed(boolean closed)
	{
		this.closed = closed;
	}

	/**
	 * Starts up this {@link GridNode} maintaining a connection
	 * with the {@link GridServer} through a {@link ServerSocketConnection}
	 */
	public void startUp()
	{
		serverSocketConnection.accept();
	}
	
	/**
	 * Returns the {@link DataOutputStream} of a {@link ServerSocketConnection} of this {@link GridNode}
	 * @return the {@link DataOutputStream} of a {@link ServerSocketConnection} of this {@link GridNode}
	 * @throws IOException
	 */
	private DataOutputStream getOuputStream() throws IOException
	{
		return serverSocketConnection.getClientOuputStream();
	}
	
	/**
	 * Returns the {@link InputStream} of a {@link ServerCloneException} of this {@link GridNode}
	 * @return the {@link InputStream} of a {@link ServerCloneException} of this {@link GridNode}
	 * @throws IOException
	 */
	public InputStream getInputStream() throws IOException
	{
		return serverSocketConnection.getClientInputStream();
	}
	
	/**
	 * Whether the {@link ServerSocketConnection} of this {@link GridNode} instance is connected to the {@link GridServer} or not 
	 * @return the {@link ServerSocketConnection} of this {@link GridNode} instance is connected to the {@link GridServer} or not
	 */
	public boolean isConnected()
	{
		return serverSocketConnection.isConnected();
	}

	/**
	 * Executes a given job on this {@link GridNode}
	 * 
	 * @param job the {@link GridJob} to be executed
	 * @param gridTask the {@link GridTask} which this {@link Grid} belongs to
	 * @param taskCompletionManager a callback object to notify the server whether the jobs had completed or not
	 */
	public void executeJob(final GridJob job, final GridTask gridTask, final GridTaskCompletionManager taskCompletionManager)
	{
		final GridNode node = this;
		executor.execute(new Runnable()
		{
			@Override
			public void run() 
			{
				if (isClosed())
				{
					System.err.println("This node is finished: " + this);
					taskCompletionManager.onJobError(job, node, new RuntimeException("The GridNode is finished."));
					return;
				}
				
				while (!checkAndSetIsAvailable())
				{
					Thread.yield();
				}

				try
				{
					OutputStream outputStream = getOuputStream();

					// jar
					if (packageNames.size() > 0)
					{
						outputStream.write(new byte[] { 1 } );
						JarBuilder.createJar(packageNames, null, outputStream);
					}
					else
					{
						outputStream.write(new byte[] { 0 } );
					}

					// object to run
					{
						ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
						ObjectOutputStream objOutput = new ObjectOutputStream(byteArrayOutputStream);
						objOutput.writeObject(job);
						int sz = byteArrayOutputStream.size();
						byte[] intAsArr = ByteBuffer.allocate(4).putInt(sz).array();
						outputStream.write(intAsArr);
						byte[] byteArray = byteArrayOutputStream.toByteArray();
						outputStream.write(byteArray);
					}
					
					outputStream.flush();
					
					readClientResponse(node, job, gridTask, taskCompletionManager);
					
					setAvailable(true);
				} 
				catch (IOException e)
				{
					e.printStackTrace();
					setAvailable(true);
					taskCompletionManager.onJobError(job, node, e);
				}
			}
		});
	}
	
	/**
	 * Close all the connections from the {@link ServerSocketConnection} of this
	 * {@link GridNode} instance
	 *  
	 * @throws IOException
	 */
	public void closeAllConnections() throws IOException
	{
		serverSocketConnection.closeAllConnections();
	}

	/**
	 * Waits for a client response when putting a job in execution
	 * 
	 * @param gridNode the {@link GridNode} with the job will be spread into
	 * @param job the executing {@link GridJob} 
	 * @param gridTask the {@link GridTask} which the {@link GridJob} came from 
	 * @param taskCompletionManager a callback object to notify the server whether the jobs had completed or not
	 * @throws IOException
	 */
	private void readClientResponse(GridNode gridNode, GridJob job, GridTask gridTask, GridTaskCompletionManager taskCompletionManager) throws IOException
	{
		InputStream is = gridNode.getInputStream();
		
		ObjectInputStream objectInputStream = new ObjectInputStream(is);
		
		Object resultObject = null;

		try
		{
			Object readObject = objectInputStream.readObject();
			JobResult jobResult = (JobResult) readObject;
			resultObject = jobResult.object;
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}

		job.setComplete(true);
		
		gridTask.notifyTaskJobReduce(job, resultObject);
		
		taskCompletionManager.onJobComplete(job);
		if (gridTask.isAllJobsComplete())
		{
			taskCompletionManager.onAllJobsComplete();
		}
	}
}