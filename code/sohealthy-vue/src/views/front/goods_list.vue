<template>
    <!-- 面包屑导航组件，用于显示当前页面在网站结构中的位置 -->
    <el-breadcrumb separator="/" class="breadcrumb">
        <!-- 首页面包屑项，点击可跳转到首页 -->
        <el-breadcrumb-item :to="{ name: 'FrontIndex' }">首页</el-breadcrumb-item>
        <!-- 当前页面面包屑项，显示为体检套餐列表页面 -->
        <el-breadcrumb-item>体检套餐列表</el-breadcrumb-item>
    </el-breadcrumb>

    <!-- 搜索条件筛选区域，包含分类、性别、价格等多维度筛选 -->
    <div class="search-rows">
        <!-- 分类筛选行：使用Element Plus的栅格布局，gutter="0"表示列间无间隔 -->
        <el-row :gutter="0" class="row">
            <!-- 分类标签，占据2列宽度 -->
            <el-col :span="2"><span class="label">【 分类 】</span></el-col>
            <!-- 动态渲染分类选项，每个选项占据2列宽度 -->
            <el-col :span="2" v-for="one in condition.type" :key="one.name">
                <!-- 条件选项，根据active状态切换样式，点击触发筛选 -->
                <span
                    :class="one.active ? 'item active' : 'item'"
                    @click="selectHandle('type', one.name)"
                >
                    {{ one.name }}
                </span>
            </el-col>
        </el-row>

        <!-- 性别筛选行：结构与分类筛选行类似 -->
        <el-row :gutter="0" class="row">
            <el-col :span="2"><span class="label">【 性别 】</span></el-col>
            <el-col :span="2" v-for="one in condition.sex" :key="one.name">
                <span
                    :class="one.active ? 'item active' : 'item'"
                    @click="selectHandle('sex', one.name)"
                >
                    {{ one.name }}
                </span>
            </el-col>
        </el-row>

        <!-- 价格区间筛选行：提供不同价格范围的筛选选项 -->
        <el-row :gutter="0" class="row">
            <el-col :span="2"><span class="label">【 价格 】</span></el-col>
            <el-col :span="2" v-for="one in condition.priceType" :key="one.name">
                <span
                    :class="one.active ? 'item active' : 'item'"
                    @click="selectHandle('priceType', one.name)"
                >
                    {{ one.name }}
                </span>
            </el-col>
        </el-row>
    </div>

    <!-- 排序筛选区域：提供多种排序方式选择 -->
    <div class="search-filter">
        <!-- 排序单选按钮组，v-model绑定当前选中的排序方式 -->
        <el-radio-group v-model="radio" @change="selectRadio">
            <!-- 按最新排序选项 -->
            <el-radio label="最新" size="large">最新</el-radio>
            <!-- 按销量排序选项 -->
            <el-radio label="销量" size="large">销量</el-radio>
        </el-radio-group>

        <!-- 价格排序操作区域，点击可切换升序/降序 -->
        <div class="sort-operate" @click="selectPrice">
            <span>价格</span>
            <!-- 价格排序图标，根据排序状态显示不同图标 -->
            <SvgIcon :name="priceOrder.icon" class="sort-icon" />
        </div>
    </div>
    <div class="goods-container">
        <el-empty
            description="无体检套餐"
            :image-size="200"
            v-if="data.dataList.length == 0"
        ></el-empty>
        <!-- 商品列表容器，使用v-infinite-scroll指令实现无限滚动加载 -->
        <ul class="goods-list" v-infinite-scroll="load">
            <!-- 遍历商品数据列表，生成商品项 -->
            <li
                class="item"
                v-for="(one, index) in data.dataList"
                :style="(index + 1) % 4 == 0 ? 'margin-right:0' : ''"
            >
                <!-- 每行第4个商品移除右边距 -->
                <div class="card">
                    <!-- 商品图片 -->
                    <img :src="one.coverImage" />
                    <!-- 商品标题 -->
                    <h4>{{ one.title }}</h4>
                    <!-- 商品描述，使用Element Plus的提示框组件 -->
                    <el-tooltip class="box-item" effect="dark" placement="top">
                        <template #content>
                            <div style="width: 260px">{{ one.description }}</div>
                        </template>
                        <p class="desc">
                            <span>折</span>
                            <!-- 折扣标签 -->
                            {{ one.description }}
                        </p>
                    </el-tooltip>
                    <!-- 价格信息区域 -->
                    <p class="price">
                        <span class="current">￥{{ one.currentPrice }}</span>
                        <!-- 当前价格 -->
                        <span class="old">￥{{ one.originalPrice }}</span>
                        <!-- 原价 -->
                        <span class="sale">已售{{ one.salesVolume }}</span>
                        <!-- 销量 -->
                    </p>
                    <!-- 立即购买按钮 -->
                    <input
                        type="button"
                        class="buy-btn"
                        value="立即购买"
                        @click="buyHandle(one.id)"
                    />
                </div>
            </li>
        </ul>
    </div>
