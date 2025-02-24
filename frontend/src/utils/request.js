import axios from 'axios';

const baseURL = 'http://localhost:8080';

const instance = axios.create({
  withCredentials: true,
  baseURL: baseURL,
});


// // 添加请求拦截器进行调试
// axios.interceptors.request.use(function (config) {
//   console.log('Request Headers:', config.headers);
//   return config;
// }, function (error) {
//   return Promise.reject(error);
// });

export default instance;