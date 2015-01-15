package hunternif.mc.atlas;

import hunternif.mc.atlas.util.ListMapValueIterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TestListMapValueIterator {
	private Map<Integer, List<String>> map;
	
	@Before
	public void init() {
		map = new HashMap<Integer, List<String>>();
		List<String> list = new ArrayList<String>(Arrays.asList("lol", "wut"));
		map.put(0, list);
		list = new ArrayList<String>(Arrays.asList("omg"));
		map.put(2, list);
		map.put(3, new ArrayList<String>());
		list = new ArrayList<String>(Arrays.asList("wtf", "bbq"));
		map.put(5, list);
		map.put(10, new ArrayList<String>());
	}
	
	@Test
	public void testSize() {
		Iterator<String> iter = new ListMapValueIterator<String>(map);
		String output = "";
		while (iter.hasNext()) {
			output += iter.next();
		}
		assertEquals("lolwutomgwtfbbq", output);
		int i = 0;
		for (iter = new ListMapValueIterator<String>(map); iter.hasNext(); iter.next()) {
			i++;
		}
		assertEquals(5, i);
	}
	
	@Test
	public void testRemove() {
		String[] answers = {"wutomgwtfbbq", "lolomgwtfbbq", "lolwutwtfbbq", "lolwutomgbbq", "lolwutomgwtf"};
		for (int i = 0; i < answers.length; i++) {
			init(); //TODO: write proper parameterized unit test
			int j = 0;
			Iterator<String> iter = new ListMapValueIterator<String>(map);
			while (j <= i) {
				j++;
				iter.next();
			}
			iter.remove();
			iter = new ListMapValueIterator<String>(map);
			String output = "";
			while (iter.hasNext()) {
				output += iter.next();
			}
			assertEquals(answers[i], output);
		}
	}
}
