// import Vue from 'vue'
import axios from 'axios'
import { Message } from 'view-ui-plus';

const storageurl = {
    'problem': '/api/admin/problem/upload',
}

// post请求
function post(url, data, options) {
    data = data || {};
    options = options || {};
    return new Promise((resolve, reject) => {
        axios.post(url, data, options)
        .then(response => {
            const contentType = response.headers['content-type'];
            if (contentType && contentType.indexOf('application/json') === -1) {
                resolve(response);
            } else if (response.data.code === 0) {
                console.log("success");
            // everything alright
                resolve(response.data);
            } else {
                Message.error(response.data.message);
                reject(response.data);
            }
        }, err => {
            // Vue.prototype.$Loading.finish();
            reject(err.response.data);
        })
    });
}
// get请求
function get(url, params, options) {
    params = params || {};
    options = options || {};
    return new Promise((resolve, reject) => {
        axios.get(url, {params, options})
        .then(response => {
            if (response.data.code === 0) {
                resolve(response.data.data);
            } else {
                Message.error(response.data.message);
                reject(response.data);
            }
        }, err => {
                // Vue.prototype.$Loading.finish();
                reject(err.response.data);
        })
    })
}
// delete请求
function del(url, data, options) {
    data = data || {};
    options = options || {};
    return new Promise((resolve, reject) => {
        axios.delete(url, {data, options})
        .then(response => {
            if (response.data.code === 0) {
                resolve(response.data.data);
            } else {
                Message.error(response.data.message);
                reject(response.data);
            }
        }, err => {
                // Vue.prototype.$Loading.finish();
                reject(err.response.data);
        })
    })
}
// put请求
function put(url, data, options) {
    data = data || {};
    options = options || {};
    return new Promise((resolve, reject) => {
        axios.put(url, data, options)
            .then(response => {
            if (response.data.code === 0) {
                resolve(response.data.data);
            } else {
                Message.error(response.data.message);
                // console.log("Test:\n",response.data);
                reject(response.data);
            }
        }, err => {
            // Vue.prototype.$Loading.finish();
            // console.log("Test:\n",err.response.data);
            reject(err.response.data);
        })
    })
}
// patch请求
function patch(url, data, options) {
    data = data || {};
    options = options || {};
    options.headers = options.headers || headers;
    console.log(options.headers);
    return new Promise((resolve, reject) => {
        axios.patch(url, data, options)
            .then(response => {
            if (response.data.code === 0) {
                resolve(response.data.data);
            } else {
                Message.error(response.data.message);
                reject(response.data);
            }
        }, err => {
            Vue.prototype.$Loading.finish();
            reject(err.response.data);
        })
    })
}

const api = {
    //----------------------管理员针对用户相关----------------------
    // 将admin去掉
    // 查询列表
    getUserList:function(){
        return get('/api/user/list');
    },
    // 更新用户
    updateUser:function(data){
        return post('/api/user/updateUser',data);
    },
    updateUserPwd:function(data){
        return post('/api/user/updateUserPwd', data);
    },
    // 增加用户
    addUser:function(data){
        return post('/api/user/add',data);
    },
    // 删除用户
    deleteUser:function(data){
        return del('/api/user/deleteListById',data);
    },
    //----------------------管理员针对题目相关----------------------
    // 查询列表
    getProblemList: function() {
        return get('/api/admin/problem/list');
    },
    // 更新题目
    updateProblem: function(data) {
        return put('/api/admin/problem/update', data);
    },
    // 增加题目
    addProblem: function(data) {
        return post('/api/admin/problem/add', data);
    },
    // 删除题目
    deleteProblem: function(data) {
        return del('/api/admin/problem/deleteList', data);
    },
    // 上传文件
    uploadProblem: function(data) {
        return post('/api/admin/problem/upload', data);
    },
    //----------------------管理员针对评测记录相关----------------------
    // 查询列表
    getSubmissionList: function() {
        return get('/api/JudgeRecord/list');
    },
    // ----------------------管理员针对评测模板相关----------------------
    // 查询列表
    getTemplateList: function() {
        return get('/api/JudgeTemplate/listUnConditional');
    },
    // 更新模板
    updateTemplate: function(data) {
        return put('/api/JudgeTemplate/update', data);
    },
    // 增加模板
    addTemplate: function(data) {
        return post('/api/JudgeTemplate/add', data);
    },
    // 删除模板
    deleteTemplate: function(data) {
        return del('/api/JudgeTemplate/deleteList', data);
    },
    //----------------------管理员针对比赛相关----------------------
    // 查询列表
    getContestList: function() {
        return get('/api/contest/list');
    },
    // 更新比赛
    updateContest: function(data) {
        return put('/api/contest/update', data);
    },
    // 增加比赛
    addContest: function(data) {
        return post('/api/contest/add', data);
    },
    // 删除比赛
    deleteContest: function(data) {
        return del('/api/contest/deleteList', data);
    },

    // 获取用户列表(权限为user)
    getContestUserList:function(){
        return get('/api/user/findByAuth');
    },
    // 添加用户
    addContestUser:function(data){
        return post('/api/contestRecord/add',data);
    },
    // 批量添加用户 
    addContestUserList:function(data){
        return post('/api/contestRecord/addByUserIdList',data);
    },
    // 删除用户
    deleteContestUser:function(data){
        return del('/api/contestRecord/deleteByUserId',data);
    },
    // 获取已经选择的用户
    getContestSelectedUser:function(params){
        return get('/api/contestRecord/userListHavingContest', params);
    },

    // 获取未绑定的题目列表
    getContestProblemList:function(){
        return get('/api/admin/problem/listNoBelong');
    },
    // 添加题目
    addContestProblem:function(data){
        return post('/api/contestProblem/addByProblemId',data);
    },
    // 删除题目
    deleteContestProblem:function(data){
        return del('/api/contestProblem/delete',data);
    },
    // 获取已经选择的题目
    getContestSelectedProblem:function(params){
        return get('/api/contestProblem/list', params);
    },
    // 获取比赛详情
    getContestDetail:function(params){
        return get('/api/BangDan/AllResultList', params);
    },
    // 获取比赛基本信息
    getContestFundamental:function(params){
        return get('/api/contest/ContestInfo', params);
    },
    // ----------------------管理员针对记登录相关----------------------
    // 登录
    login:function(data){
        // localStorage.removeItem('token');
        axios.defaults.headers.common['Authorization'] = '';
        return post('/api/user/adminLogin',data);
    },
}

export {api, storageurl};