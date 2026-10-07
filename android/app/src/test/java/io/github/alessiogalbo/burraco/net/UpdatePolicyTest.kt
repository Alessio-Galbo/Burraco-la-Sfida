// JUnit: controllo nuova versione (apertura 1 h, giornaliero 24 h, 2 h dopo errore) e notifica una sola volta per versione più nuova.
package io.github.alessiogalbo.burraco.net

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdatePolicyTest {
    private val h = 60 * 60 * 1000L
    private val t0 = 1_000_000_000_000L

    @Test
    fun openAtMostHourly() {
        assertTrue(Retry.due(0L, false, t0, UpdatePolicy.OPEN_MS))
        assertFalse(Retry.due(t0, true, t0 + h - 1, UpdatePolicy.OPEN_MS))
        assertTrue(Retry.due(t0, true, t0 + h, UpdatePolicy.OPEN_MS))
        assertFalse(Retry.due(t0, false, t0 + h, UpdatePolicy.OPEN_MS)) // dopo un errore: 2 h
        assertTrue(Retry.due(t0, false, t0 + 2 * h, UpdatePolicy.OPEN_MS))
    }

    @Test
    fun backgroundDaily() {
        assertFalse(Retry.due(t0, true, t0 + 23 * h, UpdatePolicy.DAILY_MS))
        assertTrue(Retry.due(t0, true, t0 + 24 * h, UpdatePolicy.DAILY_MS))
        assertTrue(Retry.due(t0, false, t0 + 2 * h, UpdatePolicy.DAILY_MS))
    }

    @Test
    fun notifyOncePerNewerVersion() {
        assertTrue(UpdatePolicy.shouldNotify("v1.0.1", "1.0.0", null))
        assertFalse(UpdatePolicy.shouldNotify("v1.0.1", "1.0.0", "v1.0.1")) // già notificata
        assertTrue(UpdatePolicy.shouldNotify("v1.0.2", "1.0.0", "v1.0.1")) // versione successiva: nuova notifica
    }

    @Test
    fun noNotifyForSameOrOlder() {
        assertFalse(UpdatePolicy.shouldNotify("v1.0.1", "1.0.1", null))
        assertFalse(UpdatePolicy.shouldNotify("v1.0.0", "1.0.2", null))
        assertFalse(UpdatePolicy.shouldNotify("nightly", "1.0.0", null))
    }
}
