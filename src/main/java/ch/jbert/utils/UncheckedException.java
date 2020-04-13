package ch.jbert.utils;

import java.util.concurrent.Callable;

public final class UncheckedException extends RuntimeException {

    public UncheckedException(Throwable cause) {
        super(cause instanceof UncheckedException ? cause.getCause() : cause);
    }

    public static UncheckedException wrapThrowable(Throwable cause) {
        return cause instanceof UncheckedException
            ? (UncheckedException) cause
            : new UncheckedException(cause);
    }

    /**
     * Avoid gathering stack frames for performance reasons, since this
     * exception is only a holder for a “real” cause.
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

    /**
     * Runs the given code while catching all exceptions and rethrows it inside
     * an {@link UncheckedException}.
     * <p/>
     * This method will either return an {@code A} or throw an {@link
     * UncheckedException}.
     * <p/>
     *
     * {@link InterruptedException} are also wrapped, but the current thread's
     * interrupt status is set back to true.
     */
    public static <A> A wrap(Callable<A> code) {
        try {
            return code.call();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new UncheckedException(e);
        } catch (Exception e) {
            throw wrapThrowable(e);
        }
    }

    /**
     * Run the given {@code code} while checking {@link UncheckedException}.
     * It's cause is rethrown if it is an instance of {@code e1Class}. Otherwise
     * the {@link UncheckedException} is rethrown.
     */
    public static <A, E1 extends Exception> A rethrow(Class<E1> e1Class, ThrowingSupplier<A> code) throws E1 {
        try {
            return code.get();
        } catch (UncheckedException e) {
            Throwables.propagateIfPossible(e.getCause(), e1Class);
            throw e;
        }
    }

    /**
     * Run the given {@code code} while checking {@link UncheckedException}.
     * It's cause is rethrown if it is an instance of {@code e1Class} or {@code
     * e2Class}. Otherwise the {@link UncheckedException} is rethrown.
     */
    public static <A, E1 extends Exception, E2 extends Exception> A rethrow(Class<E1> e1Class
        , Class<E2> e2Class
        , ThrowingSupplier<A> code) throws E1, E2 {
        try {
            return code.get();
        } catch (UncheckedException e) {
            Throwables.propagateIfPossible(e.getCause(), e1Class);
            Throwables.propagateIfPossible(e.getCause(), e2Class);
            throw e;
        }
    }
}
