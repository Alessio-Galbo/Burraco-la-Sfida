// Ordine degli eventi in tutte le viste "tutti" e "prossimo": in corso per fine più vicina, poi in attesa per inizio più vicino.
package io.github.alessiogalbo.burraco.schedule

object EventOrder {
    /** In corso per [EventStatus.until] crescente, poi in attesa per [EventStatus.startsAt] crescente. */
    fun ordered(all: List<EventStatus>): List<EventStatus> =
        all.filter { it.active }.sortedBy { it.until } + all.filter { !it.active }.sortedBy { it.startsAt }

    /** "Prossimo o in corso": il primo dell'ordine. */
    fun first(all: List<EventStatus>): EventStatus = ordered(all).first()
}
