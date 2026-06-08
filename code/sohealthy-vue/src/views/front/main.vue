<template>
    <div class="container">
        <header>
            <div class="logo-container" @click="router.push({ name: 'FrontIndex' })">
                <img src="../../assets/front/index/logo.png" alt="logo" class="logo" />
                <div>
                    <h3>美年大健康</h3>
                    <p>www.sohealth.com</p>
                </div>
            </div>
            <div class="search-container">
                <div>
                    <el-input
                        v-model="header.keyword"
                        size="large"
                        maxlength="50"
                        placeholder="请输入套餐名或编号"
                        class="keyword-input"
                        clearable
                    >
                        <template #append>
                            <el-button @click="searchGoods">搜索</el-button>
                        </template>
                    </el-input>
                </div>
                <div>
                    <!-- 
                    type属性用来指定tag的类型，可填写：primary、success、info等。
                    key属性是标签的唯一标识
                    effect设置主题是高亮
                    round是圆角
                    class="tag" 将来可以设置样式。比如外补丁。 
                    -->
                    <el-tag
                        v-for="item in header.tags"
                        :type="item.type"
                        :key="item.label"
                        effect="light"
                        @click="tagHandle(item.label)"
                        class="tag"
                        round
                    >
                        {{ item.label }}
                    </el-tag>
                </div>
            </div>
            <div class="oper-container">
                <!-- 将来用户登录成功后，登录/注册 按钮会隐藏，然后显示  个人中心 退出 。 -->
                <el-button
                    type="primary"
                    size="large"
                    v-if="dialog.status == 'logout'"
                    @click="showDialog"
                >
                    登录/注册
                </el-button>
                <div
                    class="btn"
                    v-if="dialog.status == 'login'"
                    @click="router.push({ name: 'FrontCustomerMine' })"
                >
                    <el-icon><User /></el-icon>
                    <span>个人中心</span>
                </div>
                <div class="btn" v-if="dialog.status == 'login'" @click="logout">
                    <el-icon><SwitchButton /></el-icon>
                    <span>退出系统</span>
                </div>
            </div>
        </header>
        <!-- 二级路由出口 -->
        <!-- 防止该组件的复用，生命周期钩子无法执行 -->
        <RouterView :key="router.currentRoute.value.fullPath" />
    </div>
    <footer>
        <!-- 灰色区域 -->
        <div class="aboutus-container">
            <ul class="list">
                <li class="item">
                    <img src="../../assets/front/index/d1.svg" alt="" />
                    <div>
                        <h3>全国连锁</h3>
                        <p>覆盖全国主要城市的专业体检网络，随时随地享受便捷服务</p>
                    </div>
                </li>
                <li class="item">
                    <img src="../../assets/front/index/d2.svg" alt="" />
                    <div>
                        <h3>智能管理</h3>
                        <p>数字化健康档案系统，为您提供个性化的健康管理方案</p>
                    </div>
                </li>
                <li class="item">
                    <img src="../../assets/front/index/d3.svg" alt="" />
                    <div>
                        <h3>权威专家</h3>
                        <p>由三甲医院资深医师组成的专业团队，确保检查结果准确可靠</p>
                    </div>
                </li>
                <li class="item">
                    <img src="../../assets/front/index/d4.svg" alt="" />
                    <div>
                        <h3>贴心服务</h3>
                        <p>一对一健康顾问，报告专业解读，后续健康指导全程陪伴</p>
                    </div>
                </li>
            </ul>
        </div>
        <div class="contactus-container">
            <ul class="list">
                <!-- 美年大健康和电话 -->
                <li class="left">
                    <!-- 美年大健康图片 -->
                    <img src="../../assets/front/index/service.png" />
                    <!-- 电话 -->
                    <p class="tel">400-6666-8888</p>
                </li>
                <!-- 了解美年 -->
                <li>
                    <ul class="link-list">
                        <li><a href="">了解美年</a></li>
                        <li><a href="">公司概况</a></li>
                        <li><a href="">成长轨迹</a></li>
                        <li><a href="">健康生态体系</a></li>
                        <li><a href="">价值理念</a></li>
                    </ul>
                </li>
                <!-- 健康资讯 -->
                <li>
                    <ul class="link-list">
                        <li><a href="">健康资讯</a></li>
                        <li><a href="">最新动态</a></li>
                        <li><a href="">媒体聚焦</a></li>
                        <li><a href="">党建风采</a></li>
                    </ul>
                </li>
                <!-- 服务支持 -->
                <li>
                    <ul class="link-list">
                        <li><a href="">服务支持</a></li>
                        <li><a href="">常见问题</a></li>
                        <li><a href="">在线预约</a></li>
                        <li><a href="">报告查询</a></li>
                        <li><a href="">服务条款</a></li>
                    </ul>
                </li>
                <!-- 合作共赢 -->
                <li>
                    <ul class="link-list">
                        <li><a href="">合作共赢</a></li>
                        <li><a href="">合作伙伴</a></li>
                        <li><a href="">加入我们</a></li>
                    </ul>
                </li>
                <!-- 公众号二维码和下载体检app的二维码 -->
                <li class="right">
                    <div class="ercode">
                        <p>微信公众号</p>
                        <img src="../../assets/front/index/ercode-1.png" />
                    </div>
                    <div class="ercode">
                        <p>下载体检APP</p>
                        <img src="../../assets/front/index/ercode-2.png" />
                    </div>
                </li>
            </ul>
        </div>
    </footer>
    <el-dialog v-model="dialog.visible" title="手机快速登录" width="400" class="dialog">
        <el-row>
            <el-col :span="24">
                <el-input
                    v-model="dialog.phone"
                    placeholder="输入手机号，快捷登录"
                    size="large"
                    maxlength="11"
                    clearable
                >
                    <template #prepend>
                        <el-icon><Iphone /></el-icon>
                    </template>
                </el-input>
            </el-col>
        </el-row>
        <el-row :gutter="10">
            <el-col :span="16">
                <el-input
                    v-model="dialog.code"
                    placeholder="输入短信验证码"
                    size="large"
                    maxlength="6"
                    clearable
                >
                    <template #prepend>
                        <el-icon><Message /></el-icon>
                    </template>
                </el-input>
            </el-col>
            <el-col :span="8">
                <el-button
                    size="large"
                    class="receive-btn"
                    type="primary"
                    plain
                    @click="sendSmsCode"
                    :disabled="dialog.disabled"
                >
                    {{ dialog.btnContent }}
                </el-button>
            </el-col>
        </el-row>
        <el-button type="primary" class="login-btn" size="large" @click="login">
            登录系统
        </el-button>
    </el-dialog>
