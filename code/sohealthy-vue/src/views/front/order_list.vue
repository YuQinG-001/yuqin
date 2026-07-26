<template>
    <el-form :inline="true" :model="dataForm" :rules="dataRule" ref="form">
        <el-form-item prop="keyword">
            <el-input
                v-model="dataForm.keyword"
                placeholder="套餐标题 / 订单编号"
                size="default"
                class="keyword"
                maxlength="32"
                clearable
            />
        </el-form-item>
        <el-form-item>
            <el-button size="default" type="primary" :icon="Search" @click="searchHandle()"
                >查询</el-button
            >
        </el-form-item>
        <el-form-item class="mold">
            <el-radio-group v-model="dataForm.statusLabel" size="default" @change="searchHandle()">
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
                        v-if="['已付款', '已预约'].includes(one.orderStatus)"
                        type="primary"
                        :disabled="one.appointCount >= one.quantity"
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
    <el-dialog
        title="体检预约"
        :close-on-click-modal="false"
        v-model="appointDialog.visible"
        width="550px"
    >
        <el-form
            :model="appointDialog.dataForm"
            ref="dialogForm"
            :rules="appointDialog.dataRule"
            label-width="80px"
        >
            <fieldset class="appointment">
                <legend>
                    <h4>我的预约</h4>
                </legend>
                <el-form-item label="预约日期" prop="appointmentDate">
                    <el-date-picker
                        v-model="appointDialog.dataForm.appointmentDate"
                        type="date"
                        placeholder="选择日期"
                        size="medium"
                        :editable="false"
                        format="YYYY-MM-DD"
                        value-format="YYYY-MM-DD"
                        :disabled-date="disabledDate"
                    />
                    <span class="desc">提示：不可预约今日</span>
                </el-form-item>
                <el-form-item label="体检人" prop="patientName">
                    <el-input
                        v-model="appointDialog.dataForm.patientName"
                        size="medium"
                        placeholder="输入姓名"
                        maxlength="10"
                        clearable
                    />
                </el-form-item>
                <el-form-item label="身份证号" prop="idCardNo">
                    <el-input
                        v-model="appointDialog.dataForm.idCardNo"
                        size="medium"
                        placeholder="输入身份证号"
                        maxlength="18"
                        clearable
                    />
                </el-form-item>
                <el-form-item label="电话号码" prop="phone">
                    <el-input
                        v-model="appointDialog.dataForm.phone"
                        size="medium"
                        placeholder="输入电话号码"
                        maxlength="11"
                        clearable
                    />
                </el-form-item>
                <el-form-item label="邮寄地址" prop="address">
                    <el-input
                        v-model="appointDialog.dataForm.address"
                        size="medium"
                        placeholder="输入接收体检报告的邮寄地址"
                        maxlength="100"
                        clearable
                    />
                </el-form-item>
                <el-form-item label="公司名称" prop="company">
                    <el-input
                        v-model="appointDialog.dataForm.company"
                        size="medium"
                        placeholder="输入公司名称"
                        maxlength="100"
                        clearable
                    />
                </el-form-item>
            </fieldset>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button size="medium" @click="appointDialog.visible = false">取消</el-button>
                <el-button type="primary" size="medium" @click="dataFormSubmit">确定</el-button>
            </span>
        </template>
    </el-dialog>
