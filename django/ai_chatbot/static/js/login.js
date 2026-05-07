//账号
const txtLoginId = new FieidValidator("txtLoginId", function (val) {
  if (!val) {
    return "请输入账号";
  }
});
//密码
const txtLoginPwd = new FieidValidator("txtLoginPwd", function (val) {
  if (!val) {
    return "请输入密码";
  }
});

//form表单提交
const form = $(".user-form");
form.onsubmit = async function (e) {
  e.preventDefault();
  const reslut = await FieidValidator.validate(txtLoginId, txtLoginPwd); //统一验证登录账号和密码
  // console.log(reslut);
  if (!reslut) {
    //未通过验证
    return;
  }
  const data = {
    //获得账号和密码的值
    loginId: txtLoginId.input.value,
    loginPwd: txtLoginPwd.input.value,
  };
  //   console.log(data);

  const resp = await API.login(data);
  if (resp.code === 0) {
    alert("登录成功，点击跳转到首页");
    location.href = "/index";
  } else {
    txtLoginId.p.innerText = "密码或账号错误";
    loginPwd.p.innerText = "";
  }
};
