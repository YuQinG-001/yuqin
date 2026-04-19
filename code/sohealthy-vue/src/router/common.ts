export default [
    {
        path: '/404',
        name: '404',
        component: () => import('../views/404.vue'),
    },
    {
        // 捕获所有未定义的路由路径
        // pathMatch这个变量名随便写
        // 这个配置需要放到所有路由的最下面，兜底。
        path: '/:pathMatch(.*)*',
        // 重定向到 /404
        redirect: '/404',
    },
];
