package javaPackage;

import student.TestCase;

public class LinkedChainTest extends TestCase {
    private LinkedChain<String> chain;

    public void setUp() {
        chain = new LinkedChain<String>();
    }

    public void testAdd() {
        assertTrue(chain.add("a"));
        assertEquals("a", chain.get(0));
        chain.add("b");
        assertEquals("[b, a]", chain.toString());
        try {
            chain.add(null);
            fail("Null must be rejected");
        }
        catch (IllegalArgumentException expected) {
            assertEquals(2, chain.size());
        }
    }

    public void testAddToEnd() {
        chain.addToEnd("a");
        chain.addToEnd("b");
        assertEquals("[a, b]", chain.toString());
        try {
            chain.addToEnd(null);
            fail("Null must be rejected");
        }
        catch (IllegalArgumentException expected) {
            assertEquals(2, chain.size());
        }
        chain.remove("b");
        chain.addToEnd("c");
        assertEquals("[a, c]", chain.toString());
        chain.clear();
        chain.addToEnd("d");
        assertEquals("d", chain.remove());
        chain.addToEnd("e");
        assertEquals("e", chain.get(0));
    }

    public void testIndexedAdd() {
        chain.add(0, "a");
        chain.add(1, "c");
        chain.add(1, "b");
        assertEquals("[a, b, c]", chain.toString());
        for (int index : new int[] {-1, 4}) {
            try {
                chain.add(index, "bad");
                fail("Invalid index");
            }
            catch (IndexOutOfBoundsException expected) {
                assertEquals(3, chain.size());
            }
        }
        try {
            chain.add(1, null);
            fail("Null must be rejected");
        }
        catch (IllegalArgumentException expected) {
            assertEquals(3, chain.size());
        }
    }

    public void testRemove() {
        assertNull(chain.remove());
        chain.add("a");
        assertEquals("a", chain.remove());
        assertTrue(chain.isEmpty());
        chain.addToEnd("b");
        assertEquals("b", chain.get(0));
    }

    public void testRemoveEntry() {
        assertFalse(chain.remove("missing"));
        assertFalse(chain.remove((String)null));
        chain.addToEnd("a");
        chain.addToEnd("b");
        chain.addToEnd("c");
        assertTrue(chain.remove("b"));
        assertEquals("[a, c]", chain.toString());
        assertFalse(chain.remove("b"));
        assertTrue(chain.remove("a"));
        assertTrue(chain.remove("c"));
        chain.addToEnd("d");
        assertEquals("[d]", chain.toString());
    }

    public void testIndexedRemoveAndGet() {
        try {
            chain.remove(0);
            fail("Empty chain");
        }
        catch (IndexOutOfBoundsException expected) {
            assertTrue(chain.isEmpty());
        }
        chain.addToEnd("a");
        chain.addToEnd("b");
        chain.addToEnd("c");
        assertEquals("b", chain.get(1));
        assertEquals("b", chain.remove(1));
        assertEquals("c", chain.remove(1));
        chain.addToEnd("d");
        assertEquals("d", chain.get(1));
        for (int index : new int[] {-1, 2}) {
            try {
                chain.get(index);
                fail("Invalid get index");
            }
            catch (IndexOutOfBoundsException expected) {
                assertEquals(2, chain.size());
            }
            try {
                chain.remove(index);
                fail("Invalid remove index");
            }
            catch (IndexOutOfBoundsException expected) {
                assertEquals(2, chain.size());
            }
        }
        assertEquals("a", chain.remove(0));
    }

    public void testQueriesAndClear() {
        assertEquals(0, chain.size());
        assertTrue(chain.isEmpty());
        assertFalse(chain.contains(null));
        assertEquals(0, chain.count("a"));
        assertEquals(0, chain.count(null));
        assertEquals("[]", chain.toString());
        chain.clear();
        chain.add("a");
        chain.add("a");
        assertEquals(2, chain.size());
        assertFalse(chain.isEmpty());
        assertTrue(chain.contains("a"));
        assertFalse(chain.contains("missing"));
        assertEquals(2, chain.count("a"));
        chain.clear();
        assertEquals(0, chain.size());
        assertTrue(chain.isEmpty());
    }

    public void testToArray() {
        assertEquals(0, chain.toArray(new String[0]).length);
        chain.addToEnd("a");
        chain.addToEnd("b");
        String[] values = new String[2];
        assertSame(values, chain.toArray(values));
        assertEquals("a", values[0]);
        assertEquals("b", values[1]);
        for (String[] bad : new String[][] {null, new String[1]}) {
            try {
                chain.toArray(bad);
                fail("Invalid array");
            }
            catch (IllegalArgumentException expected) {
                assertEquals(2, chain.size());
            }
        }
    }

    public void testReuseWithApplicationTypes() {
        LinkedChain<Place> places = new LinkedChain<Place>();
        LinkedChain<Enemy> occupants = new LinkedChain<Enemy>();
        LinkedChain<Item> inventory = new LinkedChain<Item>();
        Dungeon dungeon = new Dungeon("Floor", 1, DungeonTest.map());
        Item item = new Item("Potion", 5, 0);
        places.addToEnd(dungeon);
        Enemy enemy = new Enemy(20, 5);
        occupants.addToEnd(enemy);
        occupants.addToEnd(item);
        inventory.addToEnd(item);
        assertSame(dungeon, places.get(0));
        assertSame(enemy, occupants.get(0));
        assertSame(item, occupants.get(1));
        assertSame(item, inventory.get(0));
    }
}
