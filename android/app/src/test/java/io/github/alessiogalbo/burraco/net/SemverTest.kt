// JUnit: confronto delle versioni per il banner "Nuova versione disponibile".
package io.github.alessiogalbo.burraco.net

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SemverTest {
    @Test
    fun compare() {
        assertTrue(Semver.newer("v1.0.1", "1.0.0"))
        assertTrue(Semver.newer("v1.0.10", "1.0.9"))
        assertTrue(Semver.newer("v2.0", "1.9.9"))
        assertTrue(Semver.newer("v1.0.0", "0.1.0")) // versione vecchia simulata
        assertFalse(Semver.newer("v1.0.0", "1.0.0"))
        assertFalse(Semver.newer("v1.0.0", "1.0.1"))
        assertFalse(Semver.newer("v1.0", "1.0.0"))
        assertFalse(Semver.newer("latest", "1.0.0"))
        assertTrue(Semver.newer("v1.1.0-beta", "1.0.0"))
    }
}
