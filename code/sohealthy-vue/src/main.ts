import { createApp } from 'vue';
import App from './App.vue';

const app = createApp(App);

// 导入路由配置
import router from './router';
app.use(router);

// 导入SVG依赖库，可以在页面上显示SVG图片
import 'virtual:svg-icons-register';

// 引用ElementPlus的CSS文件，否则MacOS系统会出现控件丢失样式
import 'element-plus/dist/index.css';
// 导入ElementUI组件库
import ElementPlus from 'element-plus';
// 让日历控件显示中文，导入简体中文场景
import zhCn from 'element-plus/es/locale/lang/zh-cn';
// 将ElementUI组件库整合到Vue对象上
app.use(ElementPlus, { locale: zhCn });
// 使用ElementPlus自带的图标库
import * as ElementPlusIconsVue from '@element-plus/icons-vue';
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component);
}

// permissions 参数是需要的权限列表（当前用户至少拥有权限列表当中的1个权限，即可访问）
// permissionsJson 是什么？当前用户自己拥有的权限列表。json字符串。
app.config.globalProperties.isAuth = function (permissions: string[]) {
    try {
        let permissionsJson = localStorage.getItem('permissions');
        if (permissionsJson) {
            // 如果从本地存储中获取不到任何权限，不用说了，直接返回false。
            // 把json字符串转换成对象（对象是一个数组）
            const localPermissions = JSON.parse(permissionsJson);
            for (let mustPermission of permissions) {
                if (localPermissions.includes(mustPermission)) {
                    return true;
                }
            }
        }
        return false;
    } catch {
        // 出现错误就是没有权限。
        return false;
    }
};

app.mount('#app');
