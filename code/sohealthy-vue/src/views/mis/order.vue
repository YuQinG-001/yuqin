<template>
    <div v-if="proxy!.isAuth(['ROOT', 'ORDER:SELECT'])">
        <el-form :inline="true" :model="dataForm" :rules="dataRule" ref="form">
            <el-form-item prop="packageCode">
                <el-input
                    v-model="dataForm.packageCode"
                    placeholder="套餐编号"
                    maxlength="20"
                    class="input"
                    clearable="clearable"
                />
            </el-form-item>
            <el-form-item prop="keyword">
                <el-input
                    v-model="dataForm.keyword"
                    placeholder="套餐名称"
                    class="keyword"
                    maxlength="50"
                    clearable="clearable"
                />
            </el-form-item>
            <el-form-item prop="phone">
                <el-input
                    v-model="dataForm.phone"
                    placeholder="电话号码"
                    maxlength="11"
                    class="input"
                    clearable="clearable"
                />
            </el-form-item>
            <el-form-item class="range">
                <el-date-picker
                    v-model="dataForm.dateRange"
                    type="daterange"
                    range-separator="~"
                    start-placeholder="起始日期"
                    end-placeholder="结束日期"
                    format="YYYY-MM-DD"
                    value-format="YYYY-MM-DD"
                />
            </el-form-item>
            <el-form-item>
                <el-select
                    v-model="dataForm.orderStatus"
                    class="input"
                    placeholder="订单状态"
                    :clearable="true"
                >
                    <el-option label="未付款" value="1" />
                    <el-option label="已关闭" value="2" />
                    <el-option label="已付款" value="3" />
                    <el-option label="已退款" value="4" />
                    <el-option label="已预约" value="5" />
                    <el-option label="已结束" value="6" />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="searchHandle()">查询</el-button>
            </el-form-item>
            <el-form-item>
                <el-button
                    type="primary"
                    v-if="proxy!.isAuth(['ROOT', 'ORDER:UPDATE'])"
                    :icon="Refresh"
                    @click="checkPaymentResultHandle()"
                    >同步付款结果</el-button
                >
            </el-form-item>
        </el-form>
    </div>
    <el-table
        :data="data.dataList"
        :header-cell-style="{ background: '#f5f7fa' }"
        border
        v-loading="data.loading"
        @selection-change="selectionChangeHandle"
        @expand-change="expand"
        :row-key="data.getRowKeys"
        :expand-row-keys="data.expands"
    >
        <el-table-column type="expand">
            <template #default="scope">
                <div class="content-container">
                    <div class="left-panel">
                        <el-card class="box-card" shadow="never">
                            <div class="info">
                                <div class="left">
                                    <el-avatar :size="57" shape="square" :src="scope.row.photoUrl">
                                        <el-icon size="35"><UserFilled /></el-icon>
                                    </el-avatar>
                                </div>
                                <div class="right">
                                    <h4 class="customer-name">{{ scope.row.customerName }}</h4>
                                    <p class="customer-desc">
                                        <el-icon class="icon"><User /></el-icon>
                                        <span class="value">{{ scope.row.gender }}</span>
                                        <el-icon class="icon"><Phone /></el-icon>
                                        <span class="value">{{ scope.row.phone }}</span>
                                        <el-icon class="icon"><Calendar /></el-icon>
                                        <span class="value">{{ scope.row.registerTime }}</span>
                                    </p>
                                </div>
                            </div>
                            <el-divider />
                            <el-row :gutter="16">
                                <el-col :span="6">
                                    <div class="statistic-card">
                                        <el-statistic
                                            :value="scope.row.quantity - scope.row.num"
                                            suffix="个"
                                        >
                                            <template #title>
                                                <div class="title">可预约体检</div>
                                            </template>
                                        </el-statistic>
                                    </div>
                                </el-col>
                                <el-col :span="6">
                                    <div class="statistic-card">
                                        <el-statistic :value="scope.row.num" suffix="个">
                                            <template #title>
                                                <div class="title">已预约体检</div>
                                            </template>
                                        </el-statistic>
                                    </div>
                                </el-col>
                                <el-col :span="6">
                                    <div class="statistic-card">
                                        <el-statistic :value="scope.row.quantity" suffix="个">
                                            <template #title>
                                                <div class="title">总计数量</div>
                                            </template>
                                        </el-statistic>
                                    </div>
                                </el-col>
                            </el-row>
                        </el-card>
                        <el-descriptions :column="1" class="order-code" border>
                            <el-descriptions-item label="订单编号：" label-align="center">
                                {{ scope.row.outTradeNo }}
                            </el-descriptions-item>
                            <el-descriptions-item label="付款编号：" label-align="center">
                                {{
                                    scope.row.transactionId == null ? '无' : scope.row.transactionId
                                }}
                            </el-descriptions-item>
                            <el-descriptions-item label="退款编号：" label-align="center">
                                {{ scope.row.outRefundNo == null ? '无' : scope.row.outRefundNo }}
                            </el-descriptions-item>
                        </el-descriptions>
                    </div>
                    <div class="right-panel">
                        <el-table
                            :data="data.appointment"
                            :header-cell-style="{ background: '#f5f7fa' }"
                            height="350"
                            border
                        >
                            <el-table-column
                                label="序号"
                                type="index"
                                label-align="center"
                                align="center"
                                min-width="90"
                            >
                                <template #default="scope">
                                    <span>{{ scope.$index + 1 }}</span>
                                </template>
                            </el-table-column>
                            <el-table-column
                                prop="patientName"
                                label="体检人"
                                label-align="center"
                                align="center"
                                min-width="180"
                            />
                            <el-table-column
                                prop="gender"
                                label="性别"
                                label-align="center"
                                align="center"
                                min-width="120"
                            />
                            <el-table-column
                                prop="age"
                                label="年龄"
                                label-align="center"
                                align="center"
                                min-width="120"
                            />
                            <el-table-column
                                prop="phone"
                                label="电话"
                                label-align="center"
                                align="center"
                                min-width="180"
                            />
                            <el-table-column
                                prop="appointmentDate"
                                label="体检日"
                                label-align="center"
                                align="center"
                                min-width="180"
                            />
                            <el-table-column
                                prop="status"
                                label="状态"
                                label-align="center"
                                align="center"
                                min-width="130"
                            />
                        </el-table>
                    </div>
                </div>
            </template>
        </el-table-column>
        <el-table-column
            type="selection"
            header-align="center"
            align="center"
            width="50"
            :selectable="selectable"
        />
        <el-table-column type="index" header-align="center" align="center" width="100" label="序号">
            <template #default="scope">
                <span>{{ (data.pageIndex - 1) * data.pageSize + scope.$index + 1 }}</span>
            </template>
        </el-table-column>
        <el-table-column
            prop="goodsTitle"
            header-align="left"
            align="left"
            min-width="220"
            label="套餐名称"
        />
        <el-table-column header-align="center" align="center" min-width="80" label="价格">
            <template #default="scope">
                <span>￥{{ scope.row.goodsPrice }}</span>
            </template>
        </el-table-column>
        <el-table-column
            prop="quantity"
            header-align="center"
            align="center"
            min-width="100"
            label="数量"
        />
        <el-table-column header-align="center" align="center" min-width="100" label="总计">
            <template #default="scope">
                <span>￥{{ scope.row.totalAmount }}</span>
            </template>
        </el-table-column>
        <el-table-column
            prop="orderStatus"
            header-align="center"
            align="center"
            min-width="100"
            label="状态"
        />
        <el-table-column
            prop="createTime"
            header-align="center"
            align="center"
            min-width="100"
            label="下单时间"
        />
        <el-table-column
            prop="refundTime"
            header-align="center"
            align="center"
            min-width="100"
            label="退款时间"
        />
        <el-table-column header-align="center" align="center" width="200" label="操作">
            <template #default="scope">
                <el-button
                    type="text"
                    v-if="proxy!.isAuth(['ROOT', 'ORDER:SELECT'])"
                    @click="viewHandle(scope.row.snapshotId)"
                >
                    预览
                </el-button>
                <el-button
                    type="text"
                    v-if="proxy!.isAuth(['ROOT', 'ORDER:DELETE'])"
                    :disabled="scope.row.orderStatus != '已关闭'"
                    @click="deleteHandle(scope.row.orderId)"
                >
                    删除
                </el-button>
                <el-button
                    type="text"
                    v-if="proxy!.isAuth(['ROOT', 'ORDER:UPDATE'])"
                    :disabled="scope.row.orderStatus != '已付款'"
                    @click="updateHandle(scope.row.outTradeNo)"
                >
                    线下退款
                </el-button>
            </template>
        </el-table-column>
    </el-table>
    <el-pagination
        @size-change="sizeChangeHandle"
        @current-change="currentChangeHandle"
        :current-page="data.pageIndex"
        :page-sizes="[10, 20, 50]"
        :page-size="data.pageSize"
        :total="data.totalCount"
        layout="total, sizes, prev, pager, next, jumper"
    >
    </el-pagination>
