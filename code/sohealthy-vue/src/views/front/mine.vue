<template>
    <div class="summary-container">
        <el-card class="box-card" shadow="never">
            <div class="info">
                <div class="left">
                    <el-avatar :size="45" shape="square" :src="data.photoUrl">
                        <el-icon size="25">
                            <UserFilled />
                        </el-icon>
                    </el-avatar>
                </div>
                <div class="right">
                    <div class="base">
                        <span>姓名：{{ data.customerName }}</span>
                        <span>性别：{{ data.gender }}</span>
                        <span>电话号码：{{ data.phone }}</span>
                        <div class="operate" @click="updateHandle">
                            <el-icon :size="18">
                                <Edit />
                            </el-icon>
                            <div>修改资料</div>
                        </div>
                    </div>
                    <p>注册时间：{{ data.registerTime }}</p>
                </div>
            </div>
            <el-divider />
            <el-row :gutter="16">
                <el-col :span="6">
                    <div class="statistic-card">
                        <el-statistic :value="data.amount" suffix="元">
                            <template #title>
                                <div class="title">累计消费金额</div>
                            </template>
                        </el-statistic>
                    </div>
                </el-col>
                <el-col :span="5">
                    <div class="statistic-card">
                        <el-statistic :value="data.count" suffix="笔">
                            <template #title>
                                <div class="title">有效订单数量</div>
                            </template>
                        </el-statistic>
                    </div>
                </el-col>
                <el-col :span="5">
                    <div class="statistic-card">
                        <el-statistic :value="data.number" suffix="个">
                            <template #title>
                                <div class="title">体检套餐数量</div>
                            </template>
                        </el-statistic>
                    </div>
                </el-col>
            </el-row>
        </el-card>
    </div>
    <el-dialog
        title="修改资料"
        :close-on-click-modal="false"
        v-model="dialog.visible"
        width="420px"
    >
        <el-form
            :model="dialog.dataForm"
            ref="dialogForm"
            :rules="dialog.dataRule"
            label-width="60px"
        >
            <el-form-item label="姓名" prop="customerName">
                <el-input
                    v-model="dialog.dataForm.customerName"
                    placeholder="输入姓名"
                    maxlength="10"
                    size="medium"
                    class="input"
                    clearable
                />
            </el-form-item>
            <el-form-item label="性别" prop="gender">
                <el-select
                    v-model="dialog.dataForm.gender"
                    placeholder="选择性别"
                    size="medium"
                    clearable="clearable"
                >
                    <el-option label="男" value="男" />
                    <el-option label="女" value="女" />
                </el-select>
            </el-form-item>
            <el-form-item label="电话" prop="phone">
                <el-input
                    v-model="dialog.dataForm.phone"
                    placeholder="输入电话"
                    maxlength="11"
                    size="medium"
                    class="input"
                    clearable
                />
            </el-form-item>
            <el-form-item label="头像" prop="coverImage">
                <el-upload
                    class="image-uploader"
                    :action="dialog.upload.action"
                    :headers="dialog.upload.headers"
                    :data="dialog.upload.data"
                    :show-file-list="false"
                    accept=".jpg,.jpeg"
                    :on-success="imageUploadSuccess"
                    :before-upload="imageBeforeUpload"
                    :on-error="imageUploadError"
                >
                    <!-- 如果已上传图片，显示预览图 -->
                    <img v-if="dialog.imageUrl" :src="dialog.imageUrl" class="image" />
                    <!-- 未上传图片时显示上传图标 -->
                    <el-icon v-else class="image-uploader-icon"><Plus /></el-icon>
                </el-upload>
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button size="medium" @click="dialog.visible = false"> 取消 </el-button>
                <el-button type="primary" size="medium" @click="dataFormSubmit"> 确定 </el-button>
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
    import router from '../../router/index';
    import request from '../../utils/request';
    import { ElMessage, type FormRules, type UploadRawFile } from 'element-plus';
    const { proxy } = getCurrentInstance()!;
    const dialogFormRef = useTemplateRef('dialogForm');
    const data = reactive({
        customerName: '',
        gender: '',
        phone: '',
        photoUrl: '',
        registerTime: '',
        count: 0,
        number: 0,
        amount: 0,
    });
    const dialog = reactive({
        // 控制可见性
        visible: false,
        // 表单数据
        dataForm: {
            customerName: '',
            gender: '',
            phone: '',
            coverImage: null as string | null,
        },
        imageUrl: '',
        upload: {
            action: '/meinian-api/front/customer/uploadImage',
            headers: {
                satoken: localStorage.getItem('token'),
            },
            data: {
                id: null,
            },
        },
        // 校验规则
        dataRule: {
            customerName: [{ pattern: '^[\u4e00-\u9fa5]{2,10}$', message: '姓名格式错误' }],
            phone: [
                { required: true, message: '电话不能为空' },
                { pattern: '^1[1-9]\\d{9}$', message: '电话格式错误' },
            ],
            coverImage: [
                {
                    validator: (_rule: FormRules, value: String, callback: any) => {
                        // 如果是编辑模式且有封面图片，跳过验证
                        if (dialog.imageUrl) {
                            callback();
                            return;
                        }
                        // 否则检查是否上传
                        if (!value) {
                            callback(new Error('请上传封面图片'));
                        } else {
                            callback();
                        }
                    },
                    trigger: 'change',
                },
            ],
        },
    });

    //文件上传前的回调函数
    const imageBeforeUpload = (file: UploadRawFile) => {
        if (file.size / 1024 / 1024 > 2) {
            ElMessage.error('上传文件过大，应小于等于2MB');
            return false;
        }
        return true;
    };
    interface UploadApiResponse {
        result?: string; // 上传后的文件路径
        code: number;
        msg?: string;
    }
    //文件上传成功的回调函数
    async function imageUploadSuccess(response: UploadApiResponse) {
        if (response.code === 200) {
            ElMessage.success('上传成功！');
            dialog.dataForm.coverImage = response.result || null;
            dialog.imageUrl = `${proxy?.$minioUrl}${response.result}`;
        }
        dialogFormRef.value?.clearValidate(['coverImage']);
    }

    function imageUploadError(e: Error) {
        ElMessage.error('图片上传失败');
        console.error(e);
    }
    async function updateHandle() {
        dialog.visible = true;
        await nextTick();
        dialogFormRef.value?.clearValidate();
        dialog.dataForm.customerName = data.customerName == '未填写' ? '' : data.customerName;
        dialog.dataForm.gender = data.gender == '未填写' ? '' : data.gender;
        dialog.dataForm.phone = data.phone;
        dialog.imageUrl = data.photoUrl;
    }

    async function dataFormSubmit() {
        try {
            const ok = await dialogFormRef.value?.validate();
            if (!ok) {
                return ElMessage.error('表单校验失败');
            }
            const sendData = {
                customerName: dialog.dataForm.customerName,
                gender: dialog.dataForm.gender,
                phone: dialog.dataForm.phone,
                photoUrl: dialog.dataForm.coverImage,
            };

            await request.post('/front/customer/modify', sendData);
            await loadSummary();
        } finally {
            dialog.visible = false;
        }
    }
    async function loadSummary() {
        const result = await request.get('/front/customer/getSummary');
        const {
            registerTime,
            customerName,
            gender,
            phone,
            photoUrl,
            totalAmount,
            totalCount,
            totalQuantity,
        } = result;
        data.customerName = customerName ? customerName : '未填写';
        data.gender = gender ? gender : '未知';
        data.phone = phone ? phone : '未填写';
        data.photoUrl = `${proxy?.$minioUrl}${photoUrl}`;
        data.registerTime = registerTime;
        data.count = totalCount;
        data.number = totalQuantity;
        data.amount = totalAmount;
    }
    onMounted(() => {
        loadSummary();
    });
</script>
<style lang="less" scoped>
    @import url(mine.less);
</style>
