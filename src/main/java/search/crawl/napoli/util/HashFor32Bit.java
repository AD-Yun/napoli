package search.crawl.napoli.util;

//public class HashFor32Bit implements HashFunction {
public class HashFor32Bit {
	public static final int NATE_SEARCH_INITVAL = 1009;
	
	/**
	 * 정수형 Mask. 자바에는 부호 없는 정수형이 없기 때문에 long에 정수 Mask를 씌우는 방법을 이용한다.
	 */
	private static long INT_MASK = 0x00000000ffffffffL;
	/**
	 * 1 바이트 Mask.
	 */
	private static long BYTE_MASK = 0x00000000000000ffL;
	/**
	 * We use longs because we don't have unsigned ints
	 */
	private long a, b, c;
	

	public int hash( byte[] bytes, int initval ){
		return hash( bytes, bytes.length, initval );
	}
	public int hash( byte[] bytes ){		
		return hash( bytes, bytes.length, NATE_SEARCH_INITVAL );
	}
	
	public int hash( byte[] bytes, int length, int initval )
	{
		int len = length;
		a = b = 0x000000009e3779b9L;
		c = initval;

		int offset = 0;
		for( ; len >= 12; offset += 12, len -= 12 ) {
			a = (a + (bytes[offset + 0] & BYTE_MASK)) & INT_MASK;
			a = (a + (((bytes[offset + 1] & BYTE_MASK) << 8) & INT_MASK)) & INT_MASK;
			a = (a + (((bytes[offset + 2] & BYTE_MASK) << 16) & INT_MASK)) & INT_MASK;
			a = (a + (((bytes[offset + 3] & BYTE_MASK) << 24) & INT_MASK)) & INT_MASK;
			b = (b + (bytes[offset + 4] & BYTE_MASK)) & INT_MASK;
			b = (b + (((bytes[offset + 5] & BYTE_MASK) << 8) & INT_MASK)) & INT_MASK;
			b = (b + (((bytes[offset + 6] & BYTE_MASK) << 16) & INT_MASK)) & INT_MASK;
			b = (b + (((bytes[offset + 7] & BYTE_MASK) << 24) & INT_MASK)) & INT_MASK;
			c = (c + (bytes[offset + 8] & BYTE_MASK)) & INT_MASK;
			c = (c + (((bytes[offset + 9] & BYTE_MASK) << 8) & INT_MASK)) & INT_MASK;
			c = (c + (((bytes[offset + 10] & BYTE_MASK) << 16) & INT_MASK)) & INT_MASK;
			c = (c + (((bytes[offset + 11] & BYTE_MASK) << 24) & INT_MASK)) & INT_MASK;
			mix();
		}
		c = c + length;

		switch( len ) { // all the case statements fall through
			case 11:
				c = ( c + ( ( bytes[offset + 10] & BYTE_MASK ) << 24 & INT_MASK ) ) & INT_MASK;
			case 10:
				c = ( c + ( ( ( bytes[offset + 9] & BYTE_MASK ) << 16 ) & INT_MASK ) ) & INT_MASK;
			case 9:
				c = ( c + ( ( ( bytes[offset + 8] & BYTE_MASK ) << 8 ) & INT_MASK ) ) & INT_MASK;
			case 8:
				b = ( b + ( ( ( bytes[offset + 7] & BYTE_MASK ) << 24 ) & INT_MASK ) ) & INT_MASK;
			case 7:
				b = ( b + ( ( ( bytes[offset + 6] & BYTE_MASK ) << 16 ) & INT_MASK ) ) & INT_MASK;
			case 6:
				b = ( b + ( ( ( bytes[offset + 5] & BYTE_MASK ) << 8 ) & INT_MASK ) ) & INT_MASK;
			case 5:
				b = ( b + ( bytes[offset + 4] & BYTE_MASK ) ) & INT_MASK;
			case 4:
				a = ( a + ( ( ( bytes[offset + 3] & BYTE_MASK ) << 24 ) & INT_MASK ) ) & INT_MASK;
			case 3:
				a = ( a + ( ( ( bytes[offset + 2] & BYTE_MASK ) << 16 ) & INT_MASK ) ) & INT_MASK;
			case 2:
				a = ( a + ( ( ( bytes[offset + 1] & BYTE_MASK ) << 8 ) & INT_MASK ) ) & INT_MASK;
			case 1:
				a = ( a + ( bytes[offset + 0] & BYTE_MASK ) ) & INT_MASK;
		}

		mix();

		return ( int ) ( c & INT_MASK );
	}
	private void mix() {
		a = ( a - b ) & INT_MASK;
		a = ( a - c ) & INT_MASK;
		a = a ^ (( c >> 13 ) & INT_MASK );
		b = ( b - c ) & INT_MASK;
		b = ( b - a ) & INT_MASK;
		b = b ^ ( ( a << 8 ) & INT_MASK );
		c = ( c - a ) & INT_MASK;
		c = ( c - b ) & INT_MASK;
		c = c ^ ( ( b >> 13 ) & INT_MASK );
		a = ( a - b ) & INT_MASK;
		a = ( a - c ) & INT_MASK;
		a = a ^ ( ( c >> 12 ) & INT_MASK );
		b = ( b - c ) & INT_MASK;
		b = ( b - a ) & INT_MASK;
		b = b ^ ( ( a << 16 ) & INT_MASK );
		c = ( c - a ) & INT_MASK;
		c = ( c - b ) & INT_MASK;
		c = c ^ ( ( b >> 5 ) & INT_MASK );
		a = ( a - b ) & INT_MASK;
		a = ( a - c ) & INT_MASK;
		a = a ^ ( ( c >> 3 ) & INT_MASK );
		b = ( b - c ) & INT_MASK;
		b = ( b - a ) & INT_MASK;
		b = b ^ ( ( a << 10 ) & INT_MASK );
		c = ( c - a ) & INT_MASK;
		c = ( c - b ) & INT_MASK;
		c = c ^ ( ( b >> 15 ) & INT_MASK );
	}
}
