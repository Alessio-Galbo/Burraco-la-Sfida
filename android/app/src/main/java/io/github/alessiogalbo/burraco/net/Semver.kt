// Confronto di versioni numeriche "v1.2.3" (prefisso v e suffissi come "-beta" ignorati).
package io.github.alessiogalbo.burraco.net

object Semver {
    /** Numeri della versione, es. "v1.0.10" → [1, 0, 10]; null se non è una versione. */
    fun parts(v: String): List<Int>? {
        val core = v.trim().removePrefix("v").removePrefix("V").substringBefore('-').substringBefore('+')
        val nums = core.split('.').map { it.toIntOrNull() ?: return null }
        return nums.takeIf { it.isNotEmpty() }
    }

    /** Vero se [tag] è strettamente più nuova di [current]. */
    fun newer(tag: String, current: String): Boolean {
        val a = parts(tag) ?: return false
        val b = parts(current) ?: return false
        for (i in 0 until maxOf(a.size, b.size)) {
            val x = a.getOrElse(i) { 0 }
            val y = b.getOrElse(i) { 0 }
            if (x != y) return x > y
        }
        return false
    }
}
