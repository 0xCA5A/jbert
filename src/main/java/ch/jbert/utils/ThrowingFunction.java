package ch.jbert.utils;

import java.util.function.Function;

/**
 * A special function interface that allows throwing {@link Exception}s. <p/>
 *
 * It implements the default {@code apply} method by catching all exceptions and
 * rethrowing them wrapped in the {@link UncheckedException}. It will ever only
 * throw this exception, carrying the actual exception as its {@link
 * Exception#getCause() cause}.<p/>
 *
 * This can be useful when dealing with checked exceptions inside functional
 * interfaces, like {@link java.util.stream.Stream}. Example:
 *
 * <pre>
 *     try {
 *         stream.map(ThrowingConsumer.of(param -> doSomethingThatThrows(param)));
 *     } catch (UncheckedException e) {
 *         Throwables.propagateIfPossible(e.getCause(), IOException.class);
 *         throw e;
 *     }
 * </pre>
 *
 * This would rethrow the checked {@code IOException} and all unchecked
 * exceptions. If {@code doSomethingThatHrows} would throw another checked
 * exception, then this would be rethrown in an {@link UncheckedException}.<p/>
 *
 * The advantage is, that the catching code is not required to show up in every
 * lambda. The downside is, that the compiler cannot detect whether all checked
 * exceptions are rethrown properly.
 */
@FunctionalInterface
public interface ThrowingFunction<A, B> extends Function<A, B> {
    B unsafeApply(A a) throws Exception;

    default B apply(A a) {
        return UncheckedException.wrap(() -> unsafeApply(a));
    }

    /**
     * This is useful when you want to use a {@link ThrowingFunction} for your
     * lambda in a place that expects a {@link Function}:
     *
     * {@code Stream.of(1,2,3).map(ThrowingFunction.of(n -> 2 / n))}
     */
    static <A, B> ThrowingFunction<A, B> of(ThrowingFunction<A, B> f) {
        return f;
    }
}
