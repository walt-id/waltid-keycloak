// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  devtools: true,
  modules: ['@sidebase/nuxt-auth'],
  nitro: {
    compressPublicAssets: true,
    devProxy: {
      "/r/": "http://localhost:8080/"
    }
    },
  auth: {
    isEnabled: true,
    baseUrl: process.env.AUTH_ORIGIN,
    provider: {
      type: 'authjs'
    },
    globalAppMiddleware: {
      isEnabled: true
    }
  }
})