</template>
<script lang="ts" setup>
    import { reactive, ref, getCurrentInstance, onMounted } from 'vue';
    import router from '../../router/index';
    import SvgIcon from '../../components/SvgIcon.vue';
    import request from '../../utils/request';
    const { proxy } = getCurrentInstance()!;

    let radio = ref('销量');
    interface ConditionItem {
        name: string;
        active: boolean;
        value?: string | number; // 根据实际情况
    }
    const priceOrder = reactive({
        icon: 'sort-default', // sort-asc 升序图标。sort-desc 降序图标。
    });

    const dataForm = reactive({
        keyword: '',
        type: '',
        sex: '',
        priceType: '',
        orderType: [] as number[],
    });

    const condition: Record<string, ConditionItem[]> = reactive({
        type: [
            { name: '不限', active: true },
            { name: '父母体检', value: '父母体检', active: false },
            { name: '入职体检', value: '入职体检', active: false },
            { name: '职场白领', value: '职场白领', active: false },
            { name: '个人高端', value: '个人高端', active: false },
            { name: '中青年体检', value: '中青年体检', active: false },
        ],
        sex: [
            { name: '不限', active: true },
            { name: '男性', value: '男性', active: false },
            { name: '女性', value: '女性', active: false },
        ],
        priceType: [
            { name: '不限', active: true },
            { name: '0~100', value: 1, active: false },
            { name: '100~500', value: 2, active: false },
            { name: '500~1000', value: 3, active: false },
            { name: '1000以上', value: 4, active: false },
        ],
    });
    const data = reactive({
        dataList: [],
        pageIndex: 0, // 这个是当前页码，为什么从0开始，mis端都是从1开始呀。这个后面再说。
        pageSize: 12, // 12正好是4的倍数，三行比较好看。
        totalCount: 0,
        isLast: false, // 是否为最后一页的标记。
    });
    function buyHandle(id: number) {
        router.push({ name: 'FrontGoods', params: { id } });
    }
    // 转换函数：将后端数据转换为前端格式
    const transformPackage = (item: any) => ({
        id: item.id,
        packageCode: item.packageCode,
        coverImage: `${proxy?.$minioUrl}${item.coverImage}`, // 拼接完整图片路径
        title: item.packageName, // 后端 packageName → 前端 title
        description: item.description,
        currentPrice: parseFloat(item.currentPrice), // 字符串转数字
        originalPrice: parseFloat(item.originalPrice),
        salesVolume: item.salesVolume,
    });
    function load() {
        if (data.isLast) {
            return;
        }
        dataForm.keyword = router.currentRoute.value.query.keyword as string;
        data.pageIndex++;
        loadPageData();
        dataForm.keyword = '';
    }
    async function loadPageData() {
        const sendData = {
            keyword: dataForm.keyword,
            packageType: dataForm.type,
            sex: dataForm.sex,
            priceType: dataForm.priceType,
            orderType: dataForm.orderType,
            pageNo: data.pageIndex,
            pageSize: data.pageSize,
        };
        const result = await request.post('/front/goods/pageQuery', sendData);
        const list = result.records.map(transformPackage);
        if (list == null || list.length == 0) {
            data.isLast = true;
            data.pageIndex--;
            return;
        }
        data.dataList = data.dataList.concat(list);
        data.totalCount = result.total;
        dataForm.keyword = '';
    }

    function selectHandle(key: string, name: string) {
        const items = condition[key];
        if (!Array.isArray(items)) return;

        let selectedValue = null;
        for (const item of items) {
            const isActive = item.name === name;
            item.active = isActive;
            if (isActive) {
                selectedValue = item.value;
            }
        }
        // 只有当确实选中了某项时才更新 dataForm[key]
        if (selectedValue !== null) {
            (dataForm as any)[key] = selectedValue;
        }

        // 清空之前的分页数据
        data.dataList = [];
        data.isLast = false;
        data.pageIndex = 0;
        data.pageSize = 12;
        data.totalCount = 0;
        // 重新根据条件加载最新数据
        load();
    }
    function selectRadio(value: string) {
        // 将升降序图标恢复为默认
        priceOrder.icon = 'sort-default';
        // 维护响应式对象中的数据
        if (value == '最新') {
            dataForm.orderType = [1];
        } else if (value == '销量') {
            dataForm.orderType = [2];
        }
        // 清空之前的分页数据
        data.dataList = [];
        data.isLast = false;
        data.pageIndex = 0;
        data.pageSize = 12;
        data.totalCount = 0;
        // 重新根据条件加载最新数据
        load();
    }
    function selectPrice() {
        // 先把销量和最新单选按钮取消选中。
        radio.value = '';

        // 然后再维护响应式对象的状态。
        if (priceOrder.icon == 'sort-default' || priceOrder.icon == 'sort-desc') {
            // 维护图标状态
            priceOrder.icon = 'sort-asc';
            // 维护响应式数据
            dataForm.orderType = [3];
        } else {
            // 维护图标状态
            priceOrder.icon = 'sort-desc';
            dataForm.orderType = [4];
        }

        // 清空之前数据，并加载新数据。
        // 清空之前的分页数据
        data.dataList = [];
        data.isLast = false;
        data.pageIndex = 0;
        data.pageSize = 12;
        data.totalCount = 0;
        // 重新根据条件加载最新数据
        load();
    }
    onMounted(() => {
        load();
    });
</script>
<style lang="less" scoped>
    @import url('goods_list.less');
</style>
