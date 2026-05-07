const loginIdValideator = new FieidValidator("txtLoginId", async function (
  val
) {
  if (!val) {
    return "请填写账号";
  }
  const resp = await API.exists(val);
  if (resp.data) {
    return "该账号已存在，请换个账号填写";
  }
});

const nicknameValidator = new FieidValidator("txtNickname", function (val) {
  if (!val) {
    return "请填写昵称";
  }
});

const txtLoginPwdValidator = new FieidValidator("txtLoginPwd", function (val) {
  if (!val) {
    return "请输入密码";
  }
});

const toPasswordValidator = new FieidValidator("txtLoginPwdConfirm", function (
  val
) {
  if (!val) {
    return "请输入相同的密码";
  }
  if (val !== txtLoginPwdValidator.input.value) {
    return "两次密码不一致，请重新输入";
  }
});

const form = $(".user-form");
form.addEventListener("submit", async function (e) {
  e.preventDefault();
  //统一验证账号，昵称，密码
  const result = await FieidValidator.validate(
    loginIdValideator,
    nicknameValidator,
    txtLoginPwdValidator,
    toPasswordValidator
  );
  console.log(result);

  if (!result) {
    //未通过注册
    return;
  }
  //通过注册后
  // 方法一：
  const data = {//获得账号，昵称，密码的值
    loginId: loginIdValideator.input.value,
    loginPwd: txtLoginPwdValidator.input.value,
    nickname: nicknameValidator.input.value,
  };
  const obj ={
    'username': loginIdValideator.input.value,
    'password': txtLoginPwdValidator.input.value,
    'nickname': nicknameValidator.input.value,
  }
  var xhr = new XMLHttpRequest()
        // 2.打开一个连接
        // 需要传递json
        xhr.open('post', 'http://127.0.0.1:8000/user/register');
        // 设置请求头 告诉后端我们要发送的是什么格式的数据
        xhr.setRequestHeader('Content-Type', 'application/json')
        // 3.发送请求
        // 二、给后端发送json数据
        xhr.send(JSON.stringify(obj))
        // 4.接收响应
        xhr.onreadystatechange = function () {
          // 请求发送完成 请求成功
          if (xhr.readyState === 4 && xhr.status === 200) {
            // console.log(xhr.responseText);//json格式数据
            var res = JSON.parse(xhr.responseText)
            console.log(res);
          }
        }

  console.log(data);
  const resp = await API.reg(data);
  if (resp.code === 0) {
    alert("注册成功，点击确定，跳转到登录页面");
    location.href = "./login.html";
  }

  // 方法二：
  //   const formData = new FormData(form); //传入表单dom，得到一个表单数据对象
  //   const data = Object.fromEntries(formData.entries());
  //   const resp = await API.reg(data);
  //   if (resp.code === 0) {
  //     alert("注册成功，点击确定，跳转到登录页面");
  //     location.href = "./login.html";
  //   }
});
