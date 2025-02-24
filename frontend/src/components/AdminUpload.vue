<template>
  <div>
    <!-- 单文件上传，只支持zip和rar -->
    <Upload
      ref="upload"
      :data="uploadData"
      :headers="{'x-csrf-token': token}"
      :on-success="handleSuccess"
      :format="['zip']" 
      :on-format-error="handleFormatError"
      :on-exceeded-size="handleMaxSize"
      :before-upload="handleBeforeUpload"
      type="drag"
      :max-size="512*1024*1024"
      :action="url"
      :on-change="handleChange"
      :file-list="fileList"> <!-- 绑定文件列表 -->
      <div style="padding: 20px 0">
        <Icon type="ios-cloud-upload" size="52" style="color: #3399ff"></Icon>
        <p>Click or drag files here to upload</p>
      </div>
    </Upload>
    <!-- 文件列表显示 -->
    <div v-if="fileList.length" class="file-list">
      <ul>
        <li v-for="(file, index) in fileList" :key="index">{{ file.name }}</li>
      </ul>
    </div>
  </div>
</template>

<script>
import {api} from '@/utils/api';

export default {
  name: 'AdminUpload',
  props: {
    uploadData: {
      type: Object,
      required: true,
      default: () => ({})
    },
    token: {
      type: String,
      required: true,
      default: ''
    },
    url: {
      type: String,
      required: true,
      default: ''
    }
  },
  data() {
    return {
      fileList: [], // 存储选择的文件列表
      loadingStatus: false,
      finalData: {}
    };
  },
  methods: {
    handleSuccess(response, file, fileList) {
      this.$Message.success('Upload successful');
      console.log('Upload successful:', response, file, fileList);
    },
    handleFormatError(file, fileList) {
      this.$Message.error('File format not supported. Please upload zip files.');
      console.log('File format error:', file, fileList);
    },
    handleMaxSize(file, fileList) {
      this.$Message.error('File size exceeds the limit.');
      console.log('File size exceeded:', file, fileList);
    },
    handleBeforeUpload(file) {
      // 更新文件列表
      this.fileList = [file]; // 确保fileList只包含一个文件
      this.file = file; // 设置this.file为当前选择的文件
      this.finalData = {
        problemId: this.uploadData.id,
        file: file,
      };
      console.log('Before Upload', this.finalData);
      // 阻止自动上传
      return false;
    },
    handleChange(file, fileList) {
      // 当文件列表发生变化时，更新组件的fileList
      this.fileList = fileList;
      this.file = fileList.length ? fileList[fileList.length - 1] : null; 
    },
    // 上传题目
    uploadProblem(){
      if (!this.file) {
        this.$Message.error('No file selected for upload.');
        return;
      }
      this.loadingStatus = true;
      var data = new FormData();
      data.append('file', this.finalData.file);
      data.append('problemId', this.uploadData.id);
      console.log('data:', data.get('file'));
      console.log('data:', data.get('problemId'));
      api.uploadProblem(data)
      .then(() => {
        this.$Message.success('Upload successful');
        console.log('Upload successful');
      }).catch(() => {
        this.$Message.error('Upload failed');
        console.log('Upload failed');
      }).finally(() => {
        this.file = null;
        this.finalData = {};
        this.loadingStatus = false;
        this.fileList = []; // 清空文件列表
      });
    },
    // // 上传题目
    // uploadProblem() {
    //   if (!this.file) {
    //     this.$Message.error('No file selected for upload.');
    //     return;
    //   }
    //   this.loadingStatus = true;
    //   api.addProblem(this.finalData).then(() => {
    //     console('Upload successful');
    //   }).catch(() => {
    //     console.log('Upload failed');
    //   }).finally(() => {
    //     this.file = null;
    //     this.loadingStatus = false;
    //   });
    // },
  }
}
</script>

<style scoped>
/* 添加一些样式以美化上传组件 */
.upload-container {
  padding: 20px 0;
  text-align: center;
  border: 2px dashed #3399ff;
  border-radius: 4px;
  background-color: #f9f9f9;
}
.upload-container p {
  margin-top: 10px;
  color: #666;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  margin-bottom: 5px;
}
</style>
