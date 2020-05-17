package ch.jbert.utils;

import java.util.function.Predicate;

/**
 * A special function interface that allows throwing {@link Exception}s. <p/>
 *
 * It implements the default {@code test} method by catching all exceptions and
 * rethrowing them wrapped in the {@link UncheckedException}. It will ever only
 * throw this exception, carrying the actual exception as its {@link
 * Exception#getCause() cause}.<p/>
 *
 * This can be useful when dealing with checked exceptions inside functional
 * interfaces, like {@link java.util.stream.Stream}. Example:
 *
 * <pre>
 *     try {
 *         stream.filter(ThrowingPredicate.of(param -> doSomethingThatThrows(param)));
 *     } catch (UncheckedException e) {
 *         Throwables.propagateIfPossible(e.getCause(), IOException.class);
 *         throw e;
 *     }
 * </pre>
 *
 * This would rethrow the checked {@code IOException} and all unchecked
 * exceptions. If {@code doSomethingThatThrows} would throw another checked
 * exception, then this would be rethrown in an {@link UncheckedException}.<p/>
 *
 * The advantage is, that the catching code is not required to show up in every
 * lambda. The downside is, that the compiler cannot detect whether all checked
 * exceptions are rethrown properly.
 */
@FunctionalInterface
public interface ThrowingPredicate<A> extends Predicate<A> {
    boolean unsafeTest(A a) throws Exception;

    default boolean test(A a) {
        return UncheckedException.wrap(() -> unsafeTest(a));
    }

    /**
     * This is useful when you want to use a {@link ThrowingPredicate} for your
     * lambda in a place that expects a {@link Predicate}:
     *
     * {@code Stream.of(1,2,3).filter(ThrowingPredicate.of(n -> n % 2 == 0))}
     */
    static <A> ThrowingPredicate<A> of(ThrowingPredicate<A> p) {
        return p;
    }
}
