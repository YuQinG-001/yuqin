<template>
    <div v-if="isAuth(['ROOT', 'USER:SELECT'])">
        <!-- 没有el-form同样可以显示页面，为什么用它？主要是用它来完成表单验证功能 -->
        <!-- :inline="true" 表示控件排成一行 -->
        <!-- :model="dataForm" 表单数据绑定 -->
        <!-- :rules="dataRule" 关联验证规则 -->
        <!-- ref="form" 这个给表单起个名，以后获取表单通过名字来获取 -->
        <el-form :inline="true" :model="dataForm" :rules="dataRule" ref="form">
            <!-- prop="name" 对应的就是 dataRule 中的 name 属性，这样就关联上验证了 -->
            <el-form-item prop="name">
                <!-- class="dialog-input" 是自己设置的样式，用来设置表单控件宽度，因为默认情况下，表单控件太宽了 -->
                <el-input
                    v-model="dataForm.name"
                    placeholder="姓名"
                    maxlength="10"
                    class="dialog-input"
                    clearable
                />
            </el-form-item>
            <el-form-item>
                <el-select
                    v-model="dataForm.sex"
                    class="dialog-input"
                    placeholder="性别"
                    clearable
                    style="width: 100px"
                >
                    <el-option label="男" value="男" />
                    <el-option label="女" value="女" />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-select
                    v-model="dataForm.role"
                    class="dialog-input"
                    placeholder="角色"
                    clearable
                >
                    <!-- 这个位置将来模型层有数据了，自然就显示了 -->
                    <el-option v-for="one in roleList" :label="one.roleName" :value="one.roleId" />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-select
                    v-model="dataForm.deptId"
                    class="dialog-input"
                    placeholder="部门"
                    clearable
                >
                    <!-- 这个位置也是这样，将来发送ajax请求之后就有数据了 -->
                    <el-option v-for="one in deptList" :label="one.deptName" :value="one.deptId" />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-select
                    v-model="dataForm.status"
                    class="dialog-input"
                    placeholder="状态"
                    clearable
                >
                    <el-option label="在职" value="1" />
                    <el-option label="离职" value="2" />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="searchHandle()">查询</el-button>
                <!-- 当前用户至少有 ROOM或者USER:INSERT中的任何一个权限，这个按钮才能显示 -->
                <el-button
                    type="primary"
                    :disabled="!isAuth(['ROOT', 'USER:INSERT'])"
                    @click="addHandle()"
                >
                    新增
                </el-button>
                <!-- 当前用户至少有 ROOT或者USER:DELETE中的任何一个权限，这个按钮才能显示 -->
                <el-button
                    type="danger"
                    :disabled="!isAuth(['ROOT', 'USER:DELETE'])"
                    @click="deleteHandle()"
                >
                    批量删除
                </el-button>
            </el-form-item>
        </el-form>
        <!-- :data="data.dataList" 为表格提供数据，现代开发不需要写v-for，
        直接底层自动从数组中取出数据遍历生成表格行，data.dataList存储的是
        当前页数据，不是数据库中全部数据 -->
        <!-- :header-cell-style 设置表头背景色 -->
        <!-- border 表示表格的每个单元格都有边框 -->
        <!-- v-loading="data.loading" 决定是否显示加载动画 -->
        <!-- @selection-change="selectionChangeHandle" 勾选复选框时的回调 -->
        <el-table
            :data="data.dataList"
            :header-cell-style="{ background: '#f5f7fa' }"
            border
            v-loading="data.loading"
            @selection-change="selectionChangeHandle"
        >
            <!-- type="selection" 规定这一列是复选框列 -->
            <!-- header-align="center" 表示表头内容居中 -->
            <!-- align="center" 表示列中数据居中 -->
            <el-table-column type="selection" header-align="center" align="center" width="50" />

            <!-- 这一列是序号，不是主键值哈。 -->
            <!-- type="index" 告诉EP组件，这一列不用绑定数据，请自动帮我生成行号 -->
            <el-table-column
                type="index"
                header-align="center"
                align="center"
                width="100"
                label="序号"
            >
                <!-- 
            scope 是一个回调参数，它包含了当前这行表格数据的所有信息。
            可以把它理解成一个数据快递包，ElTable 会自动把这一行的数据
            “打包”好，通过 scope 传递给你。 
            scope.$index: 当前行在【当前页面】的索引（从0开始）
            scope.row: 当前行的完整数据对象 
            scope.column: 当前列的信息
            scope.store: 表格的状态管理
        -->
                <template #default="scope">
                    <!-- 这是序号的算法 -->
                    <span>{{ (data.pageIndex - 1) * data.pageSize + scope.$index + 1 }}</span>
                </template>
            </el-table-column>
            <!-- prop="name" 这个 name是后端java对象的属性名 -->
            <el-table-column
                prop="realName"
                header-align="center"
                align="center"
                min-width="100"
                label="姓名"
            />
            <el-table-column
                prop="gender"
                header-align="center"
                align="center"
                min-width="60"
                label="性别"
            />
            <el-table-column
                prop="mobile"
                header-align="center"
                align="center"
                min-width="130"
                label="电话"
            />
            <!-- :show-overflow-tooltip="true" 设置为true表示采用缩略显示 -->
            <el-table-column
                prop="email"
                header-align="center"
                align="center"
                min-width="240"
                label="邮箱"
                :show-overflow-tooltip="true"
            />
            <el-table-column
                prop="hireDate"
                header-align="center"
                align="center"
                min-width="130"
                label="入职日期"
            />
            <el-table-column
                prop="roles"
                header-align="center"
                align="center"
                min-width="150"
                label="角色"
                :show-overflow-tooltip="true"
            />
            <el-table-column
                prop="deptName"
                header-align="center"
                align="center"
                min-width="120"
                label="部门"
            />
            <el-table-column
                prop="userStatus"
                header-align="center"
                align="center"
                min-width="100"
                label="状态"
            />
            <el-table-column header-align="center" align="center" width="150" label="操作">
                <template #default="scope">
                    <!-- type="text" 表示按钮没有边框，看起来像文字。 -->
                    <el-button
                        type="text"
                        v-if="isAuth(['ROOT', 'USER:UPDATE'])"
                        @click="updateHandle(scope.row.userId)"
                    >
                        修改
                    </el-button>
                    <el-button
                        type="text"
                        v-if="isAuth(['ROOT', 'USER:UPDATE'])"
                        :disabled="scope.row.status == '离职' || scope.row.root"
                        @click="dismissHandle(scope.row.userId)"
                    >
                        离职
                    </el-button>
                    <el-button
                        type="text"
                        :disabled="scope.row.root"
                        v-if="isAuth(['ROOT', 'USER:DELETE'])"
                        @click="deleteHandle(scope.row.userId)"
                    >
                        删除
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
        <!-- EP组件库的分页组件 -->
        <!-- :current-page 当前页码 -->
        <!-- :page-size 每页显示多少条记录 -->
        <!-- :total 总记录条数 -->
        <!-- :page-sizes  它定义了一个下拉选择框，让用户可以快速切换每页显示的数据条数-->
        <!-- layout 分页最下面一行显示什么内容，顺序是怎样的 -->
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
    <!-- :title 是动态的，是新增还是修改，看id是不是null，是null就是新增 -->
    <!-- :close-on-click-modal="false" 设置点击模态窗口之外的阴影是否隐藏模态窗口，false不隐藏 -->
    <!-- v-model="dialog.visible" 控制模态窗口隐藏/显示 -->
    <el-dialog
        v-if="isAuth(['ROOT', 'USER:SELECT', 'USER:UPDATE'])"
        :title="!dialog.update ? '新增' : '修改'"
        :close-on-click-modal="false"
        v-model="dialog.visible"
        width="450px"
    >
        <!-- :model="dialog.dataForm" 表单上绑定的数据 -->
        <!-- ref="dialogForm" 给表单起个名字 -->
        <!-- :rules="dialog.dataRule" 给表单关联校验规则 -->
        <el-form
            :model="dialog.dataForm"
            ref="dialogForm"
            :rules="dialog.dataRule"
            label-width="80px"
        >
            <!-- prop="username"是将username的校验规则绑定到表单项上 -->
            <el-form-item label="用户名" prop="username">
                <el-input v-model="dialog.dataForm.username" maxlength="20" clearable />
            </el-form-item>
            <!-- v-if="!dialog.update" 用来决定密码框显示还是隐藏 -->
            <el-form-item label="密码" prop="password" v-if="!dialog.update">
                <el-input
                    type="password"
                    v-model="dialog.dataForm.password"
                    maxlength="20"
                    clearable
                />
            </el-form-item>

            <el-form-item label="姓名" prop="realName">
                <el-input v-model="dialog.dataForm.realName" maxlength="10" clearable />
            </el-form-item>
            <el-form-item label="性别" prop="gender">
                <el-select v-model="dialog.dataForm.gender" clearable>
                    <el-option label="男" value="男"></el-option>
                    <el-option label="女" value="女"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="电话" prop="mobile">
                <el-input v-model="dialog.dataForm.mobile" maxlength="11" clearable />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
                <el-input v-model="dialog.dataForm.email" maxlength="200" clearable />
            </el-form-item>
            <el-form-item label="入职日期" prop="hireDate">
                <!-- type="date" 表示日期的格式只需要年月日 -->
                <!-- :editable="false" 表示用户不可输入，只能鼠标操作 -->
                <!-- value-format="YYYY-MM-DD" 表示js代码读取日期时的日期格式 -->
                <el-date-picker
                    v-model="dialog.dataForm.hireDate"
                    type="date"
                    placeholder="选择日期"
                    :editable="false"
                    format="YYYY-MM-DD"
                    value-format="YYYY-MM-DD"
                />
            </el-form-item>
            <el-form-item label="角色" prop="role">
                <!-- multiple 表示可以多选 -->
                <el-select
                    v-model="dialog.dataForm.role"
                    placeholder="选择角色"
                    class="dialog-input"
                    multiple
                    clearable
                >
                    <el-option
                        v-for="one in roleList"
                        :key="one.roleId"
                        :label="one.roleName"
                        :value="one.roleId"
                        :disabled="one.roleName == '超级管理员'"
                    ></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="部门">
                <el-select
                    v-model="dialog.dataForm.deptId"
                    placeholder="选择部门"
                    class="dialog-input"
                    clearable
                >
                    <el-option
                        v-for="one in deptList"
                        :key="one.deptId"
                        :label="one.deptName"
                        :value="one.deptId"
                    />
                </el-select>
            </el-form-item>
        </el-form>
        <!-- 页脚区 -->
        <template #footer>
            <span class="dialog-footer">
                <!-- 直接将 dialog.visible 设置为false 就隐藏了。 -->
                <el-button @click="dialog.visible = false">取消</el-button>
                <el-button type="primary" @click="dataFormSubmit">确定</el-button>
            </span>
        </template>
    </el-dialog>
