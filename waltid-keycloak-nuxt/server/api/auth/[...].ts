import KeycloakProvider from 'next-auth/providers/keycloak'

import { NuxtAuthHandler } from '#auth'

export default NuxtAuthHandler({
  secret: process.env.AUTH_SECRET || 'my-auth-secret',
  // TODO: ADD YOUR OWN AUTHENTICATION PROVIDER HERE, READ THE DOCS FOR MORE: https://sidebase.io/nuxt-auth
  providers: [
    KeycloakProvider.default({
      clientId: "waltid_backend",
      clientSecret: "5FXJ9IxtMTHWfGUDDU8LGZXaWEu3Qqnk",
      //authorization: process.env.AUTH_AUTHORIZATION ?? "",
      issuer: "http://localhost:8080/realms/waltid-keycloak-nuxt",
      idToken: true,
      wellKnown: "http://localhost:8080/realms/waltid-keycloak-nuxt/.well-known/openid-configuration",
      // requestTokenUrl: "http://localhost:8080/auth/realms/waltid-keycloak-nuxt/protocol/openid-connect/auth",
      // accessTokenUrl: "http://localhost:8080/auth/realms/waltid-keycloak-nuxt/protocol/openid-connect/token",
      // profileUrl: "http://localhost:8080/auth/realms/waltid-keycloak-nuxt/protocol/openid-connect/userinfo",
      authorization: 'http://localhost:8080/realms/waltid-keycloak-nuxt/protocol/openid-connect/auth',
    })
  ],
  callbacks: {
    async session({ session, token, user }) {
      // @ts-ignore
      session.user.id = token.id;
      // @ts-ignore
      session.accessToken = token.accessToken;
        // @ts-ignore
      session.refreshToken = token.refreshToken; // Add this line to store the refresh token in the session

      return session;
    },
    async jwt({ token, user, account, profile, isNewUser }) {
      if (user) {
        token.id = user.id;
        // @ts-ignore

      }
      if (account) {
        token.accessToken = account.access_token;
        token.refreshToken = account.refresh_token; // Add this line to store the refresh token in the JWT

      }
      return token;
    },
  },
})


/*
PASSWORD BASED AUTH:

import CredentialsProvider from 'next-auth/providers/credentials'
import { NuxtAuthHandler } from '#auth'

export default NuxtAuthHandler({
  // A secret string you define, to ensure correct encryption
  secret: 'your-secret-here',
  providers: [
    // @ts-expect-error You need to use .default here for it to work during SSR. May be fixed via Vite at some point
    CredentialsProvider.default({
      // The name to display on the sign in form (e.g. 'Sign in with...')
      name: 'Credentials',
      // The credentials is used to generate a suitable form on the sign in page.
      // You can specify whatever fields you are expecting to be submitted.
      // e.g. domain, username, password, 2FA token, etc.
      // You can pass any HTML attribute to the <input> tag through the object.
      credentials: {
        username: { label: 'Username', type: 'text', placeholder: '(hint: jsmith)' },
        password: { label: 'Password', type: 'password', placeholder: '(hint: hunter2)' }
      },
      authorize (credentials: any) {
        // You need to provide your own logic here that takes the credentials
        // submitted and returns either a object representing a user or value
        // that is false/null if the credentials are invalid.
        // NOTE: THE BELOW LOGIC IS NOT SAFE OR PROPER FOR AUTHENTICATION!

        const user = { id: '1', name: 'J Smith', username: 'jsmith', password: 'hunter2' }

        if (credentials?.username === user.username && credentials?.password === user.password) {
          // Any object returned will be saved in `user` property of the JWT
          return user
        } else {
          // eslint-disable-next-line no-console
          console.error('Warning: Malicious login attempt registered, bad credentials provided')

          // If you return null then an error will be displayed advising the user to check their details.
          return null

          // You can also Reject this callback with an Error thus the user will be sent to the error page with the error message as a query parameter
        }
      }
    })
  ]
})*/






