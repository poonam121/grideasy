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
package com.grideasy.server.processor;

/**
 * Implementation of the {@link DataProcessor} in
 * a thread environment
 * 
 * @author Felipe Santos
 * @email felchs@gmail.com
 */
public class ThreadDataProcessor extends DataProcessor
{
	/**
	 * Mutex for handling wait and notify of the
	 */
	public static final Object MUTEX = new Object();
	
	/**
	 * Thread used on this data processor 
	 */
	private Thread processorThread;

	/**
	 * Constructor passing id
	 * @param id a id for this data processor
	 */
	public ThreadDataProcessor(int id)
	{
		this(id, null);
		
		this.processorThread = new Thread();
	}
	
	/**
	 * Constructor passing id, task and sub-processors
	 * 
	 * @param id the id of this {@link ThreadDataProcessor}
	 * @param subProcessors the subProcessors of this {@link GridDataProcessor}
	 */
	public ThreadDataProcessor(int id, DataProcessor[] subProcessors)
	{
		super(id, subProcessors);
	}
	
	/**
	 * Changes the processor status of this {@link ThreadDataProcessor}
	 */
	@Override
	public void changeProcessorStatus(DataProcessorStatus status)
	{
		synchronized (MUTEX)
		{
			super.changeProcessorStatus(status);
		}
	}
	
	/**
	 * Gets the processor status of this {@link GridDataProcessor}
	 */
	@Override
	public DataProcessorStatus getProcessorStatus()
	{
		synchronized (MUTEX)
		{
			return super.getProcessorStatus();
		}
	}

	/**
	 * Makes the current thread to wait until the
	 * completeness of this processor
	 */
	@Override
	protected void doWaitProcessorByThisProcess() throws Exception
	{
		boolean mustWait = false;
		
		synchronized (MUTEX)
		{
			mustWait = getProcessorStatus() != DataProcessorStatus.FINISHED && mustWaitProcessor;
		}
		
		if (mustWait)
		{
			MUTEX.wait();
		}
	}

	/**
	 * Check if this thread must wait until
	 * the sub processors to complete
	 */
	@Override
	protected void checkDoWaitSubProcessors() throws Exception
	{
		synchronized (MUTEX)
		{
			if (!isAllSubProcessorsFinished())
			{
				MUTEX.wait();
			}
		}
	}
	
	/**
	 * When a sub processor finishes it notifies this {@link DataProcessor}
	 * of the completeness
	 * If all sub processors are finished this {@link DataProcessor} also finishes
	 * 
	 * @param processor the sub processor that finished
	 */
	@Override
	protected void subProcessorFinished(DataProcessor processor) throws Exception
	{
		synchronized (MUTEX)
		{
			if (isAllSubProcessorsFinished())
			{
				MUTEX.notify();
				
				updanteOnAllSubProcessorsFinished();
			}
		}
	}

	/**
	 * This thread notifies. 
	 * It is called when this thread was hold with .wait()
	 * for the completeness of this processor of sub processors
	 * associated with this processor
	 */
	@Override
	protected void doAwakeProcess() throws Exception
	{
		synchronized (MUTEX)
		{
			MUTEX.notify();
		}
	}
	
	/**
	 * Sets the status of completeness of this {@link GridDataProcessor}
	 * 
	 * @param thisTaskStatusFinished the status of completeness
	 */	
	@Override
	protected void setThisProcessorStatusFinished(boolean thisTaskStatusFinished)
	{
		synchronized (MUTEX)
		{
			super.setThisProcessorStatusFinished(thisTaskStatusFinished);
		}
	}

	/**
	 * Runs this processor with a given {@link ProcessorContext}
	 * @param processorContext
	 */	
	@Override
	protected void run(ProcessorContext context) throws Exception
	{
		this.processorThread = new Thread() {
			@Override
			public void run()
			{
				try
				{
					Thread.sleep(5000);
				} 
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}

				System.out.println("Id: " + id + ", finished.");
				
				setThisProcessorStatusFinished(true);
				
				try
				{
					updateOnProcessorFinished();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		};
		
		this.processorThread.start();
	}
}