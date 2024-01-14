// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  devtools: true,
  modules: ['@sidebase/nuxt-auth'],
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
