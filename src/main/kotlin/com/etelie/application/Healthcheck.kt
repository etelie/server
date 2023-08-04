import guru.zoroark.tegral.openapi.dsl.schema
import guru.zoroark.tegral.openapi.ktor.describe
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.healthcheckRoute() {
    get("/healthcheck") {
        call.respond("Service is healthy")
    } describe {
        summary = "healthcheck"
        200 response {
            plainText {
                schema<String>("Service is healthy")
            }
        }
    }
}
