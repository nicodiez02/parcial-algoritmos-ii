import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDateTime

class CompletarViajeTest : DescribeSpec({

    val plazaMiserere = Lugar("Plaza Miserere", floatArrayOf(-34.6092f, -58.4173f))
    val rosedal = Lugar("Rosedal", floatArrayOf(-34.5697f, -58.4144f))

    // tarjeta con más de 6 meses para que agregarPasajero a un Itinerario no falle por esa regla
    fun pasajero(saldo: Double): Cliente {
        val cliente = Cliente(edad = 30, email = "pasajero@mail.com")
        cliente.tarjeta = TarjetaViajera(fechaDeAlta = LocalDateTime.now().minusMonths(12), saldo = saldo)
        return cliente
    }

    describe("al completar un viaje") {

        it("se registra el costo del viaje en la billetera (saldo) de cada pasajero") {
            val itinerario = Itinerario(origen = plazaMiserere, destino = rosedal)
            val cliente = pasajero(saldo = 100.0)
            itinerario.agregarPasajero(cliente)

            itinerario.completarViaje()

            // costo base sin decoradores = 5.0 -> 100 - 5 = 95
            cliente.saldo() shouldBe (95.0 plusOrMinus 0.01)
        }

        it("envía un mail (de forma simulada, sin enviarlo realmente) a quien queda con saldo por debajo del umbral de deuda configurado") {
            val mailSender = mockk<MailSender>(relaxed = true)
            val itinerario = Itinerario(origen = plazaMiserere, destino = rosedal)
            itinerario.agregarObserver(ClienteObserver(mailSender))

            // saldo inicial 6: tras pagar el costo base (5) le queda 1, por debajo del tope de deuda configurado (2)
            val cliente = pasajero(saldo = 6.0)
            itinerario.agregarPasajero(cliente)

            itinerario.completarViaje()

            verify(exactly = 1) { mailSender.enviar(cliente.email) }
        }

        it("no envía ningún mail a quien mantiene saldo por encima del umbral de deuda configurado") {
            val mailSender = mockk<MailSender>(relaxed = true)
            val itinerario = Itinerario(origen = plazaMiserere, destino = rosedal)
            itinerario.agregarObserver(ClienteObserver(mailSender))

            // saldo inicial 100: tras pagar el costo base (5) le quedan 95, por encima del tope de deuda configurado (2)
            val cliente = pasajero(saldo = 100.0)
            itinerario.agregarPasajero(cliente)

            itinerario.completarViaje()

            verify(exactly = 0) { mailSender.enviar(any()) }
        }
    }

    describe("los observers registrados en un viaje") {

        it("notifica al ClienteObserver y al ViajeObserver registrados juntos al completar el viaje") {
            val mailSender = mockk<MailSender>(relaxed = true)
            val itinerario = Itinerario(origen = plazaMiserere, destino = rosedal)
            itinerario.agregarObserver(ClienteObserver(mailSender))
            itinerario.agregarObserver(ViajeObserver())

            // saldo inicial 6: tras pagar el costo base (5) le queda 1, por debajo del tope de deuda configurado (2)
            val cliente = pasajero(saldo = 6.0)
            itinerario.agregarPasajero(cliente)

            shouldNotThrowAny { itinerario.completarViaje() }

            // el ClienteObserver sigue notificando correctamente con otros observers registrados en el viaje
            verify(exactly = 1) { mailSender.enviar(cliente.email) }
        }
    }
})