</template>

<script lang="ts" setup>
    import { reactive, onMounted, useTemplateRef, getCurrentInstance } from 'vue';
    import router from '../../router/index';
    import { Refresh } from '@element-plus/icons-vue';
    import request from '../../utils/request';
    import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus';
    const { proxy } = getCurrentInstance()!;
    // ==================== 类型定义 ====================

    // 订单数据类型
    interface OrderItem {
        orderId: string;
        orderStatus: string;
        packageCode?: string;
        phone?: string;
        keyword?: string;
        [key: string]: any; // 其他可能的字段
    }

    // 预约记录类型
    interface AppointmentItem {
        status: string;
        [key: string]: any;
    }

    // 查询表单数据类型
    interface SearchFormData {
        packageCode: string | null;
        keyword: string | null;
        phone: string | null;
        dateRange: [string, string] | [];
        orderStatus: number | null;
    }

    // 分页响应数据类型
    interface PageResult<T> {
        records: T[];
        total: number;
        [key: string]: any;
    }

    // 订单状态枚举映射
    const orderStatusMap: Record<number, string> = {
        1: '未付款',
        2: '已关闭',
        3: '已付款',
        4: '已退款',
        5: '已预约',
        6: '已完成',
    };

    // 预约状态枚举映射
    const appointmentStatusMap: Record<number, string> = {
        1: '未签到',
        2: '已签到',
        3: '已完成',
        4: '已取消',
    };

    // ==================== 响应式数据 ====================

    // 表单数据
    const dataForm = reactive<SearchFormData>({
        packageCode: null,
        keyword: null,
        phone: null,
        dateRange: [],
        orderStatus: null,
    });

    // 表单验证规则
    const dataRule = reactive<FormRules>({
        packageCode: [
            { min: 6, message: '编号不能少于6个字符' },
            { pattern: /^[a-zA-Z0-9]{6,20}$/, message: '编号格式错误' },
        ],
        keyword: [{ pattern: /^[a-zA-Z0-9\u4e00-\u9fa5]{1,50}$/, message: '名称格式错误' }],
        phone: [{ pattern: /^1[1-9]\d{9}$/, message: '电话号码格式错误' }],
    });

    // 页面数据
    const data = reactive({
        dataList: [] as OrderItem[],
        pageIndex: 1,
        pageSize: 10,
        totalCount: 0,
        loading: false,
        selections: [] as OrderItem[],
        expands: [] as string[],
        getRowKeys(row: OrderItem): string {
            return row.orderId;
        },
        appointment: [] as AppointmentItem[],
    });

    // ==================== 表单引用 ====================
    const formRef = useTemplateRef<FormInstance>('form');

    // ==================== API 方法 ====================

    /**
     * 加载分页数据
     */
    async function loadPageData(): Promise<void> {
        try {
            data.loading = true;

            const [startDate, endDate] = dataForm.dateRange;
            const sendData = {
                phone: dataForm.phone,
                packageCode: dataForm.packageCode,
                keyword: dataForm.keyword,
                orderStatus: dataForm.orderStatus,
                startDate: dataForm.dateRange.length === 2 ? startDate : null,
                endDate: dataForm.dateRange.length === 2 ? endDate : null,
                pageNum: data.pageIndex,
                pageSize: data.pageSize,
            };

            const pageResult = await request.post<PageResult<OrderItem>>(
                '/mis/order/page',
                sendData,
            );
            // 映射订单状态为中文
            data.dataList = pageResult.records.map((item) => ({
                ...item,
                orderStatus: orderStatusMap[Number(item.orderStatus)] || item.orderStatus,
            }));

            data.totalCount = pageResult.total;
        } finally {
            data.loading = false;
        }
    }
    function viewHandle(id: string) {
        const routeData = router.resolve({
            name: 'FrontGoodsSnapshot',
            params: {
                id,
                mode: 'mis',
            },
        });
        window.open(routeData.href, '_blank');
    }
    function selectable(row: OrderItem) {
        if (row.orderStatus === '已关闭' || row.orderStatus === '未付款') {
            return true;
        }
        return false;
    }
    function selectionChangeHandle(rows: OrderItem[]) {
        data.selections = rows;
    }

    async function checkPaymentResultHandle() {
        if (data.selections == null || data.selections.length === 0) {
            return ElMessage.warning('请选择要同步的数据');
        }
        await ElMessageBox.confirm('您确定要同步付款结果吗', '提示', {
            cancelButtonText: '取消',
            confirmButtonText: '确定',
        });

        const outTradeNos = data.selections.map((item) => {
            return item.outTradeNo;
        });

        const result = await request.post('/mis/order/syncPaymentResult', { outTradeNos });
        if (result === 0) {
            return ElMessage.error('同步失败');
        }
        ElMessage.success('成功更新 ' + result + ' 条数据');
        loadPageData();
    }
    async function deleteHandle(orderId: number) {
        await ElMessageBox.confirm('您确定要删除吗？', '提示', {
            cancelButtonText: '取消',
            confirmButtonText: '确定',
        });
        await request.delete(`mis/order/delete?orderId=${orderId}`);
        loadPageData();
    }

    /**
     * 搜索处理
     */
    async function searchHandle(): Promise<void> {
        const isValid = await formRef.value?.validate();
        if (!isValid) {
            ElMessage.warning('输入错误');
            return;
        }
        formRef.value?.clearValidate();
        data.pageIndex = 1;
        await loadPageData();
    }

    /**
     * 页码改变处理
     */
    function currentChangeHandle(pageNum: number): void {
        data.pageIndex = pageNum;
        loadPageData();
    }

    /**
     * 每页条数改变处理
     */
    function sizeChangeHandle(pageSize: number): void {
        data.pageSize = pageSize;
        data.pageIndex = 1;
        loadPageData();
    }

    /**
     * 展开/收起行详情
     */
    async function expand(row: OrderItem): Promise<void> {
        // 切换展开状态
        const isExpanded = data.expands.includes(row.orderId);
        data.expands = isExpanded ? [] : [row.orderId];
        //收起动作
        if (isExpanded) {
            data.appointment = [];
            return;
        }
        //展开动作，发送axois
        try {
            const result = await request.get<AppointmentItem[]>('/mis/appointment/findByOrderId', {
                params: { orderId: row.orderId },
            });
            // 映射预约状态为中文
            data.appointment = result.map((item) => ({
                ...item,
                status: appointmentStatusMap[Number(item.status)] || item.status,
            }));
        } catch (error) {
            console.error('获取预约记录失败:', error);
            ElMessage.error('获取预约记录失败');
            data.appointment = [];
        }
    }
    async function updateHandle(outTradeNo: string) {
        await ElMessageBox.confirm('您确定要线下退款吗？', '提示', {
            cancelButtonText: '取消',
            confirmButtonText: '确定',
        });
        await request.put('/mis/order/offlineRefund', { outTradeNo });
        loadPageData();
    }
    // ==================== 生命周期 ====================
    onMounted(() => {
        loadPageData();
    });
</script>

<style lang="less" scoped>
    @import url('order.less');
</style>
