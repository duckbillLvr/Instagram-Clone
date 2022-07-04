package zeno.instagram.src.post;

import zeno.instagram.config.BaseException;
import zeno.instagram.config.BaseResponse;
import zeno.instagram.src.post.model.*;
import zeno.instagram.src.post.model.GetPostsRes;
import zeno.instagram.src.post.model.PatchPostReq;
import zeno.instagram.src.post.model.PostPostReq;
import zeno.instagram.src.post.model.PostPostRes;
import zeno.instagram.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static zeno.instagram.config.BaseResponseStatus.*;


@RestController
@RequestMapping("/posts")
public class PostController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final PostProvider postProvider;
    @Autowired
    private final PostService postService;
    @Autowired
    private final JwtService jwtService;


    public PostController(PostProvider postProvider, PostService postService, JwtService jwtService) {
        this.postProvider = postProvider;
        this.postService = postService;
        this.jwtService = jwtService;
    }

    /**
     * 팔로잉 게시물 조회 API
     * [GET] /posts/{userIdx}
     *
     * @param userIdx
     * @return BaseResponse<List < GetPostsRes>>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetPostsRes>> getPosts(@RequestParam("userIdx") int userIdx) {
        try {
            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
            List<GetPostsRes> getPostsResList = postProvider.retrievePosts(userIdx);

            return new BaseResponse<>(getPostsResList);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 게시물 게시 API
     * [POST] /posts
     *
     * @param postPostReq
     * @return BaseResponse<PostPostRes>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostPostRes> createPost(@RequestBody PostPostReq postPostReq) {
        // 내용 Validation
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            if(postPostReq.getUserIdx() != userIdxByJwt)
                return new BaseResponse<>(INVALID_USER_JWT);

            if (postPostReq.getContent() == null) {
                return new BaseResponse<>(POST_POSTS_EMPTY_CONTENTS);
            }
            // 내용 길이 Validation 450자 이하 허용
            if (postPostReq.getContent().length() > 450) {
                return new BaseResponse<>(POST_POSTS_INVALID_CONTENTS);
            }
            // 이미지가 들어갔는지 확인
            if (postPostReq.getPostImgsUrl() == null || postPostReq.getPostImgsUrl().size() < 1) {
                return new BaseResponse<>(POST_POSTS_EMPTY_IMG);
            }

            PostPostRes postPostRes = postService.createPost(postPostReq.getUserIdx(), postPostReq);
            return new BaseResponse<>(postPostRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 게시물 수정 API
     * [POST] /posts/{postIdx}
     *
     * @param postIdx
     * @param patchPostReq
     * @return
     */
    @ResponseBody
    @PatchMapping("/{postIdx}")
    public BaseResponse<String> modifyPost(@PathVariable("postIdx") int postIdx, @RequestBody PatchPostReq patchPostReq) {
        if (patchPostReq.getContent() == null) {
            return new BaseResponse<>(POST_POSTS_EMPTY_CONTENTS);
        }
        if (patchPostReq.getContent().length() > 450) {
            return new BaseResponse<>(POST_POSTS_EMPTY_CONTENTS);
        }
        try {
            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
//            postService.modifyPost(userIdxByJwt, postIdx, patchPostReq);
            postService.modifyPost(patchPostReq.getUserIdx(), postIdx, patchPostReq);
            String result = "회원정보 수정을 완료하였습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 게시물 삭제
    @ResponseBody
    @PatchMapping("/{postIdx}/status")
    public BaseResponse<String> deleteUser(@PathVariable("postIdx") int postIdx) {
        try {

            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
//            postService.deletePost(userIdxByJwt, postIdx);

            postService.deletePost(12, postIdx);
            String result = "삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}
