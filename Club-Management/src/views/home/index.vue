<template>
  <div class="home card">
    <img class="home-bg" src="@/assets/images/welcome.png" alt="welcome" />
  </div>
</template>

<script setup>
import { useUserStore } from "@/stores/modules/user";
import { useAuthStore } from "@/stores/modules/auth";

const userStore = useUserStore();
const authStore = useAuthStore();
</script>

<style scoped lang="scss">
@import "./index.scss";
</style>
