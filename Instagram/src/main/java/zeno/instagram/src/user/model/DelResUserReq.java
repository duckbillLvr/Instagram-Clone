package zeno.instagram.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DelResUserReq {
    private String email;
    private String password;
}
