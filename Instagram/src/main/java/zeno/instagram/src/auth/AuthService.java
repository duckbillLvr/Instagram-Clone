package zeno.instagram.src.auth;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zeno.instagram.config.BaseException;
import zeno.instagram.utils.JwtService;
import zeno.instagram.src.auth.model.*;
import zeno.instagram.utils.SHA256;

import static zeno.instagram.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class AuthService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AuthDao authDao;
    private final AuthProvider authProvider;
    private final JwtService jwtService;


    @Autowired
    public AuthService(AuthDao authDao, AuthProvider authProvider, JwtService jwtService) {
        this.authDao = authDao;
        this.authProvider = authProvider;
        this.jwtService = jwtService;
    }

    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException {
        User user = authDao.getPwd(postLoginReq);
        String encryptPwd;

        if (authProvider.checkUserStatus(postLoginReq.getEmail()).equals("INACTIVE"))
            throw new BaseException(INACTIVE_ACCOUNT);

        try {
            encryptPwd = new SHA256().encrypt(postLoginReq.getPassword());

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

        if(user.getPassword().equals(encryptPwd)){
            int userIdx = user.getUserIdx();
            String jwt = jwtService.createJwt(userIdx);
            return new PostLoginRes(userIdx, jwt);
        }
        else
            throw new BaseException(FAILED_TO_LOGIN);
    }
}
