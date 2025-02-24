<style scoped>
.layout{
    border: 1px solid #c693eb;
    background: hsl(275, 26%, 91%);
    position: relative;
    border-radius: 4px;
    overflow: hidden;
}
.layout-logo{
    width: 80px;
    height: 50px;
    background: #7523ab;
    border-radius: 2px;
    float: left;
    position: relative;
    top: 5px;
    left: 50px;
}
.layout-top-sider{
    width:550px;
    left: 80px;
    margin-left: 670px;
    color: #fff;
}
.demo-split{
        height: 200px;
        border: 1px solid #dcdee2;
}
.demo-split-pane{
    padding: 10px;
}
.demo-split-pane.no-padding{
    height: 200px;
    padding: 0;
}
.Content {
    width: 100%;
    height: 100vh; /* 使用视口高度单位vh，确保高度铺满整个视口 */
}
</style>
<template>
    <div class="layout">
        <Layout>
            <Sider ref="side1" hide-trigger collapsible :collapsed-width="78" v-model="isCollapsed">
                <Menu :active-name="$route.path.split('/')[1]" theme="dark" width="auto"  :class="menuitemClasses">
                    <MenuItem name="userManagement" :to="{ name: 'userManagement' }">
                    <!-- Icon图标 -->
                        <Icon type="ios-people"></Icon>
                        Users
                    </MenuItem>
                    <MenuItem name="problemManagement" :to="{ name: 'problemManagement' }">
                        <Icon type="ios-navigate"></Icon>
                        Problems
                    </MenuItem>
                    <MenuItem name="contestManagement" :to="{ name: 'contestManagement' }">
                        <Icon type="ios-keypad"></Icon>
                        Contests
                    </MenuItem>
                    <MenuItem name="templateManagement" :to="{ name: 'templateManagement' }">
                        <Icon type="ios-paper"></Icon>
                        Templates
                    </MenuItem>
                    <MenuItem name="submissionManagement" :to="{ name: 'submissionManagement' }">
                        <Icon type="ios-stats"></Icon>
                        Submissions
                    </MenuItem>
                </Menu>
            </Sider>
            <Layout>
                <Header :style="{padding: 0}" class="layout-header-bar">
                    <Icon @click="collapsedSider" :class="rotateIcon" :style="{margin: '0 20px'}" type="md-menu" size="24" color="white"></Icon>
                    <div style="float: right; margin-right: 30px;">
                        <Icon @click="toPersonalInfo" type="md-person" size="24" color="white"></Icon>
                    </div>  
                </Header>
                <!-- 表单查询框，输入内容有username和email -->
                <Card style=" justify-content: center; align-items: center;">
                    <Form ref="searchForm" :model="searchForm" :rules="searchRules" label-width="15" inline>
                        <FormItem  prop="username">
                            <Input v-model="searchForm.username" placeholder="Username" @input="searchUser"></Input>
                        </FormItem>
                        <FormItem  prop="email">
                            <Input v-model="searchForm.email" placeholder="Email" @input="searchUser"></Input>
                        </FormItem>
                        <FormItem>
                            <Button type="primary" @click="searchUser">
                                <Icon type="md-search" size="25"></Icon>
                            </Button>
                        </FormItem>
                        <FormItem>
                            <Button type="default" @click="resetSearchForm">
                                <Icon type="md-refresh" size="25"></Icon>
                            </Button>
                        </FormItem>
                    </Form>
                </Card>
                <Card style=" justify-content: center; align-items: center;">
                    <Table stripe 
                        :columns="userTableColumns" 
                        :data="userTableData"
                        :loading="tableloading"
                        class="content-table"
                        height="620"
                        width="100%"
                        @on-selection-change="selectChange"
                        >
                        <template v-slot:id="{row}">
                            <span>{{ row.id }}</span>
                        </template>
                        <template v-slot:username="{row}">
                            <span>{{ row.username }}</span>
                        </template>
                        <template v-slot:email="{row}">
                            <span>{{ row.email }}</span>
                        </template>
                        <template v-slot:nickname="{row}">
                            <span>{{ row.nickname }}</span>
                        </template>
                        <template v-slot:gender="{row}">
                            <Icon v-if="row.gender === '男'" type="md-male"></Icon>
                            <Icon v-else-if="row.gender === '女'" type="md-female"></Icon>
                        </template>
                        <template v-slot:auth="{row}">
                            <span>
                                <Tag :color="USER_AUTH[row.auth].color">{{ row.auth}}</Tag>
                            </span>
                        </template>
                        <template v-slot:edit="{row}">
                            <span class="clickable" @click="onEditUser(row)">
                                <Icon type="md-create" size="25"></Icon>
                            </span>
                            <Divider type="vertical" />
                            <span class="clickable" @click="onChangePassword(row)">
                                <Icon type="md-lock" size="25"></Icon>
                            </span>
                        </template>
                    </Table>
                </Card>
                <div class="footer-tools">
                    <Button type="default" size="big" class="float-left footer-btn" @click="onAddUser" style="margin-right: 15px; margin-left:15px">
                        <Icon type="md-add" size="25"></Icon>
                    </Button>
                    <Button type="default" size="big" class="float-left footer-btn" @click="commitDeleteUser">
                        <Icon type="md-trash" size="25"></Icon>
                    </Button>
                </div>

                <!-- 点击Edit后，出现用户信息修改框 -->
                <Modal 
                    v-model="editUserModal"
                    :title="userForm.username"
                    :loading="loading"
                    @on-ok="commitEditUserForm">
                    <Form ref="editUserModal" :model="userForm" :rules="editRules">
                        <FormItem label="Username" prop="username">
                            <Input v-model="userForm.username" :placeholder="userForm.username"></Input>
                        </FormItem>
                        <FormItem label="Email" prop="email">
                            <Input v-model="userForm.email" :placeholder="userForm.email"></Input>
                        </FormItem>
                        <FormItem label="Nickname" prop="nickname">
                            <Input v-model="userForm.nickname" :placeholder="userForm.nickname"></Input>
                        </FormItem>
                        <FormItem label="Gender" prop="gender">
                            <RadioGroup v-model="userForm.gender">
                                <Radio :label="'男'"><Icon type="md-male"></Icon></Radio>
                                <Radio :label="'女'"><Icon type="md-female"></Icon></Radio>
                            </RadioGroup>
                        </FormItem>
                        <FormItem label="Authority" prop="auth">
                            <Select v-model="userForm.auth" >
                                <Option value="user">user</Option>
                                <Option value="admin">admin</Option>
                            </Select>
                        </FormItem>
                    </Form>
                </Modal>

                <!-- 点击Password后，出现修改密码框 -->
                <Modal
                    v-model="userPasswordModal"
                    title="Password"
                    :loading="loading"
                    @on-ok="commitChangeUserPassword">
                    <Form ref="userForm" :model="userForm" :rules="pwdRules" :label-width="80">
                        <FormItem label="Username">
                            <span>{{ userForm.username }}</span>
                        </FormItem>

                        <FormItem label="New password" prop="password">
                            <Input type="password" v-model="userForm.password" />
                        </FormItem>

                        <FormItem label="Confirm password" prop="passwordCheck">
                            <Input type="password" v-model="userForm.passwordCheck" />
                        </FormItem>
                    </Form>
                </Modal>

                <!-- 点击add按钮，出现添加用户框 -->
                <Modal
                    v-model="addUserModal"
                    title="Add User"
                    :loading="loading"
                    @on-ok="commitAddUser">
                    <Form ref="addUserModal" :model="userForm" :rules="addRules">
                        <FormItem label="Username" prop="username">
                            <Input v-model="userForm.username" />
                        </FormItem>
                        <FormItem label="Email" prop="email">
                            <Input v-model="userForm.email" />
                        </FormItem>
                        <FormItem label="Nickname" prop="nickname">
                            <Input v-model="userForm.nickname" />
                        </FormItem>
                        <FormItem label="Gender" prop="gender">
                            <RadioGroup v-model="userForm.gender">
                                <Radio :label="'男'"><Icon type="md-male"></Icon></Radio>
                                <Radio :label="'女'"><Icon type="md-female"></Icon></Radio>
                            </RadioGroup>
                        </FormItem>
                        <FormItem label="Authority" prop="auth">
                            <Select v-model="userForm.auth" :placeholder="userForm.auth">
                                <Option value="user">user</Option>
                                <Option value="admin">admin</Option>
                            </Select>
                        </FormItem>
                        <FormItem label="Password" prop="password">
                            <Input type="password" v-model="userForm.password" />
                        </FormItem>
                        <FormItem label="Confirm password" prop="passwordCheck">
                            <Input type="password" v-model="userForm.passwordCheck" />
                        </FormItem>
                    </Form>
                </Modal>
            </Layout>
        </Layout>
    </div>
    <Footer> </Footer>
