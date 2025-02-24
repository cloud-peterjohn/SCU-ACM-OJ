import { createApp } from 'vue'
import ViewUIPlus from 'view-ui-plus'
import App from './App.vue'
import router from './router'
import store from './store'
import locale from 'view-ui-plus/dist/locale/en-US';
import '../my-theme/index.less';
// import './mock'
import axios from 'axios'

const app = createApp(App)
app.config.globalProperties.$Message = ViewUIPlus.Message;
// // 添加请求拦截器进行调试
// axios.interceptors.request.use(function (config) {
//     console.log('Reauest Config:', config);
//     console.log('Request Headers:', config.headers);
//     console.log('Request URL:', config.url);
//     return config;
// }, function (error) {
//     return Promise.reject(error);
// });
// 修改为内网地址
axios.defaults.baseURL = 'http://47.108.171.45:8081';
// 设置全局默认的请求头
axios.defaults.headers.common['Authorization'] = localStorage.getItem('token');

app.use(store)
    .use(router)
    .use(ViewUIPlus)
    .mount('#app')
// 使用英文界面
app.use(ViewUIPlus, {
    locale
});
