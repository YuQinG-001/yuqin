<template>
    <div v-if="proxy!.isAuth(['ROOT', 'APPOINTMENT:SELECT'])">
        <el-form :inline="true" :model="dataForm" :rules="dataRule" ref="form">
            <el-form-item>
                <el-date-picker
                    v-model="dataForm.appointmentDate"
                    type="date"
                    placeholder="选择日期"
                    :editable="false"
                    format="YYYY-MM-DD"
                    value-format="YYYY-MM-DD"
                    :clearable="false"
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
                <el-button type="success" @click="checkinHandle()">体检签到</el-button>
                <el-button type="danger" @click="finishHandle()">完成体检</el-button>
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
                        :disabled="!proxy!.isAuth(['ROOT', 'APPOINTMENT:SELECT'])"
                        @click="guidanceHandle(scope.row.id)"
                    >
                        导引单
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
        title="体检导引单"
        v-if="proxy.isAuth(['ROOT', 'APPOINTMENT:SELECT'])"
        v-model="guidanceDialog.visible"
        width="800px"
    >
        <div class="guidance" id="pdfDom" :name="guidanceDialog.name">
            <h2 class="title">美年大健康体检项目导引单</h2>
            <div class="summary">
                <table class="base-info">
                    <tr>
                        <td class="label">姓名:</td>
                        <td class="value">{{ guidanceDialog.name }}</td>
                        <td class="label">性别:</td>
                        <td class="value">{{ guidanceDialog.sex }}</td>
                        <td class="label">年龄:</td>
                        <td class="value">{{ guidanceDialog.age }}</td>
                    </tr>
                    <tr>
                        <td class="label">身份证:</td>
                        <td class="value">{{ guidanceDialog.pid }}</td>
                        <td class="label">电话:</td>
                        <td class="value">{{ guidanceDialog.tel }}</td>
                        <td class="label">日期:</td>
                        <td class="value">{{ guidanceDialog.date }}</td>
                    </tr>
                    <tr>
                        <td class="label">公司:</td>
                        <td colspan="5" class="value">
                            {{ guidanceDialog.company }}
                        </td>
                    </tr>
                </table>
                <!-- 以体检流水号做二维码 -->
                <img class="qrcode" :src="guidanceDialog.qrCodeBase64" />
            </div>
            <table class="checkup">
                <tr>
                    <th>序号</th>
                    <th align="left">检查地点</th>
                    <th align="left">检查项目</th>
                    <th>体检医生</th>
                </tr>
                <tr v-for="(one, index) in guidanceDialog.checkup">
                    <td align="center">{{ index + 1 }}</td>
                    <td>{{ one.place }}</td>
                    <td>{{ one.name }}</td>
                    <td></td>
                </tr>
            </table>
            <div class="desc">
                <p>
                    请注意：体检结束10天后，即可在美年大健康网站（http://www.meinianhealth.com）查询到您的体检报告。之后的5个工作日之内，您将收到本体检中心邮寄的体检报告，请留意查收！
                </p>
                <ul>
                    <li>
                        <el-icon><PhoneFilled /></el-icon>
                        <span>体检咨询：010-12345678</span>
                    </li>
                    <li>
                        <el-icon><PhoneFilled /></el-icon>
                        <span>体检咨询：010-12345679</span>
                    </li>
                    <li>
                        <el-icon><PhoneFilled /></el-icon>
                        <span>体检咨询：010-12345670</span>
                    </li>
                </ul>
            </div>
        </div>
        <div class="operate">
            <el-button type="primary" size="large" :icon="Document" @click="getPdf()">
                下载导引单
            </el-button>
        </div>
    </el-dialog>
    <el-dialog
        title="体检签到"
        v-if="proxy.isAuth(['ROOT', 'APPOINTMENT:UPDATE'])"
        :close-on-click-modal="false"
        v-model="checkinDialog.visible"
        width="500px"
        :before-close="closeHandle"
    >
        <div class="card-info">
            <div class="left">
                <el-form :model="checkinDialog.dataForm" ref="dialogForm" label-width="60px">
                    <el-form-item label="姓名">
                        <el-input v-model="checkinDialog.dataForm.name" disabled />
                    </el-form-item>
                    <el-form-item label="性别">
                        <el-input v-model="checkinDialog.dataForm.sex" disabled />
                    </el-form-item>
                    <el-form-item label="身份证">
                        <el-input v-model="checkinDialog.dataForm.pid" disabled />
                    </el-form-item>
                </el-form>
            </div>
            <div class="right">
                <!-- 这两个控件是排他的，通过photo_1的状态来决定显示谁。-->
                <img
                    :src="checkinDialog.dataForm.photo_1"
                    class="photo"
                    v-if="checkinDialog.dataForm.photo_1 != null"
                />
                <div class="empty" v-if="checkinDialog.dataForm.photo_1 == null">
                    <el-icon :size="60" class="icon"><Avatar /></el-icon>
                </div>
            </div>
        </div>

        <!-- 
    控件排他显示实现原理：
    通过 v-show 控制三个视觉元素的显隐，确保同一时间只有一个元素可见
    showEmpty: 显示操作指引
    showVideo: 显示摄像头实时画面  
    showPhoto: 显示拍摄的照片
    这三个状态在业务逻辑中互斥，实现排他效果
    -->
        <div class="photo-container">
            <!-- 初始状态：显示操作指引 -->
            <div class="empty" v-show="checkinDialog.showEmpty">
                <SvgIcon name="camera" class="camera" />
                <ol>
                    <li>完成体检人身份证刷卡验证后，系统将自动启用摄像头进行人脸采集。</li>
                    <li>拍摄画面中须确保仅包含单一人脸特征，如检测到多张人脸将触发系统告警。</li>
                    <li>要求采集正面免冠人脸图像，禁止佩戴墨镜、口罩等影响面部识别的遮挡物。</li>
                </ol>
            </div>

            <!-- 拍照状态：显示摄像头实时画面 -->
            <!--video标签：HTML5原生视频播放元素，用于在网页中嵌入和播放视频内容或显示摄像头实时流。-->
            <video id="video" autoplay v-show="checkinDialog.showVideo"></video>

            <!-- 完成状态：显示拍摄的照片 -->
            <!--canvas标签：HTML5原生画布元素，提供JavaScript绘图API，用于在网页上动态生成和操作图像图形-->
            <canvas id="photo" width="460" height="345" v-show="checkinDialog.showPhoto"> </canvas>
        </div>

        <template #footer>
            <span class="dialog-footer">
                <!-- 
            按钮状态控制：
            - 拍照按钮在showEmpty时为禁用状态（需要先刷身份证）
            - 签到按钮需要pid和photo_2同时存在才可用
            通过disabled属性实现功能逻辑的排他控制
            -->
                <el-button
                    type="primary"
                    :icon="checkinDialog.btnIcon"
                    :disabled="checkinDialog.showEmpty"
                    @click="takePhotoHandle"
                >
                    {{ checkinDialog.btnText }}
                </el-button>
                <el-button @click="closeHandle">取消</el-button>
                <el-button
                    type="success"
                    :disabled="
                        checkinDialog.dataForm.pid == null || checkinDialog.dataForm.photo_2 == null
                    "
                    @click="dataFormSubmit"
                >
                    签到
                </el-button>
            </span>
        </template>
    </el-dialog>
