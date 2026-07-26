<template>
    <div class="person-container" v-if="proxy.isAuth(['ROOT', 'CHECKUP:INSERT', 'CHECKUP:UPDATE'])">
        <div class="left">
            <el-descriptions title="【 体检人信息 】" direction="vertical" :column="5" border>
                <template #extra>
                    <div class="operate">
                        <el-input
                            placeholder="输入 / 扫码体检编号"
                            v-model="data.dataForm.uuid"
                            ref="uuid"
                            class="uuid"
                            maxlength="32"
                            @keyup.enter.native="searchHandle"
                            clearable
                        />
                        <el-button type="primary" @click="searchHandle">执行查询</el-button>
                    </div>
                </template>
                <el-descriptions-item align="center">
                    <template #label>
                        <div class="cell-item">
                            <el-icon class="icon"><User /></el-icon>
                            姓名
                        </div>
                    </template>
                    {{ data.customer.name }}
                </el-descriptions-item>
                <el-descriptions-item align="center">
                    <template #label>
                        <div class="cell-item">
                            <el-icon class="icon"><Male /></el-icon>
                            性别
                        </div>
                    </template>
                    {{ data.customer.sex }}
                </el-descriptions-item>
                <el-descriptions-item align="center">
                    <template #label>
                        <div class="cell-item">
                            <el-icon class="icon"><Calendar /></el-icon>
                            年龄
                        </div>
                    </template>
                    {{ data.customer.age }}
                </el-descriptions-item>
                <el-descriptions-item align="center">
                    <template #label>
                        <div class="cell-item">
                            <el-icon class="icon"><Phone /></el-icon>
                            电话号码
                        </div>
                    </template>
                    {{ data.customer.tel }}
                </el-descriptions-item>
                <el-descriptions-item align="center">
                    <template #label>
                        <div class="cell-item">
                            <el-icon class="icon"><Postcard /></el-icon>
                            体检编号
                        </div>
                    </template>
                    {{ data.customer.uuid }}
                </el-descriptions-item>
            </el-descriptions>
        </div>
        <div class="right">
            <el-descriptions title="【 医生信息 】" direction="vertical" :column="5" border>
                <template #extra>
                    <el-button type="primary" @click="selectDeptHandle">选择科室</el-button>
                </template>
                <el-descriptions-item align="center">
                    <template #label>
                        <div class="cell-item">
                            <el-icon class="icon"><User /></el-icon>
                            姓名
                        </div>
                    </template>
                    {{ data.doctor.name }}
                </el-descriptions-item>
                <el-descriptions-item align="center">
                    <template #label>
                        <div class="cell-item">
                            <el-icon class="icon"><Male /></el-icon>
                            性别
                        </div>
                    </template>
                    {{ data.doctor.sex }}
                </el-descriptions-item>
                <el-descriptions-item align="center">
                    <template #label>
                        <div class="cell-item">
                            <el-icon class="icon"><Phone /></el-icon>
                            电话号码
                        </div>
                    </template>
                    {{ data.doctor.tel }}
                </el-descriptions-item>
                <el-descriptions-item align="center">
                    <template #label>
                        <div class="cell-item">
                            <el-icon class="icon"><OfficeBuilding /></el-icon>
                            体检科室
                        </div>
                    </template>
                    {{ data.dataForm.place }}
                </el-descriptions-item>
                <el-descriptions-item align="center">
                    <template #label>
                        <div class="cell-item">
                            <el-icon class="icon"><Calendar /></el-icon>
                            当前日期
                        </div>
                    </template>
                    {{ data.doctor.date }}
                </el-descriptions-item>
            </el-descriptions>
        </div>
    </div>
    <div class="checkup-container">
        <table>
            <thead>
                <tr>
                    <th width="50">序号</th>
                    <th width="100" align="left">体检项</th>
                    <th width="300" align="left">检查结果</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(one, index) in data.dataForm.checkup">
                    <td align="center">{{ index + 1 }}</td>
                    <td>{{ one.item }}</td>
                    <td>
                        <div class="value-container">
                            <el-select
                                v-model="one.input.select"
                                class="m-2"
                                placeholder="选择模板值"
                            >
                                <el-option v-for="item in one.value" :label="item" :value="item" />
                            </el-select>
                            <el-input
                                v-model="one.input.value"
                                :disabled="one.input.select != '其他'"
                                placeholder="输入体检值"
                                class="input-value"
                                clearable
                            />
                        </div>
                    </td>
                </tr>
            </tbody>
        </table>
        <div class="operate" v-if="data.dataForm.checkup.length > 0">
            <el-button type="danger" size="large" @click="clearCheckupHandle"> 重置数据 </el-button>
            <el-button type="primary" size="large" @click="dataFormSubmit">
                提交体检结果
            </el-button>
        </div>
    </div>
</template>
<script lang="ts" setup>
    import { reactive, getCurrentInstance, ref } from 'vue';
    import { ElMessageBox } from 'element-plus';
    import { dayjs } from 'element-plus';
    import { stringIsEmpty } from '../../utils/validate';

    const { proxy } = getCurrentInstance();

    const data = reactive({
        dataForm: {
            place: null,
            uuid: null,
            checkup: [],
            template: null,
        },
        customer: {
            name: '<无>',
            sex: '<无>',
            age: '<无>',
            tel: '<无>',
            uuid: '<无>',
            date: null,
        },
        doctor: {
            name: null,
            sex: null,
            tel: null,
            date: new Date().toLocaleDateString(),
        },
    });

    function searchHandle() {}
</script>
<style lang="less" scoped>
    @import url('doctor_checkup.less');
</style>
