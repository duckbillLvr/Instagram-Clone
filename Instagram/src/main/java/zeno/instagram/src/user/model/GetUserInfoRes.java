package zeno.instagram.src.user.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetUserInfoRes {
    private String nickName;
    private String name;
    private String profileImgUrl;
    private String website;
    private String introduction;
    private int followerCount;
    private int followeeCount;
    private int postCount;
}
