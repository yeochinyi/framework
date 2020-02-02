package blog

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.auth.*
import io.ktor.auth.jwt.jwt
import io.ktor.features.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.util.*

// https://ktor.io/quickstart/quickstart/gradle.html

data class Snippet(val user: String, val text: String)

val snippets = Collections.synchronizedList(mutableListOf(
    Snippet(user = "test", text = "hello"),
    Snippet(user = "test", text = "world")
))

data class PostSnippet(val snippet: PostSnippet.Text) {
    data class Text(val text: String)
}

open class SimpleJWT(val secret: String) {
    private val algorithm = Algorithm.HMAC256(secret)
    val verifier = JWT.require(algorithm).build()
    fun sign(name: String): String = JWT.create().withClaim("name", name).sign(algorithm)
}

class User(val name: String, val password: String)

val users = Collections.synchronizedMap(
    listOf(User("test", "test"))
        .associateBy { it.name }
        .toMutableMap()
)
class LoginRegister(val user: String, val password: String)


val simpleJwt = SimpleJWT("my-super-secret-for-jwt")

class InvalidCredentialsException(message: String) : RuntimeException(message)

//fun Application.module() {
fun Application.main() {
    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Get)
        method(HttpMethod.Post)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        allowCredentials = true
        anyHost()
    }
    install(Authentication) {
//        basic {
//            realm = "myrealm"
//            validate { if (it.name == "user" && it.password == "password") UserIdPrincipal("user") else null }
//        }

        jwt {
            verifier(simpleJwt.verifier)
            validate {
                UserIdPrincipal(it.payload.getClaim("name").asString())
            }
        }
    }
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT) // Pretty Prints the JSON
        }
    }
    install(StatusPages) {
        exception<InvalidCredentialsException> { exception ->
            call.respond(HttpStatusCode.Unauthorized, mapOf("OK" to false, "error" to (exception.message ?: "")))
        }
    }
    install(DefaultHeaders)
    install(CallLogging)
    install(Routing) {
        get("/") {
            call.respondText("My Example Blog", ContentType.Text.Html)
//                call.respond(mapOf("OK" to true))
        }
        post("/login-register") {
            val post = call.receive<LoginRegister>()
            val user = users.getOrPut(post.user) { User(post.user, post.password) }
            if (user.password != post.password) throw InvalidCredentialsException("Invalid credentials")
            call.respond(mapOf("token" to simpleJwt.sign(user.name)))
        }
        route("/snippets") {
            authenticate {

                get {
                    call.respond(mapOf("snippets" to synchronized(snippets) { snippets.toList() }))
                }

                //  curl --request POST --header "Content-Type: application/json" --data '{"snippet" : {"text" : "mysnippet"}}' http://127.0.0.1:8080/snippets
                post {
                    val post = call.receive<PostSnippet>()
                    val principal = call.principal<UserIdPrincipal>() ?: error("No principal")
                    snippets += Snippet(principal.name, post.snippet.text)
                    call.respond(mapOf("OK" to true))
                }
            }
        }



    }
}

//fun main(args: Array<String>) {
//    embeddedServer(Netty, 8080,
//        watchPaths = listOf("/home/chris/workspace/lang/framework/kotlin/ktor-quickstart/src/main/kotlin"),
//        module = Application::module
//    ).start()
//}