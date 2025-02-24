<style scoped>
.layout{
    border: 1px solid #c693eb;
    background: hsl(275, 26%, 91%);
    position: relative;
    border-radius: 4px;
    overflow: hidden;
}
.layout .ivu-menu{
    z-index: 0
}
.layout-header-bar{
    background: #7523ab;
    box-shadow: 0 1px 1px rgba(0,0,0,.1);
}
.layout-logo-left{
    width: 90%;
    height: 30px;
    background: #5b6270;
    border-radius: 3px;
    margin: 15px auto;
}
.menu-icon{
    transition: all .3s;
}
.rotate-icon{
    transform: rotate(-90deg);
}
.menu-item span{
    display: inline-block;
    overflow: hidden;
    width: 69px;
    text-overflow: ellipsis;
    white-space: nowrap;
    vertical-align: bottom;
    transition: width .2s ease .2s;
}
.menu-item i{
    transform: translateX(0px);
    transition: font-size .2s ease, transform .2s ease;
    vertical-align: middle;
    font-size: 16px;
}
.collapsed-menu span{
    width: 0px;
    transition: width .2s ease;
}
.collapsed-menu i{
    transform: translateX(5px);
    transition: font-size .2s ease .2s, transform .2s ease .2s;
    vertical-align: middle;
    font-size: 22px;
}
.table-row-flex{
    display: flex;
    justify-content: center;
    align-items: center;
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
                <Card style=" justify-content: center; align-items: center;">
                    <Form ref="searchForm" :model="searchForm" :rules="searchRules" label-width="15" inline>
                        <FormItem  prop="id">
                            <Input v-model.number="searchForm.id" placeholder="ID" @input="searchContest"></Input>
                        </FormItem>
                        <FormItem prop="title">
                            <Input v-model="searchForm.title" placeholder="Title" @input="searchContest"></Input>
                        </FormItem>
                        <FormItem>
                            <Button type="primary" @click="searchContest">
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
                <Card style=" justify-content: center; align-items: center; height: 100vh;">
                   <Table stripe
                        :columns="contestTableColumns"
                        :data="contestTableData"
                        :loading="tableloading"
                        height="620"
                        width="100%"
                        class="content-table"
                        @on-selection-change="selectChange">
                        <template v-slot:id="{row}">
                            <span @click="viewContestDetails(row)">{{ row.id }}</span>
                        </template>
                        <template v-slot:title="{row}">
                            <span>{{ row.title }}</span>
                        </template>
                        <template v-slot:startTime="{row}">
                            <span>{{ row.startTime }}</span>
                        </template>
                        <template v-slot:endTime="{row}">
                            <span>{{ row.endTime }}</span>
                        </template>
                        <template v-slot:edit="{row}">
                            <span class="clickable" @click="onEditContest(row)">
                                <Icon type="md-create" size="25"></Icon>
                            </span>
                            <Divider type="vertical" />
                            <span class="clickable" @click="onAddContestUsers(row)">
                                <img src="../assets/personadd.svg" alt="Add User" style="width: 25px; height: 25px;">
                            </span>
                            <Divider type="vertical" />
                            <span class="clickable" @click="onAddContestUserList(row)">
                                <img src="../assets/peopleadd.svg" alt="Add User List" style="width: 25px; height: 25px;">
                            </span>
                            <Divider type="vertical" />
                            <span class="clickable" @click="onDeleteContestUsers(row)">
                                <img src="../assets/personsub.svg" alt="Sub User" style="width: 25px; height: 25px;">
                            </span>
                            <Divider type="vertical" />
                            <span class="clickable" @click="onAddContestProblems(row)">
                                <img src="../assets/add.svg" alt="Add Problem" style="width: 25px; height: 25px;">
                            </span>
                            <Divider type="vertical" />
                            <span class="clickable" @click="onDeleteContestProblems(row)">
                                <img src="../assets/minus.svg" alt="SUb Problem" style="width: 25px; height: 25px;">
                            </span>
                            <Divider type="vertical" />
                            <span class="clickable" @click="onViewContest(row)">
                                <img src="../assets/view.svg" alt="View Contest" style="width: 25px; height: 25px;">
                            </span>
                        </template>
                        
                   </Table>
                    <div class="footer-tools">
                        <Button type="default" size="big" class="float-left footer-btn" @click="onAddContest" style="margin-right: 15px; margin-left:15px">
                            <Icon type="md-add" size="25"></Icon>
                        </Button>
                        <Button type="default" size="big" class="float-left footer-btn" @click="commitDeleteContest">
                            <Icon type="md-trash" size="25"></Icon>
                        </Button>
                    </div>
                </Card>

                <!-- 点击Add添加比赛模态框 -->
                <Modal
                    v-model="addContestModal"
                    title="Add Contest"
                    :loading="loading"
                    @on-ok="commitAddContest">
                    <Form ref="addContestModal" :model="contestForm" :rules="editRules" >
                        <FormItem label="Title" prop="title">
                            <Input v-model="contestForm.title" />
                        </FormItem>
                        <FormItem label="Start Time" prop="startTime">
                            <DatePicker 
                                type="datetime" 
                                format="yyyy-MM-dd HH:mm:ss" 
                                v-model="contestForm.startTime"
                                placeholder="Select start time" 
                                style="width: 400px"
                                :disabled-date="disabledStartDate"
                                :disabled-time="disabledStartTime"/>
                        </FormItem>
                        <FormItem label="End Time" prop="endTime">
                            <DatePicker 
                                type="datetime" 
                                format="yyyy-MM-dd HH:mm:ss" 
                                v-model="contestForm.endTime"
                                placeholder="Select end time" 
                                style="width: 400px"
                                :disabled-date="disabledEndDate"
                                :disabled-time="disabledEndTime"/>
                        </FormItem>
                    </Form>
                </Modal>
                <!-- 编辑比赛 -->
                <Modal
                    v-model="editContestModal"
                    :title="contestForm.title"
                    :loading="loading"
                    @on-ok="commitEditContest">
                    <Form ref="editContestModal" :model="contestForm" :rules="editRules">
                        <FormItem label="Title" prop="title">
                            <Input v-model="contestForm.title" :placeholder="contestForm.title"></Input>
                        </FormItem>
                        <FormItem label="Start Time" prop="startTime">
                            <DatePicker 
                                type="datetime" 
                                format="yyyy-MM-dd HH:mm:ss" 
                                v-model="contestForm.startTime"
                                :placeholder="contestForm.startTime" 
                                style="width: 400px"
                                :disabled-date="disabledStartDate"
                                :disabled-time="disabledStartTime"/>
                        </FormItem>
                        <FormItem label="End Time" prop="endTime">
                            <DatePicker 
                                type="datetime" 
                                format="yyyy-MM-dd HH:mm:ss" 
                                v-model="contestForm.endTime"
                                :placeholder="contestForm.startTime" 
                                style="width: 400px"
                                :disabled-date="disabledEndDate"
                                :disabled-time="disabledEndTime"/>
                        </FormItem>
                    </Form>
                </Modal>
                <!-- 添加用户 -->
                <Modal
                    v-model="addUserModal"
                    title="Add User"
                    :loading="loading"
                    @on-ok="commitAddContestUser">
                    <Form :model="contestForm">
                        <FormItem label="Contest Title">
                            <span>{{ contestForm.title }}</span>
                        </FormItem>
                    </Form>
                    <Row>
                        <Col span="15">
                            <Select v-model="userForm.selectObj" :placeholder="userForm.username" filterable>
                                <Option v-for="item in userTableData" :value="{id:item.id, username:item.username}" :key="item.id">{{ item.username }}</Option>
                            </Select>
                        </Col>
                    </Row>
                </Modal>
                <!-- 批量上传用户 -->
                <Modal v-model="showUploadModal" title="Upload User List">
                    <input type="file" accept=".xlsx, .xls" @change="handleFileChange" ref="fileInput" />
                    <!-- <Upload 
                        :show-upload-list="false" 
                        accept=".xlsx, .xls" 
                        @change="handleFileChange">
                        <Button type="primary">选择文件</Button>
                    </Upload> -->
                </Modal>
                <!-- 展示上传数据 -->
                <Modal v-model="showUploadTableModal" title="User List" @on-cancel="handleCancle">
                    <div class="upload-table">    
                        <Table stripe
                            :columns="uploadTableColumns"
                            :data="uploadTableData"
                            height="350"
                            width="100%"
                            class="content-table">
                            <template v-slot:username="{row}">
                                <span>{{ row.username }}</span>
                            </template>
                        </Table>
                    </div>
                    <template v-slot:footer>
                        <Button type="default" @click="handleCancle">取消</Button> 
                        <Button type="primary" @click="commitAddContestUserList">确认</Button> 
                    </template>
                </Modal>

                <!-- 删除用户 -->
                <Modal
                    v-model="deleteUserModal"
                    title="Delete User"
                    :loading="loading"
                    @on-ok="commitDeleteContestUser">
                    <Form :model="contestForm">
                        <FormItem label="Contest Title">
                            <span>{{ contestForm.title }}</span>
                        </FormItem>
                    </Form>
                    <Row>
                        <Col span="15">
                            <Select v-model="userForm.selectObj" :placeholder="userForm.username" filterable>
                                <Option v-for="item in userTableData" :value="{id:item.id, username:item.username}" :key="item.id">{{ item.username }}</Option>
                            </Select>
                        </Col>
                    </Row>
                </Modal>


                <!-- 添加题目 -->
                <Modal
                    v-model="addProblemModal"
                    title="Add Problem"
                    :loading="loading"
                    @on-ok="commitAddContestProblem">
                    <Form :model="contestForm">
                        <FormItem label="Contest Title">
                            <span>{{ contestForm.title }}</span>
                        </FormItem>
                    </Form>
                    <Row>
                        <Col span="15">
                            <Select v-model="problemForm.selectObj" :placeholder="problemForm.problemTitle" filterable>
                                <Option v-for="item in problemTableData" :value="{id:item.id, title:item.title}" :key="item.id">{{ item.title }}</Option>
                            </Select>
                        </Col>
                    </Row>
                </Modal>
                <!-- 删除题目 -->
                <Modal
                    v-model="deleteProblemModal"
                    title="Delete Problem"
                    :loading="loading"
                    @on-ok="commitDeleteContestProblem">
                    <Form :model="contestForm">
                        <FormItem label="Contest Title">
                            <span>{{ contestForm.title }}</span>
                        </FormItem>
                    </Form>
                    <Row>
                        <Col span="15">
                            <Select v-model="problemForm.selectObj" :placeholder="problemForm.problemTitle" filterable>
                                <Option v-for="item in problemTableData" :value="{id:item.id, title:item.title}" :key="item.id">{{ item.title }}</Option>
                            </Select>
                        </Col>
                    </Row>
                </Modal>
                <!-- 查看比赛 -->
                <Modal
                    v-model="viewContestModal"
                    title="View Contest"
                    width="460">
                    <Form :model="contestForm">
                        <FormItem label="Contest Title">
                            <span>{{ contestForm.title }}</span>
                        </FormItem>
                    </Form>
                    <Row class="table-row-flex">
                        <Col span="15">
                            <Table stripe
                                :columns="userTableColumns"
                                :data="userTableData"
                                height="350"
                                width="100%"
                                class="content-table">
                                <template v-slot:id="{row}">
                                    <span>{{ row.id }}</span>
                                </template>
                                <template v-slot:username="{row}">
                                    <span>{{ row.username }}</span>
                                </template>
                            </Table>
                        </Col>
                    </Row>
                    <br>
                    <br>
                    <Row class="table-row-flex">
                        <Col span="15">
                            <Table stripe
                                :columns="problemTableColumns"
                                :data="problemTableData"
                                height="350"
                                width="100%"
                                class="content-table">
                                <template v-slot:id="{row}">
                                    <span>{{ row.id }}</span>
                                </template>
                                <template v-slot:title="{row}">
                                    <span>{{ row.title }}</span>
                                </template>
                            </Table>
                        </Col>
                    </Row>
                </Modal>
            </Layout>
        </Layout>
    </div>
    <Footer></Footer>
</template>

<script>
import Footer from '@/components/Footer.vue'
import {api} from '@/utils/api'
import moment from 'moment-timezone'
import axios from 'axios'
import * as XLSX from 'xlsx'
export default {
    components:{
    Footer
    },
    data () {
        return {
            contestTableColumns:[
                {type:"selection", width: 60, align: "center"},
                {title:"#", key:"id", maxWidth: 80, slot:'id'},
                {title:"Title", slot:"title"},
                {title:"Start Time", slot:"startTime",key:"startTime"},
                {title:"End Time", slot:"endTime",key:"endTime"},
                {title:"Action", slot:"edit", key:"edit", width: "auto"},
            ],
            // 表单验证规则
            editRules:{
                title: [
                    { required: true, message: 'Please input the title', trigger: 'blur' }
                ],
                startTime: [
                    { type:'date', required: true, message: 'Please select the start time', trigger: 'change' },
                    { validator: this.validateStartTime, trigger: 'change' }
                ],
                endTime: [
                    { type:'date', required: true, message: 'Please select the end time', trigger: 'change' },
                    { validator: this.validateEndTime, trigger: 'change' }
                ]
            },
            contestTableData: [
                //加一条测试数据
            ],
            // 搜索表单
            searchForm: {
                id: '',
                title: ''
            },
            // 搜索表单验证规则
            searchRules: {
                id: [
                    { type:'number',required: false, message: 'Please input the id', trigger: 'blur' }
                ],
                title: [
                    { required: false, message: 'Please input the title', trigger: 'blur' }
                ]
            },
            contestForm: {
                id:'',
                title: '',
                startTime: '',
                endTime: '',
            },
            // 选中的比赛
            selectedContests: [],
            // 侧边栏是否收缩
            isCollapsed: false,
            // 模态框加载状态
            loading: true,
            // 表格加载状态
            tableLoading: false,
            // 编辑比赛模态框
            editContestModal: false,
            // 添加比赛模态框
            addContestModal: false,
            
            // 用户搜索
            userTableData: [],
            addUserModal: false,
            deleteUserModal: false,
            // 上传用户文件模态框
            showUploadModal: false,
            // 用户表格模态框
            showUploadTableModal: false,
            // 表格数据，后端获取
            userTableData: [],
            // 编辑用户信息表单
            userForm: {
                contestId:'',
                userId:'',
                username: '',
                selectObj: null
            },
            // 上传用户文件列表
            uploadTableColumns: [ 
                { title: 'Username', key: 'username' } 
            ],
            //  上传用户文件数据
            uploadTableData: [],

            // 题目搜索
            problemTableData: [],
            addProblemModal: false,
            deleteProblemModal: false,
            // 表格数据，后端获取   
            problemTableData: [],
            // 编辑题目信息表单
            problemForm: {
                contestId:'',
                contestTitle:'',
                problemId: '',
                problemTitle: '',
                selectObj: null
            },
            
            // 预览比赛题目和用户
            viewContestModal: false,
            userTableColumns:[
                {title:"#", slot:"id", key:"id"},
                {title:"Username", slot:"username", key:"username"},
            ],
            problemTableColumns:[
                {title:"#", slot:"id", key:"id"},
                {title:"Title", slot:"title", key:"title"},
            ]
        }
    },
    methods: {
        toPersonalInfo(){
            console.log('toPersonalInfo');
            this.$router.push({name: 'adminPersonInfo'});
        },
        // 获取已经选择的用户
        getContestSelectedUser(params){
            axios.defaults.headers.delete['Content-Type']='';
            api.getContestSelectedUser(
                params
            )
            .then(response => {
                this.userTableData = response
                .sort((a, b) => a.id - b.id);
                this.$Message.success('Get selected user list successfully');
            }).catch(err => {
                console.log(err);
                // this.$Message.error('Falied to get selected user list');
            });
        },
        // 获取已经选择的题目
        getContestSelectedProblem(params){
            axios.defaults.headers.delete['Content-Type']='';
            api.getContestSelectedProblem(
                params
            )
            .then(response => {
                this.problemTableData = response
                .sort((a, b) => a.id - b.id);
                this.$Message.success('Get selected problem list successfully');
            }).catch(err => {
                console.log(err);
                // this.$Message.error('Falied to get selected problem list');
            });
        },
        // 侧边栏收缩
        collapsedSider () {
            this.$refs.side1.toggleCollapse();
        },
        // 选择比赛
        selectChange: function (selection) {
            this.selectedContests = selection;
        },
        onSort: function(a, b) {
            return a.id - b.id;
        },
        // 添加比赛
        onAddContest() {
            this.contestForm = {
                title: '',
                startTime: '',
                endTime: ''
            };
            this.addContestModal= true;
        },
        // 编辑比赛
        onEditContest(row) {
            this.contestForm = { ...row };
            this.editContestModal = true;
        },
        // 添加用户
        onAddContestUsers(row) {
            this.getContestUserList();
            this.contestForm = {
                title: row.title,
            };
            this.userForm ={
                contestId: row.id,
                userId: '',
                username: '',
                selectObj: null
            }
            this.addUserModal = true;
        },
        // 批量上传用户
        onAddContestUserList(row){
            this.userForm = {
                contestTitle: row.title,
                userId: '',
                username: '',
                selectObj: null
            }
            this.showUploadModal = true;
            console.log('showUploadModal:', this.showUploadModal);
        },
        // 删除用户
        onDeleteContestUsers(row) {
            const params = {contestId:row.id};
            this.getContestSelectedUser(params);
            this.contestForm = {
                title: row.title,
            };
            this.userForm ={
                contestId: row.id,
                userId: '',
                username: '',
                selectObj: null
            }
            this.deleteUserModal = true;
        },
        // 添加题目
        onAddContestProblems(row) {
            this.contestForm = {
                title: row.title,
            };
            this.problemForm = {
                contestId: row.id,
                contestTitle: row.title,
                problemId: '',
                problemTitle: '',
                selectObj: null
            }
            this.getContestProblemList();
            this.addProblemModal = true;
        },
        // 删除题目
        onDeleteContestProblems(row) {
            const params = {contestId:row.id};
            this.getContestSelectedProblem(params);
            this.contestForm = {
                title: row.title,
            };
            this.problemForm = {
                contestId: row.id,
                contestTitle: row.title,
                problemId: '',
                problemTitle: '',
                selectObj: null
            }
            this.deleteProblemModal = true;
        },
        // 查看比赛
        onViewContest(row) {
            const data = {
                contestId:row.id
            }
            this.getContestSelectedUser(data);
            this.contestForm = {
                title: row.title,
            };
            this.getContestSelectedProblem(data);
            this.viewContestModal = true;
        },
        // 获取比赛列表
        async getContestList(){
            this.tableLoading = true;
            await api.getContestList()
            .then(response => {
                this.contestTableData = response.
                filter(pro => {
                    const idMatch = !this.searchForm.id || pro.id === this.searchForm.id;
                    const titleMatch = !this.searchForm.title || pro.title.includes(this.searchForm.title);
                    return idMatch && titleMatch;
                })
                .sort((a, b) => a.id - b.id);
            }).catch(err => {
                console.log(err);
                // this.$Message.error('Falied to get contest list');
            }).finally(() => {
                this.tableLoading = false;
            });
        },
        // 实时搜索比赛
        searchContest() {
            this.getContestList(); 
        },
        // 重置搜索表单
        resetSearchForm() {
            this.searchForm = {
                id:'',
                title:''
            };
            this.getContestList();
        },
        // 执行上传比赛
        commitAddContest(){
            this.$refs.addContestModal.validate((valid) => {
                if (valid) { 
                    const startTimeStr = moment(this.contestForm.startTime).tz('Asia/Shanghai').format('YYYY-MM-DD HH:mm:ss');
                    const endTimeStr = moment(this.contestForm.endTime).tz('Asia/Shanghai').format('YYYY-MM-DD HH:mm:ss');
                    const data = { 
                        title: this.contestForm.title,
                        startTimeStr: startTimeStr,
                        endTimeStr: endTimeStr
                     };
                    api.addContest(
                        data
                    ).then(() => {
                        this.$Message.success('Add contest successfully');
                        this.getContestList();
                        this.addContestModal = false;
                    }).catch(error => {
                        console.log(error);
                        // this.$Message.error('Failed to add contest');
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
            });
        },
        // 执行编辑比赛
        commitEditContest(){
            this.$refs.editContestModal.validate((valid) => {
                if (valid) {
                    const startTimeStr = moment(this.contestForm.startTime).tz('Asia/Shanghai').format('YYYY-MM-DD HH:mm:ss');
                    const endTimeStr = moment(this.contestForm.endTime).tz('Asia/Shanghai').format('YYYY-MM-DD HH:mm:ss');
                    const data = { 
                        id:this.contestForm.id,
                        title: this.contestForm.title,
                        startTimeStr: startTimeStr,
                        endTimeStr: endTimeStr
                     };
                    api.updateContest(
                        data
                    ).then(() => {
                        this.$Message.success('Edit contest successfully');
                        this.getContestList();
                        this.editContestModal = false;
                    }).catch(error => {
                        console.log(error);
                        // this.$Message.error('Failed to edit contest');
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
            });
        },
        // 删除比赛
        commitDeleteContest(){
            if(this.selectedContests.length === 0){
                this.$Message.error('Please select the contest to delete');
                return;
            }
            else{
                const data = this.selectedContests.map(pro => pro.id);
                this.$Modal.confirm({
                    title: 'Delete Contest',
                    content: 'Are you sure to delete the selected contests?',
                    onOk: () => {
                        axios.defaults.headers.delete['Content-Type']='application/json';
                        api.deleteContest(
                            JSON.stringify(data)
                        )
                        .then(() => {
                            this.getContestList();
                            this.selectedContests = [];
                            this.$Message.success('Delete contest successfully');
                        }).catch(err => {
                            console.log(err);
                            // this.$Message.error('Failed to delete contest');
                        });
                        axios.defaults.headers.delete['Content-Type']='';
                    }
                });
                // let dataArray = this.selectedContests.map(pro => pro.id);
                // dataArray.forEach(item =>{
                //     const data = {id:item}
                //     this.$Modal.confirm({
                //     title: 'Delete Contest',
                //     content: 'Are you sure to delete the selected contests?',
                //     onOk: () => {
                //         api.deleteContest(
                //             new URLSearchParams(data)
                //         )
                //         .then(() => {
                //             this.getContestList();
                //             this.selectedContests = [];
                //             this.$Message.success('Delete contest successfully');
                //         }).catch(err => {
                //             console.log(err);
                //             this.$Message.error('Failed to delete contest');
                //         });
                //     }
                //     });
                // })
            }
        },
        // 开始时间表单验证 
        validateStartTime(rule, value, callback) { 
            // const currentCSTTime = moment().tz('Asia/Shanghai').toDate(); 
            // const selectedCSTTime = moment(value).tz('Asia/Shanghai').toDate(); 
            // if (selectedCSTTime <= currentCSTTime) { 
            //     callback(new Error('Start time must be greater than the current time'));
            // } else { 
            //     callback(); 
            // } 
            callback();
        }, 
        // 结束时间表单验证 
        validateEndTime(rule, value, callback) { 
            const startTimeCST = moment(this.contestForm.startTime).tz('Asia/Shanghai').toDate(); 
            const endTimeCST = moment(value).tz('Asia/Shanghai').toDate(); 
            // if (endTimeCST <= startTimeCST || (endTimeCST - startTimeCST) < 60000) { 
            //     callback(new Error('End time must be at least 1 hour after start time')); 
            // } 
            if(endTimeCST <= startTimeCST){
                callback(new Error('End time must be greater than start time'));
            }
            else { 
                callback(); 
            }
        },
        // 日期限制
        disabledStartDate(date) { 
            return date && date < new Date(); 
        },
        disabledStartTime(time) {
            const now = new Date(); 
            const hours = []; 
            const minutes = []; 
            const seconds = []; 

            for (let i = 0; i < 60; i++) { 
                if (i < now.getMinutes()) minutes.push(i); 
                if (i < now.getSeconds()) seconds.push(i); 
            } 
            for (let i = 0; i < 24; i++) { 
                if (i < now.getHours()) hours.push(i); 
            } 
            
            return { 
                disabledHours: () => hours, 
                disabledMinutes: () => minutes, 
                disabledSeconds: () => seconds 
            };
        },
        disabledEndDate(date) { 
            if (!this.contestForm.startTime) return true; 
            return date && (date < new Date(this.contestForm.startTime) 
                || date <= new Date(this.contestForm.startTime).setHours(new Date(this.contestForm.startTime).getHours() + 1)); 
        },
        disabledEndTime(time) {
            const start = new Date(this.contestForm.startTime); 
            const end = new Date(start.getTime() + 3600000); // 1 hour later 
            const hours = []; const minutes = []; 
            const seconds = []; 

            for (let i = 0; i < 60; i++) { 
                if (i < end.getMinutes()) minutes.push(i); 
                if (i < end.getSeconds()) seconds.push(i); 
            } 
            for (let i = 0; i < 24; i++) { 
                if (i < end.getHours()) hours.push(i); 
            } 
            return { 
                disabledHours: () => hours, 
                disabledMinutes: () => minutes, 
                disabledSeconds: () => seconds 
            };
        },
        // 获取用户权限为user的列表
        getContestUserList(){
            api.getContestUserList()
            .then(response => {
                this.userTableData = response;
            }).catch(err => {
                console.log(err);
                // this.$Message.error('Falied to get user list');
            });
        },
        // 添加用户
        commitAddContestUser(){
            const data = {
                contestId: this.userForm.contestId,
                userId: this.userForm.userId,
            };
            api.addContestUser(
                data
            ).then(() => {
                this.$Message.success('Add user successfully');
                // this.getContestUserList();
                this.addUserModal = false;
            }).catch(err => {
                console.log(err);
                // this.$Message.error('Failed to add user');
                this.loading = false;
                this.$nextTick(() => {
                    this.loading = true;
                });
            });
        },
        // 删除用户
        commitDeleteContestUser(){
            // const data = {
            //     id: this.userForm.userId,
            // };
            let dataArray = this.userForm.userId;
            axios.defaults.headers.delete['Content-Type']='application/json';
            api.deleteContestUser(
                JSON.stringify(dataArray)
            ).then(() => {
                this.$Message.success('Delete user successfully');
                // this.getContestUserList();
                this.deleteUserModal = false;
            }).catch(err => {
                console.log(err);
                // this.$Message.error('Failed to delete user');
                this.loading = false;
                this.$nextTick(() => {
                    this.loading = true;
                });
            });
            axios.defaults.headers.delete['Content-Type']='';
        },

        // 题目列表
        getContestProblemList(){
            api.getContestProblemList()
            .then(response => {
                this.problemTableData = response;
            }).catch(err => {
                console.log(err);
                // this.$Message.error('Falied to get problem list');
            });
        },
        // 添加题目
        commitAddContestProblem(){
            const data = {
                problemId: this.problemForm.problemId,
                // problemTitle: this.problemForm.problemTitle,
                contestId: this.problemForm.contestId,
                // contestTitle: this.problemForm.contestTitle
            };
            api.addContestProblem(
                new URLSearchParams(data)
            ).then(() => {
                this.$Message.success('Add problem successfully');
                // this.getContestProblemList();
                this.addProblemModal = false;
            }).catch(err => {
                console.log(err);
                // this.$Message.error('Failed to add problem');
                this.loading = false;
                this.$nextTick(() => {
                    this.loading = true;
                });
            });
        },
        // 删除题目
        commitDeleteContestProblem(){
            // const data = {
            //     problemId: this.problemForm.problemId,
            //     // problemTitle: this.problemForm.problemTitle,
            //     // contestId: this.problemForm.contestId,
            //     // contestTitle: this.problemForm.contestTitle
            // };
            let dataArray = this.problemForm.problemId;
            axios.defaults.headers.delete['Content-Type']='application/json';
            api.deleteContestProblem(
                JSON.stringify(dataArray)
            ).then(() => {
                this.$Message.success('Delete problem successfully');
                // this.getContestProblemList();
                this.deleteProblemModal = false;
            }).catch(err => {
                console.log(err);
                // this.$Message.error('Failed to delete problem');
                this.loading = false;
                this.$nextTick(() => {
                    this.loading = true;
                });
            });
            axios.defaults.headers.delete['Content-Type']='';
        },
        // 进入比赛详情界面
        viewContestDetails(row){
            console.log(row.id);
            this.$router.push({
                name:'adminContestDetail',
                params:{contestId:row.id}
            });
        },
        // 上传文件
        handleFileChange(event) {
            const file = event.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = (e) => {
                    const data = e.target.result;
                    if (!data) {
                        this.$Message.error('Failed to read the file.');
                        return;
                    }
                    try {
                        const workbook = XLSX.read(data, { type: 'array' });
                        if (workbook.SheetNames.length === 0) {
                        this.$Message.error('No sheet found in the file');
                        return;
                        }
                        const sheetName = workbook.SheetNames[0];
                        const worksheet = workbook.Sheets[sheetName];
                        const jsonData = XLSX.utils.sheet_to_json(worksheet, { header: 1 });
                        if (jsonData.length > 1) {
                        this.uploadTableData = jsonData.slice(1).map((row) => ({
                            username: row[0] // 确保这里的键名与您的Excel文件中的列标题匹配
                        }));
                        // console.log("123",this.uploadTableData);
                        this.showUploadModal = false;
                        this.showUploadTableModal = true;
                        } else {
                        Message.error("No data found in the Excel file.");
                        }
                    } catch (err) {
                        console.error("Error reading Excel file:", err);
                        this.$Message.error('Failed to parse the file');
                    }
                    };
                reader.readAsArrayBuffer(file); // 使用readAsArrayBuffer代替readAsBinaryString
            } else {
                console.log("No file selected or file not supported");
            }
        },
        // 取消上传
        handleCancle() {
            this.showUploadTableModal = false;
            // 清除文件输入框
            this.$refs.fileInput.value = null;
        },
        // 确认上传文件
        commitAddContestUserList(){
            const data = this.uploadTableData.map((row) => row.username);
            data.unshift(this.userForm.contestTitle);
            // console.log("123",data);
            api.addContestUserList(
                data
            ).then(() => {
                this.$Message.success('Add user list successfully');
                this.showUploadTableModal = false;
                // 清除文件输入框
                this.$refs.fileInput.value = null;
            }).catch(err => {
                console.log(err);
                // this.$Message.error('Failed to add user list');
                // 清除文件输入框
                this.$refs.fileInput.value = null;
            });
        }
    },
    computed: {
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
    },
    created() {
        this.getContestList();
    },
    // 监听路由变化，重新获取列表
    watch:{
        $route: function(){
            this.getContestList();
        },
        'userForm.selectObj': function(newVal){
            if(newVal){
                this.userForm.userId = newVal.id;
                this.userForm.username = newVal.username;
            }
        },
        'problemForm.selectObj': function(newVal){
            if(newVal){
                this.problemForm.problemId = newVal.id;
                this.problemForm.problemTitle = newVal.title;
            }
        }
    },
}
</script>
