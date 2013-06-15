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
 * Data Processor holds a processing that
 * must be executed in an order.
 * A Data Processor can have multiple sub-processors and the order
 * of execution can be managed using:
 * {@link DataProcessor#setMustWaitProcessor(boolean)} waits for this processor to
 * finish before continue the program execution
 * {@link DataProcessor#setMustWaitSubProcessors(boolean)} waits for sub-processors to
 * finish before continue the program execution
 * 
 * @author Felipe Santos
 * @email felchs@gmail.com
 */
public abstract class DataProcessor
{
	/**
	 * A data processor id
	 */
	protected int id;
	
	/**
	 * The parent of this {@link DataProcessor}
	 */
	protected DataProcessor parent;
	
	/**
	 * Current status of this {@link DataProcessor}
	 */
	protected DataProcessorStatus processorStatus = DataProcessorStatus.NOT_STARTED;
	
	/**
	 * Whether this processor is finished or not
	 */
	protected boolean thisProcessorStatusFinished;
	
	/**
	 * Whether this processor locks the execution of another processes,
	 * so another processors must wait this processor to finish
	 * before continue the execution of the program
	 */
	protected boolean mustWaitProcessor;
	
	/**
	 * The sub processors attached to this {@link DataProcessor}
	 */
	protected DataProcessor[] subProcessors;
	
	
	/**
	 * Whether this processor must wait sub processors to finish 
	 * its execution or not
	 */
	private boolean mustWaitSubProcessors;
	
	/**
	 * An array of processors listeners
	 */
	protected DataProcessorListener[] processorListeners;

	/**
	 * Constructor passing arguments
	 * @param id the Id of the processor
	 * @param subProcessors the sub-processors to be attached
	 */
	public DataProcessor(int id, DataProcessor[] subProcessors)
	{
		this.id = id;
		this.subProcessors = subProcessors;
		
		if (subProcessors != null)
		{
			for (int i = 0; i < subProcessors.length; i++) 
			{
				subProcessors[i].setParent(this);
			}
		}
	}

	/**
	 * Gets the id of the processor
	 * @return the id of the processor
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Gets the processor status
	 * @return the processor status
	 */
	public DataProcessorStatus getProcessorStatus()
	{
		return processorStatus;
	}
	
	/**
	 * Change the processors status via {@link DataProcessorStatus} enum
	 * @param processorStatus the {@link DataProcessorStatus} enum
	 */
	public void changeProcessorStatus(DataProcessorStatus processorStatus)
	{
		this.processorStatus = processorStatus;
		
		notifyStatusChanged(processorStatus);
	}

	/**
	 * Notifies to the listeners when a {@link DataProcessor}'s status had changed  
	 * @param status
	 */
	private void notifyStatusChanged(DataProcessorStatus status)
	{
		if (processorListeners != null)
		{
			for (int i = 0; i < processorListeners.length; i++)
			{
				processorListeners[i].statusChanged(status, this);
			}
		}
	}

	/**
	 * Gets the parent from this {@link DataProcessor}
	 * If the parent is null, it is the root of the treee
	 * @return the parent from this {@link DataProcessor}
	 */
	public DataProcessor getParent()
	{
		return parent;
	}
	
	/**
	 * Sets the parent of this {@link DataProcessor}
	 * @param parent the parent of this {@link DataProcessor}
	 */
	private void setParent(DataProcessor parent)
	{
		this.parent = parent;
	}
	
	/**
	 * Gets the array of sub processors
	 * @return the array of sub processors
	 */
	public DataProcessor[] getSubProcessors()
	{
		return subProcessors;
	}
	
	/**
	 * Sets whether this processor locks the execution of another processes,
	 * so another processors must wait this processor to finish
	 * before continue the execution of the program
	 * @param mustWaitSubProcessors
	 */
	public void setMustWaitSubProcessors(boolean mustWaitSubProcessors)
	{
		this.mustWaitSubProcessors = mustWaitSubProcessors;
	}
	
	/**
	 * Gets the listeners of this {@link DataProcessor}
	 * @return the listeners of this {@link DataProcessor}
	 */
	public DataProcessorListener[] getProcessorListeners()
	{
		return processorListeners;
	}
	
	/**
	 * Sets a unique listener of this {@link DataProcessor}
	 * @param processorListener a unique listener of this {@link DataProcessor}
	 */
	public void setProcessorListener(DataProcessorListener processorListener)
	{
		this.processorListeners = new DataProcessorListener[] { processorListener };
	}
	
	/**
	 * Sets the listeners of this {@link DataProcessor}
	 * @param processorListeners the listeners of this {@link DataProcessor}
	 */
	public void setProcessorListeners(DataProcessorListener[] processorListeners)
	{
		this.processorListeners = processorListeners;
	}
	
	/**
	 * Gets whether this {@link DataProcessor} has sub processors or not
	 * @return whether this {@link DataProcessor} has sub processors or not
	 */
	public boolean hasSubProcessor()
	{
		return subProcessors != null && subProcessors.length > 0;
	}
	
	/**
	 * Sets the processors status finished of this {@link DataProcessor} 
	 * @param thisTaskStatusFinished the processors status finished of this {@link DataProcessor} 
	 */
	protected void setThisProcessorStatusFinished(boolean thisTaskStatusFinished)
	{
		this.thisProcessorStatusFinished = thisTaskStatusFinished;
	}
	
	/**
	 * Gets whether this processors has finished or not
	 * @return whether this processors has finished or not
	 */
	public boolean isThisProcessorStatusFinished()
	{
		return thisProcessorStatusFinished;
	}
	
	/**
	 * Sets whether this processor locks the execution of another processes,
	 * so another processors must wait this processor to finish
	 * before continue the execution of the program
	 *
	 * @param mustWaitProcessor whether this processor is blocking or not
	 */
	public void setMustWaitProcessor(boolean mustWaitProcessor)
	{
		this.mustWaitProcessor = mustWaitProcessor;
	}
	
	/**
	 * Gets whether all sub processors have finished or not
	 * @return whether all sub processors have finished or not
	 */
	public boolean isAllSubProcessorsFinished()
	{
		boolean isFinished = true;
		
		if (subProcessors.length == 0)
		{
			throw new RuntimeException();
		}
		
		for (int i = 0; i < subProcessors.length; i++)
		{
			if (subProcessors[i].getProcessorStatus() != DataProcessorStatus.FINISHED)
			{
				isFinished = false;
			}
		}
		
		return isFinished;
	}

	/**
	 * Notifies the parent {@link DataProcessor} that this processor has finished
	 * @throws Exception
	 */
	private void notifyParentProcessorFinished() throws Exception
	{
		if (!hasSubProcessor() || isAllSubProcessorsFinished())
		{
			changeProcessorStatus(DataProcessorStatus.FINISHED);
			
			if (parent != null)
			{
				parent.subProcessorFinished(this);
			}
		}
	}
	
	/**
	 * Internal method to update the status of this {@link DataProcessor}
	 * awake the processor (if necessary) and notify the parent this
	 * processor's status
	 * 
	 * @throws Exception
	 */
	protected void updateOnProcessorFinished() throws Exception
	{
		if (getProcessorStatus() == DataProcessorStatus.FINISHED)
		{
			return;
		}

		setThisProcessorStatusFinished(true);
		
		if (mustWaitProcessor)
		{
			doAwakeProcess();
		}
		
		notifyParentProcessorFinished();
	}
	
	/**
	 * Internal method to update some status of this {@link DataProcessor}
	 * when a sub processor held by this {@link DataProcessor} had finished
	 * @throws Exception
	 */
	protected void updanteOnAllSubProcessorsFinished() throws Exception
	{
		if (getProcessorStatus() == DataProcessorStatus.FINISHED)
		{
			return;
		}
		
		doAwakeProcess();
		
		notifyParentProcessorFinished();
	}
	
	/**
	 * Launches the execution of this {@link DataProcessor}
	 * All the process are executed in chain, so there is a
	 * {@link ProcessorContext} that holds a context of execution
	 * holding custom attributes that can be passes through the {@link DataProcessor}
	 * The {@link ProcessorContext} acts as a Interpreter pattern (from Design Patterns)  
	 * @param context all the process
	 * @throws Exception
	 */
	public void process(ProcessorContext context) throws Exception
	{
		changeProcessorStatus(DataProcessorStatus.PROCESSING);

		run(context);
		
		doWaitProcessorByThisProcess();

		if (subProcessors != null)
		{
			for (int i = 0; i < subProcessors.length; i++)
			{
				subProcessors[i].process(context);
			}

			if (mustWaitSubProcessors)
			{
				System.out.println("waiting sub");
				checkDoWaitSubProcessors();
				System.out.println("finished wait");
			}
		}
	}
	
	/**
	 * Called when a sub processor is finished
	 * @param processor the sub processor 
	 * @throws Exception
	 */
	protected abstract void subProcessorFinished(DataProcessor processor) throws Exception;

	/**
	 * Abstract method for implementation of wait of this processor to finish
	 * @throws Exception
	 */
	protected abstract void doWaitProcessorByThisProcess() throws Exception;
	
	/**
	 * Check if must wait sub processors to finish
	 * This method is abstract the wait behavior is held by its implementation
	 * @throws Exception
	 */
	protected abstract void checkDoWaitSubProcessors() throws Exception;
	
	/**
	 * Awake this process that previous were put to wait 
	 * This method is abstract the awake behavior is held by its implementation
	 * @throws Exception
	 */
	protected abstract void doAwakeProcess() throws Exception;

	/**
	 * Runs this {@link DataProcessor} execution
	 * 
	 * @param processorData the context data of this {@link DataProcessor}
	 * @throws Exception
	 */
	protected abstract void run(ProcessorContext processorData) throws Exception;
}