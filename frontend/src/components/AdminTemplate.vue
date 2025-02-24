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
    .code-editor {
        height: 400px;
        width: 100%;
        border: 1px solid #e8e8e8;
        border-radius: 4px;
        margin-top: 20px;
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
                <!-- 表单查询框，输入内容为TemplateID和Language -->
                <Card style=" justify-content: center; align-items: center;">
                    <Form ref="searchForm" :model="searchForm" :rules="searchRules" label-width="15" inline>
                        <FormItem  prop="id">
                            <Input v-model.number="searchForm.id" placeholder="ID" @input="searchTemplate"></Input>
                        </FormItem>
                        <FormItem prop="title">
                            <Input v-model="searchForm.language" placeholder="Language" @input="searchTemplate"></Input>
                        </FormItem>
                        <FormItem>
                            <Button type="primary" @click="searchTemplate">
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
                        :columns="templateTableColumns"
                        :data="templateTableData"
                        :loading="tableloading"
                        height="620"
                        width="100%"
                        class="content-table"
                        @on-selection-change="selectChange">
                        <template v-slot:id="{row}">
                            <span>{{ row.id }}</span>
                        </template>
                        <template v-slot:language="{row}">
                            <span>{{ row.language }}</span>
                        </template>
                        <template v-slot:comment="{row}">
                            <span>{{ row.comment }}</span>
                        </template>
                        <template v-slot:edit="{row}">
                            <span class="clickable" @click="onEditTemplate(row)">
                                <Icon type="md-create" size="25"></Icon>
                            </span>
                        </template>
                    </Table>
                    <div class="footer-tools">
                        <Button type="default" size="big" class="float-left footer-btn" @click="onAddTemplate" style="margin-right: 15px; margin-left:15px">
                            <Icon type="md-add" size="25"></Icon>
                        </Button>
                        <Button type="default" size="big" class="float-left footer-btn" @click="commitDeleteTemplate">
                            <Icon type="md-trash" size="25"></Icon>
                        </Button>
                    </div>
                </Card>
                <!-- 点击Add添加模板模态框 -->
                <Modal
                    v-model="addTemplateModal"
                    title="Add Template"
                    :loading="loading"
                    width="60%"
                    @on-ok="commitAddTemplate">
                    <Form ref="addTemplateModal" :model="templateForm" :rules="editRules">
                        <FormItem label="Language" prop="language">
                            <Input v-model="templateForm.language" ></Input>
                        </FormItem>
                        <FormItem label="Comment" prop="comment">
                            <Input v-model="templateForm.comment" ></Input>
                        </FormItem>
                        <FormItem prop="description">
                            <br>
                            <Codemirror
                                v-model="templateForm.description"
                                :options="cmOptions"
                                
                                class="editor-container"
                            >
                            </Codemirror>
                        </FormItem>
                    </Form>
                </Modal>
                <!-- 编辑模板 -->
                <Modal
                    v-model="editTemplateModal"
                    :title="templateForm.language"
                    :loading="loading"
                    width="60%"
                    @on-ok="commitEditTemplate">
                    <Form ref="editTemplateModal" :model="templateForm" :rules="editRules">
                        <FormItem label="Language" prop="language">
                            <Input v-model="templateForm.language" :placeholder="templateForm.language"></Input>
                        </FormItem>
                        <FormItem label="Comment" prop="comment">
                            <Input v-model="templateForm.comment" :placeholder="templateForm.comment"></Input>
                        </FormItem>
                        <FormItem prop="description">
                            <br>
                            <Codemirror
                                v-model="templateForm.description"
                                :options="cmOptions"
                                required
                                class="editor-container"
                            >
                            </Codemirror>
                        </FormItem>
                    </Form>
                </Modal>
            </Layout>
        </Layout>
    </div>
    <Footer></Footer>
</template>

<script>
import Footer from '@/components/Footer.vue'
import {api} from '@/utils/api'
import axios from 'axios'
import { Codemirror } from 'vue-codemirror';
import 'codemirror/lib/codemirror.css';
import 'codemirror/theme/cobalt.css';
import 'codemirror/mode/clike/clike.js'

export default {
    components:{
    Footer, Codemirror
    },
    data () {
        return {
            cmOptions: {
                mode: 'text/x-c++src',
                theme:'monokia',
                lineNumbers: true,
                autoCloseBrackets: true,
                matchBrackets: true,
                showCursorWhenSelecting: true,
                tabSize: 2,
                extraKeys: {
                    'Tab': (cm) => {
                        const spaces = Array(cm.getOption('indentUnit') + 1).join(' ');
                        cm.replaceSelection(spaces);
                    }
                }
            },
            templateTableColumns:[
                {type: 'selection', width: 60, align: 'center'},
                {title:'#', key:'id', maxWidth: 80},
                {title:'Language', slot:'language', key:'language'},
                {title:'Comment', slot:'comment',key:'comment'},
                { title: '\b', slot: 'edit', width:"auto"},
                // {},
                // {},
            ],
            editRules:{
                language: [
                    { required: true, message: 'Please input the Language', trigger: 'blur' },
                    {type:'string', max:100, min:1, message: 'The length of the Language should be between 1 and 100 characters', trigger: 'blur'}
                ],
                comment:[
                    {required:true,type:'string', max:150, min:1, message: 'The length of the Comment should be between 1 and 150 characters', trigger: 'blur'}
                ],
                description:[
                    {required:true, message: 'Please input the Script', trigger: 'blur'}
                ],
            },
            templateTableData: [
                //测试数据
                {id: 1, language: 'C++', comment:'C++14',description: 'C++模板'},
            ],
            searchForm: {
                id: '',
                language: '',
            },
            searchRules: {
                id: [
                    { type:'number',required: false, message: 'Please input the ID', trigger: 'blur' },
                ],
                language: [
                    { required: false, message: 'Please input the Title', trigger: 'blur' },
                ],
            },
            templateForm: {
                language: '',
                comment:'',
                description: '',
            },
            // 选中的模板
            selectedTemplates: [],
            // 侧边栏收缩
            isCollapsed: false,
            // 表格数据
            tableloading: false,
            // 模态框加载状态
            loading: true,
            // 表格加载状态
            tableLoading: false,
            // 编辑模板信息模态框
            editTemplateModal: false,
            // 添加模板模态框
            addTemplateModal: false,
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
        // 查询模板
        selectChange: function (selection) {
            this.selectedTemplates = selection;
        },
        onSort: function(a, b) {
            return a.id - b.id;
        },
        // 添加模板
        onAddTemplate() {
            this.templateForm = {
                language: '',
                comment:'',
                description: '',
            };
            this.addTemplateModal= true;
        },
        // 编辑模板
        onEditTemplate(row) {
            this.templateForm = { ...row };
            this.editTemplateModal = true;
        },
        // 获取模板列表
        async getTemplateList(){
            this.tableLoading = true;
            await api.getTemplateList()
                .then(response => {
                    this.templateTableData = response
                    .filter(template => {
                        const idMatch = !this.searchForm.id || template.id === this.searchForm.id;
                        const languageMatch = !this.searchForm.language || template.language.includes(this.searchForm.language);
                        return idMatch && languageMatch;
                    })
                    .sort((a, b) => a.id - b.id);
                })
                .catch(error => {
                    console.log(error);
                    // this.$Message.error('Failed to get template list data');
                })
                .finally(() => {
                    this.tableLoading = false;
                });
        },
        // 实时搜索模板
        searchTemplate() {
            this.getTemplateList(); // 重新获取模板列表
        },
        // 重置搜索表单
        resetSearchForm() {
            this.searchForm = {
                id:'',
                title:''
            };
            this.getTemplateList();
        },
        // 执行上传模板
        commitAddTemplate(){
            this.$refs.addTemplateModal.validate((valid) => {
                if (valid) {
                    const data = { ...this.templateForm };
                    console.log('Mock',data);
                    api.addTemplate(
                        data
                    ).then(() => {
                        this.$Message.success('Add template successfully');
                        this.getTemplateList();
                        this.addTemplateModal = false;
                    }).catch(error => {
                        console.log(error);
                        // this.$Message.error('Failed to add template');
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
        // 执行编辑模板
        commitEditTemplate(){
            this.$refs.editTemplateModal.validate((valid) => {
                if (valid) {
                    const data = { ...this.templateForm };
                    console.log('Mock',data);
                    api.updateTemplate(
                        data
                    ).then(() => {
                        this.$Message.success('Edit template successfully');
                        this.getTemplateList();
                        this.editTemplateModal = false;
                    }).catch(error => {
                        console.log(error);
                        // this.$Message.error('Failed to edit template');
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
        // 删除模板
        commitDeleteTemplate(){
            if(this.selectedTemplates.length === 0){
                this.$Message.error('Please select the template to delete');
                return;
            }
            else{
                const data = this.selectedTemplates.map(pro => pro.id);
                this.$Modal.confirm({
                    title: 'Delete Template',
                    content: 'Are you sure to delete the selected templates?',
                    onOk: () => {
                        axios.defaults.headers.delete['Content-Type']='application/json';
                        api.deleteTemplate(
                            JSON.stringify(data)
                        )
                        .then(() => {
                            this.getTemplateList();
                            this.selectedTemplates = [];
                            this.$Message.success('Delete template successfully');
                        }).catch(err => {
                            console.log(err);
                            // this.$Message.error('Failed to delete template');
                        });
                        axios.defaults.headers.delete['Content-Type']='application/json';
                    }
                    });
                // let dataArray = this.selectedTemplates.map(pro => pro.id);
                // dataArray.forEach((item) => {
                //     console.log(item);
                //     const data = { id: item };
                //     this.$Modal.confirm({
                //     title: 'Delete Template',
                //     content: 'Are you sure to delete the selected templates?',
                //     onOk: () => {
                //         api.deleteTemplate(
                //             new URLSearchParams(data)
                //         )
                //         .then(() => {
                //             this.getTemplateList();
                //             this.selectedTemplates = [];
                //             this.$Message.success('Delete template successfully');
                //         }).catch(err => {
                //             console.log(err);
                //             this.$Message.error('Failed to delete template');
                //         });
                //     }
                //     });
                // });
            }
        },
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
        }
    },
    created: function(){
        // 进入页面获取模板列表
        this.getTemplateList();
    },
    // 监听路由变化，重新获取模板列表
    watch:{
        $route: function(){
            this.getTemplateList();
        }
    }
}
</script>
