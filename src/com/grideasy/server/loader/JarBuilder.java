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
package com.grideasy.server.loader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * A helper class to generate jars from a given package name or 
 * list of packages.
 * 
 * @author Felipe Santos
 * @email felchs@gmail.com
 */
public class JarBuilder
{	
	/**
	 * Creates a jar with a list of package names 
	 * 
	 * @param packageNames the packages names to be used when generating the jar
	 * @param mainClass the main class of the jar file
	 * @param outputStream the {@link OutputStream} which the jars must be put into
	 * @throws IOException
	 */
	public static void createJar(Set<String> packageNames, Class<?> mainClass, OutputStream outputStream) throws IOException
	{
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		Manifest manifest = new Manifest();
		manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
		if (mainClass != null)
		{
			manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, mainClass.getName());
		}
		JarOutputStream jarOutputStream = new JarOutputStream(byteArrayOutputStream, manifest);

		Vector<Class<?>> allClasses = new Vector<Class<?>>();
		for (String packageName : packageNames)
		{
			getAllClasses(packageName, allClasses);
		}
		for (Class<?> clazz : allClasses)
		{
			addClass(clazz, jarOutputStream);
		}

		jarOutputStream.close();
		byteArrayOutputStream.close();
		
		byte[] byteArray = byteArrayOutputStream.toByteArray();
		int sz = byteArray.length;
		byte[] intAsArr = ByteBuffer.allocate(4).putInt(sz).array();
		outputStream.write(intAsArr);
		outputStream.write(byteArray);
	}

	/**
	 * Adds a class to a given {@link JarOutputStream}
	 * @param c the class to be added
	 * @param jarOutputStream the {@link JarOutputStream} to be filled
	 * @throws IOException
	 */
	private static void addClass(Class<?> c, JarOutputStream jarOutputStream) throws IOException
	{
		String name = c.getName();
		if (name.contains("$")) {
			return;
		}
		String path = name.replace('.', '/') + ".class";
		JarEntry jarEntry = new JarEntry(path);
		jarOutputStream.putNextEntry(jarEntry);
		ClassLoader classLoader = c.getClassLoader();
		byte[] byteArray = toByteArray(classLoader.getResourceAsStream(path));
		if (byteArray.length == 0)
		{
			throw new RuntimeException();
		}
		jarOutputStream.write(byteArray);
		jarOutputStream.closeEntry();
	}

	/**
	 * Gets an array of bytes from a {@link InputStream}
	 * @param in the {@link InputStream} to be used when getting the bytes
	 * @return the array of bytes from a {@link InputStream}
	 * @throws IOException
	 */
	private static byte[] toByteArray(InputStream in) throws IOException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[0x1000];
		while (true)
		{
			int r = in.read(buf);
			if (r == -1)
			{
				break;
			}
			out.write(buf, 0, r);
		}
		return out.toByteArray();
	}

	/**
	 * Gets all the classes from a given package
	 * 
	 * @param packageName the package name for getting the files
	 * @param classes gets all the classes from a given package
	 */
	private static void getAllClasses(String packageName, Vector<Class<?>> classes)
	{
		try
		{
			File directory = null;
			
			try
			{
				ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
				URL resource = contextClassLoader.getResource(packageName.replace('.', '/'));
				directory = new File(resource.getFile());
			} 
			catch (NullPointerException x)
			{
				throw new ClassNotFoundException(packageName + " does not appear to be a valid package");
			}

			if (directory.exists())
			{
				File[] files = directory.listFiles();
				
				for (File file : files)
				{
					String fileName = file.getName();
					if (file.isDirectory())
					{
						String name = file.getAbsolutePath();
						if (!name.endsWith("/"))
						{
							name += "/";
						}
						
						String subPackageName = packageName + "." + fileName;
						getAllClasses(subPackageName, classes);
					}
					else if (fileName.endsWith(".class"))
					{
						// removes the .class extension
						classes.add(Class.forName(packageName + '.' + fileName.substring(0, fileName.length() - 6)));
					}
					
				}
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}