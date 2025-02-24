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
    .content-welcome{
        display: flex;
        justify-content: center;
        align-items: center;
        height: 100vh;
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
                <Card style=" justify-content: center; align-items: center; height: 100vh;">
                    <Content class="content-welcome">
                        <h1>Welcome to OJ Management System</h1>
                    </Content>
                </Card>
            </Layout>
        </Layout>
    </div>
    <Footer></Footer>
</template>

<script>
import Footer from '@/components/Footer.vue'
export default {
    components:{
    Footer
    },
    data () {
        return {
            isCollapsed: false
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
        }
    },
    methods: {
        // 侧边栏收缩
        collapsedSider () {
            this.$refs.side1.toggleCollapse();
        },
        toPersonalInfo(){
            console.log('toPersonalInfo');
            this.$router.push({name: 'adminPersonInfo'});
        }
    }
}
</script>
