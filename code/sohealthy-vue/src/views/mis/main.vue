<template>
    <div
        class="site-wrapper"
        :class="{ 'site-sidebar--fold': sidebarFold }"
        v-loading.fullscreen.lock="loading"
        element-loading-text="加载中"
    >
        <!-- 导航栏 -->
        <nav class="site-navbar" :class="{ 'site-navbar--fold': sidebarFold }">
            <div class="site-navbar__header">
                <h1 class="site-navbar__brand">
                    <a class="site-navbar__brand-lg">大健康体检系统</a>
                    <a class="site-navbar__brand-mini">体检</a>
                </h1>
            </div>

            <div class="navbar-container" :class="{ 'navbar-container--fold': sidebarFold }">
                <div class="switch" @click="handleSwitch">
                    <SvgIcon name="zhedie" class="icon-svg" />
                </div>
                <div class="right-container">
                    <el-dropdown>
                        <span class="el-dropdown-link">
                            <span class="avatar-container">
                                <el-avatar
                                    shape="circle"
                                    :size="25"
                                    :src="user.photo"
                                    :icon="UserFilled"
                                />
                            </span>
                            {{ user.name }}
                        </span>
                        <template #dropdown>
                            <el-dropdown-menu>
                                <el-dropdown-item @click="updatePassword"
                                    >修改密码</el-dropdown-item
                                >
                                <el-dropdown-item @click="logout">退出</el-dropdown-item>
                            </el-dropdown-menu>
                        </template>
                    </el-dropdown>
                </div>
            </div>
        </nav>

        <!-- 侧边栏 -->
        <aside class="site-sidebar site-sidebar--dark">
            <div class="site-sidebar__inner">
                <el-menu
                    :default-active="menuActiveName"
                    :collapse="sidebarFold"
                    :collapseTransition="false"
                    class="site-sidebar__menu"
                    background-color="#263238"
                    active-text-color="#fff"
                    text-color="#8a979e"
                >
                    <el-menu-item index="Home" @click="router.push({ name: 'MisHome' })">
                        <el-icon><SvgIcon name="home" class="icon-svg" /></el-icon>
                        <span>首页</span>
                    </el-menu-item>

                    <el-sub-menu index="组织管理" :popper-class="'site-sidebar--dark-popper'">
                        <template #title>
                            <el-icon><SvgIcon name="users_fill" class="icon-svg" /></el-icon>
                            <span>组织管理</span>
                        </template>
                        <el-menu-item
                            index="MisDept"
                            v-if="isAuth(['ROOT', 'DEPT:SELECT'])"
                            @click="router.push({ name: 'MisDept' })"
                        >
                            <el-icon><SvgIcon name="company_fill" class="icon-svg" /></el-icon>
                            <span>部门管理</span>
                        </el-menu-item>
                        <el-menu-item
                            index="MisRole"
                            v-if="isAuth(['ROOT', 'ROLE:SELECT'])"
                            @click="router.push({ name: 'MisRole' })"
                        >
                            <el-icon><SvgIcon name="role_fill" class="icon-svg" /></el-icon>
                            <span>角色管理</span>
                        </el-menu-item>
                        <el-menu-item
                            index="MisUser"
                            v-if="isAuth(['ROOT', 'USER:SELECT'])"
                            @click="router.push({ name: 'MisUser' })"
                        >
                            <el-icon><SvgIcon name="user_fill" class="icon-svg" /></el-icon>
                            <span>用户管理</span>
                        </el-menu-item>
                    </el-sub-menu>

                    <el-sub-menu index="业务管理" :popper-class="'site-sidebar--dark-popper'">
                        <template #title>
                            <el-icon><SvgIcon name="trust_fill" class="icon-svg" /></el-icon>
                            <span>业务管理</span>
                        </template>
                        <el-menu-item
                            index="MisGoods"
                            v-if="isAuth(['ROOT', 'GOODS:SELECT'])"
                            @click="router.push({ name: 'MisGoods' })"
                        >
                            <el-icon><SvgIcon name="goods_fill" class="icon-svg" /></el-icon>
                            <span>体检套餐</span>
                        </el-menu-item>
                        <el-menu-item
                            index="MisRule"
                            v-if="isAuth(['ROOT', 'RULE:SELECT'])"
                            @click="router.push({ name: 'MisRule' })"
                        >
                            <el-icon><SvgIcon name="rule_fill" class="icon-svg" /></el-icon>
                            <span>促销规则</span>
                        </el-menu-item>
                        <el-menu-item
                            index="MisCustomer"
                            v-if="isAuth(['ROOT', 'CUSTOMER:SELECT'])"
                            @click="router.push({ name: 'MisCustomer' })"
                        >
                            <el-icon><SvgIcon name="customer_fill" class="icon-svg" /></el-icon>
                            <span>客户档案</span>
                        </el-menu-item>
                        <el-menu-item
                            index="MisOrder"
                            v-if="isAuth(['ROOT', 'ORDER:SELECT'])"
                            @click="router.push({ name: 'MisOrder' })"
                        >
                            <el-icon><SvgIcon name="order_fill" class="icon-svg" /></el-icon>
                            <span>订单管理</span>
                        </el-menu-item>
                        <el-menu-item
                            index="MisCustomerIm"
                            v-if="isAuth(['ROOT', 'CUSTOMER_IM:SELECT'])"
                            @click="router.push({ name: 'MisCustomerIm' })"
                        >
                            <el-icon><SvgIcon name="im_fill" class="icon-svg" /></el-icon>
                            <span>客服IM</span>
                        </el-menu-item>
                    </el-sub-menu>

                    <el-sub-menu index="体检管理" :popper-class="'site-sidebar--dark-popper'">
                        <template #title>
                            <el-icon><SvgIcon name="night_fill" class="icon-svg" /></el-icon>
                            <span>体检管理</span>
                        </template>
                        <el-menu-item
                            index="MisAppointment"
                            v-if="isAuth(['ROOT', 'APPOINTMENT:SELECT'])"
                            @click="router.push({ name: 'MisAppointment' })"
                        >
                            <el-icon><SvgIcon name="appointment_fill" class="icon-svg" /></el-icon>
                            <span>体检预约</span>
                        </el-menu-item>
                        <el-menu-item
                            index="MisCustomerCheckin"
                            v-if="isAuth(['ROOT', 'CUSTOMER_CHICKIN:SELECT'])"
                            @click="router.push({ name: 'MisCustomerCheckin' })"
                        >
                            <el-icon><SvgIcon name="checkin_fill" class="icon-svg" /></el-icon>
                            <span>体检签到</span>
                        </el-menu-item>
                        <el-menu-item
                            index="MisAppointmentRestriction"
                            v-if="isAuth(['ROOT', 'APPOINTMENT_RESTRICTION:SELECT'])"
                            @click="router.push({ name: 'MisAppointmentRestriction' })"
                        >
                            <el-icon><SvgIcon name="setting_fill" class="icon-svg" /></el-icon>
                            <span>预约设置</span>
                        </el-menu-item>
                        <el-menu-item
                            index="MisCheckup"
                            v-if="isAuth(['ROOT', 'CHECKUP:SELECT'])"
                            @click="router.push({ name: 'MisCheckup' })"
                        >
                            <el-icon><SvgIcon name="doctor_fill" class="icon-svg" /></el-icon>
                            <span>医生检查</span>
                        </el-menu-item>
                        <el-menu-item
                            index="MisCheckupReport"
                            v-if="isAuth(['ROOT', 'CHECKUP_REPORT:SELECT'])"
                            @click="router.push({ name: 'MisCheckupReport' })"
                        >
                            <el-icon><SvgIcon name="file_fill" class="icon-svg" /></el-icon>
                            <span>体检报告</span>
                        </el-menu-item>
                    </el-sub-menu>

                    <el-sub-menu
                        index="系统设置"
                        :popper-class="'site-sidebar--dark-popper'"
                        v-if="isAuth(['ROOT', 'SYSTEM:SELECT'])"
                    >
                        <template #title>
                            <el-icon><SvgIcon name="system_fill" class="icon-svg" /></el-icon>
                            <span>系统设置</span>
                        </template>
                        <el-menu-item
                            index="MisFlowRegulation"
                            v-if="isAuth(['ROOT', 'FLOW_REGULATION:SELECT'])"
                            @click="router.push({ name: 'MisFlowRegulation' })"
                        >
                            <el-icon><SvgIcon name="people_fill" class="icon-svg" /></el-icon>
                            <span>人员限流</span>
                        </el-menu-item>
                    </el-sub-menu>
                </el-menu>
            </div>
        </aside>

        <!-- 主内容区域 -->
        <div class="site-content__wrapper">
            <main class="site-content">
                <div>
                    <el-tabs
                        v-if="route.meta.isTab"
                        v-model="mainTabsActiveName"
                        :closable="true"
                        @tab-click="seletedTabHandle"
                        @tab-remove="removeTabHandle"
                    >
                        <el-tab-pane
                            v-for="item in mainTabs"
                            :key="item.name"
                            :label="item.title"
                            :name="item.name"
                        >
                            <el-card :body-style="contentViewHeightStyle">
                                <RouterView :key="$route.fullPath" />
                            </el-card>
                        </el-tab-pane>
                    </el-tabs>
                    <el-card :body-style="contentViewHeightStyle" v-else>
                        <RouterView :key="$route.fullPath" />
                    </el-card>
                </div>
            </main>
        </div>
    </div>
    <el-dialog title="提示" v-model="dialog.visible" width="25%">
        <el-form
            :model="dialog.dataForm"
            :rules="dialog.dataRule"
            ref="dialogForm"
            label-width="80px"
        >
            <el-form-item label="原密码" prop="password">
                <el-input
                    type="password"
                    v-model="dialog.dataForm.password"
                    size="default"
                    maxlength="20"
                    clearable
                />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
                <el-input
                    type="password"
                    v-model="dialog.dataForm.newPassword"
                    size="default"
                    maxlength="20"
                    clearable
                />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
                <el-input
                    type="password"
                    v-model="dialog.dataForm.confirmPassword"
                    size="default"
                    maxlength="20"
                    clearable
                />
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button size="default" @click="dialog.visible = false">取消</el-button>
                <el-button type="primary" size="default" @click="dataFormSubmit">确定</el-button>
            </span>
        </template>
    </el-dialog>
