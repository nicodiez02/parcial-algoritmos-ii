interface IObserver {
    fun recibirNotificacion(viaje: Viaje, pasajeros: MutableList<Cliente>): Unit
}

interface MailSender{
    fun enviar(mail: String): Unit
}

class ClienteObserver(private val smtp: MailSender) : IObserver {
    override fun recibirNotificacion(viaje: Viaje, pasajeros: MutableList<Cliente>): Unit {
        pasajeros
            .filter { it.saldo() < viaje.topeDeDeuda }
            .forEach { smtp.enviar(it.email) }
    }
}

class ViajeObserver : IObserver {
    private val tracking = TrackingDeViajes()

    override fun recibirNotificacion(viaje: Viaje, pasajeros: MutableList<Cliente>): Unit {
        pasajeros.forEach { tracking.guardarViaje(it, viaje) }
    }
}