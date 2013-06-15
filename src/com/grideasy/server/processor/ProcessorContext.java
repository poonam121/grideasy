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
 * The processor context gather info about the
 * execution of {@link DataProcessor}.
 * It is passed thought the execution of {@link DataProcessor}'s
 * acting as a interpreter pattern
 * 
 * @author Felipe Santos
 * @email felchs@gmail.com
 */
public class ProcessorContext
{
	/**
	 * Custom contexts objects associated with this processor
	 */
	protected Hashtable<Object, Object> contextObjects = new Hashtable<Object, Object>();

	/**
	 * Gets the map of context objects 
	 * @return the map of context objects
	 */
	public Hashtable<Object, Object> getContextObjects()
	{
		return contextObjects;
	}
	
	/**
	 * Adds a context object
	 * @param key the key of the context object
	 * @param object the context object
	 */
	public void addContextObject(Object key, Object object)
	{
		contextObjects.put(key, object);
	}
	
	/**
	 * Gets a context object
	 * @param key the key of the mapping of this context object
	 * @return the context object
	 */
	public Object getContextObject(Object key)
	{
		return contextObjects.get(key);
	}
}
