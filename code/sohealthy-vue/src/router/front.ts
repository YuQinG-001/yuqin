export default [
    { path: '/', redirect: '/front/index' },
    // 下面是前台系统的路由配置
    {
        path: '/front',
        name: 'Front',
        component: () => import('../views/front/main.vue'),
        children: [
            {
                path: 'index',
                name: 'FrontIndex',
                component: () => import('../views/front/index.vue'),
                props: true,
            },
        ],
    },
];