</template>



<script>
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import Footer from '@/components/Footer.vue'
import {api} from '@/utils/api';
import {USER_AUTH} from '@/utils/constants';
import axios from 'axios'

export default {
    components: { Editor, Toolbar, Footer, USER_AUTH},
    data () {
        const validatePassCheck = (auth, value, callback) => {
            if (value !== this.userForm.password) {
                callback(new Error('Password dost not match'));
                console.log(value);
                console.log(this.userForm.password);
            } else {
                callback();
            }
        };
        return {
            // 表格列
            userTableColumns: [
                {type: 'selection', width: 60, align: 'center'},
                {title:'#', key:'id', maxWidth: 80},
                {title:'Username', slot:'username'},
                {title:'Email', slot:'email', key:'email'},
                {title:'Nickname', slot:'nickname'},
                {title:'Gender', slot:'gender'},
                {title:'Authority', slot:'auth',key:'auth'},
                { title: '\b', slot: 'edit', width:"auto"},
            ],
            // 编辑用户信息表单验证规则
            addRules: {
                username: [
                    { required: true, message: 'Username can not be empty', trigger: 'blur' },
                    { type: 'string', max: 30, message: 'Maximum 30 characters', trigger: 'blur' }
                ],
                email: [
                    { required: true, message: 'Email can not be empty', trigger: 'blur' },
                    { type: 'email', message: 'Invalid email address' }
                ],
                nickname: [
                    { type: 'string', max: 30, message: 'Maximum 30 characters', trigger: 'blur' }
                ],
                gender:[
                    { required: true, message: 'Please select the gender', trigger: 'change' }
                ],
                auth: [
                    { required: true, message: 'Please select the authority', trigger: 'change' }
                ],
                password: [
                    { required: true, trigger: 'blur', min: 6, max: 32, message: 'Password length is between 6 and 32' }
                ],
                passwordCheck: [
                    { required: true, validator: validatePassCheck, trigger: 'change' }
                ]
            },
            editRules:{
                username: [
                    { required: true, message: 'Username can not be empty', trigger: ['blur', 'change'] },
                    { type: 'string', max: 30, message: 'Maximum 30 characters', trigger: ['blur', 'change'] }
                ],
                email: [
                    { required: true, message: 'Email can not be empty', trigger: ['blur', 'change'] },
                    { type: 'email', message: 'Invalid email address' }
                ],
                nickname: [
                    { type: 'string', max: 100, message: 'Maximum 100 characters', trigger: ['blur', 'change'] }
                ],
                auth: [
                    { required: true, message: 'Please select the authority', trigger: 'change' }
                ],
                gender:[
                    { required: true, message: 'Please select the gender', trigger: 'change' }
                ]
            },
            pwdRules:{
                password: [
                    { required: true, trigger: 'blur', min: 6, max: 32, message: 'Password length is between 6 and 32' }
                ],
                passwordCheck: [
                    { required: true, validator: validatePassCheck, trigger: 'change' }
                ]
            },
            // 表格数据，后续从后端获取
            userTableData: [],
            // 编辑用户信息表单
            userForm: {
                userame:'',
                email:'',
                nickname:'',
                gender:'',
                auth:'',
            },
            // 搜索表单
            searchForm: {
                username: '',
                email: ''
            },
            // 搜索表单验证规则
            searchRules: {
                username: [
                    { required: false, message: 'Please input the username', trigger: 'blur' }
                ],
                email: [
                    { required: false, message: 'Please input the email', trigger: 'blur' }
                ]
            },
            // 选中的用户
            selectedUsers: [],
            // 侧边栏是否收缩
            isCollapsed: false,
            // 模态框加载状态
            loading: true,
            // 表格加载状态
            tableLoading: false,
            // 编辑用户信息模态框
            editUserModal: false,
            // 修改密码模态框
            userPasswordModal: false,
            // 添加用户模态框
            addUserModal: false,
        }
    },
    methods: {
        toPersonalInfo(){
            console.log('toPersonalInfo');
            this.$router.push({name: 'adminPersonInfo'});
        },
        // 侧边栏收缩
        collapsedSider () {
            this.$refs.side1.toggleCollapse();
        },
        // 选择用户
        selectChange: function (selection) {
            this.selectedUsers = selection;
        },
        // 编辑用户模态修改
        onEditUser(row){
            this.userForm = { ...row };
            this.editUserModal = true;
        },
        // 修改密码模态修改
        onChangePassword(row){
            this.userForm = { 
                ...row,
                password:'',
                passwordCheck:''
             };
            this.userPasswordModal = true;
        },
        // 添加用户模态修改
        onAddUser(){
            this.userForm = {
                username: '',
                email: '',
                nickname: '',
                gender: '',
                auth: '',
                password: '',
                passwordCheck: ''
            }
            this.addUserModal = true;
        },
        // 获取用户列表get
        async getUserList() {
            this.tableLoading = true;
            await api.getUserList()
                .then(response => {
                    this.userTableData = response
                    .filter(user => {
                        const usernameMatch = !this.searchForm.username || user.username.includes(this.searchForm.username);
                        const emailMatch = !this.searchForm.email || user.email.includes(this.searchForm.email);
                        return usernameMatch && emailMatch;
                    })
                    .sort((a, b) => a.id - b.id);
                })
                .catch(error => {
                    console.log(error);
                    // this.$Message.error('Failed to get user list data');
                })
                .finally(() => {
                    this.tableLoading = false;
                });
        },
        // 实时搜索用户
        searchUser() {
            this.getUserList(); // 重新获取用户列表
        },
        resetSearchForm() {
            this.searchForm = {
                username: '',
                email: ''
            };
            this.getUserList();
        },
        // 编辑用户信息函数
        commitEditUserForm(){
            this.$refs.editUserModal.validate(valid =>{
                if(valid){
                    // delete this.userForm.passwordCheck;
                    const data = { 
                        ...this.userForm,
                        password:'E'
                    };
                    api.updateUser(
                        data
                    ).then(() => {
                        this.$Message.success('Update user information successfully');
                        this.getUserList();
                        this.editUserModal = false;
                    }).catch(error => {
                        console.log(error);
                        // this.$Message.error('Failed to update user information');
                        this.loading = false;
                            this.$nextTick(() => {
                            this.loading = true;
                            });
                    });
                }
                else{
                    this.$Message.error('Please fill in the form correctly');
                    console.log(this.userForm);
                    this.loading = false;
                    this.$nextTick(() => {
                        this.loading = true;
                        });
                }
            })
        },
        // 修改密码函数
        commitChangeUserPassword(){
            this.$refs.userForm.validate(valid =>{
                if(valid){
                    // delete this.userForm.passwordCheck;
                    const data = { 
                        ...this.userForm,
                    };
                    api.updateUserPwd(
                        data
                    ).then(() => {
                        this.$Message.success('Update user information successfully');
                        this.getUserList();
                        this.userPasswordModal = false;
                    }).catch(error => {
                        console.log(error);
                        // this.$Message.error('Failed to update user information');
                        this.loading = false;
                            this.$nextTick(() => {
                            this.loading = true;
                            });
                    });
                }
                else{
                    this.$Message.error('Please fill in the form correctly');
                    console.log(this.userForm);
                    this.loading = false;
                    this.$nextTick(() => {
                        this.loading = true;
                        });
                }
            })
        },
        // commitChangeUserPassword(){
        //     this.$refs.userForm.validate(valid =>{
        //         if(valid){
        //             // delete this.userForm.passwordCheck;
        //             api.updateUserPwd({
        //                 data
        //             }).then(_ =>{
        //                 this.$Message.success('Password changed successfully');
        //                 this.userPasswordModal = false;
        //             }).catch(error => {
        //                 console.log(error);
        //                 this.$Message.error('Failed to change password');
        //                 this.loading = false;
        //                 this.$nextTick(() => {
        //                     this.loading = true;
        //                 });
        //             });
        //         }
        //         else{
        //             this.$Message.error('Please fill in the form correctly');
        //             this.loading = false;
        //             this.$nextTick(() => {
        //                 this.loading = true;
        //             });
        //         }
        //     })
        // },
        // 添加用户函数
        commitAddUser(){
            this.$refs.addUserModal.validate(valid =>{
                if(valid){
                    // delete this.userForm.passwordCheck;
                    const data = { ...this.userForm };
                    api.addUser(
                        data
                    ).then(() => {
                        this.$Message.success('Add user successfully');
                        this.getUserList();
                        this.addUserModal = false;
                    }).catch(error => {
                        console.log(error);
                        // this.$Message.error('Failed to add user');
                        this.loading = false;
                        this.$nextTick(() => {
                            this.loading = true;
                        });
                    });
                }
                else{
                    this.$Message.error('Please fill in the form correctly');
                    this.loading = false;
                    this.$nextTick(() => {
                        this.loading = true;
                    });
                }
            })
        },
        // 删除用户函数
        commitDeleteUser(){
            console.log(localStorage.getItem('token'));
            if (this.selectedUsers.length === 0) {
                this.$Message.error('No selected users');
            } 
            else {
                const data = this.selectedUsers.map(o => o.id);
                this.$Modal.confirm({
                    title: 'Confirm',
                    content: `Delete ${this.selectedUsers.length} selected users?`,
                    loading: true,
                    onOk: () => {
                        axios.defaults.headers.delete['Content-Type']='application/json';
                        api.deleteUser(
                            JSON.stringify(data)
                        ).then(_ => {
                            this.$Message.success('Success');
                            this.getUserList();
                            // 停顿
                        }).catch(err => {
                            // this.$Message.error(err.message);
                        }).finally(() => {
                            this.selectedUsers = [];
                            this.$Modal.remove();
                        });
                        axios.defaults.headers.delete['Content-Type']='';
                    }
                });
                // let dataArray = this.selectedUsers.map(o => o.id);
                // console.log(this.selectedUsers.map(o => o.id))
                // dataArray.forEach((item) => {
                //     console.log(item);
                //     const data = { id: item };
                //     this.$Modal.confirm({
                //     title: 'Confirm',
                //     content: `Delete ${this.selectedUsers.length} selected users?`,
                //     loading: true,
                //     onOk: () => {
                //         api.deleteUser(
                //             new URLSearchParams(data)
                //         ).then(_ => {
                //             this.$Message.success('Success');
                //             this.getUserList();
                //             // 停顿
                //         }).catch(err => {
                //             this.$Message.error(err.message);
                //         }).finally(() => {
                //             this.selectedUsers = [];
                //             this.$Modal.remove();
                //         });
                //     }
                //     });
                // });
            }
        },
    },
    computed:{
        USER_AUTH: () => USER_AUTH,
        // 侧边栏收缩   
        rotateIcon () {
            return [
                'menu-icon',
                this.isCollapsed ? 'rotate-icon' : ''
            ];
        },
        // 侧边栏收缩
        menuitemClasses () {
            return [
                'menu-item',
                this.isCollapsed ? 'collapsed-menu' : ''
            ]
        },
        // 过滤后的数据
        filteredData() {
            return this.userTableData.filter(user => {
                const usernameMatch = !this.searchForm.username || user.username.includes(this.searchForm.username);
                const emailMatch = !this.searchForm.email || user.email.includes(this.searchForm.email);
                return usernameMatch && emailMatch;
            });
        },
    },
    created: function(){
        // 进入页面获取用户列表
        this.getUserList();
    },
    // 监听路由变化，重新获取用户列表
    watch:{
        $route: function(){
            this.getUserList();
        }
    }
}
</script>