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
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

/**
 * The classes are loaded into a custom {@link ClassLoader} by this 
 * {@link CustomObjectInputStream}
 *  
 * @author Felipe Santos
 * @email felchs@gmail.com
 */
public class CustomObjectInputStream extends ObjectInputStream 
{
	/**
	 * Custom {@link ClassLoader} which the clases will be loaded into
	 */
	private ClassLoader loader;
	
	/**
	 * Constructor passing paramters
	 * @param in the {@link InputStream} of incoming classes and objects
	 * @param loader custom {@link ClassLoader} which the clases will be loaded into
	 * @throws IOException
	 * @throws SecurityException
	 */
	protected CustomObjectInputStream(InputStream in, ClassLoader loader) throws IOException, SecurityException
	{
		super(in);
		this.loader = loader;
	}

	@Override
	protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException,	ClassNotFoundException 
	{
		try 
		{
			String className = desc.getName();
			
			return loader.loadClass(className);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		return super.resolveClass(desc);
	}
}