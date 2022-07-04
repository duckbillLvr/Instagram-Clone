package zeno.instagram.src.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import zeno.instagram.config.BaseException;
import zeno.instagram.config.BaseResponse;
import zeno.instagram.utils.JwtService;
import zeno.instagram.src.auth.model.*;

import static zeno.instagram.config.BaseResponseStatus.*;
import static zeno.instagram.utils.ValidationRegex.isRegexEmail;


@RestController
@RequestMapping("/auth")
public class AuthController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final AuthProvider authProvider;
    @Autowired
    private final AuthService authService;
    @Autowired
    private final JwtService jwtService;


    public AuthController(AuthProvider authProvider, AuthService authService, JwtService jwtService) {
        this.authProvider = authProvider;
        this.authService = authService;
        this.jwtService = jwtService;
    }

    // 로그인 API (jwt 인증)
    @ResponseBody
    @PostMapping("/logIn")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq) {
        try {
            if (postLoginReq.getEmail()==null)
                return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
            if (postLoginReq.getPassword()==null)
                return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
            if (!isRegexEmail(postLoginReq.getEmail()))
                return new BaseResponse<>(POST_USERS_INVALID_EMAIL);


            PostLoginRes postLoginRes = authService.logIn(postLoginReq);

            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // jwt 자동 로그인 API
     /* @ResponseBody
    @GetMapping("/jwt")
    public BaseResponse<GetAutoLoginRes> autologin() throws BaseException{
        try{
            if(jwtService.getJwt()==null){
                return new BaseResponse<>(EMPTY_JWT);
            }
            else if(authProvider.checkJwt(jwtService.getJwt())==1){
                return new BaseResponse<>(INVALID_JWT);

            }

            else{
                String jwt=jwtService.getJwt();
                int userIdx=jwtService.getUserIdx();
                GetAutoLoginRes getAutoRes = userProvider.getAuto(jwt,userIdx);
                return new BaseResponse<>(getAutoRes);
            }

        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }*/
}
