package org.acreo.cleint.test;

import org.acreo.common.entities.lc.SmartContract;

/*
 * Copyright (c) 2009. Venish Joe Clarence (http://venishjoe.net)
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

public class JavaClassLoader extends ClassLoader {
	private int[] data = { -54, -2, -70, -66, 0, 0, 0, 49, 0, 31, 7, 0, 2, 1, 0, 38, 111, 114, 103, 47, 97, 99, 114,
			101, 111, 47, 99, 108, 101, 105, 110, 116, 47, 116, 101, 115, 116, 47, 67, 108, 97, 115, 115, 76, 111, 97,
			100, 101, 114, 73, 110, 112, 117, 116, 7, 0, 4, 1, 0, 16, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 79,
			98, 106, 101, 99, 116, 1, 0, 6, 60, 105, 110, 105, 116, 62, 1, 0, 3, 40, 41, 86, 1, 0, 4, 67, 111, 100, 101,
			10, 0, 3, 0, 9, 12, 0, 5, 0, 6, 1, 0, 15, 76, 105, 110, 101, 78, 117, 109, 98, 101, 114, 84, 97, 98, 108,
			101, 1, 0, 18, 76, 111, 99, 97, 108, 86, 97, 114, 105, 97, 98, 108, 101, 84, 97, 98, 108, 101, 1, 0, 4, 116,
			104, 105, 115, 1, 0, 40, 76, 111, 114, 103, 47, 97, 99, 114, 101, 111, 47, 99, 108, 101, 105, 110, 116, 47,
			116, 101, 115, 116, 47, 67, 108, 97, 115, 115, 76, 111, 97, 100, 101, 114, 73, 110, 112, 117, 116, 59, 1, 0,
			11, 112, 114, 105, 110, 116, 83, 116, 114, 105, 110, 103, 9, 0, 16, 0, 18, 7, 0, 17, 1, 0, 16, 106, 97, 118,
			97, 47, 108, 97, 110, 103, 47, 83, 121, 115, 116, 101, 109, 12, 0, 19, 0, 20, 1, 0, 3, 111, 117, 116, 1, 0,
			21, 76, 106, 97, 118, 97, 47, 105, 111, 47, 80, 114, 105, 110, 116, 83, 116, 114, 101, 97, 109, 59, 8, 0,
			22, 1, 0, 6, 75, 75, 75, 75, 75, 75, 10, 0, 24, 0, 26, 7, 0, 25, 1, 0, 19, 106, 97, 118, 97, 47, 105, 111,
			47, 80, 114, 105, 110, 116, 83, 116, 114, 101, 97, 109, 12, 0, 27, 0, 28, 1, 0, 7, 112, 114, 105, 110, 116,
			108, 110, 1, 0, 21, 40, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59,
			41, 86, 1, 0, 10, 83, 111, 117, 114, 99, 101, 70, 105, 108, 101, 1, 0, 21, 67, 108, 97, 115, 115, 76, 111,
			97, 100, 101, 114, 73, 110, 112, 117, 116, 46, 106, 97, 118, 97, 0, 33, 0, 1, 0, 3, 0, 0, 0, 0, 0, 2, 0, 1,
			0, 5, 0, 6, 0, 1, 0, 7, 0, 0, 0, 47, 0, 1, 0, 1, 0, 0, 0, 5, 42, -73, 0, 8, -79, 0, 0, 0, 2, 0, 10, 0, 0, 0,
			6, 0, 1, 0, 0, 0, 24, 0, 11, 0, 0, 0, 12, 0, 1, 0, 0, 0, 5, 0, 12, 0, 13, 0, 0, 0, 1, 0, 14, 0, 6, 0, 1, 0,
			7, 0, 0, 0, 55, 0, 2, 0, 1, 0, 0, 0, 9, -78, 0, 15, 18, 21, -74, 0, 23, -79, 0, 0, 0, 2, 0, 10, 0, 0, 0, 10,
			0, 2, 0, 0, 0, 26, 0, 8, 0, 27, 0, 11, 0, 0, 0, 12, 0, 1, 0, 0, 0, 9, 0, 12, 0, 13, 0, 0, 0, 1, 0, 29, 0, 0,
			0, 2, 0, 30 };

	public static void main(String args[]) throws Exception {

		JavaClassLoader _classLoader = new JavaClassLoader();
		
		
		/*args = new String[2];
		args[0] = "org.acreo.cleint.test.ClassLoaderInput";
		args[1] = "printString";

		JavaClassLoader _classLoader = new JavaClassLoader();
		byte[] rawBytes = new byte[_classLoader.data.length];
		for (int index = 0; index < rawBytes.length; index++)
			rawBytes[index] = (byte) _classLoader.data[index];
		Class regeneratedClass = _classLoader.defineClass(args[0], rawBytes, 0, rawBytes.length);
		regeneratedClass.getMethod(args[1], null).invoke(null, new Object[] { args });*/
	}
}
