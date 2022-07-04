package zeno.instagram.src.user;

import zeno.instagram.config.BaseException;

import zeno.instagram.src.user.model.*;
import zeno.instagram.utils.JwtService;
import zeno.instagram.utils.SHA256;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static zeno.instagram.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;


    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;

    }

    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
        // 이메일 중복 확인
        if (userProvider.checkEmail(postUserReq.getEmail()) == 1) {
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
            // 닉네임 중복 확인
        }
        if (userProvider.checkNickName(postUserReq.getNickName()) == 1) {
            throw new BaseException(POST_USERS_EXISTS_NICKNAME);
            // 전화번호 중복 확인
        }
        if (userProvider.checkPhone(postUserReq.getPhone()) == 1) {
            throw new BaseException(POST_USERS_EXISTS_PHONE);
        }

        String pwd;
        try {
            //암호화
            pwd = new SHA256().encrypt(postUserReq.getPassword());
            postUserReq.setPassword(pwd);
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        try {
            int userIdx = userDao.createUser(postUserReq); // 유저 생성
            //jwt 발급.
            // TODO: jwt는 다음주차에서 배울 내용입니다!
            String jwt = jwtService.createJwt(userIdx);
            return new PostUserRes(jwt, userIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyUserName(PatchUserReq patchUserReq) throws BaseException {
        try {
            int result = userDao.modifyUserName(patchUserReq);
            if (result == 0) {
                throw new BaseException(MODIFY_FAIL_USERNAME);
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public DelResUserRes deleteUser(DelResUserReq delResUserReq, String order) throws BaseException {
        // 변경하려는 상태가 현재와 같은지 확인
        String nowUserStatus;
        try {
            nowUserStatus = userProvider.checkStatus(delResUserReq.getEmail());
            if (nowUserStatus.equals("ACTIVE"))
                nowUserStatus = "restore";
            else
                nowUserStatus = "delete";
        } catch (Exception e){
             throw new BaseException(DATABASE_ERROR);
        }

        if (order.equals(nowUserStatus)) {
            throw new BaseException(ALREADY_SAME_STATUS);
        }

        // 다를 경우 실행
        try {
            DelResUserRes delResUserRes = userDao.deleteUser(delResUserReq, order);

            return delResUserRes;
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
