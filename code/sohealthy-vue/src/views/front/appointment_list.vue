<template>
    <el-form :inline="true" :model="dataForm" :rules="dataRule" ref="form">
        <el-form-item prop="keyword">
            <el-input
                v-model="dataForm.keyword"
                placeholder="套餐名称 / 姓名 / 电话"
                size="medium"
                class="keyword"
                clearable
            />
        </el-form-item>
        <el-form-item class="date">
            <el-date-picker
                v-model="dataForm.date"
                type="date"
                placeholder="选择日期"
                :editable="false"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
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
                <el-radio-button label="未签到"></el-radio-button>
                <el-radio-button label="已签到"></el-radio-button>
                <el-radio-button label="已完成"></el-radio-button>
                <el-radio-button label="已取消"></el-radio-button>
            </el-radio-group>
        </el-form-item>
    </el-form>
    <div class="table-conatainer" v-show="!empty">
        <el-table
            :data="data.dataList"
            class="appointment-table"
            :header-cell-style="{ background: '#f5f7fa' }"
            border
            v-loading="data.loading"
        >
            <el-table-column
                type="index"
                header-align="center"
                align="center"
                width="120"
                label="序号"
                fixed
            >
                <template #default="scope">
                    <span>{{ (data.pageIndex - 1) * data.pageSize + scope.$index + 1 }}</span>
                </template>
            </el-table-column>
            <el-table-column
                prop="goodsTitle"
                header-align="center"
                align="center"
                label="套餐名称"
                min-width="250"
                fixed
            />
            <el-table-column
                prop="patientName"
                header-align="center"
                align="center"
                label="体检人"
                min-width="120"
            />
            <el-table-column
                prop="appointmentDate"
                header-align="center"
                align="center"
                label="预约日期"
                min-width="120"
            />
            <el-table-column
                prop="status"
                header-align="center"
                align="center"
                label="状态"
                min-width="120"
            />
            <el-table-column
                fixed="right"
                header-align="center"
                align="center"
                width="150"
                label="操作"
            >
                <template #default="scope">
                    <el-button
                        type="text"
                        :disabled="scope.row.fileUrl == null"
                        @click="downloadHandle(scope.row.name, scope.row.fileUrl)"
                    >
                        体检报告
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
    </div>

    <div class="empty" v-show="empty">
        <el-empty :image-size="200" />
    </div>
</template>

<script lang="ts" setup>
    import { reactive, ref, Ref, getCurrentInstance } from 'vue';
    import { Search } from '@element-plus/icons-vue';
    import router from '../../router/index';
    import { dayjs } from 'element-plus';
    import isBetween from 'dayjs/plugin/isBetween';
    import axios from 'axios';
    import request from '../../utils/request';
    dayjs.extend(isBetween);
    const { proxy } = getCurrentInstance()!;

    let empty = ref(false);
    const dataForm = reactive<DataForm>({
        keyword: null,
        date: null,
        status: 0,
        statusLabel: '全部',
    });
    const dataRule = reactive({
        keyword: [
            {
                required: false,
                pattern: '^[a-zA-Z0-9\u4e00-\u9fa5]{1,30}$',
                message: '关键字内容不正确',
            },
        ],
    });
    const data = reactive<Data>({
        dataList: [] as AppointmentItem[],
        pageIndex: 1,
        pageSize: 10,
        totalCount: 0,
        loading: false,
    });

    // 定义类型
    interface DataForm {
        keyword: string | null;
        date: string | null;
        status: number | null;
        statusLabel: string;
    }

    interface Data {
        loading: boolean;
        dataList: AppointmentItem[];
        totalCount: number;
        pageIndex: number;
        pageSize: number;
    }

    interface AppointmentItem {
        status: string;
        // 其他字段...
    }

    // 状态映射常量
    const STATUS_MAP = {
        全部: null,
        未签到: 1,
        已签到: 2,
        已完成: 3,
        已取消: 4,
    } as const;

    const STATUS_LABEL_MAP: Record<number, string> = {
        1: '未签到',
        2: '已签到',
        3: '已完成',
        4: '已取消',
    };
    // PageResult 是一个泛型接口
    interface PageResult<T> {
        records: T[]; // T 类型的数组
        total: number; // 总记录数
    }

    async function loadPageData(): Promise<void> {
        try {
            // 打开加载进度条
            data.loading = true;

            // 根据 statusLabel 映射 status
            dataForm.status = STATUS_MAP[dataForm.statusLabel as keyof typeof STATUS_MAP] ?? null;

            // 构建请求参数
            const sendData = {
                keyword: dataForm.keyword,
                status: dataForm.status,
                appointmentDate: dataForm.date,
                pageNum: data.pageIndex,
                pageSize: data.pageSize,
            };

            // 发送请求
            const pageResult = await request.post<PageResult<AppointmentItem>>(
                '/front/appointment/page',
                sendData,
            );

            // 更新数据列表，映射状态显示文本
            data.dataList = pageResult.records.map((item) => ({
                ...item,
                status: STATUS_LABEL_MAP[Number(item.status)] || item.status,
            }));

            // 更新总数
            data.totalCount = pageResult.total;
        } catch (error) {
            // 错误处理
            console.error('加载数据失败:', error);
            // 可以在这里添加用户提示
        } finally {
            // 确保无论成功还是失败都关闭加载进度条
            data.loading = false;
        }
    }

    loadPageData();

    const form: Ref = ref(null);

    async function searchHandle() {
        // 表单验证
        let ok = await form.value?.validate();
        if (ok) {
            // 清除表单错误提示信息
            form.value.clearValidate();
            // 设置页码为1
            data.pageIndex = 1;
            // 分页查询
            loadPageData();
        }
    }

    function sizeChangeHandle(pageSize: number) {
        data.pageSize = pageSize;
        data.pageIndex = 1;
        loadPageData();
    }

    function currentChangeHandle(pageIndex: number) {
        data.pageIndex = pageIndex;
        loadPageData();
    }

    function downloadHandle(name, fileUrl) {
        // 直接发送一个get请求，直接从minio服务器上下载文件
        // 没有经过我们的后端程序，直接从minio服务器上下载的。
        document.location.href = `${proxy.$minioUrl}/${fileUrl}`;
    }
</script>

<style lang="less" scoped>
    @import url('appointment_list.less');
</style>
