package id.walt.plugins

import com.auth0.jwk.JwkProviderBuilder
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.apache.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.util.*


// based on https://medium.com/@chendenny/ktor-with-oauth-2-0-443b7367c508

val jwkEndpointUrl = URLBuilder("http://0.0.0.0:8080/realms/waltid-keycloak-nuxt/protocol/openid-connect/certs").build().toURI().toURL()
val jwkProvider = JwkProviderBuilder(jwkEndpointUrl).build()

val issuer = "http://0.0.0.0:8080/realms/waltid-keycloak-nuxt" // ???

@OptIn(InternalAPI::class)
fun Application.configureSecurity() {
    authentication {
        oauth("keycloakOAuth") {
            client = HttpClient(Apache)
            providerLookup = {
                // https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth/io.ktor.server.auth/-o-auth-server-settings/index.html
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "keycloak",
                    authorizeUrl = "http://0.0.0.0:8080/realms/waltid-keycloak-nuxt/protocol/openid-connect/auth",
                    accessTokenUrl = "http://0.0.0.0:8080/realms/waltid-keycloak-nuxt/protocol/openid-connect/token",
                    clientId = "waltid_backend",
                    clientSecret = "5FXJ9IxtMTHWfGUDDU8LGZXaWEu3Qqnk",
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
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }
    routing {

        authenticate("keycloakOAuth") {
            get("/login") {
                // Redirects for authentication
            }
            get("/") {
                val principal: OAuthAccessTokenResponse.OAuth2? = call.principal()

                call.respondText("Hello, ${principal?.accessToken} , refresh token ${principal?.refreshToken}!")

            }
            authenticate("auth-jwt") {
                get("/world") {
                    val principal = call.principal<JWTPrincipal>()
                    val username = principal!!.payload.getClaim("preferred_username").asString()
                    val expiresAt = principal.expiresAt?.time?.minus(System.currentTimeMillis())

                    call.respondText("Hello, $username! Token is expired at $expiresAt ms. ${principal?.payload?.subject}")
                }
                get("/signout") {
                    val client = HttpClient()

                    val realm = "waltid-keycloak-nuxt"
                    val logoutUrl = "http://0.0.0.0:8080/realms/$realm/protocol/openid-connect/logout"


                    val response: HttpResponse = client.post(logoutUrl) {
                        header("Authorization", "Bearer ${call.principal<OAuthAccessTokenResponse.OAuth2>()?.accessToken}")
                        header("Content-Type", "application/x-www-form-urlencoded")
                        body = FormDataContent(Parameters.build {
                            append("client_id", "waltid_backend")
                            call.principal<OAuthAccessTokenResponse.OAuth2>()?.refreshToken?.let { it1 ->
                                append("refresh_token",
                                    it1
                                )
                            }
                        })
                    }

                    val status = response.bodyAsText()
                    call.respondText("Logout status: $status")



                }
            }



        }
    }
}

