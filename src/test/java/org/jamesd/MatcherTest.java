package org.jamesd;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MatcherTest {

    private Matcher<Double> matcher = new Matcher<Double>()
            .on(Double.class).then(d -> d * d)
            .on(String.class).then(Double::valueOf)
            .on(Integer.class).then(Double::new)
            .on(SomeData.class).then(someData -> {
                System.out.println(someData.text);
                return someData.value;
            })
            ;

    @Test
    public void testDouble() {
        assertEquals(Double.valueOf(4.0), matcher.apply(2.0));
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
        assertEquals(Double.valueOf(42.0), matcher.apply(sd) );
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