</template>

<script lang="ts" setup>
    import router from '../../router';
    import { reactive, getCurrentInstance, onMounted } from 'vue';
    import { ElMessage, type TagProps } from 'element-plus';
    import { isPhone, stringIsEmpty, isSmsCode } from '../../utils/validate';
    import request from '../../utils/request';
    interface TagItem {
        label: string;
        type: TagProps['type'];
    }

    // 获取当前组件实例，组件实例中有proxy属性，解构拿到proxy
    // 以后通过proxy访问全局属性/全局变量
    //const { proxy } = getCurrentInstance();

    // 定义响应式对象
    const header = reactive({
        // 对应头部区域的文本框
        keyword: '',
        // 文本框下面的tag标签，以下tag标签的内容
        tags: [
            {
                label: '青少年体检',
                type: 'info',
            },
            {
                label: '婚前体检',
                type: 'info',
            },
            {
                label: '精英体检',
                type: 'info',
            },
        ] as TagItem[],
    });
    const dialog = reactive({
        visible: false,
        phone: '',
        code: null,
        disabled: false,
        btnContent: '获取短信验证码',
        num: 0,
        status: 'logout',
    });

    const dataRule = reactive({
        phone: [{ required: true, pattern: '^1[1-9]\d{9}$', message: '手机号码错误' }],
    });
    function showDialog() {
        dialog.visible = true;
    }
    async function sendSmsCode() {
        const phone = dialog.phone;
        if (stringIsEmpty(phone)) {
            ElMessage.error('手机号不能为空');
            return;
        }
        if (!isPhone(phone)) {
            ElMessage.error('请填写正确手机号');
            return;
        }
        const result = await request.get('/front/customer/sendSmsCode', { params: { phone } });
        if (result) {
            ElMessage.success('已发送验证码');
        }
        dialog.disabled = true;
        const interval = setInterval(() => {
            dialog.num++;
            dialog.btnContent = `${60 - dialog.num}秒后重新获取`;
            if (dialog.num === 60) {
                dialog.num = 0;
                dialog.disabled = false;
                dialog.btnContent = '获取短信验证码';
                clearInterval(interval);
            }
        }, 1000);
    }
    async function checkLogin() {
        try {
            const token = localStorage.getItem('token');
            if (!token) return //ElMessage.error('登入过期，请重新登入');
            await request.get('/front/customer/isLogin');
            dialog.status = 'login';
        } catch (error: any) {
            // 根据后端返回的状态码判断（例如 401）
            localStorage.removeItem('token');
            ElMessage.error(error.response?.data?.msg || '登录状态已失效，请重新登录');
            // 可选：跳转登录页
        }
    }
    async function login() {
        try {
            //验证表单
            if (stringIsEmpty(dialog.phone)) {
                ElMessage.error('手机号不能为空');
                return;
            }
            if (!isPhone(dialog.phone)) {
                ElMessage.error('请填写正确手机号');
                return;
            }
            if (stringIsEmpty(dialog.code)) {
                ElMessage.error('请输入验证码');
                return;
            }
            //发送axios请求
            const result = await request.post(
                '/front/customer/login?phone=' + dialog.phone + '&code=' + dialog.code,
            );
            // 将token上传到localStorage;
            localStorage.setItem('token', result.token);

            dialog.status = 'login';
            ElMessage.success('登入成功');
            dialog.visible = false;
        } finally {
            dialog.visible = false;
            dialog.code = null;
            dialog.phone = '';
        }
    }
    async function logout() {
        await request.get('/front/customer/logout');
        dialog.status = 'logout';
        router.push({ name: 'FrontIndex' });
        ElMessage.success('退出成功');
    }
    function searchGoods() {
        if (header.keyword.trim() === '' || header.keyword === null) return;
        router.push({
            name: 'FrontGoodsList',
            query: { keyword: header.keyword, random: new Date().getTime() },
        });
    }
    header.keyword = router.currentRoute.value.query.keyword as string;
    function tagHandle(value: string) {
        header.keyword = value;
    }
    onMounted(() => {
        checkLogin();
    });
</script>

<style scoped>
    @import url(./main.less);
</style>
