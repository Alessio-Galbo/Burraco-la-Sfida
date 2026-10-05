// JUnit: intervalli dei tentativi di rete (lungo dopo un successo, 2 h dopo un fallimento).
package io.github.alessiogalbo.burraco.net

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RetryTest {
    private val h = 60 * 60 * 1000L
    private val t0 = 1_000_000_000_000L

    @Test
    fun intervals() {
        assertTrue(Retry.due(0L, false, t0, 24 * h)) // mai provato
        assertFalse(Retry.due(t0, true, t0 + 23 * h, 24 * h)) // successo: aspetta 24 h
        assertTrue(Retry.due(t0, true, t0 + 24 * h, 24 * h))
        assertFalse(Retry.due(t0, true, t0 + 11 * h, 12 * h))
        assertTrue(Retry.due(t0, true, t0 + 12 * h, 12 * h))
        assertFalse(Retry.due(t0, false, t0 + 2 * h - 1, 24 * h)) // fallimento: solo 2 h
        assertTrue(Retry.due(t0, false, t0 + 2 * h, 24 * h))
        assertTrue(Retry.due(t0, false, t0 + 2 * h, 12 * h))
        assertTrue(Retry.due(t0 + h, true, t0, 24 * h)) // orologio tornato indietro
    }
}
