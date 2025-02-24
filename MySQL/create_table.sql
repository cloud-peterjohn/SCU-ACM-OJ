create table user
(
    id          int unsigned auto_increment comment 'ID'
        primary key,
    username    varchar(200)                not null comment '用户名',
    password    varchar(320)                null comment '密码',
    nickname    varchar(100) default ''     null comment '昵称',
    email       varchar(128) default ''     null comment '邮箱',
    user_pic    varchar(128) default ''     null comment '头像',
    gender      char         default ''     null comment '性别',
    auth        varchar(300) default 'user' not null comment '权限',
    create_time datetime                    not null comment '创建时间',
    update_time datetime                    not null comment '修改时间',
    constraint user_email
        unique (email),
    constraint username
        unique (username)
)
    comment '用户表';
create table problem
(
    id           int unsigned auto_increment comment 'ID'
        primary key,
    title        varchar(100)                 not null comment '题目标题',
    problem_url  varchar(1280) default ''     null comment '题目配置',
    create_user  int unsigned                 not null comment '创建人ID',
    create_time  datetime                     not null comment '创建时间',
    update_time  datetime                     not null comment '修改时间',
    memory_limit int unsigned                 not null comment '内存限制',
    time_limit   int unsigned                 not null comment '时间限制',
    is_file      tinyint(1)    default 0      null comment '是否提交文件',
    file_name    varchar(500)  default 'null' null,
    constraint problem_title
        unique (title),
    constraint fk_problem_user
        foreign key (create_user) references user (id)
            on update cascade on delete cascade
)
    comment '题目表';

create table judgetemplate
(
    id          int unsigned auto_increment comment 'ID'
        primary key,
    language    varchar(50)    null comment '模板语言',
    description varchar(10000) null,
    comment     varchar(5000)  null,
    constraint judgetemplate_language
        unique (language),
    constraint judgetemplate_pk
        unique (language),
    constraint language
        unique (language)
)
    comment '评测模板表';
create table judgerecord
(
    id              int unsigned auto_increment comment 'ID'
        primary key,
    problem_id      int unsigned                  not null comment '评测题目ID',
    user_id         int unsigned                  not null comment '评测用户ID',
    template_id     int unsigned   default '1'    not null comment '评测模板ID',
    judge_time      datetime                      not null comment '评测时间',
    judge_result    varchar(100)   default 'null' not null comment '评测结果',
    runtime         int unsigned   default '0'    null comment '运行时间',
    runmemory       int unsigned   default '0'    null comment '运行内存',
    code            varchar(10000) default 'null' null,
    compile_message varchar(5000)  default 'null' null comment '编译信息',
    language        varchar(50)                   not null,
    contest_id      int unsigned                  null comment '比赛ID',
    constraint fk_judgeRecord_problem
        foreign key (problem_id) references problem (id)
            on update cascade on delete cascade,
    constraint fk_judgeRecord_user
        foreign key (user_id) references user (id)
            on update cascade on delete cascade,
    constraint fk_template_id
        foreign key (template_id) references judgetemplate (id)
            on update cascade on delete cascade,
    constraint judgerecord_judgetemplate_language_fk
        foreign key (language) references judgetemplate (language)
            on update cascade on delete cascade
)
    comment '评测记录表';

create index fk_judgeRecord_judgeTemplate
    on judgerecord (language);
		
create table contest
(
    id          int unsigned auto_increment comment 'ID'
        primary key,
    title       varchar(300) null comment '比赛标题',
    create_user int unsigned not null comment '创建人ID',
    start_time  datetime     not null comment '开始时间',
    end_time    datetime     not null comment '结束时间',
    create_time datetime     not null comment '创建时间',
    update_time datetime     not null comment '修改时间',
    constraint contest_id
        unique (id),
    constraint contest_pk
        unique (title),
    constraint fk_contest_user
        foreign key (create_user) references user (id)
            on update cascade on delete cascade
)
    comment '题目表';
		
create table contestrecord
(
    id         int unsigned auto_increment comment 'ID'
        primary key,
    contest_id int unsigned             not null comment '比赛ID',
    user_id    int unsigned             not null comment '用户ID',
    ranking    int unsigned default '0' null comment '比赛排名',
    acnum      int unsigned default '0' null comment '过题数量',
    alltime    int unsigned default '0' null comment '总罚时',
    constraint fk_contestRecord_contest
        foreign key (contest_id) references contest (id)
            on update cascade on delete cascade,
    constraint fk_contestRecord_user
        foreign key (user_id) references user (id)
            on update cascade on delete cascade
)
    comment '比赛记录表';

create table belong
(
    problem_id int unsigned not null comment '题目ID',
    contest_id int unsigned not null comment '比赛ID',
    constraint belong_pk
        unique (problem_id),
    constraint fk_belong_contest
        foreign key (contest_id) references contest (id)
            on update cascade on delete cascade,
    constraint fk_belong_problem
        foreign key (problem_id) references problem (id)
            on update cascade on delete cascade
)
    comment '题目归属表';
