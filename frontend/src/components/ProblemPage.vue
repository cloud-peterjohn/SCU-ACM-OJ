<template>
    <div>
      <div class="header-bar">
        <span class="header-text">Problem View</span>
        <div class="header-buttons">
          <Button class="header-button" @click="goToCategorized">返回比赛列表</Button>
          <Button class="header-button" @click="goToHardBased">返回比赛详情</Button>
        </div>
      </div>
      <br>
      <Card style="width:96%; left: 2%; margin-right: 2%; margin-top: 2%;">
        <template #title><h2 style="color: purple;">Contest End Time</h2></template>
        <h4>Contest will end at {{ contestEndTime }} !</h4>
      </Card>
      <br>
      <div class="demo-split" style="width: 96%; margin-left: 2%; margin-right: 2%; height: 850px;">
        <Split v-model="split">
          <template #left>
            <Card style="width:96%; left: 2%; margin-right: 2%; margin-top: 2%;">
              <template #title><h2 style="color: purple;">Problem View</h2></template>
              <h4>Problem ID</h4>
              {{ problemID }}<br><br>
              <h4>Problem Name</h4>
              {{ problemName }}<br><br>
              <h4>Problem File</h4>
              <Button type="primary" @click="download">下载文件</Button><br><br>
              <h4>Memory Limit</h4>
              {{ memoryLimit }} MB<br><br>
              <h4>Time Limit</h4>
              {{ timeLimit }} ms<br>
            </Card>
            <Divider style="color: purple;">Result</Divider>
            <Card style="width:96%; left: 2%; margin-right: 2%; margin-top: 2%;">
              <template #title><h2 style="color: purple;">Judge Result</h2></template>
              <h4>Judge Time <Icon type="ios-arrow-dropdown" /></h4>
              <Text>{{ judgeTime }}</Text><br><br>
              <h4>Time Cost <Icon type="ios-arrow-dropdown" /></h4>
              <Text>{{ timeCost }} ms</Text><br><br>
              <h4>Memory Cost <Icon type="ios-arrow-dropdown" /></h4>
              <Text>{{ memoryCost }} MB</Text><br><br>
              <h4>Result Information <Icon type="ios-arrow-dropdown" /></h4>
              <Text>{{ resultInformation }}</Text><br><br>
              <h4>Compile Message <Icon type="ios-arrow-dropdown" /></h4>
              <Text>{{ compileMessage }}</Text><br><br>
            </Card>
          </template>
          <template #right>
            <div style="margin-left: 25px;">
              <Divider style="color: purple;">Settings</Divider>
              <Card style="width:96%; left: 2%; margin-right: 2%; margin-top: 2%;">
                <template #title><h2 style="color: purple;"> Langauge </h2></template>
                <Select v-model="language" style="width:400px ; margin-left: 20px;">
                  <Option v-for="item in tempList" :value="item.value" :key="item.value">{{ item.label }}</Option>
              </Select>
                <!-- <RadioGroup v-model="language">
                  <Radio label="C++"> C/C++ </Radio>
                  <Radio label="Java"> Java </Radio>
                  <Radio label="Python"> Python </Radio>
                </RadioGroup> -->
              </Card>
              <Divider style="color: purple;">Code</Divider>
              <Card style="width:96%; left: 2%; margin-right: 2%; margin-top: 2%;">
                <template #title><h2 style="color: purple;"> Answer </h2></template>
                <Input v-model="code" type="textarea" :autosize="{ minRows: 20, maxRows: 1000 }" placeholder="Enter your code here..." />
                <br><br>
                <Button type="primary" @click="submitCode">提交</Button>
              </Card>
            </div>
          </template>
        </Split>
      </div>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  
  export default {
    data() {
      return {
        split: 0.5,
        contestEndTime: new Date(localStorage.getItem('contest_end_time')),
        language: '',
        problemName: localStorage.getItem('chosenProblemTitle'),
        problemID: localStorage.getItem('chosenProblemID'),
        problemFile: '',
        memoryLimit: 0,
        timeLimit: 0,
        judgeTime: new Date(),
        timeCost: 0,
        memoryCost: 0,
        resultInformation: '',
        compileMessage: '',
        code: '',
        tempList: [
          {
            value: 'Template',
            label: 'Template',
          },
        ],
      };
    },
    methods: {
      goToCategorized() {
        this.$router.push('/contest_list');
      },
      goToHardBased() {
        this.$router.push('/contest_details');
      },
      submitCode() {
        if (this.language === '') {
          this.$Modal.error({
            title: 'Error',
            content: 'Please select a language!'
          });
          return;
        }
        if (this.code.trim() === '') {
          this.$Modal.error({
            title: 'Error',
            content: 'Please enter your code!'
          });
          return;
        }
        const data ={
                problemId: this.problemID,
                language: this.language,
                code: this.code,
        }
        console.log(data);
        this.resultInformation = 'PD';
        axios.post('/api/submit', data
        )
          .then(response => {
            const data = response.data.data;
            console.log(data);
            if (data.result == 'success')
          {
            this.timeCost = data.runTime;
            this.memoryCost = data.runMemory;
            this.resultInformation = data.judgeResult;
            this.compileMessage = data.compileMessage;
            this.judgeTime = new Date(data.judgeTime);
          } else {
            this.timeCost = '';
            this.memoryCost = '';
            this.resultInformation = '';
            this.compileMessage = data.compileMessage;
            this.judgeTime = new Date(data.judgeTime);
          }
            this.$Modal.success({
              title: 'Result',
              content: 'Your code has been submitted successfully!'
            });
          
          })
          .catch(error => {
            this.compileMessage = error;
            this.$Modal.error({
              title: 'Error',
              content: 'Failed to submit your code!'
            });
          });
      },

    download() { 
      const problemId = this.problemID;
      const encodedProblemId = encodeURIComponent(problemId);
      if (!this.problemID) {
        alert('Please enter a problem title');
        return;
      }
      console.log("123456",problemId)
      console.log("123456",encodedProblemId)
      const data = {
            problemId : problemId
          }
      axios.post('/api/download', new URLSearchParams(data), {
        headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
        responseType: 'arraybuffer', 
      })
      .then(response => {
        // 创建一个指向PDF数据的Blob对象
        const blob = new Blob([response.data], { type: 'application/pdf ; charset=utf-8'});
        // 创建一个下载链接
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', `${this.problemName}.pdf`); // 设置下载的文件名
        document.body.appendChild(link);
        link.click(); // 模拟点击来下载文件
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url); // 下载完成后释放URL对象
        this.$Modal.success({
              title: 'Result',
              content: 'Download successfully!'
            });
      })
      .catch(error => {
        console.error('Error downloading file:', error);
        this.$Modal.error({
            title: 'Error',
            content: 'Download error!',
          });
      });
    },
      checkContestEndTime() {
        const currentTime = new Date();
        if (!(this.contestEndTime instanceof Date) || isNaN(this.contestEndTime)) { 
            return;  
        }
        if (currentTime > this.contestEndTime) {
            console.log('this.contestEndTime'+this.contestEndTime);
          this.$Modal.error({
            title: 'Error',
            content: 'The contest has ended.',
            onOk: () => {
                clearInterval(this.contestInterval);
                this.$router.push('/contest_details');
            }
          });
        }
      }
    },
    mounted() {
      axios.post('/api/admin/problem/findByTitle2', 
            new URLSearchParams({
              prob_title: this.problemName
            })
        )
        .then(response => {
            console.log(response.data);
            this.timeLimit = response.data.data.timeLimit;
            this.memoryLimit = response.data.data.memoryLimit;
            this.problemFile = response.data.data.title;
        })
        .catch(error => {
          this.$Message.error('Failed to get ' + this.problemName + '\'s information!');
        });

      axios.get('/api/JudgeTemplate/listUnConditional')
      .then(res=>{
        console.log(res);
        this.tempList = res.data.data.map(temp=>({
          value: temp.language,
          label: temp.language,
        }))
      })
      .catch(error => {
          this.$Message.error('Failed to get template information!');
        });
  
      // Check contest end time every 5 seconds
      this.contestInterval = setInterval(this.checkContestEndTime, 5000);
    },
    beforeDestroy() {
      clearInterval(this.contestInterval);
    }
};
</script>
  

<style>
    .header-bar {
        background-color: purple;
        padding: 10px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        color: white;
    }
    .header-text {
        font-weight: bold;
        font-size: 25px;
    }
    .header-buttons {
        display: flex;
        gap: 10px;
    }
    .header-button {
        background-color: lavender;
        color: white;
        border: none;
        padding: 5px 10px;
        cursor: pointer;
        font-size: 14px;
        border-radius: 5px;
    }
    .header-button:hover {
        background-color: lightpurple;
    }
    .demo-split {
        height: 200px;
        border: 1px solid #dcdee2;
    }
</style>
