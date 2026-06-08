<template>
    <el-form :inline="true" :model="dataForm" :rules="dataRule" ref="form">
        <el-form-item prop="keyword">
            <el-input
                v-model="dataForm.keyword"
                placeholder="套餐标题 / 订单编号"
                size="medium"
                class="keyword"
                maxlength="32"
                clearable="clearable"
            />
        </el-form-item>
        <el-form-item>
            <el-button size="medium" type="primary" :icon="Search" @click="searchHandle()"
                >查询</el-button
            >
        </el-form-item>
        <el-form-item class="mold">
            <el-radio-group v-model="dataForm.statusLabel" size="medium" @change="searchHandle()">
                <el-radio-button label="全部"></el-radio-button>
                <el-radio-button label="未付款"></el-radio-button>
                <el-radio-button label="已付款"></el-radio-button>
            </el-radio-group>
        </el-form-item>
    </el-form>
    <div class="order-list" v-show="!empty">
        <div class="order" v-for="one in data.dataList">
            <div class="header">
                <div class="datetime">{{ one.createTime }}</div>
                <div class="uuid">
                    订单号：
                    <span>{{ one.outTradeNo }}</span>
                </div>
                <div class="detail" @click="searchDetailHandle(one.snapshotId)">查看商品详情</div>
            </div>
            <div class="content">
                <img :src="one.goodsImage" class="image" />
                <div class="info">
                    <h4>{{ one.goodsTitle }}</h4>
                    <p>{{ one.goodsDescription }}</p>
                </div>
                <div class="price">
                    <span class="label">套餐单价</span>
                    <span class="value">￥{{ one.goodsPrice }}</span>
                </div>
                <div class="number">
                    <span class="label">购买数量</span>
                    <span class="value">×{{ one.quantity }}</span>
                </div>
                <div class="amount">
                    <span class="label">合计</span>
                    <span class="value">￥{{ one.totalAmount }}</span>
                </div>
                <div class="status">
                    <span class="label">状态</span>
                    <span class="value">{{ one.orderStatus }}</span>
                </div>
                <div class="operate">
                    <el-button
                        v-if="one.orderStatus == '未付款'"
                        type="primary"
                        :disabled="one.disabled"
                        @click="paymentHandle(one.outTradeNo)"
                    >
                        付款
                    </el-button>
                    <el-button
                        v-if="one.orderStatus == '未付款'"
                        type="danger"
                        @click="closeOrderHandle(one.orderId)"
                    >
                        取消订单
                    </el-button>
                    <el-button
                        v-if="one.orderStatus == '已付款'"
                        type="primary"
                        :disabled="one.appointCount == one.quantity"
                        @click="appointHandle(one.orderId, one.quantity, one.appointCount)"
                    >
                        预约体检
                    </el-button>
                    <el-button v-if="one.orderStatus == '已结束'">获取发票</el-button>
                    <el-button
                        v-if="one.orderStatus == '已付款'"
                        type="danger"
                        :disabled="one.appointCount > 0"
                        @click="refundHandle(one.orderId)"
                    >
                        退款
                    </el-button>
                </div>
            </div>
        </div>
        <el-pagination
            @size-change="sizeChangeHandle"
            @current-change="currentChangeHandle"
            :current-page="data.pageIndex"
            :page-sizes="[10, 20, 50]"
            :page-size="data.pageSize"
            :total="data.totalCount"
            layout="total, sizes, prev, pager, next, jumper"
        ></el-pagination>
    </div>
    <div class="empty" v-show="empty">
        <el-empty :image-size="200" />
    </div>
</template>
<script lang="ts" setup>
    import { reactive, ref, type Ref, getCurrentInstance, onMounted, useTemplateRef } from 'vue';
    import { Search } from '@element-plus/icons-vue';
    import router from '../../router/index';

    import { dayjs, ElMessage, ElMessageBox } from 'element-plus';
    import isBetween from 'dayjs/plugin/isBetween';
    import request from '../../utils/request';
    dayjs.extend(isBetween);

    const { proxy } = getCurrentInstance()!;
    const formRef = useTemplateRef('form');

    let empty = ref(false);

    const dataForm = reactive({
        keyword: '',
        statusLabel: '全部',
        status: 0 as null | number,
    });

    const dataRule = reactive({
        keyword: [
            {
                required: false,
                pattern: '^[a-zA-Z0-9\u4e00-\u9fa5]{1,32}$',
                message: '关键字内容不正确',
            },
        ],
    });

    const data = reactive({
        dataList: [],
        pageIndex: 1,
        pageSize: 10,
        totalCount: 0,
        loading: false,
    });
    async function loadPageData() {
        try {
            data.loading = true;
            // 准备数据
            if (dataForm.statusLabel == '全部') {
                dataForm.status = null;
            } else if (dataForm.statusLabel == '未付款') {
                dataForm.status = 1;
            } else {
                dataForm.status = 3;
            }
            const json = {
                keyword: dataForm.keyword,
                orderStatus: dataForm.status,
                pageNum: data.pageIndex,
                pageSize: data.pageSize,
            };
            const rusult = await request.post('/front/order/pageQuery', json);
            const list = rusult.records;
            const statusEnum: Record<string, string> = {
                '1': '未付款',
                '2': '已关闭',
                '3': '已付款',
                '4': '已退款',
                '5': '已预约',
                '6': '已结束',
            };
            for (let one of list) {
                one.goodsImage = `${proxy!.$minioUrl}/${one.goodsImage}`;
                one.orderStatus = statusEnum[one.orderStatus + ''];
            }
            data.dataList = list;
            data.totalCount = rusult.total;
            empty.value = list.length == 0;
        } finally {
            data.loading = false;
        }
    }
    function sizeChangeHandle(pageSize: number) {
        data.pageSize = pageSize;
        data.pageIndex = 1;
        loadPageData();
    }
    function currentChangeHandle(pageNum: number) {
        data.pageIndex = pageNum;
        loadPageData();
    }
    async function searchHandle() {
        const ok = await formRef.value?.validate();
        if (!ok) return false;
        formRef.value?.clearValidate();
        data.pageIndex = 1;
        loadPageData();
    }
    async function refundHandle(orderId: number) {
        await ElMessageBox.confirm('您确定要退款吗？', '提示信息', {
            type: 'warning',
            confirmButtonText: '确定',
            cancelButtonText: '取消',
        });
        const result = await request.post('/front/order/refund', { orderId });
        if (!result) {
            return ElMessage.error('退款失败请联系客服！');
        }
        ElMessage.success('退款已提交，请稍后查看');
        loadPageData();
    }
    async function closeOrderHandle(orderId: number) {
        await ElMessageBox.confirm('您确定要关闭订单吗？', '提示信息', {
            type: 'warning',
            confirmButtonText: '确定',
            cancelButtonText: '取消',
        });
        const result = await request.post('/front/order/close', { orderId });
        if (!result) return ElMessage.error('关闭订单失败请联系客服！');
        ElMessage.success('订单已关闭');
        loadPageData();
    }
    onMounted(() => {
        loadPageData();
    });
</script>
<style lang="less" scoped>
    @import url(order_list.less);
</style>
