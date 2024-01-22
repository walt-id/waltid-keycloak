package id.walt.plugins

import com.auth0.jwk.JwkProviderBuilder
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

// based on https://medium.com/@chendenny/ktor-with-oauth-2-0-443b7367c508

val jwkEndpointUrl = URLBuilder("http://localhost:8080/realms/waltid-keycloak-ktor/protocol/openid-connect/certs").build().toURI().toURL()
val jwkProvider = JwkProviderBuilder(jwkEndpointUrl).build()

val issuer = "http://localhost:8080/realms/waltid-keycloak-ktor"

fun Application.configureSecurity() {
    authentication {
        oauth("keycloakOAuth") {
            client = HttpClient(Apache)
            providerLookup = {
                // https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth/io.ktor.server.auth/-o-auth-server-settings/index.html
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "keycloak",
                    authorizeUrl = "http://localhost:8080/realms/waltid-keycloak-ktor/protocol/openid-connect/auth",
                    accessTokenUrl = "http://localhost:8080/realms/waltid-keycloak-ktor/protocol/openid-connect/token",
                    clientId = "client-ktor",
                    clientSecret = "",
                    accessTokenRequiresBasicAuth = false,
                    requestMethod = HttpMethod.Post,
                    defaultScopes = listOf("roles")
                )
            }
            urlProvider = {
                "http://localhost:8090/"
            }
        }
        jwt("auth-jwt") {
            realm = ""
            verifier(jwkProvider, issuer) {
            }
            validate { jwtCredential ->
                if (jwtCredential.payload.issuer != null) {
                    JWTPrincipal(jwtCredential.payload)
                } else {
                    null
                }
            }
            challenge { defaultScheme, realm ->


                call.request.headers["Authorization"]?.let {
                    if (it.isNotEmpty()) {
                        // Token expired
                        // Authentication exception
                        println("Token issue $it")

                        throw BadRequestException("Token issue $it")
                    } else {
                        throw BadRequestException("Authorization header can not be blank!")
                    }
                } ?: throw BadRequestException("Authorization header can not be blank!")


                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")


            }
        }
        /*        oauth("auth-oauth-google") {
            urlProvider = { "http://localhost:8080/callback" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "google",
                    authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                    accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
                    requestMethod = HttpMethod.Post,
                    clientId = System.getenv("GOOGLE_CLIENT_ID"),
                    clientSecret = System.getenv("GOOGLE_CLIENT_SECRET"),
                    defaultScopes = listOf("https://www.googleapis.com/auth/userinfo.profile")
                )
            }
            client = HttpClient(Apache)
        }*/
    }
    routing {

        authenticate("keycloakOAuth") {
            get("/login") {
                // Redirects for authentication
            }
            get("/") {
                val principal: OAuthAccessTokenResponse.OAuth2? = call.principal()
                call.respondText("$principal?.accessToken")
            }

            /*        authenticate("auth-oauth-google") {
            get("login") {
                call.respondRedirect("/callback")
            }

            get("/callback") {
                val principal: OAuthAccessTokenResponse.OAuth2? = call.authentication.principal()
                call.sessions.set(UserSession(principal?.accessToken.toString()))
                call.respondRedirect("/hello")
            }
        }*/
        }

        authenticate("auth-jwt") {
            get("/world") {
                val principal = call.principal<JWTPrincipal>()
                val username = principal!!.payload.getClaim("preferred_username").asString()
                val expiresAt = principal.expiresAt?.time?.minus(System.currentTimeMillis())
                call.respondText("Hello, $username! Token expires at $expiresAt ms. ${principal?.payload?.subject}")
            }
        }
    }
}
class UserSession(accessToken: String)
