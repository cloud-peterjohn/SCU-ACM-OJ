<template>
  <h1 style='text-align:center;'>
    <Icon type="ios-home" size="80"></Icon>
    <span> Online Judge System Login </span> 
  </h1>
  <br><br><br><br>
  <div class="demo-login">
    <RadioGroup v-model="loginType" @on-change="handleLoginTypeChange">
      <Radio label="username">Username Login</Radio>
      <Radio label="email">Email Login</Radio>
    </RadioGroup>
    <br><br>
    <Login @on-submit="handleSubmit">
      <div v-if="loginType === 'username'">
        <UserName name="username" value="zby" />
        <Password name="password" value="123456" />
        <Captcha class="demo-login-captcha" name="usernameCaptcha" :count-down="4" @on-get-captcha="handleGetCaptcha">
          <template #text>
            <img :src="'./captcha/' + this.captcha" />
          </template>
        </Captcha>
      </div>
      <div v-else>
        <Input name="email" v-model="email" placeholder="Email" /><br><br>
        <Button type="primary" @click="sendEmail">Get Email Code</Button><br><br>
        <Input name="emailCode" v-model="emailCode" placeholder="Email Code" /><br><br>
        <Captcha class="demo-login-captcha" name="emailCaptcha" :count-down="4" @on-get-captcha="handleGetCaptcha">
          <template #text>
            <img :src="'./captcha/' + this.captcha" />
          </template>
        </Captcha>
      </div>
      <Submit />
    </Login>
    <br><br>
    还没有账号？立即注册！-> -> -> 
    <Button type="primary" @click="handleClickRegister" style="margin-left: 10px;">Register</Button>
    <br><br>
    忘记了密码？立即找回！-> -> ->
    <Button type="primary" @click="handleClickPasswd" style="margin-left: 10px;">Reset Password</Button>
  </div>
  <br><br><br><br>
  <Footer />
</template>

<script>
import Footer from '@/components/Footer.vue'
import axios from 'axios';

export default {
    components: {
        Footer
    },
    data() {
        return {
            loginType: 'username',
            captcha: 'captcha' + Math.floor(Math.random() * 1000) % 20 + '.jpg',
            username: '',
            password: '',
            email: '',
            emailCode: '',
            usernameCaptcha: '',
            emailCaptcha: '',
            captcha_val_dict: {
                'captcha0.jpg': 'a2af',
                'captcha1.jpg': 'n5wn',
                'captcha2.jpg': 'nn27',
                'captcha3.jpg': 'mn6b',
                'captcha4.jpg': 'mwe2',
                'captcha5.jpg': 'pamb',
                'captcha6.jpg': 'm37w',
                'captcha7.jpg': 'e56b',
                'captcha8.jpg': '6yg7',
                'captcha9.jpg': '78g5',
                'captcha10.jpg': 'g654',
                'captcha11.jpg': 'gx5b',
                'captcha12.jpg': 'ddbm',
                'captcha13.jpg': '4y24',
                'captcha14.jpg': 'gfdf',
                'captcha15.jpg': 'yxmm',
                'captcha16.jpg': 'e66y',
                'captcha17.jpg': 'n38g',
                'captcha18.jpg': 'nbpa',
                'captcha19.jpg': 'ey77',
            }
        };
    },
    methods: {
        handleLoginTypeChange() {
            this.username = '';
            this.password = '';
            this.email = '';
            this.emailCode = '';
            this.usernameCaptcha = '';
            this.emailCaptcha = '';
        },
        handleSubmit(valid, { username, password, usernameCaptcha, emailCaptcha }) {
            if (valid) {
                if (this.loginType === 'username') {
                    this.username = username;
                    this.password = password;
                    this.usernameCaptcha = usernameCaptcha;
                } else {
                    this.emailCaptcha = emailCaptcha;
                }
                this.$Modal.info({
                    title: '请确认您的登录信息：',
                    content: this.loginType === 'username'
                        ? `username: ${username} | password: ****** | captcha: ${usernameCaptcha}`
                        : `email: ${this.email} | emailCode: ${this.emailCode} | captcha: ${emailCaptcha}`,
                    onOk: () => { this.handleUserLogin(); }
                });
            }
        },
        handleGetCaptcha() {
            this.captcha = 'captcha' + Math.floor(Math.random() * 1000) % 20 + '.jpg';
        },
        sendEmail() {
            axios.post('/api/email/sendEmailCode',
                new URLSearchParams({
                    email: this.email
                })
            ).then((response) => {
                console.log(response);
                if (response.data.code == 0) {
                    this.$Message.success('发送验证码成功!');
                } else {
                    this.$Message.error('发送验证码失败!');
                }
            }).catch((error) => {
                console.log(error);
                this.$Message.error('Backend Interface Error!');
            });
        },
        handleUserLogin() {
            console.log(this.loginType);
            console.log(this.emailCaptcha );
            console.log(this.captcha_val_dict[this.captcha]);
            if (this.loginType === 'username' && this.usernameCaptcha === this.captcha_val_dict[this.captcha]) {
                axios.post('/api/user/login', 
                    new URLSearchParams({
                        username: this.username,
                        password: this.password
                    })
                ).then((response) => {
                    this.processLoginResponse(response);
                }).catch((error) => {
                    console.error(error);
                    this.$router.push({ path: '/failure' });
                });
            } else if (this.loginType === 'email' && this.emailCaptcha === this.captcha_val_dict[this.captcha]) {
               console.log("email: "+this.email)
                axios.post('/api/user/emailLogin', 
                    new URLSearchParams({
                        email: this.email,
                        code: this.emailCode
                    })
                ).then((response) => {
                    this.processLoginResponse1(response);
                }).catch((error) => {
                    console.error(error);
                    this.$router.push({ path: '/failure' });
                });
            } else {
                this.$router.push({ path: '/failure' });
            }
        },
        handleClickRegister() {
            this.$router.push({ path: '/register' });
        },
        handleClickPasswd() {
            this.$router.push({ path: '/reset_password' });
        },
        processLoginResponse(response) {
        console.log('XXX',response);
            if (response.data.code === 0) {
                this.$Message.success('Login Success!');
                localStorage.setItem('token', response.data.data);
                localStorage.setItem('username', this.username);
                this.$router.push({ path: '/success' });
            } else {
                this.$Message.error(response.data.message);
                this.$router.push({ path: '/failure' });
            }
        },
        processLoginResponse1(response) {
        console.log('XXX',response);
            if (response.data.code === 0) {
                this.$Message.success('Login Success!');
                localStorage.setItem('token', response.data.data.token);
                localStorage.setItem('username', response.data.data.username);
                this.$router.push({ path: '/success' });
            } else {
                this.$Message.error(response.data.message);
                this.$router.push({ path: '/failure' });
            }
        }
    }
};
</script>

<style>
.demo-login {
    width: 500px !important;
    margin: 0 auto;
}
.demo-login-captcha .ivu-btn {
    padding: 0;
}
.demo-login-captcha .ivu-btn img {
    height: 28px;
    position: relative;
    top: 4px;
}
</style>
