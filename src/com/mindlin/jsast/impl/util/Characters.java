package com.mindlin.jsast.impl.util;

public final class Characters {
	public static final char NULL = '\0',
		SOH = '\u0001',
		STX = '\u0002',
		ETX = '\u0003',
		EOT = '\u0004',
		ENQ = '\u0005',
		ACK = '\u0006',
		BEL = '\u0007',
		BS  = '\u0008',
		TAB = '\u0009',
		LF  = '\n',
		VT  = '\u000b',
		FF  = '\u000c',
		CR  = '\r',
		SO  = '\u000e',
		SI  = '\u000f'
		
		//SPACE = '\u0020',
		;
	public static final char[] JS_WHITESPACE = new char[] {
			TAB,
			LF,
			VT, // tabulation line
			FF, // ff (ctrl-l)
			CR,
			' ',
			'\u00a0', // Latin-1 space
			'\u1680', // Ogham space mark
			'\u180e', // separator, Mongolian vowel
			'\u2000', // en quad
			'\u2001', // em quad
			'\u2002', // en space
			'\u2003', // em space
			'\u2004', // three-per-em space
			'\u2005', // four-per-em space
			'\u2006', // six-per-em space
			'\u2007', // figure space
			'\u2008', // punctuation space
			'\u2009', // thin space
			'\u200a', // hair space
			'\u202f', // narrow no-break space
			'\u205f', // medium mathematical space
			'\u3000', // ideographic space
			'\ufeff' // byte order mark
	};
	public static boolean isJsWhitespace(final char c) {
		//binary search
		int lo = 0;
	    int hi = JS_WHITESPACE.length - 1;
	    while (lo <= hi) {
	        // Key is in a[lo..hi] or not present.
	        int mid = lo + (hi - lo) / 2;
			if (c < JS_WHITESPACE[mid])
				hi = mid - 1;
			else if (c > JS_WHITESPACE[mid])
				lo = mid + 1;
			else
				return true;
	    }
	    return false;
	}
	public static boolean canStartIdentifier(final char c) {
		return Character.isJavaIdentifierStart(c);
	}
}