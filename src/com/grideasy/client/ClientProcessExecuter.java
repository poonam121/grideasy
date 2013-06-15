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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * This class executes classes into a new class loader
 * 
 * @author Felipe Santos
 * @email felchs@gmail.com
 */
public class ClientProcessExecuter
{
	/**
	 * Lists the classes of a given {@link ClassLoader}
	 * 
	 * @param classLoader the {@link ClassLoader} to read classes
	 */
	public static void listLoadedClasses(ClassLoader classLoader) {
	    Class<?> clazz = classLoader.getClass();
	    while (clazz != java.lang.ClassLoader.class) {
	        clazz = clazz.getSuperclass();
	    }
	    try {
	        java.lang.reflect.Field fldClasses = clazz.getDeclaredField("classes");
	        fldClasses.setAccessible(true);
	        Vector<?> classes = (Vector<?>) fldClasses.get(classLoader);
	        for (Iterator<?> iter = classes.iterator(); iter.hasNext();) {
	            System.out.println("Loaded " + iter.next());
	        }
	    } catch (SecurityException e) {
	        e.printStackTrace();
	    } catch (IllegalArgumentException e) {
	        e.printStackTrace();
	    } catch (NoSuchFieldException e) {
	        e.printStackTrace();
	    } catch (IllegalAccessException e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Initializes a new .jar at runtime
	 * The jar is created in the temporary directory of the
	 * operational system, initialized on memory and deleted
	 * after the initialization
	 *   
	 * @param jarByteArray the bytes of the jars to be initialized 
	 * @return the new {@link ClassLoader} created
	 */
	private URLClassLoader initJar(byte[] jarByteArray)
	{
		URLClassLoader newClassLoader = null;
		try
		{
			String tmpFileName = "TempClasses" + System.currentTimeMillis();
			File file = File.createTempFile(tmpFileName, ".jar");
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(jarByteArray);
			fos.close();
			URL url = file.toURI().toURL();
			URL[] urls = new URL[] { url };
			newClassLoader = URLClassLoader.newInstance(urls);
			String absolutePath = file.getAbsolutePath();
			ArrayList<String> classNamesFromPackage = getClassNamesFromPackage(absolutePath);
			for (String classNameFromPackage : classNamesFromPackage) 
			{
				String className = classNameFromPackage.replace("/", ".").replace(".class", "");
				newClassLoader.loadClass(className);
			}
			file.delete();
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return newClassLoader;
	}
	
	/**
	 * Get all classes from a jar
	 * 
	 * @param jarFileName the jar to get the classes
	 * @return the classes of the jar as String in a {@link ArrayList}
	 * @throws IOException
	 */
	public static ArrayList<String> getClassNamesFromPackage(String jarFileName) throws IOException
	{
	    ArrayList<String> names = new ArrayList<String>();

        Enumeration<JarEntry> jarEntries;
        JarFile jf = new JarFile(jarFileName);
        jarEntries = jf.entries();
        while(jarEntries.hasMoreElements())
        {
        	String name = jarEntries.nextElement().getName();
        	if (name.endsWith(".class")) 
        	{
        		names.add(name);
        	}
        }

	    return names;
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Whether this execution is running or not
	 */
	private boolean running = true;
	
	/**
	 * Sets whether this client executer is running or not
	 * @param running whether this client executer is running or not
	 */
	public synchronized void setRunning(boolean running)
	{
		this.running = running;
	}
	
	/**
	 * Gets whether this client is running or not 
	 * @return whether this client is running or not
	 */
	public synchronized boolean isRunning()
	{
		return running;
	}

	/**
	 * Executes external program with data from a {@link Socket}
	 * 
	 * @param in the {@link DataInputStream} of the {@link Socket}
	 * @param os the {@link DataOutputStream} of the {@link Socket}
	 * @param socket the {@link Socket} of external execution
	 * @throws IOException
	 */
	public void execute(DataInputStream in, DataOutputStream os, Socket socket) throws IOException
	{
		while (isRunning())
		{
			if (socket.isClosed() || !socket.isConnected())
			{
				notifyError();
				return;
			}
			
			try
			{
				// jar bytes
				int sz = in.readInt();
				byte[] jarByteArray = new byte[sz];
				in.readFully(jarByteArray);
	
				URLClassLoader newLoader = initJar(jarByteArray);
	
				// object
				sz = in.readInt();
				byte objInBytes[] = new byte[sz];
				in.readFully(objInBytes);
	
				ByteArrayInputStream inputStream = new ByteArrayInputStream(objInBytes);
				CustomObjectInputStream objectInputStream = new CustomObjectInputStream(inputStream, newLoader);

				Object readObject = objectInputStream.readObject();
				objectInputStream.close();

				GridJob gridJob = (GridJob) readObject;
				gridJob.setOutputStream(os);
				gridJob.callJob();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				notifyError();
			}
		}
	}

	/**
	 * Notifies an error in the execution if exists
	 */
	private void notifyError()
	{
		setRunning(false);
		System.out.println("The grid closed the connection.");
	}
}
