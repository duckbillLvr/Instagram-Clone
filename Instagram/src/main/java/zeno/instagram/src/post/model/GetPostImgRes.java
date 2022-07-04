package zeno.instagram.src.post.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetPostImgRes {
    private int postImgUrlIdx;
    private String imgUrl;
}
