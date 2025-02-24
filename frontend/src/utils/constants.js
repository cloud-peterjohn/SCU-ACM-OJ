export const USER_AUTH = {
  user: {
    color: 'volcano',
    isAdmin: false
  },
  admin: {
    color: 'gold',
    isAdmin: true
  },
  superadmin: {
    color: 'black',
    isAdmin: true
  }
};
export const JUDGE_RESULT = {
'CP': {
  name: 'Compiling',
  abbr: 'CP',
  css: 'verdict-pending',
  color: 'grey',
  icon: 'md-information'
},
'JG': {
  name: 'Judging',
  abbr: 'JG',
  css: 'verdict-pending',
  color: 'grey',
  icon: 'md-information'
},
'PD': {
  name: 'Pending',
  abbr: 'PD',
  css: 'verdict-pending',
  color: 'grey',
  icon: 'md-information'
},
'AC': {
  name: 'Accepted',
  abbr: 'AC',
  css: 'verdict-accepted',
  color: '#5cb85c',
  icon: 'md-checkmark'
},
'TLE': {
  name: 'Time Limit Exceeded',
  abbr: 'TLE',
  css: 'verdict-failed',
  color: '#FFD700',
  icon: 'md-close'
},
'MLE': {
  name: 'Memory Limit Exceeded',
  abbr: 'MLE',
  css: 'verdict-failed',
  color: 'black',
  icon: 'md-close'
},
'RE': {
  name: 'Runtime Error',
  abbr: 'RE',
  css: 'verdict-failed',
  color: 'rebeccapurple',
  icon: 'md-close'
},
'SE': {
  name: 'System Error',
  abbr: 'SE',
  css: 'verdict-system-error',
  color: 'rebeccapurple',
  icon: 'md-close'
},
'WA': {
  name: 'Wrong Answer',
  abbr: 'WA',
  css: 'verdict-failed',
  color: '#d9534f',
  icon: 'md-close'
},
'PE': {
  name: 'Presentation Error',
  abbr: 'PE',
  css: 'verdict-failed',
  color: '#d9534f',
  icon: 'md-close'
},
'CE': {
  name: 'Compilation Error',
  abbr: 'CE',
  css: 'verdict-compile-error',
  color: 'orange',
  icon: 'md-close'
},
'CAN': {
  name: 'Canceled',
  abbr: 'CAN',
  css: 'verdict-canceled',
  color: '#02672e',
  icon: 'md-close'
},
'Default':{
  name: 'Unknown',
  abbr: 'Default',
  css: 'verdict-unknown',
  color: 'grey',
  icon: 'md-information'
}
};
