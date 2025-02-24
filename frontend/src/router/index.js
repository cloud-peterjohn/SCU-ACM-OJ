import axios from 'axios'
import { createRouter, createWebHistory } from 'vue-router'
import { Message } from 'view-ui-plus'
//用户
import Home from '@/components/Home.vue'
import Login from '@/components/Login.vue'
import MainPage from '@/components/CategorizedExercises.vue'
import LoginSuccess from '@/components/LoginSuccess.vue'
import HardBasedExercises from '@/components/HardBasedExercises.vue'
import History from '@/components/History.vue'
import LoginFailure from '@/components/LoginFailure.vue'
import PersonInfo from '@/components/PersonInfo.vue'
import AdminLogin from '@/components/AdminLogin.vue'
import Register from '@/components/Register.vue'
import ResetPassword from '@/components/ResetPassword.vue'
import ProblemPage from '@/components/ProblemPage.vue'
import UserBoard from '@/components/UserBoard.vue'

// 管理员
import AdminNav from '@/components/AdminNav.vue'
import AdminUser from '@/components/AdminUser.vue'
import AdminProblem from '@/components/AdminProblem.vue'
import AdminContest from '@/components/AdminContest.vue'
import AdminTemplate from '@/components/AdminTemplate.vue'
import AdminSubmission from '@/components/AdminSubmission.vue'
import AdminPersonInfo from '@/components/AdminPersonInfo.vue'
import AdminContestDetail from '@/components/AdminContestDetail.vue'
import AdminResetPassword from '@/components/AdminResetPassword.vue'

const routes = [
    {
        path: '/',
        name: 'home',
        component: Home
    },
    {
        path: '/login',
        name: 'login',
        component: Login
    },
    {
        path: '/contest_list',
        name: 'categorized_exercises',
        component: MainPage,
        meta: { requiresUser: true }
    },
    {
        path: '/success',
        name: 'success',
        component: LoginSuccess
    },
    {
        path: '/contest_details',
        name: 'hard_based_exercises',
        component: HardBasedExercises,
        meta: { requiresUser: true }
    },
    {
        path: '/history',
        name: 'history',
        component: History,
        meta: { requiresUser: true }
    },
    {
        path: '/failure',
        name: 'failure',
        component: LoginFailure
    },
    {
        path: '/person_info',
        name: 'person_info',
        component: PersonInfo,
        meta: { requiresUser: true }
    },
    {
        path: '/user_board/:contestId',
        name: 'user_board',
        component: UserBoard,
        meta: { requiresUser: true }
    },
    {
        path: '/admin_login',
        name: 'admin_login',
        component: AdminLogin
    },
    {
        path: '/register',
        name: 'register',
        component: Register
    },
    {
        path: '/reset_password',
        name: 'reset_password',
        component: ResetPassword
    },
    {
        path: '/problem_page',
        name: 'problem_page',
        component: ProblemPage,
        meta: { requiresUser: true }
    },


    //管理员部分
    {
        path: '/admin_nav',
        name: 'admin_nav',
        component: AdminNav,
        meta: { requiresAdmin: true }
    },
    {
        path: '/userManagement',
        name: 'userManagement',
        component: AdminUser,
        meta: { requiresAdmin: true }
    },
    {
        path: '/problemManagement',
        name: 'problemManagement',
        component: AdminProblem,
        meta: { requiresAdmin: true }
    },
    {
        path: '/contestManagement',
        name: 'contestManagement',
        component: AdminContest,
        meta: { requiresAdmin: true }
    },
    {
        path: '/templateManagement',
        name: 'templateManagement',
        component: AdminTemplate,
        meta: { requiresAdmin: true }
    },
    {
        path: '/submissionManagement',
        name: 'submissionManagement',
        component: AdminSubmission,
        meta: { requiresAdmin: true }
    },
    {
        path: '/adminPersonInfo',
        name: 'adminPersonInfo',
        component: AdminPersonInfo,
        meta: { requiresAdmin: true }
    },
    {
        path: '/contestDetail/:contestId',
        name: 'adminContestDetail',
        component: AdminContestDetail,
        meta: { requiresAdmin: true }
    },
    {
        path: '/adminResetPassword',
        name: 'adminResetPassword',
        component: AdminResetPassword,
    }
]

const router = createRouter({
    routes,
    history: createWebHistory(process.env.BASE_URL),
    scrollBehavior() {
        return { top: 0 }
    }
})

router.beforeEach((to, from, next) => {
    const username = localStorage.getItem('username');
    // 如果用户名为空，说明未登录
    if (to.matched.some(record => record.meta.requiresAdmin)) {
        if (username == '') {
            next({ name: 'admin_login' });
        } else {
            axios.post('/api/user/isAdminLogin', 
                new URLSearchParams({
                    username: username,
                    token: localStorage.getItem('token')
                })
            )
                .then(res => {
                    console.log(res);
                    if (res.data.code == 0) {
                        next();
                    } else {
                        Message.error('请先登录')
                        next({ name: 'admin_login' });
                    }
                })
                .catch(err => {
                    console.log(err);
                    next({ name: 'admin_login' });
                });
        }
    } else if (to.matched.some(record => record.meta.requiresUser)) {
        if (username == '') {
            next({ name: 'login' });
        } else {

            axios.post('/api/user/isUserLogin', new URLSearchParams({
                username: username,
                token: localStorage.getItem('token')
            }))
                .then(res => {
                    console.log(res);
                    if (res.data.code == 0) {
                        next();
                    } else {
                        Message.error('请先登录')
                        next({ name: 'login' });
                    }
                })
                .catch(err => {
                    console.log(err);
                    next({ name: 'login' });
                });
        }
    } else {
        next(); // Always call next() to proceed with the navigation
    }
});


export default router
