import java.time.Year
import java.time.YearMonth

class Reporte(val kilometrosPorMes: Map<YearMonth, Double>) {

    fun kilometrosDelMes(mes:Int, anio:Int = Year.now().value): Double? {
        if(mes !in 1..12) throw BusinessException("Mes invalido")
        return kilometrosPorMes[YearMonth.of(anio,mes)]
    }

}

class TrackingDeViajes {
    private val baseDeDatos = mutableMapOf<Cliente, MutableList<Viaje>>()

    fun guardarViaje(pasajero: Cliente, viaje: Viaje) = baseDeDatos.getOrPut(pasajero) { mutableListOf() }.add(viaje)
    fun generarReportePara(pasajero: Cliente): Reporte {
        val viajes = baseDeDatos[pasajero] ?: emptyList()
        val kilometrosPorMes = viajes
            .groupBy { YearMonth.from(it.timestamp) }
            .mapValues { (_, viajesDelMes) -> viajesDelMes.sumOf { it.kilometrosRecorridos() } }

        return Reporte(kilometrosPorMes)
    }

}