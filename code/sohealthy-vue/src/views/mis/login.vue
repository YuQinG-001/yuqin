<template>
    <!-- 整个页面，设置背景图片 -->
    <div class="page">
        <!-- 登录面板 -->
        <div class="panel">
            <!-- 左边一个小div -->
            <div class="left">
                <!-- 上面一个图片 -->
                <img src="../../assets/login/logo.png" class="logo" />
                <!-- 下面一个图片 -->
                <img src="../../assets/login/big.png" class="big" />
            </div>
            <!-- 右边一个小div -->
            <div class="right">
                <!-- 显示标题字和版本号的div -->
                <div class="title-container">
                    <h2>美年大健康体检系统</h2>
                    <span>V1.0</span>
                </div>
                <!-- 登录表单控件div -->
                <div>
                    <div class="row">
                        <el-input
                            v-model="loginInfo.username"
                            placeholder="用户名"
                            prefix-icon="user"
                            size="large"
                            clearable
                        ></el-input>
                    </div>
                    <div class="row">
                        <el-input
                            type="password"
                            v-model="loginInfo.password"
                            placeholder="密码"
                            prefix-icon="Lock"
                            size="large"
                            clearable
                        ></el-input>
                    </div>
                    <div class="row">
                        <el-button type="primary" class="btn" size="large" @click="login">
                            登录系统
                        </el-button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
    import { reactive } from 'vue';
    import { isPassword, isUsername, stringIsEmpty } from '../../utils/validate';
    import { ElMessage } from 'element-plus';
    import axios from 'axios';
    import { useRoute, useRouter } from 'vue-router';

    const route = useRoute();
    const router = useRouter();
    // 登录数据
    const loginInfo = reactive({
        username: '',
        password: '',
    });

    async function login() {
        let username = loginInfo.username;
        let password = loginInfo.password;
        let error = null;
        if (stringIsEmpty(username)) error = '用户名不能为空';
        else if (!isUsername(username)) error = '用户名格式错误';
        else if (!isPassword(password)) error = '密码格式错误';
        if (error) {
            ElMessage({ type: 'error', message: error, duration: 1200 });
            return;
        }
        const loginData = {
            username,
            password,
        };
        // 配置
        const config = {
            headers: {
                'Content-Type': 'application/json',
            },
        };
        const response = await axios.post('/meinian-api/mis/user/login', loginData, config);
        //登入失败，没有返回response
        // console.log("🚀 ~ login ~ response.data:", response.data)
        if (!(response.data.code === 200)) {
            ElMessage({ type: 'error', message: '登入失败', duration: 1200 });
            return;
        }
        localStorage.setItem('token', response.data.result.token);
        localStorage.setItem('permissions', JSON.stringify(response.data.result.permissions));

        /* //登入后重定向
        const redirect = route.query.redirect;
        if (redirect && typeof redirect === 'string' && redirect !== '/mis/login') {
            router.push(redirect);
            return;
        } */
        router.push({ name: 'MisHome' });
    }
</script>

<style lang="less" scoped>
    @import url('login.less');
</style>
