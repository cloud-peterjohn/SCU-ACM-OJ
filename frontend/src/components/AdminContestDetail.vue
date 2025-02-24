<style scoped>
.layout{
    border: 1px solid #c693eb;
    background: hsl(275, 26%, 91%);
    position: relative;
    border-radius: 4px;
    /* overflow: hidden; */
}
.rate-demo {
    display: flex;
    justify-content: space-between;
    align-items: center;   
}
.layout-logo{
    width: 80px;
    height: 50px;
    background: #921a9d;
    border-radius: 2px;
    float: left;
    position: relative;
    top: 5px;
    left: 30px;
}
.demo-split{
    height: 250px;
    border: 1px solid #dcdee2;
}
.demo-split-1{
    height: 400px;
    border: 1px solid #dcdee2;
}
.demo-split-pane{
    padding: 10px;
    height: 255px;
}
.layout-nav{
    width: 240px;
    margin: 0 auto;
    margin-right: 20px;
}
.title-bar {
    font-size: 24px;
    font-weight: bold;
    margin-bottom: 10px;
    text-align: center;
}
.info {
    font-size: 14px;
    color: #555;
}
.table-container {
    height: auto; /* 移除max-height属性 */
    overflow-y: visible; /* 设置为visible，允许内容超出 */
    text-align: center;
    margin:auto;
}

.table-container .ivu-table-cell {
    padding: 10px;
    text-align: center;
    vertical-align: middle;
    width:100px;
}

.table-container .ivu-table{
    border: 1px solid #f4f4f4;
    border-collapse: separate;
    border-spacing: 0 10px;
}

.card-view{
    position: relative;
    top: 20px;
    width:96%; 
    left: 2%; 
    height: auto; /* 将height设置为auto，以适应内容的高度 */
    min-height: 100vh; /* 设置一个最小高度，确保Card至少占据视口的高度 */
    overflow: visible; /* 允许内容超出 */
    display: flex; /* 使用flex布局 */
    justify-content: center; /* 水平居中 */
    align-items: center; /* 垂直居中 */
}
</style>
<template>
    <div class="layout">
        <Layout>
            <Header>
                <Menu mode="horizontal" theme="" active-name="1">
                    <div class="layout-nav">
                        <MenuItem name="1" @click="toAdminNav">
                            <Icon type="md-home" size="25"></Icon>
                            <span>Home</span>
                        </MenuItem>
                        <MenuItem @click="goBack">
                            <Icon type="md-arrow-back" size="25"></Icon>
                            <span>Back</span>
                        </MenuItem>
                    </div>
                </Menu>
            </Header>
        </Layout>
    </div>
    <br>
    <Card class="card-view">
        <!-- 标题 -->
        <div class="title-bar">
            {{contestFundamental.title}}
        </div>
        <!-- 时间 -->
        <div class="info">
            <span>开始时间:{{contestFundamental.startTime}}</span>
            <span style="float: right;">结束时间:{{contestFundamental.endTime}}</span>
        </div>
        <!-- 进度条 -->
        <div>
            <Progress 
                :percent="progressPercentage" 
                :stroke-color="['#108ee9', '#87d068']" 
                status="active" :stroke-width="20" 
                style="margin-bottom: 20px;"
                text-inside
                >
            </Progress>
        </div>
        <br>
        <!-- 表格 -->
        <div class="table-container">
            <Table
                :columns="tableColumns"
                :data="formattedContestDetail"
                >
            </Table>
        </div>
    </Card>
    <Footer></Footer>
</template>

<script>
import Footer from '@/components/Footer.vue'
import {api} from '@/utils/api'
import axios from 'axios'

