<template>
    <div>
        首页面
        <h3>{{ msg }}</h3>
        <p>
            <el-button type="primary" @click="changeMsg">hello</el-button>
        </p>
        <SvgIcon name="home" />
    </div>
</template>

<script setup lang="ts">
    import { ref } from 'vue';
    import type { Ref } from 'vue';
    let msg: Ref<string> = ref('你好世界');
    const changeMsg = () => {
        msg.value = 'Hello World!';
    };
</script>

<style></style>
