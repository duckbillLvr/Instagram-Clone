package zeno.instagram.src.user;

import zeno.instagram.config.BaseResponse;
import zeno.instagram.config.BaseException;
import zeno.instagram.src.user.model.*;
import zeno.instagram.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static zeno.instagram.config.BaseResponseStatus.*;
import static zeno.instagram.utils.ValidationRegex.isRegexEmail;
import static zeno.instagram.utils.ValidationRegex.isRegexPassword;

@RestController
@RequestMapping("/users")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;


    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService) {
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }


    /**
     * 회원 조회 API
     * [GET] /users
     * 이메일과 비밀번호를 이용하여 회원조회
     * [GET] /users/userInfo?Email=?&Pwd=?
     *
     * @param email, pwd
     * @return BaseResponse<GetUserRes>
     */
//    Query String
    @ResponseBody
    @GetMapping("/userInfo") // (GET) 127.0.0.1:8080/users?Email=&Pwd=
    public BaseResponse<GetUserRes> getUsers(@RequestParam(value = "Email", required = true) String email, @RequestParam(value = "Pwd") String pwd) {
        try {
            // TODO: email 관련한 짧은 validation 예시입니다. 그 외 더 부가적으로 추가해주세요!
            if (email.length() == 0) {
                return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
            }
            // 이메일 정규표현
            if (!isRegexEmail(email)) {
                return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
            }
            // 비밀번호 확인
            if (userProvider.checkPassword(email, pwd) == 0)
                return new BaseResponse<>(FAILED_TO_LOGIN);


            GetUserRes getUsersRes = userProvider.getUsersByEmail(email);
            return new BaseResponse<>(getUsersRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/feed/{userIdx}") // (GET) 127.0.0.1:8080/users/feed/12
    public BaseResponse<GetUserFeedRes> getUserFeed(@PathVariable("userIdx") int userIdx) {
        try {
            GetUserFeedRes getUserFeedRes = userProvider.retrieveUserFeed(userIdx, userIdx);

            return new BaseResponse<>(getUserFeedRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 모든 회원 조회 API
     * [GET] /users/getAll
     *
     * @return BaseResponse<List < GetUserRes>>
     */
    @ResponseBody
    @GetMapping("/getAll")
    public BaseResponse<GetUsersRes> getAllUsers() {
        System.out.println("Get All User");
        try {
            List<GetUserRes> getAllUsers = userProvider.getAllUsers();
            GetUsersRes getUsersRes = new GetUsersRes(getAllUsers);

            return new BaseResponse<>(getUsersRes);
        } catch (BaseException e) {
            return new BaseResponse<>((e.getStatus()));
        }
    }

    /**
     * 회원 조회 API
     * [GET] /users/{userIdx}
     * userIdx를 이용한 회원조회
     *
     * @param userIdx
     * @return BaseResponse
     */
    @ResponseBody
    @GetMapping("/{userIdx}") // (GET) 127.0.0.1:8080/users/:userIdx
    public BaseResponse<GetUserRes> getUserByIdx(@PathVariable("userIdx") int userIdx) {
        try {
            GetUserRes getUsersRes = userProvider.getUsersByIdx(userIdx);
            return new BaseResponse<>(getUsersRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 회원가입 API
     * [POST] /users
     *
     * @return BaseResponse<PostUserRes>
     */
    // Body
    @ResponseBody
    @PostMapping("") // (POST) 127.0.0.1:8080/users
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {
        if (postUserReq.getEmail() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        // 이메일 정규표현
        if (!isRegexEmail(postUserReq.getEmail())) {
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        if (postUserReq.getName() == null)
            return new BaseResponse<>(POST_USERS_EMPTY_NAME);
        if (postUserReq.getNickName() == null)
            return new BaseResponse<>(POST_USERS_EMPTY_NICKNAME);
        if (postUserReq.getPassword() == null)
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
        if (postUserReq.getPhone() == null)
            return new BaseResponse<>(POST_USERS_EMPTY_PHONE);
        // 전화번호 정규표현
        if (!isRegexPassword(postUserReq.getPhone()))
            return new BaseResponse<>(POST_USERS_INVALID_PHONE);

        try {
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 유저정보변경 API
     * [PATCH] /users/:userIdx
     *
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{userIdx}") // (PATCH) 127.0.0.1:9000/users/:userIdx
    public BaseResponse<String> modifyUserName(@PathVariable("userIdx") int userIdx, @RequestBody User user) {
        try {
            /* TODO: jwt는 다음주차에서 배울 내용입니다!
            jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            */

            PatchUserReq patchUserReq = new PatchUserReq(userIdx, user.getNickName());
            userService.modifyUserName(patchUserReq);

            String result = "";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 유저정보삭제/복구 API
     * [PATCH] /users/status/{order}
     * 유저의 이메일과 비밀번호를 입력 받고 유저 정보 삭제/복구(STATUS 변경)
     * String order: delete(삭제), restore(복구)
     *
     * @return BaseResponse
     */
    @ResponseBody
    @PatchMapping("/status/{order}")
    public BaseResponse<DelResUserRes> deleteUser(@RequestBody DelResUserReq delResUserReq, @PathVariable("order") String order) {
        try {
            //로그인 가능 확인
            if (userProvider.checkPassword(delResUserReq.getEmail(), delResUserReq.getPassword()) == 0)
                return new BaseResponse<>(FAILED_TO_LOGIN);

            DelResUserRes delResUserRes = userService.deleteUser(delResUserReq, order);

            return new BaseResponse<>(delResUserRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
