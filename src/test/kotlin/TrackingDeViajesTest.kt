import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime
import java.time.YearMonth

class TrackingDeViajesTest : DescribeSpec({

    val obelisco = Lugar("Obelisco", floatArrayOf(-34.6037f, -58.3816f))
    val congreso = Lugar("Congreso", floatArrayOf(-34.6095f, -58.3924f))
    val retiro = Lugar("Retiro", floatArrayOf(-34.5916f, -58.3736f))
    val constitucion = Lugar("Constitución", floatArrayOf(-34.6276f, -58.3806f))

    fun viaje(origen: Lugar, destino: Lugar, timestamp: LocalDateTime): Viaje =
        Itinerario(origen = origen, destino = destino, timestamp = timestamp)

    val pedro = Cliente(edad = 30, email = "pedro.aguilar@mail.com")

    describe("el reporte de distancia recorrida por usuario y por mes") {

        it("agrupa por mes y suma los kilómetros de todos los viajes registrados en ese mes") {
            val tracking = TrackingDeViajes()
            val viajeJulio1 = viaje(obelisco, congreso, LocalDateTime.of(2026, 7, 5, 10, 0))
            val viajeJulio2 = viaje(retiro, constitucion, LocalDateTime.of(2026, 7, 20, 18, 0))
            val viajeAgosto = viaje(obelisco, retiro, LocalDateTime.of(2026, 8, 2, 9, 0))

            tracking.guardarViaje(pedro, viajeJulio1)
            tracking.guardarViaje(pedro, viajeJulio2)
            tracking.guardarViaje(pedro, viajeAgosto)

            val reporte = tracking.generarReportePara(pedro)

            // Ej: "Pedro Aguilar en julio del 2026 se teletransportó 132,50 kilómetros" -> suma de los viajes de ese mes
            reporte.kilometrosPorMes[YearMonth.of(2026, 7)] shouldBe
                ((viajeJulio1.kilometrosRecorridos() + viajeJulio2.kilometrosRecorridos()) plusOrMinus 0.01)
            reporte.kilometrosPorMes[YearMonth.of(2026, 8)] shouldBe
                (viajeAgosto.kilometrosRecorridos() plusOrMinus 0.01)
        }

        it("genera un reporte sin totales para un pasajero que no registra viajes") {
            val tracking = TrackingDeViajes()

            val reporte = tracking.generarReportePara(pedro)

            reporte.kilometrosPorMes.shouldBeEmpty()
        }

        it("el reporte queda asociado al pasajero consultado") {
            val tracking = TrackingDeViajes()
            tracking.guardarViaje(pedro, viaje(obelisco, congreso, LocalDateTime.of(2026, 7, 5, 10, 0)))

            tracking.generarReportePara(pedro).pasajero shouldBe pedro
        }
    }
})
