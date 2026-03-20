package com.eam_simulator.engine;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.greaterThan;

@SpringBootTest
@ActiveProfiles("test")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class GameEngineTest {

    private final GameEngineFacade gameEngineFacade;
    private final TestTickCounter tickCounter;

    @AfterEach
    void tearDown() {
        gameEngineFacade.stop();
    }

    @Test
    void shouldStartEngineAndNotifyTickables() {
        long initialTicks = tickCounter.getTickCount();

        // Używamy nowej, lepszej nazwy metody
        gameEngineFacade.startSimulation();

        await()
                .atMost(5, SECONDS)
                .pollInterval(Duration.ofMillis(100))
                .until(tickCounter::getTickCount, greaterThan(initialTicks + 10));
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public TestTickCounter testTickCounter() {
            return new TestTickCounter();
        }
    }

    static class TestTickCounter implements Tickable {
        private final AtomicLong tickCount = new AtomicLong(0);

        @Override
        public void onTick(TickEmitted tick) {
            tickCount.incrementAndGet();
        }

        public long getTickCount() {
            return tickCount.get();
        }
    }
}
