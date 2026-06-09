import java.time.LocalDateTime

data class TarjetaViajera(
    val fechaDeAlta: LocalDateTime = LocalDateTime.now(),
    var saldo: Double = 0.0
)

class Cliente(private val edad: Int, val email: String) {
    var tarjeta: TarjetaViajera = TarjetaViajera()

    fun saldo() = this.tarjeta.saldo
    fun cargarTarjeta(monto: Double){
        this.tarjeta.saldo += monto
    }

    fun calcularCosto(): Double = TODO()
    fun esMenorDeEdad(): Boolean = this.edad < 18
    fun esJubilado(): Boolean = this.edad > 65
    fun cobrarViaje(monto: Double) {
        this.tarjeta.saldo -= monto
    }
}
