package zeno.instagram.src.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import zeno.instagram.src.auth.model.*;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class AuthDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 로그인
    public User getPwd(PostLoginReq postLoginReq){
        String getPwdQuery = "SELECT userIdx, name, nickName, email, password from User where email = ?";
        String getPwdParams = postLoginReq.getEmail();

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs, rowNum) -> new User(
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("nickName"),
                        rs.getString("email"),
                        rs.getString("password")
                ), getPwdParams);
    }

    // 유저 확인
    public int checkUserExist(int userIdx){
        String checkUserExistQuery = "SELECT exists(SELECT userIdx FROM User WHERE userIdx = ?)";
        int checkUserExistParams = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserExistQuery,
                int.class,
                checkUserExistParams);
    }

    // 이메일 확인
    public int checkEmailExist(String email){
        String checkEmailQuery = "SELECT EXISTS(SELECT email from User where email = ?)";
        String checkEmailParams = email;

        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);
    }

    public String checkUserStatus(String email){
        String checkUserStatusQuery = "SELECT status FROM User where email=?";
        String checkUserStatusParams = email;

        return this.jdbcTemplate.queryForObject(checkUserStatusQuery,
                String.class,
                checkUserStatusParams);
    }
}
