var API = (function () {
  const BASE_URL = "https://study.duyiedu.com";
  const TOKEN_KEY = "token";

  function get(path) {
    const headers = {};
    const token = localStorage.getItem(TOKEN_KEY);
    if (token) {
      headers.authorization = `Bearer ${token}`;
    }
    return fetch(BASE_URL + path, { headers });
  }

  function post(path, bodyObj) {
    const headers = {
      "Content-Type": "application/json",
    };
    const token = localStorage.getItem(TOKEN_KEY);
    if (token) {
      headers.authorization = `Bearer ${token}`;
    }
    return fetch(BASE_URL + path, {
      headers,
      method: "POST",
      body: JSON.stringify(bodyObj),
    });
  }

  async function reg(loginInfo) {
    const resp = await post("/api/user/reg", loginInfo);
    return await resp.json();
  }
  async function regOne(loginInfo) {
    const resp = await post("http://127.0.0.1:8000/user/register", loginInfo);
    return await resp.json();
  }

  async function login(loginInfo) {
    const resp = await post("/api/user/login", loginInfo);

    // const resp = await fetch(BASE_URL+"/api/user/login",{
    //   method: "POST",
    //   headers: {
    //     "Content-Type": "application/json",
    //   },
    //   body:JSON.stringify(loginInfo),
    // });

    const result = await resp.json();
    if (result.code === 0) {
      const token = resp.headers.get("authorization");
      localStorage.setItem(TOKEN_KEY, token);
    }
    return result;
  }
  // login({loginId:'xiao',loginPwd:'123123'}).then(h=>console.log(h));

  // 验证账号
  async function exists(loginId) {
    const resp = await get("/api/user/exists?loginId=" + loginId);
    return await resp.json();
  }

  async function profile() {
    const resp = await get("/api/user/profile");
    return await resp.json();
  }

  async function sendChat(content) {
    const resp = await post("/api/chat", { content });
    return await resp.json();
  }

  async function getHistory() {
    const resp = await get("/api/chat/history");
    return await resp.json();
  }
  // API.getHistory().then(h=>console.log(h));

  function loginOut() {
    localStorage.removeItem(TOKEN_KEY);
  }

  return {
    reg,
    regOne,
    login,
    exists,
    profile,
    sendChat,
    getHistory,
    loginOut,
  };
})();
