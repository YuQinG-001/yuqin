<template>
    <div v-if="isAuth(['ROOT', 'GOODS:SELECT'])">
        <el-form :inline="true" :model="dataForm" :rules="dataRule" ref="form">
            <el-form-item prop="packageName">
                <el-input
                    v-model="dataForm.packageName"
                    placeholder="套餐名称"
                    maxlength="50"
                    class="input"
                    clearable
                />
            </el-form-item>
            <el-form-item prop="packageCode">
                <el-input
                    v-model="dataForm.packageCode"
                    placeholder="套餐编号"
                    class="input"
                    maxlength="20"
                    clearable
                />
            </el-form-item>
            <el-form-item>
                <el-select
                    v-model="dataForm.packageType"
                    class="input"
                    placeholder="类别"
                    clearable
                >
                    <el-option label="父母体检" value="父母体检" />
                    <el-option label="入职体检" value="入职体检" />
                    <el-option label="职场白领" value="职场白领" />
                    <el-option label="个人高端" value="个人高端" />
                    <el-option label="中青年体检" value="中青年体检" />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-select
                    v-model="dataForm.categoryId"
                    class="input"
                    placeholder="展示区"
                    clearable
                >
                    <el-option label="活动专区" value="1" />
                    <el-option label="热卖套餐" value="2" />
                    <el-option label="新品推荐" value="3" />
                    <el-option label="孝敬父母" value="4" />
                    <el-option label="白领精英" value="5" />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="searchHandle()">查询</el-button>
                <el-button
                    type="primary"
                    :disabled="!isAuth(['ROOT', 'GOODS:INSERT'])"
                    @click="addHandle()"
                >
                    新增
                </el-button>
                <el-button
                    type="danger"
                    :disabled="!isAuth(['ROOT', 'GOODS:DELETE'])"
                    @click="deleteHandle()"
                >
                    批量删除
                </el-button>
            </el-form-item>
            <el-form-item class="mold">
                <el-radio-group v-model="dataForm.statusLabel" @change="searchHandle()">
                    <el-radio-button label="全部"></el-radio-button>
                    <el-radio-button label="已上架"></el-radio-button>
                    <el-radio-button label="已下架"></el-radio-button>
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
                header-align="center"
                align="center"
                width="50"
                :selectable="selectable"
            />
            <el-table-column
                type="index"
                header-align="center"
                align="center"
                width="100"
                label="序号"
            >
                <template #default="scope">
                    <span>{{ (data.pageIndex - 1) * data.pageSize + scope.$index + 1 }}</span>
                </template>
            </el-table-column>
            <el-table-column
                prop="packageName"
                header-align="left"
                align="left"
                min-width="250"
                label="套餐名称"
            />
            <el-table-column
                prop="packageCode"
                header-align="left"
                align="left"
                min-width="130"
                label="套餐编号"
            />

            <el-table-column header-align="center" align="center" min-width="80" label="现价">
                <!--如果你要自定义单元格中的内容，就需要单独编写下面的 template 标签-->
                <template #default="scope">
                    <span>￥{{ scope.row.currentPrice }}</span>
                </template>
            </el-table-column>

            <el-table-column header-align="center" align="center" min-width="100" label="原价">
                <template #default="scope">
                    <span>￥{{ scope.row.originalPrice }}</span>
                </template>
            </el-table-column>
            <el-table-column
                prop="ruleName"
                header-align="center"
                align="center"
                min-width="100"
                label="促销方案"
            />
            <el-table-column
                prop="salesVolume"
                header-align="center"
                align="center"
                min-width="100"
                label="销量"
            />
            <el-table-column
                prop="packageType"
                header-align="center"
                align="center"
                min-width="100"
                label="类型"
            />
            <el-table-column header-align="center" align="center" min-width="100" label="体检内容">
                <template #default="scope">
                    <span
                        :class="scope.row.hasCheckup ? 'link-blue' : 'link-red'"
                        @click="documentHandle(scope.row.id, scope.row.hasCheckup)"
                    >
                        {{ scope.row.hasCheckup ? '有文档' : '无文档' }}
                    </span>
                </template>
            </el-table-column>
            <el-table-column
                prop="status"
                header-align="center"
                align="center"
                min-width="80"
                label="状态"
            >
                <template #default="scope">
                    <!-- v-model="scope.row.status" 为true表示上架，false表示下架 -->
                    <!-- active-text="上架" 开启时显示上架-->
                    <!-- inactive-text="下架" 关闭时显示下架-->
                    <!-- style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949;" 开启时绿色，关闭时红色-->
                    <!-- :disabled="!scope.row.hasCheckup" 只有当 hasCheckup 为 true 时开关才可用-->
                    <!-- inline-prompt 控制文本显示的位置和方式，这个配置表示文本显示在开关的内部，如果不写它，文本显示在开关的外面-->
                    <el-switch
                        v-model="scope.row.status"
                        inline-prompt
                        style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949"
                        active-text="上架"
                        inactive-text="下架"
                        :disabled="!scope.row.hasCheckup"
                        @change="changeSwitchHandle(scope.row.id, scope.row.status)"
                    />
                </template>
            </el-table-column>
            <el-table-column header-align="center" align="center" width="150" label="操作">
                <template #default="scope">
                    <el-button
                        type="text"
                        :disabled="!scope.row.status"
                        @click="viewHandle(scope.row.id)"
                    >
                        预览
                    </el-button>
                    <el-button
                        type="text"
                        v-if="isAuth(['ROOT', 'GOODS:UPDATE'])"
                        :disabled="scope.row.status"
                        @click="updateHandle(scope.row.id)"
                    >
                        修改
                    </el-button>
                    <el-button
                        type="text"
                        v-if="isAuth(['ROOT', 'GOODS:DELETE'])"
                        :disabled="scope.row.salesVolume > 0 || scope.row.status"
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
    <el-dialog
        :title="!goodsDialog.dataForm.id ? '新增' : '修改'"
        v-if="isAuth(['ROOT', 'GOODS:INSERT', 'GOODS:UPDATE'])"
        :close-on-click-modal="false"
        v-model="goodsDialog.visible"
        width="750px"
    >
        <el-form
            :model="goodsDialog.dataForm"
            ref="dialogForm"
            :rules="goodsDialog.dataRule"
            label-width="80px"
        >
            <el-form-item label="套餐名称" prop="packageName">
                <el-input v-model="goodsDialog.dataForm.packageName" maxlength="50" clearable />
            </el-form-item>
            <el-form-item label="套餐编号" prop="packageCode">
                <el-input v-model="goodsDialog.dataForm.packageCode" maxlength="20" clearable />
            </el-form-item>
            <el-form-item label="简介信息" prop="description">
                <el-input
                    type="textarea"
                    v-model="goodsDialog.dataForm.description"
                    :rows="4"
                    maxlength="200"
                    clearable
                />
            </el-form-item>
            <el-form-item label="套餐原价" prop="originalPrice">
                <el-input
                    v-model="goodsDialog.dataForm.originalPrice"
                    placeholder="输入原价"
                    class="price"
                    maxlength="20"
                    clearable
                >
                    <template #append> 元 </template>
                </el-input>
                <span class="desc">提示：价格精确到分（小数点后两位）</span>
            </el-form-item>
            <el-form-item label="套餐现价" prop="currentPrice">
                <el-input
                    v-model="goodsDialog.dataForm.currentPrice"
                    placeholder="输入现价"
                    class="price"
                    maxlength="20"
                    clearable
                >
                    <template #append> 元 </template>
                </el-input>
                <span class="desc">提示：价格精确到分（小数点后两位）</span>
            </el-form-item>
            <el-form-item label="折扣列表">
                <el-select
                    v-model="goodsDialog.dataForm.promotionId"
                    placeholder="选择折扣信息"
                    clearable
                >
                    <el-option
                        :label="one.ruleName"
                        :value="one.ruleId"
                        v-for="one in goodsDialog.ruleList"
                    />
                </el-select>
            </el-form-item>
            <el-form-item label="封面图片" prop="coverImage">
                <!--EP组件库底层自动发送请求，不需要我们发请求：用户点击 → 弹出文件选择框 → 选择文件 → 自动发送POST请求-->
                <!-- :action="goodsDialog.upload.action" 指定上传时的请求路径 -->
                <!-- :headers="goodsDialog.upload.headers" 上传请求头（通常用于身份验证，如token） -->
                <!-- :data="goodsDialog.upload.data" 上传附加数据（如业务参数） -->
                <!-- :show-file-list="false" 不显示文件列表，适用于单文件上传，如果显示的话：每个文件会显示文件名、大小、状态 -->
                <!-- accept=".jpg,.jpeg" 只接受jpg和jpeg格式的图片 -->
                <!-- :on-success="imageUploadSuccess" 上传成功回调函数 -->
                <!-- :before-upload="imageBeforeUpload" 上传前校验函数（可用于文件类型、大小校验） -->
                <!-- :on-error="imageUploadError" 上传失败回调函数 -->
                <el-upload
                    class="image-uploader"
                    :action="goodsDialog.upload.action"
                    :headers="goodsDialog.upload.headers"
                    :data="goodsDialog.upload.data"
                    :show-file-list="false"
                    accept=".jpg,.jpeg"
                    :on-success="imageUploadSuccess"
                    :before-upload="imageBeforeUpload"
                    :on-error="imageUploadError"
                >
                    <!-- 如果已上传图片，显示预览图 -->
                    <img v-if="goodsDialog.imageUrl" :src="goodsDialog.imageUrl" class="image" />
                    <!-- 未上传图片时显示上传图标 -->
                    <el-icon v-else class="image-uploader-icon"><Plus /></el-icon>
                </el-upload>
            </el-form-item>
            <el-form-item label="套餐类别" prop="packageType">
                <el-select
                    v-model="goodsDialog.dataForm.packageType"
                    placeholder="检查类别"
                    clearable
                >
                    <el-option label="父母体检" value="父母体检" />
                    <el-option label="入职体检" value="入职体检" />
                    <el-option label="职场白领" value="职场白领" />
                    <el-option label="个人高端" value="个人高端" />
                    <el-option label="中青年体检" value="中青年体检" />
                </el-select>
            </el-form-item>
            <el-form-item label="特征标签">
                <div class="tag-row">
                    <el-input
                        class="tag-input"
                        @keyup.enter="addtag"
                        v-model="goodsDialog.newTag"
                        clearable
                    />
                    <span class="desc">提示：输入标签后按回车键</span>
                </div>
                <div class="tags">
                    <el-tag
                        v-for="one in goodsDialog.dataForm.tags"
                        closable
                        :disable-transitions="false"
                        @close="closeTag(one)"
                    >
                        {{ one }}
                    </el-tag>
                </div>
            </el-form-item>
            <el-form-item label="展示区" prop="categoryId">
                <el-select
                    v-model="goodsDialog.dataForm.categoryId"
                    placeholder="选择展示区"
                    clearable
                >
                    <el-option label="活动专区" value="1" />
                    <el-option label="热卖套餐" value="2" />
                    <el-option label="新品推荐" value="3" />
                    <el-option label="孝敬父母" value="4" />
                    <el-option label="白领精英" value="5" />
                </el-select>
            </el-form-item>
            <el-form-item label="体检内容">
                <!-- 
            商品体检项目动态表单行
            功能：循环渲染体检项目配置，每行包含类别选择、项目名称、内容和删除操作
            结构说明：
            - :gutter="10" 列间距10像素
            - v-for 遍历goodsDialog.item数组，动态生成多个配置行
            - :key="$index" 使用索引作为key

            el-col 列布局(EP的栅格布局，EP规定一行共24个栅格：把一行等分成24份)：
            - 第1列(span=6，6就是表示占6份)：检查类别下拉选择，包含科室/实验室/医技/其他检查
            - 第2列(span=6)：体检项目名称输入框，最多50字符
            - 第3列(span=11)：体检内容详细说明输入框，最多500字符  
            - 第4列(span=1)：删除按钮，点击移除当前行配置
        -->
                <el-row
                    :gutter="10"
                    class="item-row"
                    v-for="(one, $index) in goodsDialog.item"
                    :key="$index"
                >
                    <el-col :span="6">
                        <el-select v-model="one.type" placeholder="检查类别" clearable>
                            <el-option
                                v-for="opt in typeOptions"
                                :key="opt.value"
                                :label="opt.label"
                                :value="opt.value"
                            />
                        </el-select>
                    </el-col>
                    <el-col :span="6">
                        <el-input
                            v-model="one.title"
                            placeholder="体检项目"
                            maxlength="50"
                            clearable
                        />
                    </el-col>
                    <el-col :span="11">
                        <el-input
                            v-model="one.content"
                            placeholder="体检内容"
                            maxlength="500"
                            clearable
                        />
                    </el-col>
                    <el-col :span="1">
                        <el-button type="primary" :icon="Delete" @click="deleteItem($index)" />
                    </el-col>
                </el-row>
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button type="danger" @click="addItem">添加项目</el-button>
                <el-button @click="goodsDialog.visible = false">取消</el-button>
                <el-button type="primary" @click="dataFormSubmit">确定</el-button>
            </span>
        </template>
    </el-dialog>
    <el-dialog
        title="提示信息"
        v-if="isAuth(['ROOT', 'GOODS:INSERT', 'GOODS:UPDATE'])"
        v-model="documentDialog.visible"
        width="350px"
    >
        <div class="message-content">
            <el-icon :size="18" class="icon">
                <WarningFilled />
            </el-icon>
            <p>
                请您选择【上传】或者【下载】体检内容文档？如果未上传体检内容文档，则体检套餐将无法上架。
            </p>
        </div>
        <!-- 
        1. 上传功能：
            - 使用el-upload组件实现Excel文件上传
            - 上传地址：documentDialog.upload.action
            - 附加数据：documentDialog.data
            - 请求头：documentDialog.upload.headers（通常包含认证信息）
            - 只接受.xlsx格式文件
            - 上传前校验：documentBeforeUpload（校验文件格式、大小等）
            - 上传成功回调：documentUploadSuccess（处理上传结果）
            - 上传失败回调：documentUploadError（错误处理）
            - 显示为带Upload图标的成功样式按钮
        
        2. 下载功能：
            - 使用el-button实现文件下载
            - 带Download图标的主样式按钮
            - 禁用条件：!documentDialog.data.hasCheckup（未通过审核时禁用下载）
            - 点击事件：documentDownloadHandle（触发下载操作）
        -->
        <template #footer>
            <span class="document-dialog-footer">
                <el-upload
                    :action="documentDialog.upload.action"
                    :data="documentDialog.data"
                    :show-file-list="false"
                    :headers="documentDialog.upload.headers"
                    accept=".xlsx"
                    :before-upload="documentBeforeUpload"
                    :on-success="documentUploadSuccess"
                    :on-error="documentUploadError"
                >
                    <el-button type="success" :icon="Upload" class="uploadBtn">上传</el-button>
                </el-upload>
                <el-button
                    type="primary"
                    :icon="Download"
                    class="downloadBtn"
                    :disabled="!documentDialog.data.hasCheckup"
                    @click="documentDownloadHandle"
                >
                    下载
                </el-button>
            </span>
        </template>
    </el-dialog>
