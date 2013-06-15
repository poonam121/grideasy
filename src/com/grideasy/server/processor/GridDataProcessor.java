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

import java.io.IOException;
import java.util.List;

import com.grideasy.client.NetDiscoverException;
import com.grideasy.server.impl.Grid;
import com.grideasy.server.impl.GridTask;

/**
 * Implementation of {@link DataProcessor} in the 
 * Grideasy environment
 * 
 * @author Felipe Santos
 * @email felchs@gmail.com
 */
public class GridDataProcessor extends DataProcessor
{
	/**
	 * Mutex for handling wait and notify of the
	 */
	public static final Object MUTEX = new Object();

	/**
	 * The return of the tasks
	 */
	private List<Object> taskReturnList;
	
	/**
	 * The GridTask as a processor
	 */
	private final ProcessorGridTask gridTask;
	
	/**
	 * Constructor passing id and the task
	 * 
	 * @param id the id of this {@link GridDataProcessor}
	 * @param gridTask the {@link GridTask} of this processor
	 */
	public GridDataProcessor(int id, ProcessorGridTask gridTask)
	{
		this(id, gridTask, null);
	}

	/**
	 * Constructor passing id, task and sub-processors
	 * 
	 * @param id the id of this {@link GridDataProcessor}
	 * @param gridTask the {@link GridTask} of this processor
	 * @param subProcessors the subProcessors of this {@link GridDataProcessor}
	 */
	public GridDataProcessor(int id, ProcessorGridTask gridTask, DataProcessor[] subProcessors)
	{
		super(id, subProcessors);
		this.gridTask = gridTask;
	}
	
	/**
	 * Gets the tasks that had returned
	 * @return the tasks that had returned
	 */
	public List<Object> getTaskReturnList()
	{
		return taskReturnList;
	}
	
	/**
	 * Changes the processor status of this {@link GridDataProcessor}
	 */
	@Override
	public void changeProcessorStatus(DataProcessorStatus processorStatus)
	{
		synchronized (MUTEX)
		{
			super.changeProcessorStatus(processorStatus);
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
	 * When a sub processor finishes it notifies this {@link GridDataProcessor}
	 * of the completeness
	 * If all sub processors are finished this {@link GridDataProcessor} also finishes
	 * 
	 * @param processor the sub processor that finished
	 */
	@Override
	protected void subProcessorFinished(DataProcessor processor) throws Exception
	{
		System.out.println("subProcessorFinisehd: " + processor.getId());
		
		synchronized (MUTEX)
		{
			if (isAllSubProcessorsFinished())
			{		
				MUTEX.notify();
				
				updateOnProcessorFinished();
			}
		}
	}

	/**
	 * Makes the current thread to wait until the
	 * completeness of this processor
	 */
	@Override
	protected void doWaitProcessorByThisProcess() throws Exception
	{
		if (isThisProcessorStatusFinished())
		{
			return;
		}

		if (mustWaitProcessor)
		{
			synchronized (MUTEX)
			{
				MUTEX.wait();
			}
			
			updateOnProcessorFinished();
		}
		else
		{
			updateOnProcessorFinished();
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
	 * Executes the {@link ProcessorGridTask} associated with
	 * this {@link GridDataProcessor}
	 * @param gridTask the processor that will be executed
	 */
	protected void executeTask(ProcessorGridTask gridTask) 
	{
		try
		{
			Grid.getInstance().startUp();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		try
		{
			Object[] params = new Object[] { null };
			Grid.execute(gridTask, params);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (NetDiscoverException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Runs this processor with a given {@link ProcessorContext}
	 * @param processorContext
	 */
	@Override
	protected void run(ProcessorContext processorContext) throws Exception
	{
		gridTask.setProcessorContext(processorContext);
		
		executeTask(gridTask);
	
		setThisProcessorStatusFinished(true);
		
		updateOnProcessorFinished();
	}
}