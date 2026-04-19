export default [
    // 下面是MIS系统的路由配置
    {
        path: '/mis/login',
        name: 'MisLogin',
        component: () => import('../views/mis/login.vue'),
    },
    {
        path: '/mis',
        name: 'Mis',
        component: () => import('../views/mis/main.vue'),
        redirect: '/mis/home',
        children: [
            {
                path: 'dept',
                name: 'MisDept',
                component: () => import('../views/mis/dept.vue'),
                props: true,
                meta: {
                    isTab: true,
                    title: '部门管理',
                },
            },
            {
                path: 'home',
                name: 'MisHome',
                component: () => import('../views/mis/home.vue'),
                meta: {
                    isTab: false,
                    title: '首页',
                },
            },
            {
                path: 'role',
                name: 'MisRole',
                component: () => import('../views/mis/role.vue'),
                meta: {
                    isTab: true,
                    title: '角色管理',
                },
            },
                        {
                path: 'user',
                name: 'MisUser',
                component: () => import('../views/mis/user.vue'),
                meta: {
                    isTab: true,
                    title: '用户管理',
                },
            },
        ],
    },
];