</template>

<script lang="ts" setup>
    import { getCurrentInstance, nextTick, onMounted, reactive, useTemplateRef } from 'vue';
    //引入ElementPlus组件库中的三个图标，要给按钮设置图标
    import { Delete, Download, Plus, Upload } from '@element-plus/icons-vue';
    import axios from 'axios';
    import { ElMessage, ElMessageBox, type UploadRawFile } from 'element-plus';
    import useAuth from '../../hooks/useAuth';
    import request from '../../utils/request';
    import { stringIsEmpty } from '../../utils/validate';
    import router from '../../router';
    const { isAuth } = useAuth();
    const dialogFormRef = useTemplateRef('dialogForm');
    const { proxy } = getCurrentInstance()!;

    interface DataList {
        id: number;
        packageName: String;
        packageCode: String;
        originalPrice: number;
        currentPrice: number;
        salesVolume: number;
        packageType: String;
        ruleName: String;
        hasCheckup: Boolean;
        status: Boolean | null;
        categoryId: number;
    }
    interface Rule {
        ruleId: number | string;
        ruleName: string;
    }
    interface UploadApiResponse {
        result?: string; // 上传后的文件路径
        code: number;
        msg?: string;
    }
    interface Exam {
        type: string;
        title: string;
        content: string;
    }
    interface SendExam {
        title: string;
        content: string;
    }

    // ----------------------表单-----------------------------------
    const dataForm = reactive({
        packageName: null,
        packageCode: null,
        packageType: null,
        categoryId: null,
        statusLabel: '全部',
        status: null as null | Boolean,
    });

    const dataRule = reactive({
        packageName: [{ pattern: '^[a-zA-Z0-9\u4e00-\u9fa5]{1,50}$', message: '关键字内容不正确' }],
        packageCode: [
            { min: 6, message: '编号不能少于6个字符' },
            { pattern: '^[a-zA-Z0-9]{6,20}$', message: '编号格式错误' },
        ],
    });

    // -------------------------页面数据内容---------------------------------------
    const data = reactive({
        dataList: [] as DataList[],
        pageIndex: 1,
        pageSize: 10,
        totalCount: 0,
        loading: false,
        selections: [],
    });
    const formRef = useTemplateRef('form');

    const searchHandle = async () => {
        await formRef.value?.validate();
        data.pageIndex = 1;
        loadData();
    };
    const currentChangeHandle = (pageIndex: number) => {
        data.pageIndex = pageIndex;
        loadData();
    };
    const sizeChangeHandle = (pageSize: number) => {
        data.pageIndex = 1;
        data.pageSize = pageSize;
        loadData();
    };
    async function changeSwitchHandle(id: number, status: boolean) {
        request.put('/mis/goods/modifyStatus', { id, status });
        ElMessage.success('操作成功');
    }
    const loadData = async () => {
        try {
            data.loading = true;
            if (dataForm.statusLabel == '全部') {
                dataForm.status = null;
            } else if (dataForm.statusLabel == '已上架') {
                dataForm.status = true;
            } else {
                dataForm.status = false;
            }
            const sendData = {
                packageName: dataForm.packageName,
                packageCode: dataForm.packageCode,
                packageType: dataForm.packageType,
                categoryId: dataForm.categoryId,
                status: dataForm.status,
                pageNum: data.pageIndex,
                pageSize: data.pageSize,
            };
            const result = await request.post('/mis/goods/page', sendData);

            const rawList = result.records;
            data.dataList = rawList.map((item: any) => ({
                ...item,
                status: item.status === 1 ? true : item.status === 0 ? false : null,
            }));
            data.totalCount = result.total;
        } finally {
            data.loading = false;
        }
    };
    onMounted(() => {
        loadData();
    });

    function viewHandle(id: number) {
        const route = router.resolve({
            name: 'FrontGoods',
            params: {
                id: id,
            },
        });
        window.open(route.href, '_blank');
    }

    async function deleteHandle(id?: number) {
        try {
            let ids: number[] = [];
            if (id) {
                ids = [id];

                await ElMessageBox.confirm('确定删除这条吗？', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning',
                });
            } else {
                ids = data.selections.map((item: any) => item.id);
                if (ids.length === 0) {
                    ElMessage.warning('请选择要删除的用户');
                    return;
                }
                await ElMessageBox.confirm(`确定删除选中的数据吗？`, '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning',
                });
            }
            data.loading = true;
            await request.delete('/mis/goods/remove', { params: { ids } });
            ElMessage.success('删除成功');
            data.pageIndex = 1;
            loadData();
        } catch (error) {
            // 用户点“取消”会进入这里，不需要当作错误提示
            if (error !== 'cancel' && error !== 'close') {
                console.log('发生未知错误', error);
                ElMessage.error('删除失败');
            }
        } finally {
            data.loading = false;
        }
    }
    async function selectionChangeHandle(row: any) {
        data.selections = row;
    }
    function selectable(row: any) {
        return row.salesVolume === 0 && !row.status;
    }
    // -----------------------模态窗口--------------------------------------------------
    const goodsDialog = reactive({
        visible: false,
        newTag: '',
        item: [{}] as Exam[],
        imageUrl: null as string | null,
        ruleList: [] as Rule[],
        dataForm: {
            id: null as null | number,
            packageName: '' as string,
            packageCode: null,
            description: '' as string,
            originalPrice: null,
            currentPrice: null,
            promotionId: null,
            coverImage: null as string | null,
            packageType: null,
            tags: [] as string[],
            categoryId: null,
        },
        upload: {
            action: '/meinian-api/mis/goods/uploadImage',
            headers: {
                satoken: localStorage.getItem('token'),
            },
            data: {
                id: null,
            },
        },

        dataRule: {
            packageName: [
                { required: true, message: '名称不能为空' },
                { min: 2, message: '名称不能少于2个字符' },
                { pattern: '^[a-zA-Z0-9\u4e00-\u9fa5]{2,50}$', message: '名称格式错误' },
            ],
            packageCode: [
                { required: true, message: '编号不能为空' },
                { min: 6, message: '编号不能少于6个字符' },
                { pattern: '^[a-zA-Z0-9]{6,20}$', message: '编号格式错误' },
            ],
            description: [{ required: true, message: '简介信息不能为空' }],
            originalPrice: [
                {
                    required: true,
                    message: '价格不能为空',
                },
                {
                    pattern:
                        '(^[1-9]([0-9]+)?(\\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\\.[0-9]([0-9])?$)',
                    message: '价格不正确',
                },
            ],
            currentPrice: [
                {
                    required: true,
                    message: '价格不能为空',
                },
                {
                    pattern:
                        '(^[1-9]([0-9]+)?(\\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\\.[0-9]([0-9])?$)',
                    message: '价格不正确',
                },
            ],
            coverImage: [
                {
                    required: true,
                    message: '没有上传封面图片',
                },
            ],
            packageType: [
                {
                    required: true,
                    message: '没有选择套餐类别',
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

    //文件上传成功的回调函数
    async function imageUploadSuccess(
        response: UploadApiResponse,
    ) {
        if (response.code === 200) {
            ElMessage.success('上传成功！');
            goodsDialog.imageUrl = `${proxy?.$minioUrl}${response.result}`;
            goodsDialog.dataForm.coverImage = response.result || null;
        }
        dialogFormRef.value?.clearValidate(['coverImage']);
    }

    function imageUploadError(e: Error) {
        ElMessage.error('图片上传失败');
        console.error(e);
    }

    //文件上传失败的回调函数
    const addHandle = async () => {
        try {
            goodsDialog.dataForm.id = null;
            goodsDialog.visible = true;
            await nextTick();
            dialogFormRef.value?.resetFields();
            goodsDialog.dataForm.promotionId = null;
            goodsDialog.dataForm.coverImage = null;
            goodsDialog.imageUrl = null;
            goodsDialog.newTag = '';
            goodsDialog.dataForm.tags = [];
            goodsDialog.dataForm.categoryId = null;
            goodsDialog.item = [{}] as Exam[];

            loadRules();
        } catch (error) {}
    };

    const typeOptions = [
        { label: '科室检查', value: '科室检查', field: 'departmentExam' },
        { label: '实验室检查', value: '实验室检查', field: 'labExam' },
        { label: '医技检查', value: '医技检查', field: 'medicalExam' },
        { label: '其他检查', value: '其他检查', field: 'otherExam' },
    ];
    async function dataFormSubmit() {
        try {
            const ok = await dialogFormRef.value?.validate();
            if (!ok) {
                ElMessage.error('校验失败');
                return;
            }

            const examMap: Record<string, SendExam[]> = {};
            for (const opt of typeOptions) {
                examMap[opt.value] = [];
            }
            // 遍历 items
            for (const one of goodsDialog.item) {
                //后端已检查
                examMap[one.type]?.push({ title: one.title, content: one.content });
            }
            const sendData = {
                id: goodsDialog.dataForm.id,
                packageCode: goodsDialog.dataForm.packageCode,
                packageName: goodsDialog.dataForm.packageName,
                description: goodsDialog.dataForm.description,
                departmentExam: examMap['科室检查'],
                labExam: examMap['实验室检查'],
                medicalExam: examMap['医技检查'],
                otherExam: examMap['其他检查'],
                coverImage: goodsDialog.dataForm.coverImage,
                originalPrice: goodsDialog.dataForm.originalPrice,
                currentPrice: goodsDialog.dataForm.currentPrice,
                packageType: goodsDialog.dataForm.packageType,
                tags: goodsDialog.dataForm.tags,
                categoryId: goodsDialog.dataForm.categoryId,
                promotionId: goodsDialog.dataForm.promotionId,
            };
            const send = goodsDialog.dataForm.id === null ? 'save' : 'modify';
            await request.post(`/mis/goods/${send}`, sendData);
            ElMessage.success('操作成功！');
            // data.pageIndex = 1;
            loadData();
        } catch (error: any) {
            console.log(error.response.data?.msg || '操作失败');
        } finally {
            goodsDialog.visible = false;
        }
    }

    // 弹出修改的modal窗口
    async function updateHandle(id: number) {
        // 设置id
        goodsDialog.dataForm.id = id;
        // 渲染窗口
        goodsDialog.visible = true;
        // 等待渲染完毕

        await nextTick();
        dialogFormRef.value?.resetFields();
        goodsDialog.dataForm.promotionId = null;
        goodsDialog.dataForm.coverImage = null;
        goodsDialog.imageUrl = null;
        goodsDialog.newTag = '';
        goodsDialog.dataForm.tags = [];
        goodsDialog.dataForm.categoryId = null;
        goodsDialog.item = [{}] as Exam[];
        // 发送ajax请求，获取数据
        let result = await request.post('/mis/goods/findExam', { id });

        // 加载折扣列表
        loadRules();
        if (!result) {
            ElMessage.warning('查询到当前套餐没有任何数据');
            return;
        }
        // 填充数据
        goodsDialog.dataForm.packageName = result.packageName;
        goodsDialog.dataForm.packageCode = result.packageCode;
        goodsDialog.dataForm.description = result.description;
        goodsDialog.dataForm.originalPrice = result.originalPrice;
        goodsDialog.dataForm.currentPrice = result.currentPrice;
        goodsDialog.dataForm.currentPrice = result.currentPrice;
        goodsDialog.dataForm.promotionId = result.ruleId;
        goodsDialog.dataForm.coverImage = result.coverImage;
        goodsDialog.imageUrl = `${proxy?.$minioUrl}${result.coverImage}`;
        goodsDialog.dataForm.packageType = result.packageType;
        goodsDialog.dataForm.tags = result.tags ? result.tags : [];
        goodsDialog.dataForm.categoryId = result.categoryId ? result.categoryId : null;

        for (let key of typeOptions) {
            if (result.hasOwnProperty(key.field)) {
                for (let one of result[key.field]) {
                    goodsDialog.item.push({
                        type: key.value,
                        title: one.title,
                        content: one.content,
                    });
                }
            }
        }
        goodsDialog.item.shift();
    }
    // -------------------- 文件上传模态窗----------------------------
    const documentDialog = reactive({
        visible: false,
        upload: {
            action: '/meinian-api/mis/goods/uploadExcel',
            headers: {
                satoken: localStorage.getItem('token'),
            },
        },
        data: {
            id: 0,
            hasCheckup: false,
        },
    });
    async function documentDownloadHandle() {
        try {
            const response = await axios.get('/meinian-api/mis/goods/downloadExcel', {
                params: { id: documentDialog.data.id },
                responseType: 'blob',
                headers: { satoken: localStorage.getItem('token') },
            });

            // 检查是否为错误响应（后端返回 JSON 错误）
            const contentType = response.headers['content-type'];
            if (contentType && contentType.includes('application/json')) {
                const text = await response.data.text();
                const errorJson = JSON.parse(text);
                ElMessage.error(errorJson.msg || '下载失败');
                return;
            }

            // 如果是正常二进制流
            const blobUrl = URL.createObjectURL(response.data); // 直接使用 response.data 是 Blob
            const a = document.createElement('a');
            a.href = blobUrl;
            a.download = `${documentDialog.data.id}.xlsx`;
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);

            // 延迟释放 URL 并关闭弹窗
            setTimeout(() => {
                URL.revokeObjectURL(blobUrl);
            }, 1000);
            documentDialog.visible = false; // 立即关闭弹窗
        } catch (error) {
            console.error(error);
            ElMessage.error('下载请求失败');
        }
    }

    async function documentUploadSuccess(response: UploadApiResponse) {
        try {
            if (response.code === 200) {
                ElMessage({
                    type: 'success',
                    message: '文件上传成功',
                    duration: 1200,
                    onClose: () => {
                        loadData();
                    },
                });
            }
        } finally {
            documentDialog.visible = false;
        }
    }
    function documentUploadError(e: Error) {
        ElMessage({
            message: '文件上传失败',
            type: 'error',
            duration: 1200,
        });
        console.error(e);
    }
    function documentBeforeUpload(file: UploadRawFile) {
        const size = file.size / 1024 / 1024;
        if (size > 20) {
            ElMessage.error('文件内容超出20MB');
            return false;
        }
        return true;
    }
    async function documentHandle(id: number, hasCheckup: boolean) {
        documentDialog.visible = true;
        documentDialog.data.hasCheckup = hasCheckup;
        documentDialog.data.id = id;
    }
    async function loadRules() {
        const result: any = await request.get('/mis/rule/queryAllRule');
        goodsDialog.ruleList = result;
    }
    function addtag() {
        const tag = goodsDialog.newTag;
        if (stringIsEmpty(tag)) {
            ElMessage.error('添加的标签为空');
            return;
        }

        if (goodsDialog.dataForm.tags.includes(tag)) {
            ElMessage.error('已经添加过该标签');
            return;
        }
        goodsDialog.dataForm.tags.push(tag);
        goodsDialog.newTag = '';
    }
    function closeTag(one: string) {
        const index = goodsDialog.dataForm.tags.indexOf(one);
        goodsDialog.dataForm.tags.splice(index, 1);
    }
    function addItem() {
        goodsDialog.item.push({} as Exam);
    }
    function deleteItem($index: number) {
        if (goodsDialog.item.length === 1) {
            ElMessage.error('最后一个不能删除');
            return;
        }
        goodsDialog.item.splice($index, 1);
    }
</script>

<style lang="less" scoped>
    @import url('goods.less');
    @import url('../style.less');
</style>

<style lang="less">
    /* 确保 el-dialog 内部内容正确滚动 */
    .el-dialog__body {
        max-height: 60vh;
        overflow-y: auto;
    }
</style>
