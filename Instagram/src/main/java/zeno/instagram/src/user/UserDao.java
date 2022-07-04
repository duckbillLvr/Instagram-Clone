package zeno.instagram.src.user;

import zeno.instagram.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import zeno.instagram.utils.SHA256;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) { this.jdbcTemplate = new JdbcTemplate(dataSource); }

    public List<GetUserRes> getUsers() {
        String getUsersQuery = "select userIdx,name,nickName,phone,email from Instagram.User";
        return this.jdbcTemplate.query(getUsersQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("nickName"),
                        rs.getString("phone"),
                        rs.getString("email")
                ));
    }

    public GetUserInfoRes selectUserInfo(int userIdx) {
        String selectUsersInfoQuery = "SELECT nickName, name, profileImgUrl, website, introduce, \n" +
                "       IF(FollowerCount is null, 0, FollowerCount) as followerCount,\n" +
                "       IF(FolloweeCount is null, 0, FolloweeCount) as followeeCount,\n" +
                "       IF(postCount is null, 0, postCount) as postCount\n" +
                "FROM User u\n" +
                "         left JOIN (SELECT userIdx, COUNT(postIdx) as postCount FROM Post WHERE STATUS='ACTIVE' group by userIdx) p ON p.userIdx=u.userIdx\n" +
                "         left JOIN (SELECT followerIdx, COUNT(followIdx) as FollowerCount FROM Follow WHERE STATUS='ACTIVE' group by followerIdx) f1 ON f1.followerIdx=u.userIdx\n" +
                "         left JOIN (SELECT followeeIdx, COUNT(followIdx) as FolloweeCount FROM Follow WHERE STATUS='ACTIVE' group by followeeIdx) f2 ON f2.followeeIdx=u.userIdx\n" +
                "WHERE u.userIdx=? and u.status = 'ACTIVE'";

        int selectUserInfoParam = userIdx;

        return this.jdbcTemplate.queryForObject(selectUsersInfoQuery,
                (rs, rowNum) -> new GetUserInfoRes (
                        rs.getString("nickName"),
                        rs.getString("name"),
                        rs.getString("profileImgUrl"),
                        rs.getString("website"),
                        rs.getString("introduce"),
                        rs.getInt("followerCount"),
                        rs.getInt("followeeCount"),
                        rs.getInt("postCount")
                ), selectUserInfoParam);
    }

    public List<GetUserPostsRes> selectUserPosts(int userIdx) {
        /* 특정 유저에 대하여 해당 유저가 작성한 게시물의 대표 이미지를 출력*/
        String selectUserPostsQuery = "SELECT p.postIdx as postIdx,\n" +
                "       pi.imgUrl as postImgUrl\n" +
                "From Post as p\n" +
                "       join PostImgUrl pi on p.postIdx = pi.postIdx and pi.status = 'ACTIVE'\n" +
                "       join User as u on u.userIdx = p.userIdx\n" +
                "WHERE p.status = 'ACTIVE' and u.userIdx = ?\n" +
                "group by p.postIdx\n" +
                "HAVING min(pi.postImgUrlIdx) order by p.postIdx";

        int selectUserPostsParam = userIdx;

        return this.jdbcTemplate.query(selectUserPostsQuery,
                (rs, rowNum) -> new GetUserPostsRes(
                        rs.getInt("postIdx"),
                        rs.getString("postImgUrl")
                ), selectUserPostsParam);
    }

    public GetUserRes getUsersByEmail(String email) {
        String getUsersByEmailQuery = "select userIdx,name,nickName,phone,email from Instagram.User where email=?";
        String getUsersByEmailParams = email;
        return this.jdbcTemplate.queryForObject(getUsersByEmailQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("nickName"),
                        rs.getString("phone"),
                        rs.getString("email")),
                getUsersByEmailParams);
    }


    public GetUserRes getUsersByIdx(int userIdx) {
        String getUsersByIdxQuery = "select userIdx,name,nickName,phone,email from Instagram.User where userIdx=?";
        int getUsersByIdxParams = userIdx;
        return this.jdbcTemplate.queryForObject(getUsersByIdxQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("nickName"),
                        rs.getString("phone"),
                        rs.getString("email")),
                getUsersByIdxParams);
    }

    public int createUser(PostUserReq postUserReq) {
        String createUserQuery = "INSERT INTO Instagram.User (name, nickName, email, password, phone, profileImgUrl, website, introduce) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Object[] createUserParams = new Object[]{postUserReq.getName(), postUserReq.getNickName(), postUserReq.getEmail(),
                postUserReq.getPassword(), postUserReq.getPhone(), postUserReq.getProfileImgUrl(), postUserReq.getWebsite(), postUserReq.getIntroduce()};

        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    public int checkEmail(String email) {
        String checkEmailQuery = "select exists(select email from Instagram.User where email = ?)";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);

    }

    public int checkNickName(String nickName) {
        String checkNickNameQuery = "select exists(select nickName from Instagram.User where nickName = ?)";
        String checkNickNameParams = nickName;
        return this.jdbcTemplate.queryForObject(checkNickNameQuery, int.class, checkNickNameParams);

    }

    public int checkPhone(String phone) {
        String checkPhoneQuery = "select exists(select phone from Instagram.User where phone = ?)";
        String checkPhoneParams = phone;
        return this.jdbcTemplate.queryForObject(checkPhoneQuery, int.class, checkPhoneParams);
    }

    public int checkUserExist(int userIdx){
        String checkUserExistQuery = "select exists(select userIdx from User where userIdx = ?)";
        int checkUserExistParams = userIdx;

        return this.jdbcTemplate.queryForObject(checkUserExistQuery,
                int.class,
                checkUserExistParams);
    }

    public int checkUserPassword(String email, String password) {
        String checkPasswordQuery = "select password from Instagram.User where email = ?";
        String checkPasswordParams = email;
        String correctPassword = this.jdbcTemplate.queryForObject(checkPasswordQuery, String.class, checkPasswordParams);

        String encryptPassword = new SHA256().encrypt(password);
//        String encryptPassword = password; // jwt 비밀번호 미사용시

        if (correctPassword.equals(encryptPassword)) return 1;
        else return 0;
    }

    public int modifyUserName(PatchUserReq patchUserReq) {
        String modifyUserNameQuery = "update Instagram.User set nickName = ? where userIdx = ? ";
        Object[] modifyUserNameParams = new Object[]{patchUserReq.getNickName(), patchUserReq.getUserIdx()};

        return this.jdbcTemplate.update(modifyUserNameQuery, modifyUserNameParams);
    }

    public String checkUserStatus(String email) {
        String checkUserStatusQuery = "select status from Instagram.User where email=?";
        String checkUserEmailParams = email;

        return this.jdbcTemplate.queryForObject(checkUserStatusQuery, String.class, checkUserEmailParams);
    }

    public DelResUserRes deleteUser(DelResUserReq delResUserReq, String order) {
        String delResUserQuery = null;
        if (order.equals("delete"))
            delResUserQuery = "UPDATE Instagram.User SET status = 'INACTIVE' WHERE email=?";
        else if (order.equals("restore"))
            delResUserQuery = "UPDATE Instagram.User SET status = 'ACTIVE' WHERE email=?";
        String deleteUserEmailParams = delResUserReq.getEmail();

        this.jdbcTemplate.update(delResUserQuery, deleteUserEmailParams);

        String getUsersStatusQuery = "select name, nickName, phone, email, status from Instagram.User where email=?";
        return this.jdbcTemplate.queryForObject(getUsersStatusQuery,
                (rs, rowNum) -> new DelResUserRes(
                        rs.getString("name"),
                        rs.getString("nickName"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("status")),
                deleteUserEmailParams);
    }
}
