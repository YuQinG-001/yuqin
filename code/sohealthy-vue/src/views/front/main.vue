<template>
    <div class="container">
        <header>
            <div class="logo-container">
                <img
                    src="../../assets/front/index/logo.png"
                    alt="logo"
                    class="logo"
                />
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
                            <el-button>搜索</el-button>
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
                        class="tag"
                        round
                    >
                        {{ item.label }}
                    </el-tag>
                </div>
            </div>
            <div class="oper-container">
                <!-- 将来用户登录成功后，登录/注册 按钮会隐藏，然后显示  个人中心 退出 。 -->
                <el-button type="primary" size="large"> 登录/注册 </el-button>
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
                        <p>
                            覆盖全国主要城市的专业体检网络，随时随地享受便捷服务
                        </p>
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
                        <p>
                            由三甲医院资深医师组成的专业团队，确保检查结果准确可靠
                        </p>
                    </div>
                </li>
                <li class="item">
                    <img src="../../assets/front/index/d4.svg" alt="" />
                    <div>
                        <h3>贴心服务</h3>
                        <p>
                            一对一健康顾问，报告专业解读，后续健康指导全程陪伴
                        </p>
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
</template>

<script lang="ts" setup>
    import router from '../../router';
    import { reactive, getCurrentInstance } from 'vue';
    import type { TagProps } from 'element-plus';

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
</script>

<style scoped>
    @import url(./main.less);
</style>
