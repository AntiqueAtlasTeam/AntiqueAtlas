package hunternif.mc.impl.atlas.util;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;

public class Streams {
    public static <T> Collection<List<T>> chunked(Stream<T> s, int chunkSize) {
        AtomicInteger counter = new AtomicInteger();

        return s.collect(groupingBy(x -> counter.getAndIncrement() / chunkSize)).values();
    }
}
