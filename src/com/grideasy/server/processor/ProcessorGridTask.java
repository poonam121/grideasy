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

import java.util.LinkedList;
import java.util.Set;

import com.grideasy.client.GridJob;
import com.grideasy.server.GridTask;

/**
 * Subclass of the {@link GridTask}
 * This processor holds a context that is used as 
 * a interpreter when executing this processors and
 * sub processors 
 * @author Felipe Santos
 * @email felchs@gmail.com
 */
public abstract class ProcessorGridTask extends GridTask
{ 
	/**
	 * The context of this task
	 */
	private ProcessorContext processorContext;

	/**
	 * Constructor passing package names and jobs list
	 * 
	 * @param packageNames the package names of code spreading
	 * @param jobsList the jobs associated with this task
	 */
	public ProcessorGridTask(Set<String> packageNames, LinkedList<GridJob> jobsList) 
	{
		super(jobsList);
		
		addPackageNames(packageNames);
	}
	
	/**
	 * Sets a context to this task
	 * The context that is used as a interpreter when 
	 * executing this processors and sub processors
	 *   
	 * @param processorContext
	 */
	public void setProcessorContext(ProcessorContext processorContext)
	{
		this.processorContext = processorContext;
	}

	/**
	 * Gets the context of this task
	 * @return
	 */
	public ProcessorContext getProcessorContext()
	{
		return processorContext;
	}
}