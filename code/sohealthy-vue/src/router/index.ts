import { createRouter, createWebHistory } from 'vue-router';
import frontRoutes from './front';
import misRoutes from './mis';
import commonRoutes from './common';

const router = createRouter({
    history: createWebHistory(),
    routes: [...frontRoutes, ...misRoutes, ...commonRoutes],
});

// router.beforeEach 在每次路由跳转前执行，用于权限控制、登录验证等全局路由守卫。
// to：要跳转到的目标路由对象
// from：当前离开的路由对象
// next：控制跳转的函数，必须调用才能继续
router.beforeEach((to, _from) => {
    // 从本地存储中获取权限
    let permissions = localStorage.getItem('permissions');
    // 从本地存储中获取令牌
    let token = localStorage.getItem('token');
    // 获取要跳转的路径
    let fullPath = to.fullPath;

    // mis端
    if (fullPath.startsWith('/mis') && fullPath !== '/mis/login') {
        // 是mis端，不是访问登录页，那要鉴权。
        if (!permissions || !token) {
            // 权限和令牌只要有一个是空，跳转到登录页。
            return { name: 'MisLogin' };
        }
    } else if (
        // 业务端：业务端是客户详情页或者是查看客户商品快照，那就需要登录了。
        fullPath.startsWith('/front/customer') ||
        fullPath.startsWith('/front/goods_snapshot')
    ) {
        if (!token) {
            return { name: 'FrontIndex' };
        }
    }

    return;
});

export default router;
