(async function () {
  //验证是否有登录，如果没有登录，跳转到登录页面，如果有登陆，获得用户信息
  const resp = await API.profile();
  const user = resp.data; //获得登录后用户的个人信息
  //   console.log(resp, user);
  if (!user) {
    alert("未登录或登录已过期");
    location.href = "/";
    return;
  }

  //下面的代码一定时登录状态

  //设置用户信息
  const nickname = $("#nickname");
  const loginId = $("#loginId");
  nickname.innerText = user.nickname;
  loginId.innerText = user.loginId;
  //退出登录
  const close = $(".close");
  close.onclick = function () {
    API.loginOut();
    location.href = "/";
  };


  function addChat(chatinof) {
    const div = $$$("div");
    div.classList.add("chat-item");
    if (chatinof.from) {
      div.classList.add("me");
    }

    const img = $$$("img");
    img.className = "chat-avatar";
    img.src = chatinof.from ? "../static/asset/avatar.png" : "../static/asset/robot-avatar.jpg";

    const content = $$$("div");
    content.className = "chat-content";
    content.innerText = chatinof.content;

    const data = $$$("div");
    data.className = "chat-date";
    data.innerText = formatData(chatinof.createdAt);

    //把创建好的元素添加到指定的位置；
    div.appendChild(img);
    div.appendChild(content);
    div.appendChild(data);

    const container = $(".chat-container");
    container.appendChild(div);
  }

  function formatData(time) {
    const date = new Date(time);
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, "0");
    const day = date.getDate().toString().padStart(2, "0");
    const hour = date.getHours().toString().padStart(2, "0");
    const minute = date.getMinutes().toString().padStart(2, "0");
    const second = date.getSeconds().toString().padStart(2, "0");
    return `${year}-${month}-${day} ${hour}:${minute}:${second}`;
  }

  //加载历史记录
  const val = await API.getHistory();
  //   console.log(val);
  for (const item of val.data) {
    //addChat(item);
    scrollBottom();
  }

  //让聊天记录的滚动条滚到最后
  function scrollBottom() {
    const container = $(".chat-container");
    const height = container.scrollHeight;
    container.scrollTop = height;
    // console.log(height)
  }

  // 发送聊天信息
  async function sendChat() {
    const txt = $("#txtMsg");//获取dom元素
    const content = txt.value.trim();
    if (!content) {
      return;
    }
    //用户发送消息
    addChat({
      from: user.loginId,
      to: null,
      createdAt: Date.now(),
      content,
    });
    txt.value = "";
    scrollBottom();
    const res =await API.sendChat(content);//发送聊天消息
    console.log(res);
    const obj = {
      question:content,
      text:res.data.content
    }
      var xhr = new XMLHttpRequest()
        // 2.打开一个连接
        // 需要传递json
        xhr.open('post', 'http://127.0.0.1:8000/add_info');
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
    //机器人回复的消息
    addChat({
      from: null,
      to: user.loginId,
      ...res.data,
    });
    scrollBottom();
    // console.log(res);
  }

//发送消息事件
const msgContainer = $('.msg-container');
msgContainer.onsubmit = function(e) {
    e.preventDefault();
    sendChat();
}

})();
