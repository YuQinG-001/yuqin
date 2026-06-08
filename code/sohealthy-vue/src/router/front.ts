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
            {
                path: 'goods/:id',
                name: 'FrontGoods',
                component: () => import('../views/front/goods.vue'),
                props: true,
            },
            {
                path: 'goods_list',
                name: 'FrontGoodsList',
                component: () => import('../views/front/goods_list.vue'),
            },
            {
                path: 'customer',
                name: 'FrontCustomer',
                component: () => import('../views/front/customer.vue'),
                children: [
                    {
                        path: 'mine',
                        name: 'FrontCustomerMine',
                        component: () => import('../views/front/mine.vue'),
                    },
                    {
                        path: 'order_list',
                        name: 'FrontOrderList',
                        component: () => import('../views/front/order_list.vue'),
                    },
                    /*  {
                        path: 'order_list',
                        name: 'FrontAppointmentList',
                        component: () => import('../views/front/Appointment_List.vue'),
                    },
                    {
                        path: 'order_list',
                        name: 'FrontCustomerIm',
                        component: () => import('../views/front/FrontCustomerIm.vue'),
                    }, */
                ],
            },
        ],
    },
];