</template>
``

<script setup lang="ts">
    import { UserFilled } from '@element-plus/icons-vue';
    import { ElMessage, type FormInstance } from 'element-plus';
    import { computed, nextTick, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue';
    import { useRoute, useRouter } from 'vue-router';
    import SvgIcon from '../../components/SvgIcon.vue';
    import useAuth from '../../hooks/useAuth';
    import request from '../../utils/request.ts';

    // 类型定义
    interface TabItem {
        title: string;
        name: string;
    }

    // 路由相关
    const route = useRoute();
    const router = useRouter();

    // 用户信息
    const user = reactive({
        name: 'admin',
        photo: '',
        updatePasswordVisible: false,
    });

    // 侧边栏折叠
    const sidebarFold = ref(false);
    const handleSwitch = () => {
        sidebarFold.value = !sidebarFold.value;
    };

    // 权限判断
    const { isAuth } = useAuth();

    // 标签页管理
    const mainTabs = ref<TabItem[]>([]);
    const mainTabsActiveName = ref('');
    const menuActiveName = ref('');

    const updateTabsByRoute = (currentRoute: typeof route) => {
        const { meta, name } = currentRoute;
        if (meta.isTab && name) {
            const existing = mainTabs.value.find((tab) => tab.name === name);
            if (!existing) {
                mainTabs.value.push({
                    title: meta.title as string,
                    name: name as string,
                });
            }
            menuActiveName.value = name as string;
            mainTabsActiveName.value = name as string;
        } else {
            mainTabs.value = [];
            menuActiveName.value = '';
            mainTabsActiveName.value = 'MisHome';
        }
    };

    const removeTabHandle = (name: string | number) => {
        const tabName = String(name);
        const index = mainTabs.value.findIndex((tab) => tab.name === tabName);
        if (index === -1) return;

        const isActiveClosing = mainTabsActiveName.value === tabName;
        let newActiveName = mainTabsActiveName.value;

        if (isActiveClosing) {
            if (mainTabs.value.length > 1) {
                newActiveName = index > 0 ? mainTabs.value[index - 1].name : mainTabs.value[1].name;
            } else {
                newActiveName = '';
            }
        }

        mainTabs.value.splice(index, 1);

        if (isActiveClosing) {
            mainTabsActiveName.value = newActiveName;
            router.push({ name: newActiveName || 'MisHome' });
        }
    };

    const seletedTabHandle = (tab: { paneName?: string | number }) => {
        if (tab.paneName) {
            router.push({ name: String(tab.paneName) });
        }
    };

    // 内容高度计算
    const contentViewHeight = ref(0);
    const contentViewHeightStyle = computed(() => ({
        minHeight: `${contentViewHeight.value}px`,
    }));

    const updateContentViewHeight = () => {
        const windowHeight = window.innerHeight;
        let height = windowHeight - 50 - 30 - 2;
        if (route.meta.isTab) height -= 40;
        contentViewHeight.value = height;
    };

    const handleResize = () => updateContentViewHeight();

    onMounted(() => {
        updateContentViewHeight();
        window.addEventListener('resize', handleResize);
        updateTabsByRoute(route);
    });

    onBeforeUnmount(() => {
        window.removeEventListener('resize', handleResize);
    });

    watch(
        () => route,
        (newRoute) => {
            updateTabsByRoute(newRoute);
            updateContentViewHeight();
        },
        { deep: true, immediate: true },
    );

    // 退出登录
    const logout = async () => {
        const token = localStorage.getItem('token');
        if (!token) {
            router.push({ name: 'MisLogin' });
            return;
        }

        try {
            // 拦截器已自动添加 token 和处理业务码
            await request.get('/mis/user/logout');
            localStorage.removeItem('token');
            localStorage.removeItem('permissions');
            router.push({ name: 'MisLogin' });
            ElMessage.success('已退出登录');
        } catch (error) {
            console.error('退出失败:', error);
            // 错误提示已在拦截器中处理，这里可额外做页面跳转等
            // 如果是 token 失效，拦截器已跳转登录页
        }
    };

    // 修改密码对话框相关
    const dialogForm = ref<FormInstance>();
    const dialog = reactive({
        visible: false,
        dataForm: {
            password: '',
            newPassword: '',
            confirmPassword: '',
        },
        dataRule: {
            password: [{ required: true, pattern: /^[a-zA-Z0-9]{6,20}$/, message: '密码格式错误' }],
            newPassword: [
                { required: true, pattern: /^[a-zA-Z0-9]{6,20}$/, message: '密码格式错误' },
            ],
            confirmPassword: [
                { required: true, pattern: /^[a-zA-Z0-9]{6,20}$/, message: '密码格式错误' },
                { validator: validateConfirmPassword, trigger: 'blur' },
            ],
        },
    });

    // 自定义校验器
    function validateConfirmPassword(_rule: any, value: string, callback: (error?: Error) => void) {
        if (value !== dialog.dataForm.newPassword) {
            callback(new Error('两次输入的密码不一致'));
        } else {
            callback();
        }
    }

    // 打开修改密码弹窗
    const updatePassword = async () => {
        dialog.visible = true;
        await nextTick();
        dialogForm.value?.resetFields();
    };

    // 提交修改密码
    const dataFormSubmit = async () => {
        if (!dialogForm.value) {
            ElMessage.warning('表单初始化异常');
            return;
        }

        try {
            await dialogForm.value.validate();

            await request.post('/mis/user/modifyPassword', {
                oldPassword: dialog.dataForm.password,
                newPassword: dialog.dataForm.newPassword,
            });

            ElMessage.success('密码修改成功');
            dialog.visible = false;
        } catch (error) {
            // 表单校验失败或请求失败均会进入此处
            // 错误提示已由拦截器或 Element Plus 自行处理
            console.error('密码修改失败:', error);
        }
    };
</script>

<style lang="scss" scoped>
    // 引入变量和工具函数（路径可根据项目实际调整）
    @use '../../assets/scss/index.scss' as *;

    // 消除头像黑边框
    .el-dropdown-link:focus-visible {
        outline: none;
    }

    // 定位头像样式
    .avatar-container {
        vertical-align: -8px;
        margin-right: 5px;
    }
</style>
