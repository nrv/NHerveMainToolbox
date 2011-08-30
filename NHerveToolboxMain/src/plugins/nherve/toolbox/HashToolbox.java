/*
 * Copyright 2010, 2011 Institut Pasteur.
 * 
 * This file is part of NHerve Main Toolbox, which is an ICY plugin.
 * 
 * NHerve Main Toolbox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * NHerve Main Toolbox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with NHerve Main Toolbox. If not, see <http://www.gnu.org/licenses/>.
 */

package plugins.nherve.toolbox;

import icy.file.FileUtil;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashToolbox {

	private final static byte EQUALS_SIGN = (byte) '_';
	private final static byte[] _STANDARD_ALPHABET = { (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F', (byte) 'G', (byte) 'H', (byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N', (byte) 'O', (byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U', (byte) 'V', (byte) 'W', (byte) 'X', (byte) 'Y', (byte) 'Z', (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f', (byte) 'g', (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n', (byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's', (byte) 't', (byte) 'u', (byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z', (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) '+', (byte) '-' };

	private static byte[] encode3to4(byte[] source, int srcOffset, int numSigBytes, byte[] destination, int destOffset) {
		byte[] ALPHABET = _STANDARD_ALPHABET;
	
		int inBuff = (numSigBytes > 0 ? ((source[srcOffset] << 24) >>> 8) : 0) | (numSigBytes > 1 ? ((source[srcOffset + 1] << 24) >>> 16) : 0) | (numSigBytes > 2 ? ((source[srcOffset + 2] << 24) >>> 24) : 0);
	
		switch (numSigBytes) {
		case 3:
			destination[destOffset] = ALPHABET[(inBuff >>> 18)];
			destination[destOffset + 1] = ALPHABET[(inBuff >>> 12) & 0x3f];
			destination[destOffset + 2] = ALPHABET[(inBuff >>> 6) & 0x3f];
			destination[destOffset + 3] = ALPHABET[(inBuff) & 0x3f];
			return destination;
	
		case 2:
			destination[destOffset] = ALPHABET[(inBuff >>> 18)];
			destination[destOffset + 1] = ALPHABET[(inBuff >>> 12) & 0x3f];
			destination[destOffset + 2] = ALPHABET[(inBuff >>> 6) & 0x3f];
			destination[destOffset + 3] = EQUALS_SIGN;
			return destination;
	
		case 1:
			destination[destOffset] = ALPHABET[(inBuff >>> 18)];
			destination[destOffset + 1] = ALPHABET[(inBuff >>> 12) & 0x3f];
			destination[destOffset + 2] = EQUALS_SIGN;
			destination[destOffset + 3] = EQUALS_SIGN;
			return destination;
	
		default:
			return destination;
		}
	}

	private static String encodeBytes(byte[] source, int off, int len, String preferredEncoding) {
		byte[] encoded = encodeBytesToBytes(source, off, len);
	
		try {
			return new String(encoded, preferredEncoding);
		} catch (java.io.UnsupportedEncodingException uue) {
			return new String(encoded);
		}
	
	}

	private static byte[] encodeBytesToBytes(byte[] source, int off, int len) {
	
		if (source == null) {
			throw new NullPointerException("Cannot serialize a null array.");
		}
	
		if (off < 0) {
			throw new IllegalArgumentException("Cannot have negative offset: " + off);
		}
	
		if (len < 0) {
			throw new IllegalArgumentException("Cannot have length offset: " + len);
		}
	
		if (off + len > source.length) {
			throw new IllegalArgumentException("Cannot have offset of " + off + " and length of " + len + " with array of length " + source.length);
		}
	
		int encLen = (len / 3) * 4 + (len % 3 > 0 ? 4 : 0);
		byte[] outBuff = new byte[encLen];
	
		int d = 0;
		int e = 0;
		int len2 = len - 2;
		for (; d < len2; d += 3, e += 4) {
			encode3to4(source, d + off, 3, outBuff, e);
		}
	
		if (d < len) {
			encode3to4(source, d + off, len - d, outBuff, e);
			e += 4;
		}
	
		if (e <= outBuff.length - 1) {
			byte[] finalOut = new byte[e];
			System.arraycopy(outBuff, 0, finalOut, 0, e);
			return finalOut;
		} else {
			return outBuff;
		}
	}
	
	public static String hashMD5(byte[] b) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] bts = digest.digest(b);
			return encodeBytes(bts, 0, bts.length, "US-ASCII");
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	
	}
	
	public static String hashMD5(File f) {
		return hashMD5(FileUtil.load(f, false));	
	}

	public static String hashMD5(String f) {
		return hashMD5(f.getBytes());	
	}

}
