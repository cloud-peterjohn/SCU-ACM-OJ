<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>邮箱验证码</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f7f7f7;
            padding: 20px;
        }

        .container {
            max-width: 600px;
            margin: 0 auto;
            background-color: #fff;
            padding: 20px;
            border-radius: 5px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        h1 {
            color: #333;
            text-align: center;
        }

        p {
            color: #666;
            font-size: 16px;
            line-height: 1.6;
        }


        .code-container {
            background-color: #f0f0f0;
            padding: 10px;
            text-align: center;
            border-radius: 5px;
            margin-top: 10px;
        }

        .code {
            font-size: 24px;
            font-weight: bold;
            margin: 0;
            padding: 5px; /* 添加内边距增加文字间距 */
        }


        .footer {
            background: linear-gradient(to right, #007bff, #00bfff);
            height: 10px;
            border-radius: 5px;
            margin-top: 20px;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>尊敬的用户，</h1>

    <p>您收到来自scuoj的邮件，用于验证您的邮箱地址。</p>

    <div class="code-container">
        <p class="code">${verifyCode}</p>
    </div>

    <p>请使用此验证码完成验证过程 ( 有效时间 ${validTime} 分钟 )。</p>

    <p>如果您没有请求此验证码，请忽略此邮件。</p>

    <p>感谢您的配合，</p>

    <div class="footer"></div>
</div>
</body>
</html>
