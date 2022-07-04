package zeno.instagram.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetUsersRes {
    private List<GetUserRes> getUserResList;
}
