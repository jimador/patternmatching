package org.jamesd;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class MatcherTest {

    enum Foo {
        FOO, BAR, BAZ;

        Foo() {
            Lookup.cache.put(this, this.name());
        }

        static class Lookup {
            private static Map<Foo, String> cache = new HashMap<>();
        }

        static String get(Foo foo) {
            return Lookup.cache.get(foo);
        }
    }

    private TypeHandler matcher = TypeHandler.forType(Double.class)
                                             .when(Double.class).then(d -> d * d)
                                             .when(String.class).then(Double::valueOf)
                                             .when(Integer.class).then(Double::valueOf)
                                             .when(SomeData.class).then(someData -> {
                                                System.out.println(someData.text);
                                                    return someData.value;
                                             })
                                             .when(Date.class).then(d -> (double) d.getTime())
                                             .when(Float.class).then(f -> (double) f)
            ;

    double d = 2.0;

    private TypeHandler fooClassMatcher = TypeHandler.forType(String.class)
                                                     .when(Foo.FOO.getClass())
                                                     .then(Foo::get);

    @Test
    public void testDouble() {
        assertEquals(Double.valueOf(4.0), matcher.apply(d));
    }

    @Test
    public void testString() {
        assertEquals(Double.valueOf(4.0), matcher.apply("4"));
    }

    @Test
    public void testInt() {
        assertEquals(Double.valueOf(2.0), matcher.apply(2));
    }

    @Test
    public void testSomeData() {
        SomeData sd = new SomeData("poo", 42.0);
        assertEquals(Double.valueOf(42.0), matcher.apply(sd));
    }

    @Test
    public void testDateAsTimeStamp() {
        Timestamp ts = Timestamp.from(Instant.EPOCH);
        assertEquals((double)ts.getTime(), matcher.apply(ts));
    }

    @Test
    public void fooTest() {
        assertEquals(Foo.FOO.name(), fooClassMatcher.apply(Foo.FOO));
    }

    @Test
    public void barNegTest() {
        assertNotEquals(Foo.BAR.name(), fooClassMatcher.apply(Foo.FOO));
    }

    @Test
    public void failTest() {
        try {
            matcher.apply(1L);
        } catch (NullPointerException e) {
            assertTrue(e.getLocalizedMessage().contains("Could not find resolver for type: "));
            return;
        }
        fail();
    }

    class SomeData {
        public final String text;
        public final Double value;

        SomeData(String text, Double value) {
            this.text = text;
            this.value = value;
        }
    }

}