package zeno.instagram.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostUserReq {
    private String name;
    private String nickName;
    private String email;
    private String password;
    private String phone;
    private String profileImgUrl;
    private String website;
    private String introduce;
}
