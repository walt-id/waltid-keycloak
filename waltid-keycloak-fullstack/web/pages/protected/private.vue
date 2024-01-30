<template>
  <div class="text-center mt-10">
    <h1 class="text-2xl font-bold">
      I am only available for authenticated users! with an email  : {{email}}  🎉
    </h1>
  </div>
</template>


<script lang="ts" setup>


import {sendRequest} from "~/composables/authentification";
import {SessionProvider ,getSession , useSession ,} from "next-auth/react";
const session = await getSession()

const email = ref()


definePageMeta({auth: true})

async function world() {

  // @ts-ignore
    await sendRequest(session.accessToken).then((res)=>{
      email.value = res
    })

}


world()
</script>

