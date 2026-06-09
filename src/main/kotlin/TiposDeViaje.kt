import java.time.LocalDateTime

class ViajeProgramado(
    origen: Lugar,
    destino: Lugar,
    timestamp: LocalDateTime,
    private val topeDeudor: Double,
    private val topeDeGente: Number
) : Viaje(origen, destino, timestamp) {

    init {
        require(timestamp > LocalDateTime.now()) {"Los viajes programados deben ser futuros"}
        require(topeDeudor < 0) {"El tope deudor debe ser un número negativo"}    }

    override fun validarPasajero(cliente: Cliente) {
        if(LocalDateTime.now() > timestamp.minusHours(2)) throw BusinessException("Los viajes programados aceptan pasajeros hasta 2 horas antes de comenzar")
        if(this.pasajeros.size == topeDeGente) throw BusinessException("El viaje esta lleno!")
        if(cliente.saldo() < topeDeudor) throw BusinessException("Saldo insuficiente")
    }

}

class Itinerario(
    origen: Lugar,
    destino: Lugar,
    timestamp: LocalDateTime = LocalDateTime.now(),
    private val puntosIntermedios: MutableList<Itinerario> = mutableListOf()
) : Viaje(origen, destino, timestamp) {

    override fun validarPasajero(cliente: Cliente) {
        if(cliente.saldo() < costoViaje(cliente)) throw BusinessException("Saldo insuficiente")
        if(!cliente.tarjeta.fechaDeAlta.isBefore(LocalDateTime.now().minusMonths(6))) throw BusinessException("Su tarjeta debe tener 6 meses de antiguedad minimo")
    }
}