</template>
<script lang="ts" setup>
    import { reactive, ref, onMounted, useTemplateRef, nextTick } from 'vue';
    import { dayjs, ElMessage, ElMessageBox } from 'element-plus';
    import useAuth from '../../hooks/useAuth';
    import request from '../../utils/request';

    const { isAuth } = useAuth();
    const formRef = useTemplateRef('form');
    const dialogFormRef = useTemplateRef('dialogForm');

    // ==================== 类型定义 ====================
    interface Dept {
        deptId: number;
        deptName: string;
    }
    interface Role {
        roleId: number;
        roleName: string;
    }
    interface DataList {
        userStatus: number | string;
    }

    // ==================== Hook: 部门/角色下拉数据 ====================
    function useDeptRole() {
        const deptList = ref<Dept[]>([]);
        const roleList = ref<Role[]>([]);

        return { deptList, roleList };
    }

    // ==================== Hook: 查询表单 & 表格数据 ====================
    function useTableData() {
        const currentChangeHandle = (pageNum: number) => {
            data.pageIndex = pageNum;
            loadPageData();
        };
        const sizeChangeHandle = (pageSize: number) => {
            data.pageSize = pageSize;
            data.pageIndex = 1;
            loadPageData();
        };
        const searchHandle = async () => {
            const valid = formRef.value?.validate;
            if (valid) {
                formRef.value?.clearValidate();
                data.pageIndex = 1;
                loadPageData();
            }
        };
        // 查询表单
        const dataForm = reactive({
            name: '',
            sex: '',
            role: '',
            deptId: '',
            status: '',
        });
        // 查询表单校验规则
        const dataRule = reactive({
            name: [
                { required: false, pattern: '^[\u4e00-\u9fa5]{1,10}$', message: '姓名格式错误' },
            ],
        });
        // 表格及分页状态
        const data = reactive({
            dataList: [] as DataList[],
            pageIndex: 1,
            pageSize: 10,
            totalCount: 0,
            loading: false,
            selections: [] as DataList[],
        });

        // 多选事件处理
        const selectionChangeHandle = (val: DataList[]) => {
            data.selections = val;
        };

        return {
            dataForm,
            dataRule,
            data,
            searchHandle,
            currentChangeHandle,
            sizeChangeHandle,
            selectionChangeHandle,
        };
    }

    // ==================== Hook: 新增/修改弹窗 ====================
    function useDialog() {
        const dialog = reactive({
            visible: false, // 原代码为 true，保持原样
            update: false,
            dataForm: {
                id: null as null | number,
                username: null as null | string,
                password: null as null | string,
                realName: null as null | string,
                gender: null as null | string,
                mobile: null as null | string,
                email: null as null | string,
                hireDate: dayjs(new Date()).format('YYYY-MM-DD'),
                role: null as null | string[],
                deptId: null as null | string,
                userStatus: 1,
            },
            dataRule: {
                username: [
                    { required: true, message: '用户名不能为空' },
                    { pattern: '^[a-zA-Z0-9]{5,20}$', message: '用户名格式错误' },
                ],
                password: [
                    { required: true, message: '密码不能为空' },
                    { pattern: '^[a-zA-Z0-9]{6,20}$', message: '密码格式错误' },
                ],
                realName: [
                    { required: true, message: '姓名不能为空' },
                    { pattern: '^[\u4e00-\u9fa5]{2,10}$', message: '姓名格式错误' },
                ],
                gender: [{ required: true, message: '性别不能为空' }],
                mobile: [
                    { required: true, message: '电话不能为空' },
                    { pattern: '^1[1-9]\\d{9}$', message: '电话格式错误' },
                ],
                email: [
                    { required: true, message: '邮箱不能为空' },
                    {
                        pattern: '^([a-zA-Z]|[0-9])(\\w|\\-)+@[a-zA-Z0-9]+\\.([a-zA-Z]{2,4})$',
                        message: '邮箱格式错误',
                    },
                ],
                hireDate: [{ required: true, message: '入职日期不能为空' }],
                role: [{ required: true, message: '角色不能为空' }],
                userStatus: [{ required: true, message: '状态不能为空' }],
            },
        });
        const addHandle = async () => {
            dialog.dataForm.id = null;
            dialog.update = false;
            dialog.visible = true;
            await nextTick();
            dialogFormRef.value?.resetFields();
            dialog.dataForm.deptId = null;
        };

        const dataFormSubmit = async () => {
            try {
                dialogFormRef.value?.clearValidate();

                const valid = await dialogFormRef.value?.validate();
                if (valid) {
                    const sendData = {
                        userId: dialog.dataForm.id, // 提交时带上id，修改接口需要
                        username: dialog.dataForm.username,
                        password: dialog.dataForm.password,
                        realName: dialog.dataForm.realName,
                        gender: dialog.dataForm.gender,
                        mobile: dialog.dataForm.mobile,
                        email: dialog.dataForm.email,
                        deptId: dialog.dataForm.deptId,
                        hireDate: dialog.dataForm.hireDate,
                        roleIds: dialog.dataForm.role,
                        userStatus: dialog.dataForm.userStatus,
                    };
                    const method = dialog.update ? 'put' : 'post';
                    const url = `/mis/user/${dialog.update ? 'modifyUser' : 'save'}`;
                    await request[method](url, sendData);
                    ElMessage.success(dialog.update ? '修改成功' : '新增成功');
                    dialog.visible = false;
                    loadPageData(); // 操作成功后刷新列表
                }
            } catch (error: unknown) {
                // 接口错误已经在request拦截器里统一提示了，这里只需要处理表单校验和其他本地错误
                if (error && typeof error === 'object' && Object.keys(error).length > 0) {
                    // 表单校验失败，Element Plus 已经显示红色提示，无需额外弹窗
                    console.log('表单校验未通过:', error);
                } else if (error instanceof Error) {
                    // 本地代码错误
                    console.error('代码运行错误:', error.message);
                    ElMessage.error('操作失败，请稍后重试');
                } else {
                    console.error('提交过程发生未知异常:', error);
                }
            }
        };
        async function updateHandle(userId: number) {
            try {
                // 先打开弹窗，同时可显示加载动画
                dialog.update = true;
                dialog.visible = true;
                await nextTick();
                dialogFormRef.value?.resetFields();
                dialog.dataForm.id = userId; // 保留，用于后续提交更新

                const result = await request.get('/mis/user/queryUserById', {
                    params: { userId: dialog.dataForm.id },
                });
                console.log('🚀 ~ updateHandle ~ result:', result);

                // 安全地解构赋值，避免 result 为空时报错
                if (result) {
                    const { deptId, username, realName, gender, mobile, hireDate, roleIds, email } =
                        result;

                    // 批量赋值（可读性更好）
                    Object.assign(dialog.dataForm, {
                        deptId,
                        username,
                        realName,
                        gender,
                        mobile,
                        hireDate,
                        role: JSON.parse(roleIds), // 注意字段映射
                        email,
                    });
                } else {
                    ElMessage.warning('用户数据为空');
                }
            } catch (error) {
                // 拦截器已经提示过错误了
                console.error('updateHandle 请求异常:', error);
            }
        }

        // 用户离职处理
        const dismissHandle = async (userId: number) => {
            try {
                await ElMessageBox.confirm('确定要将该用户标记为离职吗？', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning',
                });
                await request.post(`/mis/user/dismiss/${userId}`);
                ElMessage.success('用户离职成功');
                loadPageData();
            } catch (error) {
                console.error('用户离职操作异常:', error);
            }
        };

        // 删除用户（支持单个和批量）
        const deleteHandle = async (userId?: number) => {
            try {
                let ids: number[] = [];
                if (userId) {
                    ids = [userId];
                    await ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning',
                    });
                } else {
                    ids = data.selections.map((item: any) => item.userId);
                    if (ids.length === 0) {
                        ElMessage.warning('请选择要删除的用户');
                        return;
                    }
                    await ElMessageBox.confirm(
                        `确定要删除选中的 ${ids.length} 个用户吗？`,
                        '提示',
                        {
                            confirmButtonText: '确定',
                            cancelButtonText: '取消',
                            type: 'warning',
                        },
                    );
                }
                await request.post('/mis/user/delete', { ids });
                ElMessage.success('删除成功');
                loadPageData();
            } catch (error) {
                console.error('删除用户操作异常:', error);
            }
        };

        return { dialog, dataFormSubmit, addHandle, updateHandle, dismissHandle, deleteHandle };
    }

    function useUserCommon() {
        const loadRole = async () => {
            try {
                const result = await request.get('/mis/role/list');
                // 防御性赋值：确保 result 是数组
                roleList.value = Array.isArray(result) ? result : [];
            } catch (error) {
                // 拦截器已经提示过错误了，这里只需要清空列表
                roleList.value = [];
                console.error('获取角色列表异常:', error);
            }
        };

        // 加载部门列表
        const loadDept = async () => {
            try {
                const result = await request.get('/mis/dept/list');
                deptList.value = Array.isArray(result) ? result : [];
            } catch (error) {
                deptList.value = [];
                console.error('获取部门列表异常:', error);
            }
        };

        const loadPageData = async () => {
            try {
                // 开启加载状态
                data.loading = true;

                // 准备请求参数
                const sendData = {
                    pageNum: data.pageIndex,
                    pageSize: data.pageSize,
                    realName: dataForm.name ?? '', // 防止 undefined 传给后端
                    gender: dataForm.sex ?? '',
                    roleId: dataForm.role ?? '',
                    deptId: dataForm.deptId ?? '',
                    userStatus: dataForm.status ?? '',
                };

                // 发送 POST 请求
                const result = await request.post('/mis/user/page', sendData);

                // 安全取出分页数据（防止 result 为 null 时报错）
                const { records = [], total = 0 } = result || {};
                data.dataList = records;
                data.dataList.forEach((item: DataList) => {
                    if (item.userStatus == 1) {
                        item.userStatus = '在职';
                    } else if (item.userStatus == 2) {
                        item.userStatus = '离职';
                    }
                });
                data.totalCount = total;
            } catch (error) {
                // 拦截器已经提示过错误了
                data.dataList = [];
                data.totalCount = 0;
                console.error('loadPageData 请求异常:', error);
            } finally {
                // 无论成功或失败，最终都要关闭 loading
                data.loading = false;
            }
        };
        return { loadDept, loadRole, loadPageData };
    }
    onMounted(() => {
        loadRole();
        loadDept();
        loadPageData();
    });
    // ==================== 实例化 Hooks ====================
    const { deptList, roleList } = useDeptRole();
    const {
        dataForm,
        dataRule,
        data,
        searchHandle,
        sizeChangeHandle,
        currentChangeHandle,
        selectionChangeHandle,
    } = useTableData();
    const { dialog, addHandle, dataFormSubmit, updateHandle, dismissHandle, deleteHandle } =
        useDialog();
    const { loadDept, loadRole, loadPageData } = useUserCommon();

    // 注：原代码没有 onMounted 或其他初始化逻辑，此处不添加
</script>

<style lang="less" scoped>
    @import url('user.less');
    .dialog-input {
        width: auto; /* 宽度自适应内容 */
        min-width: 150px; /* 最小宽度，防止太窄 */
    }
</style>
