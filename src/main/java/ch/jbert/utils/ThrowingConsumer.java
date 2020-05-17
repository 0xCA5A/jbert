package ch.jbert.utils;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * A special consumer interface that allows throwing {@link Exception}s.
 *
 * <p>It implements the default {@code accept} method by catching all exceptions and rethrowing them
 * wrapped in the {@link UncheckedException}. It will ever only throw this exception, carrying the
 * actual exception as its {@link Exception#getCause() cause}.
 *
 * <p>This can be useful when dealing with checked exceptions inside functional interfaces, like
 * {@link java.util.stream.Stream}. Example:
 *
 * <pre>
 *     try {
 *         stream.foreach(ThrowingConsumer.of(param -> doSomethingThatThrows(param)));
 *     } catch (UncheckedException e) {
 *         Throwables.propagateIfPossible(e.getCause(), IOException.class);
 *         throw e;
 *     }
 * </pre>
 *
 * This would rethrow the checked {@code IOException} and all unchecked exceptions. If {@code
 * doSomethingThatHrows} would throw another checked exception, then this would be rethrown in an
 * {@link UncheckedException}.
 *
 * <p>The advantage is, that the catching code is not required to show up in every lambda. The
 * downside is, that the compiler cannot detect whether all checked exceptions are rethrown
 * properly.
 */
@FunctionalInterface
public interface ThrowingConsumer<A> extends Consumer<A> {
  void unsafeAccept(A a) throws Exception;

  default void accept(A a) {
    UncheckedException.wrap(
        () -> {
          unsafeAccept(a);
          return null;
        });
  }

  default ThrowingConsumer<A> next(ThrowingConsumer<A> next) {
    Objects.requireNonNull(next);
    return a -> {
      unsafeAccept(a);
      next.unsafeAccept(a);
    };
  }

  default <B> ThrowingConsumer<B> contramap(ThrowingFunction<B, A> f) {
    return b -> unsafeAccept(f.unsafeApply(b));
  }

  static <A> ThrowingConsumer<A> of(ThrowingConsumer<A> c) {
    return c;
  }

  static <A> ThrowingConsumer<A> doNothing() {
    return a -> {};
  }
}
