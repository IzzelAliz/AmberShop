package io.izzel.ambershop.util;

import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.util.Optional;
import java.util.concurrent.*;

@NonnullByDefault
public class ProvidingFutureTask<T> extends FutureTask<Optional<T>> {

    private final ArrayBlockingQueue<T> queue;

    private final long timeout;
    private final TimeUnit unit;

    public ProvidingFutureTask(long timeout, TimeUnit unit) {
        super(Optional::empty);
        this.timeout = timeout;
        this.unit = unit;
        queue = new ArrayBlockingQueue<>(1);
    }

    @Override
    public Optional<T> get() throws InterruptedException, ExecutionException {
        return Optional.ofNullable(queue.poll(timeout, unit));
    }

    @Override
    public Optional<T> get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return Optional.ofNullable(queue.poll(timeout, unit));
    }

    public void provide(T value) {
        queue.offer(value);
    }

}
