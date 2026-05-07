package group.teachingmanagerbk.utils.ReturnResult;

import lombok.Data;

/**
 * 统一响应格式
 */
@Data
public class Result {
    private Integer code;
    private String message;
    private Object data;

    public Result success() {
        this.code = 1;
        this.message = "success";
        return this;
    }

    public Result success(Object data) {
        this.success();
        this.data = data;
        return this;
    }

    public Result error() {
        this.code = 0;
        this.message = "error";
        return this;
    }

    public Result error(String message) {
        this.error();
        this.message = message;
        return this;
    }

    public Result error(Integer code, String message) {
        this.code = code;
        this.message = message;
        this.data = null;
        return this;
    }
}
