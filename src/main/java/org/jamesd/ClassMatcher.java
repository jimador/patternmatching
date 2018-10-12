package org.jamesd;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public final class ClassMatcher<R> implements Function<Object, R> {
    private final Map<Class<?>, Function<Object, R>> fMap = new HashMap<>();

    public <B> Acceptor<B,R> on(Class<B> clazz) {
        return new ClassAcceptor<>(this, clazz);
    }

    public R apply(Object o) {
        return Optional.ofNullable(fMap.get(o.getClass()))
                       .map(f -> f.apply(o))
                       .orElseThrow(() -> new NullPointerException("No function for for class: " + o.getClass().getSimpleName()));
    }

    static class ClassAcceptor<B,R> implements Acceptor<B,R>{
        private final ClassMatcher matcher;
        private final Class<B> clazz;

        ClassAcceptor(ClassMatcher matcher, Class<B> clazz) {
            this.matcher = matcher;
            this.clazz = clazz;
        }

        @Override
        @SuppressWarnings("unchecked")
        public ClassMatcher<R> then(Function<B, R> function) {
            matcher.fMap.put(clazz, function);
            return matcher;
        }
    }
}
