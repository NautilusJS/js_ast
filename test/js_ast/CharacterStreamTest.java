package js_ast;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

import com.mindlin.jsast.impl.util.CharacterArrayStream;
import com.mindlin.jsast.impl.util.CharacterStream;

public class CharacterStreamTest {
	@Test
	public void testNextPrev() {
		CharacterStream chars = new CharacterArrayStream("123456789");
		assertEquals(chars.next(), '1');
		System.out.println(chars.position());
		assertEquals(chars.next(), '2');
		System.out.println(chars.position());
		assertEquals(chars.prev(), '1');
		System.out.println(chars.position());
		assertEquals(chars.next(), '2');
		System.out.println(chars.position());
	}
	
	@Test
	public void testNextCurrent() {
		CharacterStream chars = new CharacterArrayStream("123456789");
		assertEquals('1', chars.next());
		System.out.println(chars.position());
		assertEquals('1', chars.current());
	}
	
	@Test
	public void testSkipAndGet() {
		CharacterArrayStream chars = new CharacterArrayStream("abcde");
		assertEquals('a', chars.next());
		Assert.assertNotNull(chars.skip(1));
		assertEquals('b', chars.current());
		assertEquals('c', chars.next());
	}
	
}