</template>

<script lang="ts" setup>
    import html2Canvas from 'html2canvas';
    import jsPDF from 'jspdf';
    import { reactive, getCurrentInstance, onMounted, useTemplateRef, nextTick } from 'vue';
    import { ElMessage, ElMessageBox, ElNotification } from 'element-plus';
    import { Camera, RefreshRight, Document } from '@element-plus/icons-vue';
    import router from '../../router/index';
    import { dayjs } from 'element-plus';
    import isBetween from 'dayjs/plugin/isBetween';
    import request from '../../utils/request';
    import message from 'element-plus/es/components/message/index.mjs';

    dayjs.extend(isBetween);
    const { proxy } = getCurrentInstance()!;
    const checkinDialog = reactive({
        visible: false,
        btnText: '拍照',
        btnIcon: Camera,
        showEmpty: true,
        showVideo: false,
        showPhoto: false,
        streamTrack: '',
        dataForm: {
            name: '',
            sex: '',
            pid: '',
            photo_1: null,
            photo_2: null,
        },
    });

    const dataForm = reactive<DataForm>({
        patientName: null,
        phone: null,
        appointmentDate: dayjs().format('YYYY-MM-DD'),
        statusLabel: '全部',
        status: null,
    });
    const guidanceDialog = reactive({
        visible: false, //测试完把这个值改成false
        name: null,
        sex: null,
        age: null,
        pid: null,
        tel: null,
        date: null,
        company: null,
        qrCodeBase64: null,
        checkup: [],
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
    async function loadPageData(): Promise<void> {
        try {
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
            console.log('🚀 ~ loadPageData ~ json:', json);
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
        } finally {
            data.loading = false;
        }
    }
    const formRef = useTemplateRef('form');
    function searchHandle() {
        const ok = formRef.value?.validate();
        if (!ok) {
            return ElMessage.error('提交数据有误');
        }
        formRef.value?.clearValidate();
        data.pageIndex = 1;
        loadPageData();
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
    onMounted(() => {
        loadPageData();
    });
    async function checkinHandle() {
        let current = dayjs().format('YYYY-MM-DD');
        if (current != dataForm.appointmentDate) {
            message.error(`请将日期改为${current}`);
        } else {
            checkinDialog.visible = true;
            await nextTick();
            checkinDialog.dataForm.name = null;
            checkinDialog.dataForm.sex = null;
            checkinDialog.dataForm.pid = null;
            checkinDialog.dataForm.photo_1 = null;
            checkinDialog.dataForm.photo_2 = null;
            checkinDialog.showEmpty = true;
            checkinDialog.showVideo = false;
            checkinDialog.showPhoto = false;
        }
    }
    /*
     * 鱼住科技的读卡器推送过来的数据是先经过base16编码，再进行base64编码，然后推送过来。
     * 我们接收到推送的数据后，要先经过base64解码，再经过base16解码，最终才能拿到原始的内容。
     * base64的解码函数JS内置就有：window.atob() 函数
     * base16的解码函数没有，以下这个函数就是base16的解码函数，从鱼住的index.js文件中拷贝过来的。
     */
    function hex2a(hex) {
        let str_list = '';
        for (let i = 0; i < hex.length && hex.substr(i, 2) !== '00'; i += 2) {
            const a = hex.charCodeAt(i);
            const b = hex.charCodeAt(i + 1);
            const c = b * 256 + a;
            str_list += String.fromCharCode(c);
        }

        return str_list.toString();
    }

    /*
     * 该函数用于把YYYYMMDD格式的日期字符串转换成YYYY-MM-DD格式
     * 这个函数也是从index.js文件中拷贝的
     */
    function parseDateString(str, deco, zero) {
        let year = str.substr(0, 4);
        let month = str.substr(4, 2);
        let date = str.substr(6);
        if (zero) {
            // 如果zero为true则去掉月和日的前导0
            month = month.substr(0, 1) === '0' ? month.substr(1) : month;
            date = date.substr(0, 1) === '0' ? date.substr(1) : date;
        }
        return `${year}${deco}${month}${deco}${date}`;
    }
    // 身份证读卡器 WebSocket 客户端
    // 功能：连接本地读卡器服务，接收并解析身份证信息

    // WebSocket 服务器地址（本地读卡器服务）
    const webUrl = 'ws://127.0.0.1:30004/ws';
    // 创建 WebSocket 连接实例
    let ws = new WebSocket(webUrl);

    // WebSocket 连接建立成功回调
    ws.onopen = function (evt) {
        console.log('身份读取WebSocket已连接');
    };

    // WebSocket 连接关闭回调
    ws.onclose = function (evt) {
        console.log('身份读取WebSocket已关闭');
    };

    // WebSocket 接收到消息回调（核心处理逻辑）
    ws.onmessage = async function (messageEvent) {
        // 业务逻辑判断：只有在登记对话框可见且为空时才处理读卡信息
        if (!checkinDialog.visible || !checkinDialog.showEmpty) {
            return;
        }

        // 解析WebSocket消息为JSON对象
        // 消息格式由读卡器SDK协议定义
        const jsonObject = JSON.parse(messageEvent.data);

        // 判断读卡操作是否成功：Ret=0表示成功，其他值表示失败
        if (jsonObject.Ret == 0) {
            // 判断消息类型：10001=被动接收读卡结果，10000=主动读取身份证内容
            // 这里处理的是读卡器感应到身份证后自动推送的结果
            if (jsonObject.Cmd == 10001) {
                // 将推送过来的数据，通过base64解码
                const userParam = JSON.parse(window.atob(jsonObject.UserParam));

                // ========== 开始提取和解析身份证信息 ==========
                // 虽然刚开始已经对全文进行了base64解码，但具体到信息中的姓名需要再进行base64的解码，鱼住的读卡器就是这么设计的，没办法。
                // 姓名处理：Base64解码 → base16解码 → 去除尾部空格
                // 注意：身份证姓名字段固定为15字符，尾部用空格填充
                const name = hex2a(window.atob(userParam.CardInfo.Name)).trim();

                // 性别处理：1=男，其他=女
                const sex = hex2a(window.atob(userParam.CardInfo.Sex)) == 1 ? '男' : '女';

                // 身份证号码
                const pid = hex2a(window.atob(userParam.CardInfo.No));

                // 生日处理：格式转换（如：19920315 → 1992-03-15）
                const temp = hex2a(window.atob(userParam.CardInfo.Birthday));
                const birthday = parseDateString(temp, '-', true);

                // 照片处理：构建Base64图片URL，供img标签直接显示
                const image = 'data:image/jpg;base64,' + userParam.BmpInfo;

                // 身份证有效期起始日
                const validityPeriodBegin = hex2a(
                    window.atob(userParam.CardInfo.ValidityPeriodBegin),
                );
                const expiryBegin = parseDateString(validityPeriodBegin, '-', true);

                // 身份证有效期截止日处理：可能是具体日期或"长期"
                const validityPeriodEnd = hex2a(
                    window.atob(userParam.CardInfo.ValidityPeriodEnd),
                ).trim();
                const expiryEnd =
                    validityPeriodEnd !== '长期'
                        ? parseDateString(validityPeriodEnd, '-', true)
                        : validityPeriodEnd;

                // 有效期检查：如果不是长期有效，检查是否在有效期内
                if (expiryEnd !== '长期') {
                    // 使用dayjs检查当前时间是否在有效期内
                    let valid = dayjs().isBetween(expiryBegin, expiryEnd);
                    if (!valid) {
                        // 身份证过期，显示错误提示
                        ElMessage.error('身份证已过期');
                        return; // 过期身份证不再进行后续处理
                    }
                    console.log('身份证有效期状态:', valid);
                }

                // ========== 信息提取完成 ==========

                // TODO: 在此处添加业务逻辑
                // 例如：比对身份证信息与体检人信息是否一致
                // 可以将解析出的信息填充到表单中或进行身份验证
                // 构建请求参数对象，包含从身份证读取的患者信息
                let json = {
                    patientName: name, // 患者姓名
                    gender: sex, // 患者性别
                    //idCardNo: pid       // 身份证号码
                    idCardNo: '310101199001011234',
                };
                // 调用后端API，检查患者今日是否已预约或已签到
                let result = await request.post('/mis/appointment/hasInToday', json);
                // 根据API返回结果进行不同处理
                if (result == -1) {
                    // 结果-1：患者今日已签到，提示无需重复签到
                    ElMessage.error('已经签到，无需重复签到');
                } else if (result == 1) {
                    // 结果1：患者已预约且未签到，进行签到流程

                    // 将身份证信息填充到登记弹窗的表单中
                    checkinDialog.dataForm.name = name; // 设置姓名
                    checkinDialog.dataForm.sex = sex; // 设置性别
                    //checkinDialog.dataForm.pid = pid;       // 设置身份证号
                    checkinDialog.dataForm.pid = '310101199001011234';
                    checkinDialog.dataForm.photo_1 = image; // 设置身份证照片

                    // 检测浏览器是否支持访问摄像头（浏览器只要支持以下三个属性中的任何一个属性，都表示支持访问摄像头）
                    let bool =
                        navigator.getUserMedia ||
                        navigator.webkitGetUserMedia ||
                        navigator.mozGetUserMedia;

                    if (bool) {
                        // 浏览器支持摄像头，开始获取视频流
                        navigator.getUserMedia(
                            {
                                audio: false, // 不获取音频
                                video: true, // 获取视频
                            },
                            // 成功回调函数：获取到视频流
                            function (stream) {
                                // 根据ID查找页面中的video元素
                                let video = document.querySelector('#video');
                                // 将视频流设置到video元素
                                video.srcObject = stream;
                                // 保存视频流轨道到对话框对象，便于后续关闭时释放资源
                                checkinDialog.streamTrack = stream.getTracks()[0]; // 从获取的媒体流中提取第一个轨道（是摄像头视频轨道）

                                // 视频元数据加载完成后的回调
                                video.onloadedmetadata = function (e) {
                                    video.play(); // 开始播放视频流
                                    // 更新对话框显示状态
                                    checkinDialog.showEmpty = false; // 隐藏空状态
                                    checkinDialog.showPhoto = false; // 隐藏照片显示
                                    checkinDialog.showVideo = true; // 显示视频画面
                                };
                            },
                            // 失败回调函数：获取视频流失败
                            function (err) {
                                // 显示摄像头开启失败提示
                                ElMessage.error('开启摄像头失败');
                            },
                        );
                    } else {
                        // 浏览器不支持摄像头访问
                        ElMessage.error('当前电脑没有连接摄像头');
                    }
                } else {
                    // 其他结果（如0）：患者未预约，提示错误信息
                    ElMessage.error('该体检人未预约');
                }
            }
        } else {
            // 读卡失败处理：输出错误信息
            console.log('websocket 协议调用失败，原因：' + jsonObject.ErrInfo);
        }
    };
    function closeHandle() {
        checkinDialog.visible = false;
        if (checkinDialog.streamTrack != null) {
            checkinDialog.streamTrack.stop();
        }
    }
    /**
     * 拍照处理函数 - 用于在打卡/签到场景中进行拍照和重拍操作
     */
    function takePhotoHandle() {
        // 当按钮文字为'拍照'时，执行拍照操作
        if (checkinDialog.btnText == '拍照') {
            // 获取视频元素和画布元素
            let video = document.querySelector('#video');
            let canvas = document.querySelector('#photo');

            // 获取2D绘图上下文
            let context = canvas.getContext('2d');
            // 把摄像头当前的取景内容绘制到canvas控件中
            // 参数说明：视频源, 起始x坐标, 起始y坐标, 绘制宽度, 绘制高度
            context.drawImage(video, 0, 0, 460, 345);

            // 显示canvas控件（照片预览），隐藏两个排他控件
            checkinDialog.showEmpty = false; // 隐藏空状态
            checkinDialog.showVideo = false; // 隐藏视频流
            checkinDialog.showPhoto = true; // 显示拍照结果

            // 显示成功消息提示
            ElMessage.success('拍照成功');
            // 更新按钮文字和图标为"重拍"状态
            checkinDialog.btnText = '重拍';
            checkinDialog.btnIcon = RefreshRight;

            // 把canvas中的图片转换成base64编码，并保存到表单数据中
            // 'image/jpeg'指定输出为JPEG格式
            checkinDialog.dataForm.photo_2 = canvas.toDataURL('image/jpeg');
        } else {
            // 当按钮文字为'重拍'时，执行重拍操作

            // 隐藏canvas（照片预览），重新显示摄像头取景
            checkinDialog.showEmpty = false; // 隐藏空状态
            checkinDialog.showVideo = true; // 显示视频流
            checkinDialog.showPhoto = false; // 隐藏拍照结果

            // 更新按钮文字和图标为"拍照"状态
            checkinDialog.btnText = '拍照';
            checkinDialog.btnIcon = Camera;
        }
    }
    async function dataFormSubmit() {
        let json = {
            idCardNo: checkinDialog.dataForm.pid,
            patientName: checkinDialog.dataForm.name,
            photo_1: checkinDialog.dataForm.photo_1,
            photo_2: checkinDialog.dataForm.photo_2,
        };
        let result = await request.post('/meinian-api/mis/appointment/checkin', json);
        if (result) {
            // 在右上角显示通知
            ElNotification({
                title: '通知消息',
                message: '签到成功',
                type: 'success',
                duration: 1200,
            });
            // 签到成功后，让签到弹窗不消失，因为工作人员可能还需要刷下一个身份证。
            // 把弹窗的所有状态重置即可。
            checkinDialog.showEmpty = true;
            checkinDialog.showVideo = false;
            checkinDialog.showPhoto = false;
            checkinDialog.btnIcon = Camera;
            checkinDialog.btnText = '拍照';
            // 数据也重置
            checkinDialog.dataForm.name = '';
            checkinDialog.dataForm.sex = '';
            checkinDialog.dataForm.pid = '';
            checkinDialog.dataForm.photo_1 = null;
            checkinDialog.dataForm.photo_2 = null;
            // 加载分页数据
            loadPageData();
        } else {
            ElMessage({
                message: '签到失败',
                type: 'error',
                duration: 1200,
            });
        }
    }
    async function guidanceHandle(id) {
        guidanceDialog.visible = true;
        // 准备数据
        let json = {
            id: id,
        };
        // 发送ajax请求
        let result = await request.post('/mis/appointment/findGuidanceInfo', json);
        guidanceDialog.name = result.patientName;
        guidanceDialog.sex = result.gender;
        guidanceDialog.age = result.age;
        guidanceDialog.pid = result.idCardNo;
        guidanceDialog.tel = result.phone;
        guidanceDialog.date = result.appointmentDate;
        guidanceDialog.company = result.company == null ? '无' : result.company;
        guidanceDialog.checkup = result.checkup;
        guidanceDialog.qrCodeBase64 = result.qrCodeBase64;
    }
    function isSplit(nodes, index, pageHeight) {
        // 计算当前这块dom是否跨越了a4大小，以此分割
        if (
            nodes[index].offsetTop + nodes[index].offsetHeight < pageHeight &&
            nodes[index + 1] &&
            nodes[index + 1].offsetTop + nodes[index + 1].offsetHeight > pageHeight
        ) {
            return true;
        }
        return false;
    }

    app.config.globalProperties.getPdf = function () {
        var title = this.htmlTitle; //PDF标题
        let ST = document.documentElement.scrollTop || document.body.scrollTop;
        let SL = document.documentElement.scrollLeft || document.body.scrollLeft;
        document.documentElement.scrollTop = 0;
        document.documentElement.scrollLeft = 0;
        document.body.scrollTop = 0;
        document.body.scrollLeft = 0;
        //获取滚动条的位置并赋值为0，因为是el-dialog弹框，并且内容较多出现了纵向的滚动条,截图出来的效果只能截取到视图窗口显示的部分,超出窗口部分则无法生成。所以先将滚动条置顶
        const A4_WIDTH = 592.28;
        const A4_HEIGHT = 841.89;
        let imageWrapper = document.querySelector('#pdfDom'); // 获取DOM
        var title = imageWrapper.getAttribute('name'); //PDF标题
        let pageHeight = (imageWrapper.scrollWidth / A4_WIDTH) * A4_HEIGHT;
        let lableListID = imageWrapper.querySelectorAll('p');
        // 进行分割操作，当dom内容已超出a4的高度，则将该dom前插入一个空dom，把他挤下去，分割
        for (let i = 0; i < lableListID.length; i++) {
            let multiple = Math.ceil(
                (lableListID[i].offsetTop + lableListID[i].offsetHeight) / pageHeight,
            );
            if (isSplit(lableListID, i, multiple * pageHeight)) {
                let divParent = lableListID[i].parentNode; // 获取该div的父节点
                let newNode = document.createElement('div');
                newNode.className = 'emptyDiv';
                newNode.style.background = '#ffffff';
                let _H =
                    multiple * pageHeight -
                    (lableListID[i].offsetTop + lableListID[i].offsetHeight);
                //留白
                newNode.style.height = _H + 30 + 'px';
                newNode.style.width = '100%';
                let next = lableListID[i].nextSibling; // 获取div的下一个兄弟节点
                // 判断兄弟节点是否存在
                if (next) {
                    // 存在则将新节点插入到div的下一个兄弟节点之前，即div之后
                    divParent.insertBefore(newNode, next);
                } else {
                    // 不存在则直接添加到最后,appendChild默认添加到divParent的最后
                    divParent.appendChild(newNode);
                }
            }
        }
        //接下来开始截图
        this.$nextTick(() => {
            // nexttick可以保证要截图的部分全部执行完毕，具体用法自行百度...
            html2Canvas(imageWrapper, {
                allowTaint: true,
                taintTest: false,
                useCORS: true,
                //width:960,
                //height:5072,
                dpi: window.devicePixelRatio * 4, //将分辨率提高到特定的DPI 提高四倍
                scale: 4, //按比例增加分辨率
            }).then((canvas) => {
                let pdf = new jsPDF('p', 'mm', 'a4'); //A4纸，纵向
                let ctx = canvas.getContext('2d'),
                    a4w = 190,
                    a4h = 277, //A4大小，210mm x 297mm，四边各保留10mm的边距，显示区域190x277
                    imgHeight = Math.floor((a4h * canvas.width) / a4w), //按A4显示比例换算一页图像的像素高度
                    renderedHeight = 0;

                while (renderedHeight < canvas.height) {
                    let page = document.createElement('canvas');
                    page.width = canvas.width;
                    page.height = Math.min(imgHeight, canvas.height - renderedHeight); //可能内容不足一页
                    //用getImageData剪裁指定区域，并画到前面创建的canvas对象中
                    page.getContext('2d').putImageData(
                        ctx.getImageData(
                            0,
                            renderedHeight,
                            canvas.width,
                            Math.min(imgHeight, canvas.height - renderedHeight),
                        ),
                        0,
                        0,
                    );
                    pdf.addImage(
                        page.toDataURL('image/jpeg', 1.0),
                        'JPEG',
                        10,
                        10,
                        a4w,
                        Math.min(a4h, (a4w * page.height) / page.width),
                    ); //添加图像到页面，保留10mm边距
                    renderedHeight += imgHeight;
                    if (renderedHeight < canvas.height) pdf.addPage(); //如果后面还有内容，添加一个空页
                }
                pdf.save(`${title}.pdf`);
            });
        });
    };
    function finishHandle() {
        ElMessageBox.prompt('请输入体检单编号，结束该次体检', '提示信息', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            inputPlaceholder: '体检编号',
            type: 'warning',
            inputPattern: /^[0-9a-zA-Z]{32}$/,
            inputErrorMessage: '体检编号不正确',
        }).then(async ({ value }) => {
            let json = {
                appointmentNo: value,
                status: 3,
            };
            let result = await request.post(
                '/meinian-api/mis/appointment/modifyStatusByAppointmentNo',
                json,
            );
            if (result) {
                ElMessage({
                    message: '操作成功',
                    type: 'success',
                    duration: 1200,
                    onClose: () => {
                        loadPageData();
                    },
                });
            } else {
                ElMessage({
                    message: '未能更新记录',
                    type: 'error',
                    duration: 1200,
                });
            }
        });
    }
</script>

<style lang="less" scoped>
    @import url('customer_checkin.less');
</style>
