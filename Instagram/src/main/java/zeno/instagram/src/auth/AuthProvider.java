package zeno.instagram.src.auth;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zeno.instagram.config.BaseException;
import zeno.instagram.src.auth.model.*;
import zeno.instagram.utils.JwtService;

import static zeno.instagram.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service
public class AuthProvider {

    private final AuthDao authDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public AuthProvider(AuthDao authDao, JwtService jwtService) {
        this.authDao = authDao;
        this.jwtService = jwtService;
    }

    // 유저 확인
    public int checkUserExist(int userIdx) throws BaseException{
        try{
            return authDao.checkUserExist(userIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 이메일 확인
    public int checkEmailExist(String email) throws BaseException{
        try{
            return authDao.checkEmailExist(email);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 이메일 확인
    public String checkUserStatus(String email) throws BaseException{
        //   try{
        return authDao.checkUserStatus(email);
        // } catch (Exception exception){
        //   throw new BaseException(DATABASE_ERROR);
        //}
    }
}
