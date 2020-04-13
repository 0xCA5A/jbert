package ch.jbert.utils;

public final class Throwables {
    private Throwables() {}

    /**
     * Rethrows the given throwable if it is an {@link Error} or {@link
     * RuntimeException}.
     */
    public static void propagateUnchecked(Throwable error) {
        if (error instanceof Error) {
            throw (Error) error;
        }
        if (error instanceof RuntimeException) {
            throw (RuntimeException) error;
        }
    }

    /**
     * Rethrows the given throwable if it is an {@link Error} or {@link
     * RuntimeException} or an instance of {@code E}.
     */
    @SuppressWarnings("unchecked")
    public static <E extends Exception> void propagateIfPossible(Throwable error, Class<E> eclass) throws E {
        propagateUnchecked(error);
        if (eclass.isInstance(error)) {
            throw (E) error;
        }
    }
}
