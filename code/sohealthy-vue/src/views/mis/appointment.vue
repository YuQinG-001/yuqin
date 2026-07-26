<template>
    <div v-if="proxy!.isAuth(['ROOT', 'APPOINTMENT:SELECT'])">
        <el-form :inline="true" :model="dataForm" :rules="dataRule" ref="form">
            <el-form-item>
                <el-date-picker
                    v-model="dataForm.appointmentDate"
                    type="appointmentDate"
                    placeholder="选择日期"
                    :editable="false"
                    format="YYYY-MM-DD"
                    value-format="YYYY-MM-DD"
                    clearable
                />
            </el-form-item>
            <el-form-item prop="patientName">
                <el-input
                    v-model="dataForm.patientName"
                    placeholder="姓名"
                    maxlength="10"
                    class="input"
                    clearable
                />
            </el-form-item>
            <el-form-item prop="phone">
                <el-input
                    v-model="dataForm.phone"
                    placeholder="电话号码"
                    maxlength="11"
                    class="input"
                    clearable
                />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="searchHandle()">查询</el-button>
                <el-button
                    type="danger"
                    :disabled="!proxy!.isAuth(['ROOT', 'APPOINTMENT:DELETE'])"
                    @click="deleteHandle()"
                >
                    批量删除
                </el-button>
            </el-form-item>
            <el-form-item class="mold">
                <el-radio-group v-model="dataForm.statusLabel" @change="searchHandle()">
                    <el-radio-button label="全部"></el-radio-button>
                    <el-radio-button label="未签到"></el-radio-button>
                    <el-radio-button label="已签到"></el-radio-button>
                    <el-radio-button label="已结束"></el-radio-button>
                    <el-radio-button label="已关闭"></el-radio-button>
                </el-radio-group>
            </el-form-item>
        </el-form>
        <el-table
            :data="data.dataList"
            :header-cell-style="{ background: '#f5f7fa' }"
            border
            v-loading="data.loading"
            @selection-change="selectionChangeHandle"
        >
            <el-table-column
                type="selection"
                :selectable="selectable"
                header-align="center"
                align="center"
                width="50"
                fixed
            />
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
                prop="patientName"
                header-align="center"
                align="center"
                label="姓名"
                width="200"
                fixed
            />
            <el-table-column
                prop="gender"
                header-align="center"
                align="center"
                label="性别"
                width="100"
            />
            <el-table-column
                prop="age"
                header-align="center"
                align="center"
                label="年龄"
                width="100"
            />
            <el-table-column
                prop="phone"
                header-align="center"
                align="center"
                label="联系电话"
                width="150"
            />
            <el-table-column
                prop="idCardNo"
                header-align="center"
                align="center"
                label="身份证号"
                width="190"
            />
            <el-table-column
                prop="company"
                header-align="center"
                align="center"
                label="公司名称"
                width="200"
            />
            <el-table-column
                prop="goodsTitle"
                header-align="center"
                align="center"
                label="体检套餐"
                min-width="200"
            />
            <el-table-column
                prop="status"
                header-align="center"
                align="center"
                label="状态"
                width="120"
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
                        :disabled="
                            !proxy!.isAuth(['ROOT', 'APPOINTMENT:DELETE']) ||
                            scope.row.status != '未签到'
                        "
                        @click="deleteHandle(scope.row.id)"
                    >
                        删除
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
</template>

<script lang="ts" setup>
    import { reactive, getCurrentInstance } from 'vue';
    import { dayjs, ElMessage, ElMessageBox } from 'element-plus';
    import isBetween from 'dayjs/plugin/isBetween';
    import request from '../../utils/request';
    dayjs.extend(isBetween);

    const { proxy } = getCurrentInstance()!;
    const dataForm = reactive<DataForm>({
        patientName: null,
        phone: null,
        appointmentDate: null,
        statusLabel: '全部',
        status: null,
    });

    const dataRule = reactive({
        patientName: [{ pattern: '^[\u4e00-\u9fa5]{1,10}$', message: '姓名格式错误' }],
        phone: [{ pattern: '^1[1-9]\\d{9}$', message: '电话号码格式错误' }],
    });

    const data = reactive<Data>({
        dataList: [],
        pageIndex: 1,
        pageSize: 10,
        totalCount: 0,
        loading: false,
        selections: [],
    });
    // 定义类型
    interface DataForm {
        status: number | null;
        statusLabel: string;
        patientName: string | null;
        phone: null | string;
        appointmentDate: null | string;
    }
    interface Data {
        loading: boolean;
        dataList: AppointmentItem[];
        totalCount: number;
        pageIndex: number;
        pageSize: number;
        selections: any;
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
        data.loading = true;

        dataForm.status = STATUS_MAP[dataForm.statusLabel as keyof typeof STATUS_MAP] ?? null;

        let json = {
            patientName: dataForm.patientName,
            phone: dataForm.phone,
            appointmentDate: dataForm.appointmentDate,
            status: dataForm.status,
            pageNum: data.pageIndex,
            pageSize: data.pageSize,
        };

        let pageResult = await request.post<PageResult<AppointmentItem>>(
            '/mis/appointment/pageQuery',
            json,
        );
        let records = pageResult.records;
        records = records.map((item) => ({
            ...item,
            status: STATUS_LABEL_MAP[Number(item.status)] || item.status,
        }));
        data.dataList = records;
        data.totalCount = pageResult.total;
        data.loading = false;
    }
    function sizeChangeHandle(val: number) {
        data.pageSize = val;
        data.pageIndex = 1;
        loadPageData();
    }

    function currentChangeHandle(val: number) {
        data.pageIndex = val;
        loadPageData();
    }

    function selectable(row: any, _index: number) {
        if (row.status == '未签到') {
            return true;
        }
        return false;
    }
    async function deleteHandle(id: number) {
        let ids =
            id != null && id != undefined ? [id] : data.selections.map((selection) => selection.id);
        if (ids == null || ids.length <= 0) {
            return ElMessage.error('请选择您的数据');
        }

        // 提醒用户是否确定删除
        ElMessageBox.confirm('您确定要删除预约记录吗？', '提示信息', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
        }).then(async () => {
            // 准备数据
            let json = {
                ids: ids,
            };
            // 发送请求删除数据
            const rows = await request.post('/mis/appointment/deleteByIds', json);
            if (rows > 0) {
                // 删除成功
                ElMessage({
                    message: `删除了[${rows}]条记录`,
                    type: 'success',
                    duration: 1200,
                });
                // 重新加载分页数据
                loadPageData();
            }
        });
    }
    function selectionChangeHandle(row: any) {
        data.selections = row;
    }
    loadPageData();
</script>

<style lang="less" scoped>
    @import url('appointment.less');
</style>
