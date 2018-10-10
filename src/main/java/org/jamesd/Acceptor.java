package org.jamesd;

import java.util.function.Function;

public interface Acceptor<B, R> {
    Matcher<R> then(Function<B, R> function);
}
