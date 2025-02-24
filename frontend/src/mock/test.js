import Mock from 'mockjs'
import { JUDGE_RESULT } from '@/utils/constants'
import { options } from 'less'

const Random = Mock.Random

Mock.setup({
  timeout: '200-600'
})

//  ---------------------用户列表---------------------
const authUser =[
  'user', 'admin'
]
const genderUser=[
  'male','female'
]
Mock.mock(/\/api\/user\/list/, 'get', {
  code: 0,
  data: [
    ...Array.from({ length: 15 }).map(() => ({
      id: Random.integer(1, 100),
      username: Random.cname(), 
      email:Random.email(),
      nickname:Random.cname(),
      gender:Random.pick(genderUser),
      auth:Random.pick(authUser),
      create_time:Random.datetime(),
      update_time:Random.datetime()
    }))
  ]
})
Mock.mock(/\/api\/user\/updateUser/, 'post', options => {
  console.log(options); // 打印请求体
  return {
    code: 0,
    message: '添加成功',
    data: {} // 如果有需要返回的数据，可以在这里添加
  };
})
Mock.mock(/\/api\/user\/add/, 'post', options => {
  console.log(options); // 打印请求体
  return {
    code: 0,
    message: '添加成功',
    data: {} // 如果有需要返回的数据，可以在这里添加
  };
})
Mock.mock(/\/api\/user\/delete/, 'delete', function(options) {
  console.log('Mock received request:', options);
  return {
    code: 0,
    message: '删除成功',
    data: {} // 如果有需要返回的数据，可以在这里添加
  };
})

// ---------------------评测记录---------------------
const judgeResultKeys = Object.keys(JUDGE_RESULT)
Mock.mock(/\/admin\/submission\/list/, 'get', {
  code: 0,
  data: [
    ...Array.from({ length: 15 }).map(() => ({
      id: Random.integer(1, 100),
      problemTitle: Random.ctitle(5, 10),
      username: Random.cname(),
      templateId: Random.integer(1, 100),
      runmemory: Random.integer(1, 100),
      runTime: Random.integer(1, 100),
      judgeTime: Random.datetime(),
      judgeResult: Random.pick(judgeResultKeys) // 从 JUDGE_RESULT 的键中随机选择一个值
    }))
  ]
})

// ---------------------题目---------------------
Mock.mock(/\/api\/admin\/problem\/list/, 'get', {
  code: 0,
  data: [
    ...Array.from({ length: 15 }).map(() => ({
      id: Random.integer(1, 100),
      title: Random.ctitle(5, 10),
      timeLimit: Random.integer(100,10000),
      memoryLimit: Random.integer(512, 4096),
    }))
  ]
})

// 模拟 PUT 请求
Mock.mock(/\/api\/admin\/problem\/add/, 'post', function(options) {
  // 打印请求体
  console.log(options.body);

  // 返回模拟的响应对象
  return {
    // 模拟的响应数据
    // data: {
      code: 0,
      message: '添加成功',
      data: {} // 这里可以放置您想要返回的具体数据
    // },
    // 模拟的响应状态码
    // status: 200,
    // // 模拟的响应状态文本
    // statusText: 'OK',
    // // 模拟的响应头
    // headers: {
    //   'Content-Type': 'application/json'
    // },
  };
});
Mock.mock(/\/api\/admin\/problem\/update/, 'put', options =>{
  console.log(options.body);
  return {
    // data:
      code: 0,
      message: '修改成功',
      data: {}
    
  }
})
Mock.mock(/\/api\/admin\/problem\/delete/, 'delete', options =>{
  console.log(options.body);
  return {
    code: 0,
    message: '删除成功',
    data: {}
  }
})
Mock.mock(/\/api\/admin\/problem\/upload/, 'post', options =>{
  console.log(options.body.get('file'));
  return {
    code: 0,
    message: '上传成功',
    data: {}
  }
})



