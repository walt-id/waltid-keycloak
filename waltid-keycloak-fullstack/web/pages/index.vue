<template>
  <h3 class="text-xl font-bold ">
    <NuxtLink to="/protected/public">public page</NuxtLink>
  </h3>
  <h3 class="text-xl font-bold ">
    <NuxtLink to="/protected/private">private page</NuxtLink>
  </h3>


  <div class="w-full max-w-5xl mx-auto bg-white px-5 py-4 rounded-t shadow-xl">
    <div class="flex items-center justify-between">
      <div class="flex items-center space-x-2">
        <img
            v-if="status === 'authenticated' && data?.user?.image"
            class="w-12 h-12 rounded-full"
            :src="data.user.image"
            alt="User Avatar"
        >
        <h1 v-if="status === 'authenticated'" class="text-lg">
          Authenticated as {{ data?.user?.name }}!
        </h1>
        <h1 v-else>
          Not logged in
        </h1>
      </div>
      <button v-if="status === 'authenticated'" class="flex items-center justify-center space-x-2 bg-red-500 text-white rounded-lg py-2 px-3 text-lg" @click="logout()">
        <span>Logout</span>
      </button>
      <button v-else class="flex items-center justify-center space-x-2 bg-green-500 text-white rounded-lg py-2 px-3 text-lg" @click="signIn('keycloak')">
        <i class="fa fa-right-to-bracket pt-0.5" />
        <span>Login</span>
      </button>
    </div>
  </div>


  <div class="max-w-5xl mx-auto mt-5 px-5">
    <h3 class="text-xl font-bold ">
      Authentication Overview
    </h3>
    <p class="text-sm">
      See all available authentication & session information below.
    </p>
    <pre v-if="status"><span>Status:</span> {{ status }}</pre>
    <pre v-if="data"><span>Data:</span> {{ data }}</pre>


    <pre v-if="session"><span>Session:</span> {{ session }}</pre>


    {{status}}


  </div>
</template>

<script lang="ts" setup>



import {SessionProvider ,getSession , useSession ,} from "next-auth/react";


definePageMeta({auth: false})
const {status, data, signIn  , signOut} = useAuth()


const session = await getSession()



async function logout() {
  try {
     signOut()
    window.location.href = "http://localhost:8080/realms/waltid-keycloak-nuxt/protocol/openid-connect/logout"


  } catch (e) {
    console.error(e)
  }
}




</script>

