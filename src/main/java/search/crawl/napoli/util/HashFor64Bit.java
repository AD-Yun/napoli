package search.crawl.napoli.util;

//public class HashFor32Bit implements HashFunction {
public class HashFor64Bit {
	public static final int NATE_SEARCH_INITVAL = 0;
	
	/**
	 * 정수형 Mask. 자바에는 부호 없는 정수형이 없기 때문에 long에 정수 Mask를 씌우는 방법을 이용한다.
	 */
	private static long INT_MASK = 0xffffffffffffffffL;
	//private static long INT_MASK = (1 << 63) -1;
	/**
	 * 1 바이트 Mask.
	 */
	private static long BYTE_MASK = 0x00000000000000ffL;
	/**
	 * We use longs because we don't have unsigned ints
	 */
	private long a, b, c;
	

	public long hash(byte[] bytes, int initval) {
		return hash(bytes, bytes.length, initval);
	}
	public long hash(byte[] bytes) {		
		return hash(bytes, bytes.length, NATE_SEARCH_INITVAL);
	}
	
	public long hash(byte[] bytes, int length, int initval) {
		int len = length;
		a = b = initval;
		c = 0x9e3779b97f4a7c13L;

		int offset = 0;
		for( ; len >= 24; offset += 24, len -= 24 ) {
			a = (a + (bytes[offset + 0] & BYTE_MASK)) & INT_MASK;
			a = (a + (((bytes[offset + 1] & BYTE_MASK) << 8) & INT_MASK)) & INT_MASK;
			a = (a + (((bytes[offset + 2] & BYTE_MASK) << 16) & INT_MASK)) & INT_MASK;
			a = (a + (((bytes[offset + 3] & BYTE_MASK) << 24) & INT_MASK)) & INT_MASK;
			a = (a + (((bytes[offset + 4] & BYTE_MASK) << 32) & INT_MASK)) & INT_MASK;
			a = (a + (((bytes[offset + 5] & BYTE_MASK) << 40) & INT_MASK)) & INT_MASK;
			a = (a + (((bytes[offset + 6] & BYTE_MASK) << 48) & INT_MASK)) & INT_MASK;
			a = (a + (((bytes[offset + 7] & BYTE_MASK) << 56) & INT_MASK)) & INT_MASK;
			b = (b + (bytes[offset + 8] & BYTE_MASK)) & INT_MASK;
			b = (b + (((bytes[offset + 9] & BYTE_MASK) << 8) & INT_MASK)) & INT_MASK;
			b = (b + (((bytes[offset + 10] & BYTE_MASK) << 16) & INT_MASK)) & INT_MASK;
			b = (b + (((bytes[offset + 11] & BYTE_MASK) << 24) & INT_MASK)) & INT_MASK;
			b = (b + (((bytes[offset + 12] & BYTE_MASK) << 32) & INT_MASK)) & INT_MASK;
			b = (b + (((bytes[offset + 13] & BYTE_MASK) << 40) & INT_MASK)) & INT_MASK;
			b = (b + (((bytes[offset + 14] & BYTE_MASK) << 48) & INT_MASK)) & INT_MASK;
			b = (b + (((bytes[offset + 15] & BYTE_MASK) << 56) & INT_MASK)) & INT_MASK;
			c = (c + (bytes[offset + 16] & BYTE_MASK)) & INT_MASK;
			c = (c + (((bytes[offset + 17] & BYTE_MASK) << 8) & INT_MASK)) & INT_MASK;
			c = (c + (((bytes[offset + 18] & BYTE_MASK) << 16) & INT_MASK)) & INT_MASK;
			c = (c + (((bytes[offset + 19] & BYTE_MASK) << 24) & INT_MASK)) & INT_MASK;
			c = (c + (((bytes[offset + 20] & BYTE_MASK) << 32) & INT_MASK)) & INT_MASK;
			c = (c + (((bytes[offset + 21] & BYTE_MASK) << 40) & INT_MASK)) & INT_MASK;
			c = (c + (((bytes[offset + 22] & BYTE_MASK) << 48) & INT_MASK)) & INT_MASK;
			c = (c + (((bytes[offset + 23] & BYTE_MASK) << 56) & INT_MASK)) & INT_MASK;
			System.out.println(String.format("IN LOOP - A : %16x, B : %16x, C : %16x" , a, b, c));
			mix();
			System.out.println(String.format("IN LOOP (mix) - A : %16x, B : %16x, C : %16x" , a, b, c));
		}
		System.out.println(String.format("A : %16x, B : %16x, C : %16x" , a, b, c));
		c = c + length;
		System.out.println(String.format("A : %16x, B : %16x, C : %16x" , a, b, c));
		
		switch( len ) { // all the case statements fall through
			case 23:
				c = (c + (( bytes[offset + 22] & BYTE_MASK ) << 56 & INT_MASK)) & INT_MASK;
			case 22:
				c = (c + ((( bytes[offset + 21] & BYTE_MASK ) << 48) & INT_MASK)) & INT_MASK;
			case 21:
				c = (c + ((( bytes[offset + 20] & BYTE_MASK ) << 40) & INT_MASK)) & INT_MASK;
			case 20:
				c = (c + ((( bytes[offset + 19] & BYTE_MASK ) << 32) & INT_MASK)) & INT_MASK;
			case 19:
				c = (c + ((( bytes[offset + 18] & BYTE_MASK ) << 24) & INT_MASK)) & INT_MASK;
			case 18:
				c = (c + ((( bytes[offset + 17] & BYTE_MASK ) << 16) & INT_MASK)) & INT_MASK;
			case 17:
				c = (c + ((( bytes[offset + 16] & BYTE_MASK ) << 8) & INT_MASK)) & INT_MASK;
			case 16:
				b = (b + (((bytes[offset + 15] & BYTE_MASK ) << 56) & INT_MASK)) & INT_MASK;
			case 15:
				b = (b + (((bytes[offset + 14] & BYTE_MASK ) << 48) & INT_MASK)) & INT_MASK;
			case 14:
				b = (b + (((bytes[offset + 13] & BYTE_MASK ) << 40) & INT_MASK)) & INT_MASK;
			case 13:
				b = (b + (((bytes[offset + 12] & BYTE_MASK ) << 32) & INT_MASK)) & INT_MASK;
			case 12:
				b = (b + (((bytes[offset + 11] & BYTE_MASK ) << 24) & INT_MASK)) & INT_MASK;
			case 11:
				b = (b + (((bytes[offset + 10] & BYTE_MASK ) << 16) & INT_MASK)) & INT_MASK;
			case 10:
				b = (b + (((bytes[offset + 9] & BYTE_MASK ) << 8) & INT_MASK)) & INT_MASK;
			case 9:
				b = (b + ( bytes[offset + 8] & BYTE_MASK ) ) & INT_MASK;
			case 8:
				a = (a + ((bytes[offset + 7] & BYTE_MASK ) << 56 & INT_MASK)) & INT_MASK;
			case 7:
				a = (a + (((bytes[offset + 6] & BYTE_MASK ) << 48) & INT_MASK)) & INT_MASK;
			case 6:
				a = (a + (((bytes[offset + 5] & BYTE_MASK ) << 40) & INT_MASK)) & INT_MASK;
			case 5:
				a = (a + (((bytes[offset + 4] & BYTE_MASK ) << 32) & INT_MASK)) & INT_MASK;
			case 4:
				a = (a + (((bytes[offset + 3] & BYTE_MASK ) << 24) & INT_MASK)) & INT_MASK;
			case 3:
				a = (a + (((bytes[offset + 2] & BYTE_MASK ) << 16) & INT_MASK)) & INT_MASK;
			case 2:
				a = (a + (((bytes[offset + 1] & BYTE_MASK ) << 8) & INT_MASK)) & INT_MASK;
			case 1:
				a = (a + ( bytes[offset + 0] & BYTE_MASK ) ) & INT_MASK;
		}
		System.out.println(String.format("A : %16x, B : %16x, C : %16x" , a, b, c));
		mix();
		System.out.println(String.format("A : %16x, B : %16x, C : %16x" , a, b, c));
		return ( int ) ( c & INT_MASK );
	}
	private void mix()
	{
		a = (a - b) & INT_MASK;
		System.out.println(String.format("MIX = A : %16x, B : %16x, C : %16x" , a, b, c));
		a = (a - c) & INT_MASK;
		System.out.println(String.format("MIX = A : %16x, B : %16x, C : %d" , a, b, c));
		a = a ^ (((c& INT_MASK) >> 43) & INT_MASK);
		
		System.out.println(String.format("MIX = C >> 43 : %x" , ((c >> 43) & INT_MASK)));
		System.out.println(String.format("MIX = C >> 43 : %x" , (((c & INT_MASK)>> 43) & INT_MASK)));
		
		System.out.println(String.format("MIX = C >> 43 : %d" , (c >> 43)));
		System.out.println(String.format("MIX = A : %16x, B : %16x, C : %16x" , a, b, c));
		b = (b - c) & INT_MASK;
		b = (b - a) & INT_MASK;
		b = b ^ ((a << 9) & INT_MASK );
		System.out.println(String.format("MIX = A : %16x, B : %16x, C : %16x" , a, b, c));
		c = (c - a) & INT_MASK;
		c = (c - b) & INT_MASK;
		c = c ^ ((b >> 8) & INT_MASK);
		System.out.println(String.format("MIX = A : %16x, B : %16x, C : %16x" , a, b, c));
		a = (a - b) & INT_MASK;
		a = (a - c) & INT_MASK;
		a = a ^ ((c >> 38) & INT_MASK);
		b = (b - c) & INT_MASK;
		b = (b - a) & INT_MASK;
		b = b ^ ((a << 23) & INT_MASK );
		c = (c - a) & INT_MASK;
		c = (c - b) & INT_MASK;
		c = c ^ ((b >> 5) & INT_MASK);
		a = (a - b) & INT_MASK;
		a = (a - c) & INT_MASK;
		a = a ^ ((c >> 35) & INT_MASK);
		b = (b - c) & INT_MASK;
		b = (b - a) & INT_MASK;
		b = b ^ ((a << 49) & INT_MASK );
		c = (c - a) & INT_MASK;
		c = (c - b) & INT_MASK;
		c = c ^ ((b >> 11) & INT_MASK);
		a = (a - b) & INT_MASK;
		a = (a - c) & INT_MASK;
		a = a ^ ((c >> 12) & INT_MASK);
		b = (b - c) & INT_MASK;
		b = (b - a) & INT_MASK;
		b = b ^ ((a << 18) & INT_MASK );
		c = (c - a) & INT_MASK;
		c = (c - b) & INT_MASK;
		c = c ^ ((b >> 22) & INT_MASK);
		
	}
}