// ---------------------模板---------------------
Mock.mock(/\/api\/JudgeTemplate\/list/, 'get', {
  code: 0,
  data: [
    ...Array.from({ length: 4 }).map(() => ({
      id: Random.integer(1, 100),
      language: Random.ctitle(5, 10),
      comment: Random.ctitle(5, 10),
      description: Random.ctitle(5, 10),
    })
    )]
})
Mock.mock(/\/api\/JudgeTemplate\/add/, 'post', options=>{
  console.log(options.body);
  return {
    code: 0,
    message: '添加成功',
    data: {}
  }
})
Mock.mock(/\/api\/JudgeTemplate\/update/, 'put', options=>{
  console.log(options);
  return {
    code: 0,
    message: '修改成功',
    data: {}
  }
})
Mock.mock(/\/api\/JudgeTemplate\/delete/, 'delete', options=>{
  console.log(options.body);
  return {
    code: 0,
    message: '删除成功',
    data: {}
  }
})

// ---------------------比赛---------------------
Mock.mock(/\/api\/contest\/list/, 'get', {
  code: 0,
  data: [
    ...Array.from({ length: 14 }).map(() => {
      // 获取当前时间
      const now = new Date();
      // 开始时间不得小于当前时间，并且为标准时间格式
      const startTime = Random.datetime('yyyy-MM-dd HH:mm:ss', new Date(now.getTime() + 3600000)); // 当前时间加1小时
      // 结束时间必须大于开始时间至少一个小时，并且为标准时间格式
      const endTime = Random.datetime('yyyy-MM-dd HH:mm:ss', new Date(new Date(startTime).getTime() + 3600000 + 3600000)); // 开始时间加2小时（确保至少1小时以上）
      return {
        id: Random.integer(1, 100),
        title: Random.ctitle(5, 10),
        startTime,
        endTime,
      }
    })
  ]
});
Mock.mock(/\/api\/contest\/add/, 'post', options=>{
  console.log(options.body);
  return {
    code: 0,
    message: '添加成功',
    data: {}
  }
})
Mock.mock(/\/api\/contest\/update/, 'put', options=>{
  console.log(options.body);
  return {
    code: 0,
    message: '修改成功',
    data: {}
  }
})
Mock.mock(/\/api\/contest\/delete/, 'delete', options =>{
  console.log(options.body);
  return {
    code: 0,
    message: '删除成功',
    data: {}
  }
})
Mock.mock(/\/admin\/contest\/user\/list/, 'get', {
  code: 0,
  data: [
    ...Array.from({ length: 15 }).map(() => ({
      id: Random.integer(1, 100),
      username: Random.cname(),
      nickname: Random.cname(),
      email: Random.email(),
      auth: Random.pick(authUser),
      create_time: Random.datetime(),
      update_time: Random.datetime()
    }))
  ]
})
Mock.mock(/\/admin\/contest\/user\/add/, 'put', options=>{
  console.log(options.body);
  return {
    code: 0,
    message: '添加成功',
    data: {}
  }
})
Mock.mock(/\/admin\/contest\/user\/delete/, 'delete', options=>{
  console.log(options.body);
  return {
    code: 0,
    message: '删除成功',
    data: {}
  }
})
Mock.mock(/\/admin\/contest\/user\/selected/, 'get', options=> {
  console.log(options);
  return {code: 0,
  data: [
    ...Array.from({ length: 15 }).map(() => ({
      id: Random.integer(1, 100),
      username: Random.cname(),
    }))
  ]}
})
Mock.mock(/\/admin\/contest\/problem\/list/, 'get', {
  code: 0,
  data: [
    ...Array.from({ length: 15 }).map(() => ({
      id: Random.integer(1, 100),
      title: Random.ctitle(5, 10),
      timeLimit: Random.integer(100,10000),
      memoryLimit: Random.integer(512, 4096),
    }))
  ]
})
Mock.mock(/\/admin\/contest\/problem\/add/, 'put', options=>{
  console.log(options.body);
  return {
    code: 0,
    message: '添加成功',
    data: {}
  }
})
Mock.mock(/\/admin\/contest\/problem\/delete/, 'delete', options=>{
  console.log(options.body);
  return {
    code: 0,
    message: '删除成功',
    data: {}
  }
})
Mock.mock(/\/admin\/contest\/problem\/selected/, 'get', options=> {
  console.log(options);
  return {code: 0,
  data: [
    ...Array.from({ length: 15 }).map(() => ({
      id: Random.integer(1, 100),
      title: Random.ctitle(5, 10),
    }))
  ]}
})
Mock.mock(/\/api\/BangDan\/AllResultList/, 'get', options=>{
  console.log(options);
  return {
    code: 0,
    data: [
      {
          "userId": 2,
          "userName": "hhh",
          "judgeResultList": [
              {
                  "result": "Default",
                  "problemId": 12,
                  "newId": "A",
                  "title": "网络流",
                  "cnt": 0,
                  "acTime": null
              },
              {
                  "result": "Default",
                  "problemId": 22,
                  "newId": "B",
                  "title": "最短路",
                  "cnt": 0,
                  "acTime": null
              },
              {
                  "result": "Default",
                  "problemId": 13,
                  "newId": "C",
                  "title": "A+B",
                  "cnt": 0,
                  "acTime": null
              }
          ],
          "timePenalty": 0,
          "acNum": 0,
          "rank": 1
      },
      {
          "userId": 6,
          "userName": "zby",
          "judgeResultList": [
              {
                  "result": "WA",
                  "problemId": 12,
                  "newId": "A",
                  "title": "网络流",
                  "cnt": 6,
                  "acTime": null
              },
              {
                  "result": "WA",
                  "problemId": 22,
                  "newId": "B",
                  "title": "最短路",
                  "cnt": 1,
                  "acTime": null
              },
              {
                  "result": "Default",
                  "problemId": 13,
                  "newId": "C",
                  "title": "A+B",
                  "cnt": 0,
                  "acTime": null
              }
          ],
          "timePenalty": 0,
          "acNum": 0,
          "rank": 2
      },
      // {
      //     "userId": 6,
      //     "userName": "zby",
      //     "judgeResultList": [
      //         {
      //             "result": "WA",
      //             "problemId": 1,
      //             "newId": "A",
      //             "title": "网络流",
      //             "cnt": 6,
      //             "acTime": null
      //         },
      //         {
      //             "result": "WA",
      //             "problemId": 2,
      //             "newId": "B",
      //             "title": "最短路",
      //             "cnt": 1,
      //             "acTime": null
      //         },
      //         {
      //             "result": "Default",
      //             "problemId": 3,
      //             "newId": "C",
      //             "title": "A+B",
      //             "cnt": 0,
      //             "acTime": null
      //         }
      //     ],
      //     "timePenalty": 0,
      //     "acNum": 0,
      //     "rank": 2
      // },
      // {
      //     "userId": 6,
      //     "userName": "zby",
      //     "judgeResultList": [
      //         {
      //             "result": "WA",
      //             "problemId": 1,
      //             "newId": "A",
      //             "title": "网络流",
      //             "cnt": 6,
      //             "acTime": null
      //         },
      //         {
      //             "result": "WA",
      //             "problemId": 2,
      //             "newId": "B",
      //             "title": "最短路",
      //             "cnt": 1,
      //             "acTime": null
      //         },
      //         {
      //             "result": "Default",
      //             "problemId": 3,
      //             "newId": "C",
      //             "title": "A+B",
      //             "cnt": 0,
      //             "acTime": null
      //         }
      //     ],
      //     "timePenalty": 0,
      //     "acNum": 0,
      //     "rank": 2
      // },
      // {
      //     "userId": 6,
      //     "userName": "zby",
      //     "judgeResultList": [
      //         {
      //             "result": "WA",
      //             "problemId": 1,
      //             "newId": "A",
      //             "title": "网络流",
      //             "cnt": 6,
      //             "acTime": null
      //         },
      //         {
      //             "result": "WA",
      //             "problemId": 2,
      //             "newId": "B",
      //             "title": "最短路",
      //             "cnt": 1,
      //             "acTime": null
      //         },
      //         {
      //             "result": "Default",
      //             "problemId": 3,
      //             "newId": "C",
      //             "title": "A+B",
      //             "cnt": 0,
      //             "acTime": null
      //         }
      //     ],
      //     "timePenalty": 0,
      //     "acNum": 0,
      //     "rank": 2
      // },
      // {
      //     "userId": 6,
      //     "userName": "zby",
      //     "judgeResultList": [
      //         {
      //             "result": "WA",
      //             "problemId": 1,
      //             "newId": "A",
      //             "title": "网络流",
      //             "cnt": 6,
      //             "acTime": null
      //         },
      //         {
      //             "result": "WA",
      //             "problemId": 2,
      //             "newId": "B",
      //             "title": "最短路",
      //             "cnt": 1,
      //             "acTime": null
      //         },
      //         {
      //             "result": "Default",
      //             "problemId": 3,
      //             "newId": "C",
      //             "title": "A+B",
      //             "cnt": 0,
      //             "acTime": null
      //         }
      //     ],
      //     "timePenalty": 0,
      //     "acNum": 0,
      //     "rank": 2
      // },
      // {
      //     "userId": 6,
      //     "userName": "zby",
      //     "judgeResultList": [
      //         {
      //             "result": "WA",
      //             "problemId": 1,
      //             "newId": "A",
      //             "title": "网络流",
      //             "cnt": 6,
      //             "acTime": null
      //         },
      //         {
      //             "result": "WA",
      //             "problemId": 2,
      //             "newId": "B",
      //             "title": "最短路",
      //             "cnt": 1,
      //             "acTime": null
      //         },
      //         {
      //             "result": "Default",
      //             "problemId": 3,
      //             "newId": "C",
      //             "title": "A+B",
      //             "cnt": 0,
      //             "acTime": null
      //         }
      //     ],
      //     "timePenalty": 0,
      //     "acNum": 0,
      //     "rank": 2
      // },
      // {
      //     "userId": 6,
      //     "userName": "zby",
      //     "judgeResultList": [
      //         {
      //             "result": "WA",
      //             "problemId": 1,
      //             "newId": "A",
      //             "title": "网络流",
      //             "cnt": 6,
      //             "acTime": null
      //         },
      //         {
      //             "result": "WA",
      //             "problemId": 2,
      //             "newId": "B",
      //             "title": "最短路",
      //             "cnt": 1,
      //             "acTime": null
      //         },
      //         {
      //             "result": "Default",
      //             "problemId": 3,
      //             "newId": "C",
      //             "title": "A+B",
      //             "cnt": 0,
      //             "acTime": null
      //         }
      //     ],
      //     "timePenalty": 0,
      //     "acNum": 0,
      //     "rank": 2
      // },
      // {
      //     "userId": 6,
      //     "userName": "zby",
      //     "judgeResultList": [
      //         {
      //             "result": "WA",
      //             "problemId": 1,
      //             "newId": "A",
      //             "title": "网络流",
      //             "cnt": 6,
      //             "acTime": null
      //         },
      //         {
      //             "result": "WA",
      //             "problemId": 2,
      //             "newId": "B",
      //             "title": "最短路",
      //             "cnt": 1,
      //             "acTime": null
      //         },
      //         {
      //             "result": "Default",
      //             "problemId": 3,
      //             "newId": "C",
      //             "title": "A+B",
      //             "cnt": 0,
      //             "acTime": null
      //         }
      //     ],
      //     "timePenalty": 0,
      //     "acNum": 0,
      //     "rank": 2
      // },
      // {
      //     "userId": 6,
      //     "userName": "zby",
      //     "judgeResultList": [
      //         {
      //             "result": "WA",
      //             "problemId": 1,
      //             "newId": "A",
      //             "title": "网络流",
      //             "cnt": 6,
      //             "acTime": null
      //         },
      //         {
      //             "result": "WA",
      //             "problemId": 2,
      //             "newId": "B",
      //             "title": "最短路",
      //             "cnt": 1,
      //             "acTime": null
      //         },
      //         {
      //             "result": "Default",
      //             "problemId": 3,
      //             "newId": "C",
      //             "title": "A+B",
      //             "cnt": 0,
      //             "acTime": null
      //         }
      //     ],
      //     "timePenalty": 0,
      //     "acNum": 0,
      //     "rank": 2
      // }
  ]
  }
}
)

// 模拟登录接口
Mock.mock(/\/user\/adminLogin/, 'post', options => {
  console.log(options.body);
  return {
    code: 0,
    message: '登录成功',
    data: '1289378193712893797213981'
  }
})
Mock.mock(/\/api\/user\/login/, 'post', options => {
  console.log(options.body);
  return {
    code: 0,
    message: '登录成功',
    data: '1289378193712893797213981'
  }
})

Mock.mock(/\/api\/user\/isAdminLogin/, 'post', options => {
  console.log(options.body);
  return {
    code: 0,
    message: '登录成功',
    data: '1289378193712893797213981'
  }
})

Mock.mock(/\/api\/user\/isUserLogin/, 'post', options => {
  console.log(options.body);
  return {
    code: 0,
    message: '登录成功',
    data: '1289378193712893797213981'
  }
})