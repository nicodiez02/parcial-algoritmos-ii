import java.time.YearMonth

data class Reporte(val pasajero: Cliente, val kilometrosPorMes: Map<YearMonth, Double>)

class TrackingDeViajes {
    private val base = mutableMapOf<Cliente, MutableList<Viaje>>()

    fun guardarViaje(pasajero: Cliente, viaje: Viaje) = base.getOrPut(pasajero) { mutableListOf() }.add(viaje)
    fun generarReportePara(pasajero: Cliente): Reporte {
        val viajes = base[pasajero] ?: emptyList()
        val kilometrosPorMes = viajes
            .groupBy { YearMonth.from(it.timestamp) }
            .mapValues { (_, viajesDelMes) -> viajesDelMes.sumOf { it.kilometrosRecorridos() } }

        return Reporte(pasajero, kilometrosPorMes)
    }

}