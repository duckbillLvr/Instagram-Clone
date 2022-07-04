package zeno.instagram.src.user.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetUserFeedRes {
    private boolean _isMyFeed; // 나의 피드와 다른 사람의 피드인지 구별
    private GetUserInfoRes getUserInfoRes;
    private List<GetUserPostsRes> getUserPostsResList;
}
