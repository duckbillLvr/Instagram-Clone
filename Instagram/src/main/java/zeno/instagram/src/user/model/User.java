package zeno.instagram.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class User {
    private int userIdx;
    private String name;
    private String nickName;
    private String email;
    private String password;
    private String phone;
    private String profileImgUrl;
    private String website;
    private String introduce;
    private String status;
    private Date createdAt;
    private Date updatedAt;
}
