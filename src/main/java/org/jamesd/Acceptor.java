package org.jamesd;

import java.util.function.Function;

public interface Acceptor<B, R> {
    ClassMatcher<R> then(Function<B, R> function);
}
