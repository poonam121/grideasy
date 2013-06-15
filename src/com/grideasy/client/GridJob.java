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

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * A GridJob is where the application must be located to be
 * spread in a Compute Grid.
 * When implementing a job, this class must be extended 
 * and the method {@link GridJob#execute()} must be override by
 * a custom implementation which the code will be delivered in the grid.
 * 
 * @author Felipe Santos
 * @email felchs@gmail.com
 */
public abstract class GridJob implements Serializable
{
	/**
	 * Default serial version UID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Whether this job is complete or not
	 */
	private boolean complete;
	
	/**
	 * The {@link OutputStream} of connection between client and server
	 */
	private OutputStream outputStream;
	
	/**
	 * Sets the {@link OutputStream} of connection between client and server
	 * @param outputStream
	 */
	public void setOutputStream(OutputStream outputStream)
	{
		this.outputStream = outputStream;
	}
	
	/**
	 * Gets whether this {@link GridJob} is complete or not
	 * @return whether this {@link GridJob} is complete or not
	 */
	public boolean isComplete()
	{
		return complete;
	}
	
	/**
	 * Sets whether this {@link GridJob} is complete or not
	 * @param the boolean complete flag
	 */
	public void setComplete(boolean complete)
	{
		this.complete = complete;
	}
	
	/**
	 * Calls this job to be executed
	 * The responsible of calling this method is by the
	 * {@link ClientProcessExecuter}, so don't manually 
	 * call the execution of a job
	 * @throws IOException
	 */
	protected void callJob() throws IOException
	{
		Object executionReturn = execute();
		JobResult jobResult = new JobResult(executionReturn);

		setComplete(true);

		ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
		objectOutputStream.writeObject(jobResult);
		
		objectOutputStream.flush();
	}

	/**
	 * Override this method putting the code of the {@link GridJob}'s code here
	 * @return the response of the execution of the {@link GridJob}
	 */
	protected abstract Object execute();
}