export default{
    components:{
        Footer,
    },
    data(){
        return{
            // 比赛id
            contestId: '',
            // 比赛详情
            contestDetailTable : new Array(),
            // 比赛基本信息
            contestFundamental:[],
            // 比赛题目数
            problemCount: 3,
        }
    },
    methods:{
        toAdminNav() {
            // 跳转到管理员导航页面
            this.$router.push({ name: 'admin_nav' });
        },
        goBack(){
            // 返回上一页
            this.$router.go(-1);
        },
        //获取比赛基本信息
        async getContestFundamental(){
            axios.defaults.headers.delete['Content-Type']='';
            const params = {
                contestId: this.contestId
            }
            await api.getContestFundamental(
                params
            ).then(res => {
                this.contestFundamental = res;
                this.$Message.success('Get contest fundamental success');
            }).catch(err => {
                console.log(err);
                // this.$Message.error('Get contest fundamental failed');
            })
        },
        // 获取比赛详情
        async getContestDetail(){
            axios.defaults.headers.delete['Content-Type']='';
            const params = {
                contestId: this.contestId
            }
            await api.getContestDetail(
                params
            )
            .then(res => {
                this.contestDetailTable = res
                .sort((a, b) => {
                    // 比赛名次排序, 从小到大
                    return a.rank - b.rank;
                })
                console.log("res:",this.contestDetailTable);
                this.$Message.success('Get contest detail success');
            }).catch(err => {
                console.log(err);
                // this.$Message.error('Get contest detail failed');
            })
        },
        // 获取已经选择的题目
        getContestSelectedProblem(){
            axios.defaults.headers.delete['Content-Type']='';
            const params = {
                contestId : this.contestId
            }
            api.getContestSelectedProblem(
                params
            )
            .then(response => {
                this.problemCount = response.length;
                this.$Message.success('Get selected problem list successfully');
            }).catch(err => {
                console.log(err);
                // this.$Message.error('Falied to get selected problem list');
            });
        },
        // 更新进度条
        updateProgress() {
            this.$forceUpdate(); // 强制更新视图
            setTimeout(this.updateProgress, 2000); // 2秒调用一次
        },
        calculateMinutes(submitTime){
            if(!submitTime){
                return 0;
            }
            console.log('subTime',submitTime);
            const duration = new Date(submitTime) - new Date(this.contestFundamental.startTime);
            return Math.floor(duration / 60000);
        },
        getResultColor(result){
            return result === 'AC' ? '#5cb85c' : '#d9534f';
        },
    },
    created(){
        // 获取比赛详情
        this.contestId = this.$route.params.contestId;
        this.getContestFundamental();
        this.getContestDetail();
        this.getContestSelectedProblem();
    },
    computed:{
        // 计算比赛时间
        progressPercentage() {
            // 获取当前时间
            const currentTime = new Date();
            // 计算当前时间与开始时间之间的差值
            const startToCurrent = currentTime - new Date(this.contestFundamental.startTime);
            // 计算结束时间与开始时间之间的差值
            const startToEnd =  new Date(this.contestFundamental.endTime) - new Date(this.contestFundamental.startTime);
            // 计算进度百分比
            const percentage = (startToCurrent / startToEnd) * 100;
            return Math.min(100, Math.max(0, percentage)).toFixed(2);
        },
        // 动态生成Table列 
        tableColumns() { 
            let columns = [ 
                { title: 'Rank', key: 'rank', width:100, fixed: 'left' }, 
                { title: 'User', key: 'userName' , width:100, fixed: 'left'}, 
                { title: 'AC', key: 'acNum' , width:100, fixed: 'left'},
                { title: 'Penalty', key: 'timePenalty' , width:100, fixed: 'left'}
            ]; 
            const self = this;
            for (let i = 0; i < this.problemCount; i++) { 
                const problemTitle = String.fromCharCode(65 + i); // Convert to A, B, C, etc.
                columns.push({ 
                    title: `${problemTitle}`, 
                    key: `problem${i + 1}`, 
                    width:100,
                    render:(h,params) =>{
                        const result = params.row[`problem${i + 1}`];
                        if(result === 'Default'){
                            return h('div', {style:{backgroundColor: 'white'}}, '');
                        }
                        const cnt = params.row[`problemCnt${i + 1}`];
                        const submitTime = params.row[`problemSubmitTime${i + 1}`];
                        const color = self.getResultColor(result);
                        const symbol = result === 'AC' ? '+': '-';
                        const minutes = self.calculateMinutes(submitTime);

                        return h('div', { style: { backgroundColor: color, padding: '5px', borderRadius: '5px', textAlign: 'center' } }, [
                            h('span',{style:{display:'block'}}, symbol),
                            h('span',{style:{display:'block'}}, `${cnt} / ${minutes}`),
                        ]);
                    }
                }); 
            } 
            return columns; 
        }, 
        // 格式化比赛详情数据 
        formattedContestDetail() { 
            if (!this.contestDetailTable || this.contestDetailTable.length === 0) { 
                return []; 
            } 
            return this.contestDetailTable.map(user => { 
                let userData = { 
                    rank: user.rank, 
                    userName: user.userName, 
                    acNum: user.acNum, 
                    timePenalty: user.timePenalty 
                }; 
                user.judgeResultList.forEach(problem => { 
                    const problemNewId = problem.newId.charCodeAt(0) - 64;
                    const problemKey = `problem${problemNewId}`; 
                    userData[problemKey] = problem.result; 
                    console.log("123456",problem.result);
                    userData[`problemCnt${problemNewId}`] = problem.cnt; 
                    userData[`problemSubmitTime${problemNewId}`] = problem.submitTime;
                }); 
                return userData; 
            }); 
        }
    },
    watch:{
        // 监听路由变化
        '$route'(to, from){
            if(to.params.contestId !== from.params.contestId){
                this.contestId = to.params.contestId;
                this.getContestDetail();
            }
        }
    },
    beforeDestroy() {
        // 在组件销毁前清除定时器
        clearTimeout(this.progressInterval);
    },
}
</script>