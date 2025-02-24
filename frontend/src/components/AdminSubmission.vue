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
                <!-- 表单查询框，输入内容有username、problem_id和judge_result -->
                <Card style=" justify-content: center; align-items: center;">
                    <Form ref="searchForm" :model="searchForm" :rules="searchRules" label-width="15" inline>
                        <FormItem  prop="problemTitle">
                            <Input v-model="searchForm.problemTitle" placeholder="Problem" @input="searchSubmission"></Input>
                        </FormItem>
                        <FormItem  prop="username">
                            <Input v-model="searchForm.username" placeholder="Username" @input="searchSubmission"></Input>
                        </FormItem>
                        <FormItem  prop="judgeResult">
                            <Input v-model="searchForm.judgeResult" placeholder="Result" @input="searchSubmission"></Input>
                        </FormItem>
                        <FormItem>
                            <Button type="primary" @click="searchSubmission">
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
                        :columns="submissionTableColumns"
                        :data="submissionTableData"
                        :loading="tableloading"
                        height="620"
                        width="100%"
                        class="content-table">
                        <template v-slot:id="{row}">
                            <span>{{ row.id }}</span>
                        </template>
                        <template v-slot:problemTitle="{row}">
                            <span>{{ row.problemTitle }}</span>
                        </template>
                        <template v-slot:username="{row}">
                            <span>{{ row.username }}</span>
                        </template>
                        <template v-slot:templateId="{row}">
                            <span>{{ row.templateId }}</span>
                        </template>
                        <template v-slot:runMemory="{row}">
                            <span class="mem">{{ row.runMemory}}</span>
                        </template>
                        <template v-slot:runTime="{row}">
                            <span class="time">{{ row.runTime || 0}}</span>
                        </template>
                        <template v-slot:judgeTime="{row}">
                            <span>{{ row.judgeTime }}</span>
                        </template>
                        <template v-slot:judgeResult="{row}">
                            <span>
                                <Tag :color="JUDGE_RESULT[row.judgeResult]?.color">{{ row.judgeResult }}</Tag>
                            </span>
                        </template>
                    </Table>
                    <Footer></Footer>
                </Card>
            </Layout>
        </Layout>
    </div>
</template>

<script>
import Footer from '@/components/Footer.vue'
import {api }from '@/utils/api'
import {JUDGE_RESULT} from '@/utils/constants'
export default {
    components:{
    Footer
    },
    data () {
        return {
            submissionTableColumns:[
                {title:'#', key:'id', maxWidth: 80},
                {title:'Problem', slot:'problemTitle'},
                {title:'Username', slot:'username'},
                {title:'Template', slot:'templateId'},
                {title:'Memory', slot:'runMemory', key:'runMemory'},
                {title:'Time', slot:'runTime', key:'runTime'},
                {title:'Judge Time', slot:'judgeTime'},
                {title:'Result', slot:'judgeResult', key:'judgeResult'},
                // { title: '\b', slot: 'edit', width:"auto"},
            ],
            submissionTableData: [],
                // 搜索表单
            searchForm: {
                username: '',
                problemTitle: '',
                judgeResult: ''
            },
            // 搜索表单验证规则
            searchRules: {
                username: [
                    { required: false, message: 'Please input username', trigger: 'blur' }
                ],
                problemTitle: [
                    { required: false, message: 'Please input problem title', trigger: 'blur' }
                ],
                judgeResult: [
                    { required: false, message: 'Please input judge result', trigger: 'blur' }
                ]
            },
            // 侧边栏是否收缩
            isCollapsed: false,
            // 表格加载状态
            tableLoading: false,
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
        // 获取评测记录列表
        async getSubmissionList() {
            this.tableLoading = true;
            await api.getSubmissionList()
                .then(response => {
                    this.submissionTableData = response
                    .filter(sub => {
                        const usernameMatch = !this.searchForm.username || sub.username.includes(this.searchForm.username);
                        const problemTitleMatch = !this.searchForm.problemTitle || sub.problemTitle.includes(this.searchForm.problemTitle);
                        const judgeResultMatch = !this.searchForm.judgeResult || sub.judgeResult.includes(this.searchForm.judgeResult);
                        return usernameMatch && problemTitleMatch && judgeResultMatch;
                    })
                    .sort((a, b) => b.id - a.id);
                })
                .catch(error => {
                    console.log(error);
                    this.$Message.error('Failed to get submission list data');
                })
                .finally(() => {
                    this.tableLoading = false;
                });
        },
        // 实时搜索用户
        searchSubmission() {
            this.getSubmissionList(); // 重新获取用户列表
        },
        resetSearchForm() {
            this.searchForm = {
                username: '',
                problemTitle: '',
                judgeResult: ''
            };
            this.getSubmissionList();
        },
    },
    computed: {
        JUDGE_RESULT:function(){
            console.log(JUDGE_RESULT);
            return JUDGE_RESULT;
        },
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
            return this.submissionTableData.filter(sub => {
                const usernameMatch = !this.searchForm.username || sub.username.includes(this.searchForm.username);
                const problemTitleMatch = !this.searchForm.problemTitle || sub.problemTitle.includes(this.searchForm.problemTitle);
                const judgeResultMatch = !this.searchForm.judgeResult || sub.judgeResult.includes(this.searchForm.judgeResult);
                return usernameMatch && problemTitleMatch && judgeResultMatch;
            });
        },
    },
    mounted: function(){
        // 获取评测记录列表
        this.getSubmissionList();
    },
    // 监听路由变化，重新获取评测记录列表
    watch:{
        $route: function(){
            this.getSubmissionList();
        }
    }
}
</script>
