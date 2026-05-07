//用户登录和注册的表单项验证的通用代码
/**
 * 对某一个表单项进行验证的构造函数
 */
class FieidValidator {
  /**
   * 构造器
   * @param {String} txtID 文本框的ID
   * @param {Function} validatorFunc 验证规则函数
   */
  constructor(txtID, validatorFunc) {
    this.input = $("#" + txtID);
    this.p = this.input.nextElementSibling;
    // console.log(this.input, this.p);
    this.validatorFunc = validatorFunc;
    this.input.onblur = () => {
      //鼠标失去焦点进行验证账号是否存在
      this.validate();
    };
  }
  //验证，成功返回true，失败返回false
  async validate() {
    const err = await this.validatorFunc(this.input.value);
    if (err) {
      this.p.innerText = err;
      return false;
    } else {
      this.p.innerText = "";
      return true;
    }
  }

  /**
   * 对传入的所有验证器进行统一的验证，如果所有的验证均通过，测返回true，否则返回false
   * @param  {FieidValidator[]} validators
   */
  static async validate(...validators) {
    const proms = validators.map((v) => v.validate());
    const results = await Promise.all(proms);
    return results.every((v) => v);
  }
}

// function text() {
//   FieidValidator.validate(loginIdValideator, nicknameValidator).then(
//     (result) => {
//       console.log(result);
//     }
//   );
// }


