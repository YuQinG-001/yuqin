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
            {
                path: 'goods',
                name: 'MisGoods',
                component: () => import('../views/mis/goods.vue'),
                props: true,
                meta: {
                    isTab: true,
                    title: '体检套餐',
                },
            },
            {
                path: 'order',
                name: 'MisOrder',
                component: () => import('../views/mis/order.vue'),
                meta: {
                    title: '订单管理',
                    isTab: true,
                },
            },
            {
                path: 'rule',
                name: 'MisRule',
                component: () => import('../views/mis/rule.vue'),
                meta: {
                    title: '促销规则',
                    isTab: true,
                },
            },
            {
                path: 'customer_im',
                name: 'MisCustomerIm',
                component: () => import('../views/mis/customer_im.vue'),
                meta: {
                    title: '客服IM',
                    isTab: true,
                },
            },
            {
                path: 'appointment',
                name: 'MisAppointment',
                component: () => import('../views/mis/appointment.vue'),
                meta: {
                    title: '体检预约',
                    isTab: true,
                },
            },
            {
                path: 'customer_checkin',
                name: 'MisCustomerCheckin',
                component: () => import('../views/mis/customer_checkin.vue'),
                meta: {
                    title: '体检签到',
                    isTab: true,
                },
            },
            {
                path: 'doctor_checkup',
                name: 'MisDoctorCheckup',
                component: () => import('../views/mis/doctor_checkup.vue'),
                meta: {
                    title: '医生检查',
                    isTab: true,
                },
            },
        ],
    },
];
