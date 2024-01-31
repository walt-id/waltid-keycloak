# Keycloak Nuxt example 

## Setting up Nuxt

### Install Nuxt
```shell
nvm use  v20.11.0
npx nuxi@latest init waltid-keycloak-nuxt
cd waltid-keycloak-nuxt/
pnpm i
pnpm i -D @sidebase/nuxt-auth
pnpm i next-auth@4.22.5
```

## Running the Nuxt app
```shell
pnpm dev --open
```

## Setting up Keycloak

Run Keycloak:
```shell
${PATH_TO_KEYCLOAK}/keycloak-23.0.4/bin/kc.sh start-dev
```

When visiting the admin console at  http://0.0.0.0:8080 the first you can setup username & password of an admin user. Then configure the realm and client (named "client-nuxt") as documented here:

- Basic setup https://www.keycloak.org/getting-started/getting-started-zip
- Client config: https://www.keycloak.org/docs/latest/server_admin/#_oidc_clients


Configure NUXT (nuxt-auth from sidebase) 
- Keycloak via nuxt-auth (sidebase)  https://devandy.de/nuxt-3-mit-keycloak-autorisieren/
- Examples https://github.com/sidebase/nuxt-auth-example/tree/main