</template>
<script lang="ts" setup>
    import {
        reactive,
        ref,
        type Ref,
        getCurrentInstance,
        onMounted,
        useTemplateRef,
        nextTick,
    } from 'vue';
    import { Search } from '@element-plus/icons-vue';
    import router from '../../router/index';

    import { dayjs, ElMessage, ElMessageBox } from 'element-plus';
    import isBetween from 'dayjs/plugin/isBetween';
    import request from '../../utils/request';
    dayjs.extend(isBetween);

    const { proxy } = getCurrentInstance()!;
    const formRef = useTemplateRef('form');

    let empty = ref(false);

    interface OrderItemRaw {
        /** 订单ID */
        orderId: number;
        /** 外部订单号 */
        outTradeNo: string;
        /** 创建时间 */
        createTime: string;
        /** 商品标题 */
        goodsTitle: string;
        /** 商品描述 */
        goodsDescription: string;
        /** 商品图片路径 */
        goodsImage: string;
        /** 商品单价 */
        goodsPrice: number;
        /** 购买数量 */
        quantity: number;
        /** 总金额 */
        totalAmount: number;
        /** 订单状态（数字） */
        orderStatus: string;
        /** 套餐快照ID */
        snapshotId: number;
        /** 已预约人数 */
        appointCount: number;
        disabled: boolean;
    }
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
        dataList: [] as OrderItemRaw[],
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

    const appointDialog = reactive({
        visible: false,
        dataForm: {
            orderId: null,
            appointmentDate: null,
            patientName: null,
            idCardNo: null,
            phone: null,
            address: null,
            company: null,
        },
        dataRule: {
            appointmentDate: [{ required: true, message: '日期不能为空' }],
            patientName: [
                { required: true, message: '姓名不能为空' },
                { pattern: '^[\u4e00-\u9fa5]{2,10}$', message: '姓名格式错误' },
            ],
            idCardNo: [
                { required: true, message: '身份证号不能为空' },
                { pattern: '^[0-9Xx]{18}$', message: '身份证号格式错误' },
            ],
            phone: [
                { required: true, message: '电话号码不能为空' },
                { pattern: '^1[1-9]\\d{9}$', message: '电话号码格式错误' },
            ],
            address: [
                { required: true, message: '邮寄地址不能为空' },
                { pattern: '^[0-9A-Za-z\u4e00-\u9fa5\\-_#]{10,100}$', message: '邮寄地址格式错误' },
            ],
            company: [
                {
                    required: false,
                    pattern: '^[0-9A-Za-z\u4e00-\u9fa5\\-_#]{2,100}$',
                    message: '公司名称不正确',
                },
            ],
        },
    });

    function disabledDate(date: any) {
        //只能预约未来60天的体检
        let bool = dayjs(date).isBetween(dayjs(), dayjs().add(61, 'day'));
        return !bool;
    }
    const dialogFormRef = useTemplateRef('dialogForm');
    async function appointHandle(orderId: number, quantity: number, appointCount: number) {
        appointDialog.dataForm.orderId = orderId;
        if (quantity == appointCount) {
            ElMessage.warning('订单已经全部预约，无法再次预约');
            return;
        }
        if (appointCount == 0) {
            try {
                await ElMessageBox.confirm('一但预约不支持退款，您确定预约吗？', '提示信息', {
                    type: 'warning',
                    cancelButtonText: '取消',
                    confirmButtonText: '确定',
                });
            } catch (error) {
                console.log('用户取消操作');
                return;
            }
        }
        appointDialog.visible = true;
        await nextTick();
        dialogFormRef.value?.clearValidate();
        dialogFormRef.value?.resetFields();
    }
    async function dataFormSubmit() {
        // 校验表单
        let ok = await dialogFormRef.value?.validate();
        if (!ok) {
            return;
        }
        // 清除错误提示信息
        dialogFormRef.value?.clearValidate();
        // 表单项合法的情况下发送ajax post请求。
        const sendData = {
            orderId: appointDialog.dataForm.orderId,
            appointmentDate: appointDialog.dataForm.appointmentDate,
            patientName: appointDialog.dataForm.patientName,
            idCardNo: appointDialog.dataForm.idCardNo,
            phone: appointDialog.dataForm.phone,
            address: appointDialog.dataForm.address,
            company: appointDialog.dataForm.company,
        };
        const result = await request.post('/front/appointment/appoint', sendData);
        if (result) {
            if (result == '预约成功') {
                ElMessage({
                    type: 'success',
                    message: '预约成功',
                    duration: 1200,
                });
                // 预约成功了，隐藏弹窗，并且重新加载分页数据。
                appointDialog.visible = false;
                loadPageData();
            } else {
                // 预约失败
                ElMessage({
                    type: 'error',
                    message: result,
                    duration: 1200,
                });
            }
        }
    }
</script>
<style lang="less" scoped>
    @import url(order_list.less);
</style>
