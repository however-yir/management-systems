package group.teachingmanagerbk.dto.login;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ModifyPasswordParam extends LoginParam {
    private String userId;      //用户id
}
