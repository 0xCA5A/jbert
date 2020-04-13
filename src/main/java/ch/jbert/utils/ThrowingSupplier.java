package ch.jbert.utils;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * A special supplier interface that allows throwing {@link Exception}s.
 * <p/>
 *
 * It implements the default {@code get} method by catching all exceptions and
 * rethrowing them wrapped in the {@link UncheckedException}. It will ever only
 * throw this exception, carrying the actual exception as its {@link
 * Exception#getCause() cause}.<p/>
 *
 * This can be useful when dealing with checked exceptions inside functional
 * interfaces, like {@link java.util.stream.Stream}. Example:
 *
 * <pre>
 *     try {
 *         Stream.generate(ThrowingSupplier.of(() -> somethingThatThrows())).collect(Collectors.toList());
 *     } catch (UncheckedException e) {
 *         Throwables.propagateIfPossible(e.getCause(), IOException.class);
 *         throw e;
 *     }
 * </pre>
 *
 * This would rethrow the checked {@code IOException} and all unchecked
 * exceptions. If {@code somethingThatThrows} would throw another checked
 * exception, then this would be rethrown in an {@link UncheckedException}.<p/>
 *
 * The advantage is, that the catching code is not required to show up in every
 * lambda. The downside is, that the compiler cannot detect whether all checked
 * exceptions are rethrown properly.
 */
@FunctionalInterface
public interface ThrowingSupplier<A> extends Supplier<A>, Callable<A> {

    A unsafeGet() throws Exception;

    default A get() {
        return UncheckedException.wrap(this);
    }

    default A call() throws Exception {
        return unsafeGet();
    }

    default <B> ThrowingSupplier<B> map(ThrowingFunction<A, B> f) {
        return () -> f.unsafeApply(this.unsafeGet());
    }

    default <B> ThrowingSupplier<B> flatMap(ThrowingFunction<A, ThrowingSupplier<B>> f) {
        return () -> f.unsafeApply(this.unsafeGet()).unsafeGet();
    }

    static <A> ThrowingSupplier<A> of(ThrowingSupplier<A> s) {
        return s;
    }
}
