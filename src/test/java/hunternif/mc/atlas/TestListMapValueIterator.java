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
		assertIteration(map, "lolwutomgwtfbbq");
		int i = 0;
		for (Iterator<String> iter = new ListMapValueIterator<String>(map); iter.hasNext(); iter.next()) {
			i++;
		}
		assertEquals(5, i);
	}
	
	@Test
	public void testRemove() {
		String[] answers = {"wutomgwtfbbq", "lolomgwtfbbq", "lolwutwtfbbq", "lolwutomgbbq", "lolwutomgwtf"};
		for (int i = 0; i < answers.length; i++) {
			init();
			int j = 0;
			Iterator<String> iter = new ListMapValueIterator<String>(map);
			while (j <= i) {
				j++;
				iter.next();
			}
			iter.remove();
			assertIteration(map, answers[i]);
		}
	}
	
	@Test
	public void testInsert() {
		map.get(0).add(0, "@");
		assertIteration(map, "@lolwutomgwtfbbq");
		map.get(0).add(2, "@");
		assertIteration(map, "@lol@wutomgwtfbbq");
		map.get(0).add(4, "@");
		assertIteration(map, "@lol@wut@omgwtfbbq");
		map.get(2).add(0, "@");
		assertIteration(map, "@lol@wut@@omgwtfbbq");
		map.get(2).add(2, "@");
		assertIteration(map, "@lol@wut@@omg@wtfbbq");
		map.get(3).add(0, "@");
		assertIteration(map, "@lol@wut@@omg@@wtfbbq");
		map.get(5).add(0, "@");
		assertIteration(map, "@lol@wut@@omg@@@wtfbbq");
		map.get(5).add(2, "@");
		assertIteration(map, "@lol@wut@@omg@@@wtf@bbq");
		map.get(5).add(4, "@");
		assertIteration(map, "@lol@wut@@omg@@@wtf@bbq@");
		map.get(10).add(0, "@");
		assertIteration(map, "@lol@wut@@omg@@@wtf@bbq@@");
	}
	
	@Test
	public void testSmallMap() {
		Map<Integer, List<String>> map2 = new HashMap<Integer, List<String>>();
		assertIteration(map2, "");
		List<String> list = new ArrayList<String>(Arrays.asList("lol"));
		map2.put(0, list);
		assertIteration(map2, "lol");
		// Add and remove one item:
		list.add("wut");
		assertIteration(map2, "lolwut");
		list.remove("wut");
		assertIteration(map2, "lol");
		list.add("wut");
		assertIteration(map2, "lolwut");
		list.remove("lol");
		assertIteration(map2, "wut");
		list.remove("wut");
		assertIteration(map2, "");
	}
	
	/** Assert that iterating through the map while concatenating found values
	 * to a string will produce the specified string. */
	private static <E> void assertIteration(Map<?, List<E>> map, String result) {
		Iterator<E> iter = new ListMapValueIterator<E>(map);
		String output = "";
		while (iter.hasNext()) {
			output += iter.next();
		}
		assertEquals(result, output);
	}
}
