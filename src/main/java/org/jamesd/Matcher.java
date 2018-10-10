package org.jamesd;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class Matcher<R> implements Function<Object, R> {
    private final Map<Class<?>, Function<Object, R>> fMap = new HashMap<>();

    public <B> Acceptor<R,B> on(Class<B> clazz) {
        return new Acceptor<>(this, clazz);
    }

    public R apply(Object o) {
        return fMap.get(o.getClass()).apply(o);
    }

    static class Acceptor<R, B> {
        private final Matcher visitor;
        private final Class<B> clazz;

        Acceptor(Matcher visitor, Class<B> clazz) {
            this.visitor = visitor;
            this.clazz = clazz;
        }

        @SuppressWarnings("unchecked")
        public Matcher<R> then(Function<B, R> function) {
            visitor.fMap.put(clazz, function);
            return visitor;
        }
    }
}
