package org.jamesd;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public final class TypeHandler<R> implements Function<Object, R> {
    private final Map<Class<?>, Function<Object, R>> fMap = new HashMap<>();

    private TypeHandler() {
    }

    public static <R> TypeHandler<R> forType(Class<R> clazz) {
        return new TypeHandler<>();
    }

    public <B> TypeAcceptor<B, R> when(Class<B> clazz) {
        return new TypeAcceptor<>(this, clazz);
    }

    public <B> TypeAcceptor<B, R> whenNotMatched() {
        return new TypeAcceptor<>(this);
    }

    @Override
    public R apply(Object o) {
        return Optional.ofNullable(fMap.get(o.getClass()))
                       .map(f -> f.apply(o))
                       .orElseGet(() -> Optional.ofNullable(fMap.get(Default.class))
                                                .map(f -> f.apply(o))
                                                .orElseThrow(() -> new NullPointerException("Could not find resolver for type: " + o.getClass().getSimpleName())));
    }

    public static class TypeAcceptor<B, R> {
        private final TypeHandler matcher;
        private final Class clazz;

        TypeAcceptor(TypeHandler matcher, Class<B> clazz) {
            this.matcher = matcher;
            this.clazz = clazz;
        }

        TypeAcceptor(TypeHandler resolver) {
            this.matcher = resolver;
            this.clazz = Default.class;
        }

        @SuppressWarnings("unchecked")
        public TypeHandler<R> then(Function<B, R> function) {
            matcher.fMap.put(clazz, function);
            return matcher;
        }
    }

    private static class Default {
    }
}
