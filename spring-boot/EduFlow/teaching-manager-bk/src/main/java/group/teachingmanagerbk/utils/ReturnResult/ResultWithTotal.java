package group.teachingmanagerbk.utils.ReturnResult;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 带有数量的返回结果
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ResultWithTotal extends Result {
    Integer total;  //共计
}
