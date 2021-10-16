import com.maksu.idgenerator.IdGenerator;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

public class IdGeneratorTest {

    @Test
    public void nextId_shouldGenerateUniqueId() {
        int iterations = 5000;

        Set<Long> uniqueIdSet = new HashSet<>();
        for(int i = 0; i < iterations; i++) {
            uniqueIdSet.add(IdGenerator.getInstance().nextId());
        }

        assertEquals(iterations,uniqueIdSet.size());
    }

    @Test
    public void nextId_shouldGenerateUniqueIdIfCalledFromMultipleThreads() throws InterruptedException, ExecutionException {
        int numThreads = 50;
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        Set<Long> uniqueIdSet = new HashSet<>();
        int iterations = 10000;

        // Validate that the IDs are not same even if they are generated in the same ms in different threads
        Future<Long>[] futures = new Future[iterations];
        for(int i = 0; i < iterations; i++) {
            futures[i] =  executorService.submit(() -> {
                long id = IdGenerator.getInstance().nextId(); //In this line if we dont use syncronize test will fail.
                latch.countDown();;
                return id;
            });
        }

        latch.await();
        Arrays.stream(futures).map(future -> {
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }).forEach(uniqueIdSet::add);

        assertEquals(10000,uniqueIdSet.size());
    }
}