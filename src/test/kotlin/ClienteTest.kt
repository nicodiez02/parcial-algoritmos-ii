import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class ClienteTest : DescribeSpec({

    describe("un Cliente") {

        describe("esMenorDeEdad") {

            it("es true para una persona de menos de 18 años") {
                Cliente(edad = 15, email = "joven@mail.com").esMenorDeEdad().shouldBeTrue()
            }

            it("es false para una persona de exactamente 18 años") {
                Cliente(edad = 18, email = "adulto@mail.com").esMenorDeEdad().shouldBeFalse()
            }

            it("es false para una persona adulta") {
                Cliente(edad = 40, email = "adulto@mail.com").esMenorDeEdad().shouldBeFalse()
            }
        }

        describe("esJubilado") {

            it("es true para una persona de más de 65 años") {
                Cliente(edad = 70, email = "jubilado@mail.com").esJubilado().shouldBeTrue()
            }

            it("es false para una persona de exactamente 65 años") {
                Cliente(edad = 65, email = "adulto@mail.com").esJubilado().shouldBeFalse()
            }

            it("es false para una persona joven o adulta") {
                Cliente(edad = 40, email = "adulto@mail.com").esJubilado().shouldBeFalse()
            }
        }

        describe("la tarjeta VIAJERA") {

            it("se asocia automáticamente al crear un cliente") {
                Cliente(edad = 30, email = "persona@mail.com").tarjeta.shouldBeInstanceOf<TarjetaViajera>()
            }

            it("arranca con saldo en cero") {
                Cliente(edad = 30, email = "persona@mail.com").saldo() shouldBe (0.0 plusOrMinus 0.01)
            }
        }

        describe("la billetera (saldo de la tarjeta)") {

            it("cargarTarjeta aumenta el saldo del cliente") {
                val cliente = Cliente(edad = 30, email = "persona@mail.com")

                cliente.cargarTarjeta(50.0)

                cliente.saldo() shouldBe (50.0 plusOrMinus 0.01)
            }

            it("cobrarViaje descuenta el costo del viaje del saldo del cliente") {
                val cliente = Cliente(edad = 30, email = "persona@mail.com")
                cliente.cargarTarjeta(50.0)

                cliente.cobrarViaje(20.0)

                cliente.saldo() shouldBe (30.0 plusOrMinus 0.01)
            }

            it("cobrarViaje puede dejar al cliente con saldo negativo (en deuda)") {
                val cliente = Cliente(edad = 30, email = "persona@mail.com")

                cliente.cobrarViaje(20.0)

                cliente.saldo() shouldBe (-20.0 plusOrMinus 0.01)
            }
        }
    }
})
