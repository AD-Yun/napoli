package search.crawl.napoli.util;

import java.math.BigInteger;

//public class HashFor32Bit implements HashFunction {
public class HashForUnsigned64 {
	public static final int NATE_SEARCH_INITVAL = 0;
	
	/**
	 * 1 바이트 Mask.
	 */
	private static long BYTE_MASK = 0x00000000000000ffL;
	/**
	 * We use longs because we don't have unsigned ints
	 */
	private  BigInteger a, b, c;
	

	public int hash(byte[] bytes, int initval) {
		return hash(bytes, bytes.length, initval);
	}
	public int hash(byte[] bytes) {		
		return hash(bytes, bytes.length, NATE_SEARCH_INITVAL);
	}
	
	private static final BigInteger TWO_64 = BigInteger.ONE.shiftLeft(64);
	private BigInteger convertUnsignedLong(BigInteger in) {
		BigInteger out = in;
		if(out.signum() < 0) { 
			out = out.add(TWO_64);
		}
		return out;
	}

	public int hash(byte[] bytes, int length, int initval) {
		int len = length;
		a = b = new BigInteger("0");
		c = convertUnsignedLong(BigInteger.valueOf(0x9e3779b97f4a7c13L));
		
		//System.out.println(String.format("A : %16x, B : %16x, C : %16x" , a.longValue(), b.longValue(), c.longValue()));
		int offset = 0;
		for( ; len >= 24; offset += 24, len -= 24 ) {
			a = convertUnsignedLong(BigInteger.valueOf(a.longValue() + (bytes[offset + 0] & BYTE_MASK) + ((bytes[offset + 1] & BYTE_MASK) << 8) + 
					((bytes[offset + 2] & BYTE_MASK) << 16) + ((bytes[offset + 3] & BYTE_MASK) << 24) + ((bytes[offset + 4] & BYTE_MASK) << 32) +
					((bytes[offset + 5] & BYTE_MASK) << 40) + ((bytes[offset + 6] & BYTE_MASK) << 48) + ((bytes[offset + 7] & BYTE_MASK) << 56)));
			b = convertUnsignedLong(BigInteger.valueOf(b.longValue() + (bytes[offset + 8] & BYTE_MASK) + ((bytes[offset + 9] & BYTE_MASK) << 8) + 
					((bytes[offset + 10] & BYTE_MASK) << 16) + ((bytes[offset + 11] & BYTE_MASK) << 24) + ((bytes[offset + 12] & BYTE_MASK) << 32) +
					((bytes[offset + 13] & BYTE_MASK) << 40) + ((bytes[offset + 14] & BYTE_MASK) << 48) + ((bytes[offset + 15] & BYTE_MASK) << 56)));
			c = convertUnsignedLong(BigInteger.valueOf(c.longValue() + (bytes[offset + 16] & BYTE_MASK) + ((bytes[offset + 17] & BYTE_MASK) << 8) + 
					((bytes[offset + 18] & BYTE_MASK) << 16) + ((bytes[offset + 19] & BYTE_MASK) << 24) + ((bytes[offset + 20] & BYTE_MASK) << 32) +
					((bytes[offset + 21] & BYTE_MASK) << 40) + ((bytes[offset + 22] & BYTE_MASK) << 48) + ((bytes[offset + 23] & BYTE_MASK) << 56)));
			//System.out.println(String.format("IN LOOP (mix) - A : %16x, B : %16x, C : %16x" , a.longValue(), b.longValue(), c.longValue()));
			mix();
			//System.out.println(String.format("IN LOOP (mix) - A : %16x, B : %16x, C : %16x" , a.longValue(), b.longValue(), c.longValue()));
		}
		//System.out.println(String.format("A : %16x, B : %16x, C : %16x" , a.longValue(), b.longValue(), c.longValue()));
		c = convertUnsignedLong(BigInteger.valueOf(c.longValue() + length));
		//System.out.println(String.format("A : %16x, B : %16x, C : %16x" , a.longValue(), b.longValue(), c.longValue()));
		
		switch( len ) { // all the case statements fall through
			case 23:
				c = convertUnsignedLong(BigInteger.valueOf(c.longValue() + ((bytes[offset + 22] & BYTE_MASK ) << 56)));
			case 22:
				c = convertUnsignedLong(BigInteger.valueOf(c.longValue() + ((bytes[offset + 21] & BYTE_MASK ) << 48)));
			case 21:
				c = convertUnsignedLong(BigInteger.valueOf(c.longValue() + ((bytes[offset + 20] & BYTE_MASK ) << 40)));
			case 20:
				c = convertUnsignedLong(BigInteger.valueOf(c.longValue() + ((bytes[offset + 19] & BYTE_MASK ) << 32)));
			case 19:
				c = convertUnsignedLong(BigInteger.valueOf(c.longValue() + ((bytes[offset + 18] & BYTE_MASK ) << 24)));
			case 18:
				c = convertUnsignedLong(BigInteger.valueOf(c.longValue() + ((bytes[offset + 17] & BYTE_MASK ) << 16)));
			case 17:
				c = convertUnsignedLong(BigInteger.valueOf(c.longValue() + ((bytes[offset + 16] & BYTE_MASK ) << 8)));
			case 16:
				b = convertUnsignedLong(BigInteger.valueOf(b.longValue() + ((bytes[offset + 15] & BYTE_MASK ) << 56)));
			case 15:
				b = convertUnsignedLong(BigInteger.valueOf(b.longValue() + ((bytes[offset + 14] & BYTE_MASK ) << 48)));
			case 14:
				b = convertUnsignedLong(BigInteger.valueOf(b.longValue() + ((bytes[offset + 13] & BYTE_MASK ) << 40)));
			case 13:
				b = convertUnsignedLong(BigInteger.valueOf(b.longValue() + ((bytes[offset + 12] & BYTE_MASK ) << 32)));
			case 12:
				b = convertUnsignedLong(BigInteger.valueOf(b.longValue() + ((bytes[offset + 11] & BYTE_MASK ) << 24)));
			case 11:
				b = convertUnsignedLong(BigInteger.valueOf(b.longValue() + ((bytes[offset + 10] & BYTE_MASK ) << 16)));
			case 10:
				b = convertUnsignedLong(BigInteger.valueOf(b.longValue() + ((bytes[offset + 9] & BYTE_MASK ) << 8)));
			case 9:
				b = convertUnsignedLong(BigInteger.valueOf(b.longValue() + (bytes[offset + 8] & BYTE_MASK )));
			case 8:
				a = convertUnsignedLong(BigInteger.valueOf(a.longValue() + ((bytes[offset + 7] & BYTE_MASK ) << 56)));
			case 7:
				a = convertUnsignedLong(BigInteger.valueOf(a.longValue() + ((bytes[offset + 6] & BYTE_MASK ) << 48)));
			case 6:
				a = convertUnsignedLong(BigInteger.valueOf(a.longValue() + ((bytes[offset + 5] & BYTE_MASK ) << 40)));
			case 5:
				a = convertUnsignedLong(BigInteger.valueOf(a.longValue() + ((bytes[offset + 4] & BYTE_MASK ) << 32)));
			case 4:
				a = convertUnsignedLong(BigInteger.valueOf(a.longValue() + ((bytes[offset + 3] & BYTE_MASK ) << 24)));
			case 3:
				a = convertUnsignedLong(BigInteger.valueOf(a.longValue() + ((bytes[offset + 2] & BYTE_MASK ) << 16)));
			case 2:
				a = convertUnsignedLong(BigInteger.valueOf(a.longValue() + ((bytes[offset + 1] & BYTE_MASK ) << 8)));
			case 1:
				a = convertUnsignedLong(BigInteger.valueOf(a.longValue() + (bytes[offset + 0] & BYTE_MASK )));
		}
		//System.out.println(String.format("A : %16x, B : %16x, C : %16x" , a.longValue(), b.longValue(), c.longValue()));
		mix();
		//System.out.println(String.format("A : %16x, B : %16x, C : %16x" , a.longValue(), b.longValue(), c.longValue()));
		return c.intValue(); 
	}
	private void mix() {
		// siftleft 는 괜찮지만.. right 는 부호로 인한 문제가 생긴다.
		a = convertUnsignedLong(BigInteger.valueOf((a.longValue() - b.longValue() - c.longValue()) ^ convertUnsignedLong(c.shiftRight(43)).longValue()));
		b = convertUnsignedLong(BigInteger.valueOf((b.longValue() - c.longValue() - a.longValue()) ^ convertUnsignedLong(a.shiftLeft(9)).longValue()));
		c = convertUnsignedLong(BigInteger.valueOf((c.longValue() - a.longValue() - b.longValue()) ^ convertUnsignedLong(b.shiftRight(8)).longValue()));
		a = convertUnsignedLong(BigInteger.valueOf((a.longValue() - b.longValue() - c.longValue()) ^ convertUnsignedLong(c.shiftRight(38)).longValue()));
		b = convertUnsignedLong(BigInteger.valueOf((b.longValue() - c.longValue() - a.longValue()) ^ convertUnsignedLong(a.shiftLeft(23)).longValue()));
		c = convertUnsignedLong(BigInteger.valueOf((c.longValue() - a.longValue() - b.longValue()) ^ convertUnsignedLong(b.shiftRight(5)).longValue()));
		a = convertUnsignedLong(BigInteger.valueOf((a.longValue() - b.longValue() - c.longValue()) ^ convertUnsignedLong(c.shiftRight(35)).longValue()));
		b = convertUnsignedLong(BigInteger.valueOf((b.longValue() - c.longValue() - a.longValue()) ^ convertUnsignedLong(a.shiftLeft(49)).longValue()));
		c = convertUnsignedLong(BigInteger.valueOf((c.longValue() - a.longValue() - b.longValue()) ^ convertUnsignedLong(b.shiftRight(11)).longValue()));
		a = convertUnsignedLong(BigInteger.valueOf((a.longValue() - b.longValue() - c.longValue()) ^ convertUnsignedLong(c.shiftRight(12)).longValue()));
		b = convertUnsignedLong(BigInteger.valueOf((b.longValue() - c.longValue() - a.longValue()) ^ convertUnsignedLong(a.shiftLeft(18)).longValue()));
		c = convertUnsignedLong(BigInteger.valueOf((c.longValue() - a.longValue() - b.longValue()) ^ convertUnsignedLong(b.shiftRight(22)).longValue()));
	}
}
