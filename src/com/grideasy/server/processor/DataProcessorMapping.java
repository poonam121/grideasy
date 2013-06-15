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

import java.util.Hashtable;

/**
 * A Lazy initialization of {@link DataProcessor} 
 * The key is the {@link DataProcessor} id
 * 
 * @author Felipe Santos
 * @email felchs@gmail.com
 */
public class DataProcessorMapping
{
	/**
	 * Lazy intialization of {@link DataProcessor} mapping
	 */
	private static Hashtable<Integer, DataProcessor> lazyDataProcessorMapping = new Hashtable<Integer, DataProcessor>();

	/**
	 * Gets a {@link DataProcessor} with a given id
	 * @param processorMappingId the id of a {@link DataProcessor}
	 * @return a {@link DataProcessor}
	 */
	public static DataProcessor getDataProcessor(int processorMappingId)
	{
		return DataProcessorMapping.lazyDataProcessorMapping.get(processorMappingId);
	}
	
	/**
	 * Put a {@link DataProcessor} with a given id
	 * @param processorMappingId a {@link DataProcessor} id
	 * @param processor the {@link DataProcessor} to be insert into
	 */
	public static void putDataProcessor(int processorMappingId, DataProcessor processor)
	{
		DataProcessorMapping.lazyDataProcessorMapping.put(processorMappingId, processor);
	}
}