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
    .time::after {
        content: " ms\0A";
        white-space: pre;
    }
    .mem::after {
        content: " MB\0A";
        white-space: pre;
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
                <!-- 表单查询框，输入内容为ProblemID和Title -->
                <Card style=" justify-content: center; align-items: center;">
                    <Form ref="searchForm" :model="searchForm" :rules="searchRules" label-width="15" inline>
                        <FormItem  prop="id">
                            <Input v-model.number="searchForm.id" placeholder="ID" @input="searchProblem"></Input>
                        </FormItem>
                        <FormItem prop="title">
                            <Input v-model="searchForm.title" placeholder="Title" @input="searchProblem"></Input>
                        </FormItem>
                        <FormItem>
                            <Button type="primary" @click="searchProblem">
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
                    <!-- 表格数据 -->
                    <Table stripe
                        :columns="problemTableColumns"
                        :data="problemTableData"
                        :loading="tableLoading"
                        class="content-table"
                        height="620"
                        width="100%"
                        @on-selection-change="selectChange"
                        >
                        <template v-slot:id="{row}">
                            <span>{{ row.id }}</span>
                        </template>
                        <template v-slot:title="{row}">
                            <span>{{ row.title }}</span>
                        </template>
                        <template v-slot:memoryLimit="{row}">
                            <span class="mem">{{ row.memoryLimit || 0}}</span>
                        </template>
                        <template v-slot:timeLimit="{row}">
                            <span class="time">{{ row.timeLimit || 0}}</span>
                        </template>
                        <template v-slot:edit="{row}">
                            <span class="clickable" @click="onEditProblem(row)">
                                <Icon type="md-create" size="25"></Icon>
                            </span>
                            <Divider type="vertical" />
                            <span class="clickable" @click="onUploadFile(row)">
                                <Icon type="md-cloud-upload" size="25"></Icon>
                            </span>
                        </template>
                    </Table>
                    <div class="footer-tools">
                        <Button type="default" size="big" class="float-left footer-btn" @click="onAddProblem" style="margin-right: 15px; margin-left:15px">
                            <Icon type="md-add" size="25"></Icon>
                        </Button>
                        <Button type="default" size="big" class="float-left footer-btn" @click="commitDeleteProblem">
                            <Icon type="md-trash" size="25"></Icon>
                        </Button>
                    </div>
                </Card>
                
                <!-- 点击Add添加题目模态框 -->
                <Modal
                    v-model="addProblemModal"
                    title="Add Problem"
                    :loading="loading"
                    @on-ok="commitAddProblem">
                    <Form ref="addProblemModal" :model="problemForm" :rules="editRules">
                        <FormItem label="Title" prop="title">
                            <Input v-model="problemForm.title" ></Input>
                        </FormItem>
                        <FormItem label="Memory Limit" prop="memoryLimit">
                            <Input type="number" v-model.number="problemForm.memoryLimit">
                                <template v-slot:suffix>
                                    <span>
                                        <h4>MB</h4>
                                    </span>
                                </template>
                            </Input>
                        </FormItem>
                        <FormItem label="Time Limit" prop="timeLimit">
                            <Input type="number" v-model.number="problemForm.timeLimit">
                                <template v-slot:suffix>
                                    <span>
                                        <h4>ms</h4>
                                    </span>
                                </template>
                            </Input>
                        </FormItem>
                    </Form>
                </Modal>
                <!-- 编辑题目 -->
                <Modal
                    v-model="editProblemModal"
                    :title="problemForm.title"
                    :loading="loading"
                    @on-ok="commitEditProblem">
                    <Form ref="editProblemModal" :model="problemForm" :rules="editRules">
                        <FormItem label="Title" prop="title">
                            <Input v-model="problemForm.title" :placeholder="problemForm.title"></Input>
                        </FormItem>
                        <FormItem label="Memory Limit" prop="memoryLimit">
                            <Input type="number" v-model.number="problemForm.memoryLimit">
                                <template v-slot:suffix>
                                    <span>
                                        <h4>MB</h4>
                                    </span>
                                </template>
                            </Input>
                        </FormItem>
                        <FormItem label="Time Limit" prop="timeLimit">
                            <Input type="number" v-model.number="problemForm.timeLimit">
                                <template v-slot:suffix>
                                    <span>
                                        <h4>ms</h4>
                                    </span>
                                </template>
                            </Input>
                        </FormItem>
                    </Form>
                </Modal>
                <!-- 上传文件 -->
                <Modal
                    v-model="uploadFileModal"
                    :title="uploadData.title"
                    :loading="loading">
                    <AdminUpload
                        ref="uploadFile"
                        :uploadData="uploadData"
                        :token="token"
                        :url="uploadUrl"
                        >
                    </AdminUpload>
                    <template v-slot:footer>
                        <Button @click="uploadFileModal = false" class="ivu-btn ivu-btn-text">Cancel</Button>
                        <Button @click="commitUploadFile" type="primary">Confirm</Button>
                    </template>
                </Modal>
            </Layout>
        </Layout>
    </div>
    <Footer></Footer>
</template>

<script>
import Footer from '@/components/Footer.vue'
import AdminUpload from '@/components/AdminUpload.vue'
import {api, storageurl} from '@/utils/api'
import axios from 'axios'
export default {
    components:{
    Footer, AdminUpload
    },
    data () {
        return {
            problemTableColumns:[
                {type: 'selection', width: 60, align: 'center'},
                {title:'#', key:'id', maxWidth: 80},
                {title:'Title', slot:'title'},
                {title:'Memory', slot:'memoryLimit', key:'memoryLimit'},
                {title:'Time', slot:'timeLimit', key:'timeLimit'},
                { title: '\b', slot: 'edit', width:"auto"},
                // {},
                // {},
            ],
            editRules:{
                title: [
                    { required: true, message: 'Please input the Title', trigger: 'blur' },
                    {type: 'string', min: 1, max: 50, message: 'The length of Title should be 1-50', trigger: 'blur'}
                ],
                memoryLimit: [
                    {required: true, type: 'number', min: 0, max: 524288, message: 'The length of Memory should be 0-524288', trigger: 'blur'}
                ],
                timeLimit: [
                    {required:true,type: 'number', min: 0, max: 10000, message: 'The length of Time should be 0-10000', trigger: 'blur'}
                ],
            },
            problemTableData: [
                //测试数据
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
            problemForm: {
                title: '',
                memoryLimit: '',
                timeLimit: '',
            },
            // 选中的题目
            selectedProblems: [],
            // 侧边栏是否收缩
            isCollapsed: false,
            // 模态框加载状态
            loading: true,
            // 表格加载状态
            tableLoading: false,
            // 编辑题目模态框
            editProblemModal: false,
            // 添加题目模态框
            addProblemModal: false,
            // 上传文件模态框
            uploadFileModal: false,
            // 上传参数
            token: 'Bearer ' + localStorage.getItem('token'),
            uploadUrl: storageurl.problem,
            uploadData: {
                id:'',
            },
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
        // 选择题目
        selectChange: function (selection) {
            this.selectedProblems = selection;
        },
        onSort: function(a, b) {
            return a.id - b.id;
        },
        // 添加题目
        onAddProblem() {
            this.problemForm = {
                title: '',
                memoryLimit: '',
                timeLimit: '',
            };
            this.addProblemModal= true;
        },
        // 编辑题目
        onEditProblem(row) {
            this.problemForm = { ...row };
            this.editProblemModal = true;
        },
        // 上传文件
        onUploadFile(row){
            this.uploadData = {
                id:row.id,
            }
            console.log(this.uploadData.title);
            this.uploadFileModal = true;
        },
        // 获取题目列表
        async getProblemList(){
            this.tableLoading = true;
            await api.getProblemList()
            .then(response => {
                this.problemTableData = response.
                filter(pro => {
                    const idMatch = !this.searchForm.id || pro.id === this.searchForm.id;
                    const titleMatch = !this.searchForm.title || pro.title.includes(this.searchForm.title);
                    return idMatch && titleMatch;
                })
                .sort((a, b) => a.id - b.id);
            }).catch(err => {
                console.log(err);
                // this.$Message.error('Falied to get problem list');
            }).finally(() => {
                this.tableLoading = false;
            });
        },
        // 实时搜索题目
        searchProblem() {
            this.getProblemList(); 
        },
        // 重置搜索表单
        resetSearchForm() {
            this.searchForm = {
                id:'',
                title:''
            };
            this.getProblemList();
        },
        // 执行上传题目
        commitAddProblem(){
            this.$refs.addProblemModal.validate((valid) => {
                if (valid) {
                    const data = { ...this.problemForm };
                    api.addProblem(
                        data
                    ).then(() => {
                        this.$Message.success('Add problem successfully');
                        this.getProblemList();
                        this.addProblemModal = false;
                    }).catch(error => {
                        console.log(error);
                        // this.$Message.error('Failed to add problem');
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
        // 执行编辑题目
        commitEditProblem(){
            this.$refs.editProblemModal.validate((valid) => {
                if (valid) {
                    const data = { ...this.problemForm };
                    api.updateProblem(
                        data
                    ).then(() => {
                        this.$Message.success('Edit problem successfully');
                        this.getProblemList();
                        this.editProblemModal = false;
                    }).catch(error => {
                        console.log(error);
                        // this.$Message.error('Failed to edit problem');
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
        // 删除题目
        commitDeleteProblem(){
            if(this.selectedProblems.length === 0){
                this.$Message.error('Please select the problem to delete');
                return;
            }
            else{
                const data = this.selectedProblems.map(pro => pro.id);
                this.$Modal.confirm({
                    title: 'Delete Problem',
                    content: 'Are you sure to delete the selected problems?',
                    onOk: () => {
                        axios.defaults.headers.delete['Content-Type']='application/json';
                        api.deleteProblem(
                            JSON.stringify(data)
                        )
                        .then(() => {
                            this.getProblemList();
                            this.selectedProblems = [];
                            this.$Message.success('Delete problem successfully');
                        }).catch(err => {
                            console.log(err);
                            // this.$Message.error('Failed to delete problem');
                        });
                        axios.defaults.headers.delete['Content-Type']='';
                    }
                });
                // let dataArray = this.selectedProblems.map(pro => pro.id);
                // dataArray.forEach((item) => {
                //     const data = {id: item};
                //     this.$Modal.confirm({
                //         title: 'Delete Problem',
                //         content: 'Are you sure to delete the selected problems?',
                //         onOk: () => {
                //             api.deleteProblem(
                //                 new URLSearchParams(data)
                //             )
                //             .then(() => {
                //                 this.getProblemList();
                //                 this.selectedProblems = [];
                //                 this.$Message.success('Delete problem successfully');
                //             }).catch(err => {
                //                 console.log(err);
                //                 this.$Message.error('Failed to delete problem');
                //             });
                //         }
                //     });
                // });
            }
        },
        // 上传文件
        commitUploadFile(){
            const status = this.$refs.uploadFile.uploadProblem();
            this.uploadFileModal = false;
            this.getProblemList();
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
        this.getProblemList();
    },
    // 监听路由变化，重新获取用户列表
    watch:{
        $route: function(){
            this.getProblemList();
        }
    },
}
</script>
