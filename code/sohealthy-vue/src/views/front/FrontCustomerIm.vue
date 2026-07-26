<template>
    <div id="app">
        <div class="home-TUIKit-main">
            <TUIKit
                v-if="sdkAppId && userId && userSig"
                :SDKAppID="Number(sdkAppId)"
                :userID="userId"
                :userSig="userSig"
            />
        </div>
    </div>
</template>

<script lang="ts" setup>
    import { onMounted, ref } from 'vue';
    import { TUIKit } from '../../TUIKit';
    import request from '../../utils/request';

    const sdkAppId = ref('');
    const userId = ref('');
    const userSig = ref('');

    // 发送ajax请求
    async function login() {
        const result = await request.get('/front/customer/im/createAccount');
        sdkAppId.value = result.sdkAppId;
        userSig.value = result.userSig;
        userId.value = result.account;
    }
    onMounted(() => {
        login();
    });
</script>
<style lang="less">
    @import url('FrontCustomerIm.less');
</style>
