<template>
    <GlobalFooter :links="links" :copyright="copyright" />
    <Button type="primary" @click="exit" style="width: 10%; margin-left: 45%;">Exit Login</Button>
    <br><br><br>
</template>
<script>
    export default {
        data () {
            return {
                links: [
                    {
                        key: 'Help',
                        title: 'Help',
                        href: 'https://www.scu.edu.cn/',
                        blankTarget: true
                    },
                    {
                        key: 'github',
                        icon: 'logo-github',
                        href: 'https://github.com/',
                        blankTarget: true
                    },
                    {
                        key: 'Feedback',
                        title: 'Feedback',
                        href: 'https://jwc.scu.edu.cn/',
                        blankTarget: true
                    }
                ],
                copyright: '24Fall SCU ACM OJ'
            }
        },
        methods: {
            exit () {
                localStorage.clear();
                this.$router.push('/')
            }
        }
    }
</script>
