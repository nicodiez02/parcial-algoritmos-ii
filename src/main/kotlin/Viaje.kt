import java.time.LocalDateTime
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

interface IViaje {
    fun costoViaje(pasajero: Cliente): Double
}

data class Lugar(val nombre: String, val coordenadas: FloatArray) {
    init { require(coordenadas.size == 2) { "Coordenadas invalida!" } }
    fun latitud() = this.coordenadas[0]
    fun longitud() = this.coordenadas[1]
}

abstract class Viaje(
    val origen: Lugar,
    val destino: Lugar,
    val timestamp: LocalDateTime = LocalDateTime.now()
) : IViaje {
    val topeDeDeuda = 2

    protected val pasajeros: MutableList<Cliente> = mutableListOf()
    private val observers: MutableList<IObserver> = mutableListOf()
    private val estrategiasDeCobro: MutableList<EstrategiaDeCobro> = mutableListOf()

    override fun costoViaje(pasajero: Cliente) =
        estrategiasDeCobro.fold(5.0) { costoAcumulado, estrategia -> estrategia.aplicar(costoAcumulado, pasajero) }

    abstract fun validarPasajero(cliente: Cliente)

    fun agregarObserver(observer: IObserver) = observers.add(observer)
    fun agregarEstrategiaDeCobro(estrategia: EstrategiaDeCobro) = estrategiasDeCobro.add(estrategia)
    fun removePasajero(cliente: Cliente)= pasajeros.remove(cliente)
    fun completarViaje() {
        pasajeros.forEach { it.cobrarViaje(this.costoViaje(it)) }
        observers.forEach { it.nuevoViajeCompletado(this, pasajeros) }
    }

    fun agregarPasajero(cliente: Cliente) {
        this.validarPasajero(cliente)
        this.pasajeros.add(cliente)
    }

    fun kilometrosRecorridos(): Double {
        val radioTierra = 6371.0
        val dLat = Math.toRadians((destino.latitud() - origen.latitud()).toDouble())
        val dLon = Math.toRadians((destino.longitud() - origen.longitud()).toDouble())
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(origen.latitud().toDouble())) *
                cos(Math.toRadians(destino.latitud().toDouble())) *
                sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return radioTierra * c
    }
}