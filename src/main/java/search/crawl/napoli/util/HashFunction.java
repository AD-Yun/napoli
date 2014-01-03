package search.crawl.napoli.util;

import java.io.UnsupportedEncodingException;

import search.crawl.napoli.common.VeniceException;
import search.crawl.napoli.common.VeniceEnums.Errors;

public class HashFunction {
	public static long hash(String str, int hashingNum) throws VeniceException {
		return hash(str, System.getProperty("file.encoding"), hashingNum);
	}
	public static long hash(String str, String encoding, int hashingNum) throws VeniceException {
		HashFor64Bit clazz = new HashFor64Bit();
		long returnValue = 0;
		try {
			returnValue = clazz.hash(str.getBytes(encoding)) & ((1 << hashingNum)-1);
		} catch (UnsupportedEncodingException e) {
			throw new VeniceException(Errors.HASH_NUM_CREATE_FAILURE, e);
		}
		return returnValue;
	}
	
	public static long hash32(String str, int hashingNum) throws VeniceException {
		return hash32(str, System.getProperty("file.encoding"), hashingNum);
	}
	public static long hash32(String str, String encoding, int hashingNum) throws VeniceException {
		HashFor32Bit clazz = new HashFor32Bit();
		long returnValue = 0;
		try {
			returnValue = clazz.hash(str.getBytes(encoding)) & ((1 << hashingNum)-1);
		} catch (UnsupportedEncodingException e) {
			throw new VeniceException(Errors.HASH_NUM_CREATE_FAILURE, e);
		}
		return returnValue;
	}
	
	public static long hash64(String str, int hashingNum) throws VeniceException {
		return hash32(str, System.getProperty("file.encoding"), hashingNum);
	}
	public static long hash64(String str, String encoding, int hashingNum) throws VeniceException {
		HashForUnsigned64 clazz = new HashForUnsigned64();
		long returnValue = 0;
		try {
			returnValue = clazz.hash(str.getBytes(encoding)) & ((1 << hashingNum)-1);
		} catch (UnsupportedEncodingException e) {
			throw new VeniceException(Errors.HASH_NUM_CREATE_FAILURE, e);
		}
		return returnValue;
	}
}
