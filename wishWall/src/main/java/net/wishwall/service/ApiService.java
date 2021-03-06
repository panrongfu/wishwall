package net.wishwall.service;

import net.wishwall.domain.AllChatroomsDTO;
import net.wishwall.domain.ApplyAddFriendListDTO;
import net.wishwall.domain.CenterPicDTO;
import net.wishwall.domain.CodeByNameDTO;
import net.wishwall.domain.FriendIdsDTO;
import net.wishwall.domain.FriendLikeNameDTO;
import net.wishwall.domain.FriendListDTO;
import net.wishwall.domain.GroupDTO;
import net.wishwall.domain.GroupsDTO;
import net.wishwall.domain.LoginDTO;
import net.wishwall.domain.ProvCityAreaDTO;
import net.wishwall.domain.RegisterDTO;
import net.wishwall.domain.ResultDTO;
import net.wishwall.domain.UploadTokenDTO;
import net.wishwall.domain.UserDTO;
import net.wishwall.domain.UsersDTO;
import net.wishwall.domain.VersionDTO;
import net.wishwall.domain.WishsDTO;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * @author panRongFu on 2016/4/8.
 * @Description
 * @email pan@ipushan.com
 */
public interface ApiService {

    /**
     * 用户注册
     * @param userName
     * @param password
     * @return
     */
    @POST("user/register")
    @FormUrlEncoded
    Call<RegisterDTO> userRegister(
            @Field("userName") String userName,
            @Field("password") String password);

    /**
     * 用户使用第三方登录的时候系统默认给该用户注册一个账号
     * @param userName
     * @param userIcon
     * @param userSex
     * @return
     */
    @POST("/defaultRegister")
    @FormUrlEncoded
    Call<RegisterDTO> defaultRegister(
            @Field("userId") String userId,
            @Field("userName") String userName,
            @Field("userIcon") String userIcon,
            @Field("userSex") String userSex);

    /**
     * 设置新密码
     * @param account
     * @param password
     * @return
     */
    @POST("/setNewPassword")
    @FormUrlEncoded
    Call<ResultDTO> setNewPassword(
            @Field("account") String account,
            @Field("password") String password);
    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    @POST("user/login")
    @FormUrlEncoded
    Call<LoginDTO> userLogin(
            @Field("username") String username,
            @Field("password") String password);

    /**
     * 通过用户名查询用户信息
     * @param nickname
     * @return
     */
    @POST("/findUsersLikeName")
    @FormUrlEncoded
    Call<FriendLikeNameDTO> findUsersLikeName(@Field("nickname") String nickname);


    /**
     * 根据用户ID查询用户信息
     * @param userid
     * @return
     */
    @POST("/findUserById")
    @FormUrlEncoded
    Call<UserDTO> findUserById(@Field("userid") String userid);

    /**
     * 发送申请添加好友请求
     * @param userid
     * @param friendid
     * @return
     */
    @POST("/applyAddFriend")
    @FormUrlEncoded
    Call<ResultDTO> applyAddFriend(
            @Field("userid") String userid,
            @Field("friendid") String friendid);

    /**
     * 发送添加好友请求
     * @param userid
     * @param friendid
     * @return
     */
    @POST("/addFriend")
    @FormUrlEncoded
    Call<ResultDTO> addFriend(
            @Field("userid") String userid,
            @Field("friendid") String friendid);

    /**
     * 获取好友列表的ID
     * @return
     */
    @POST("/findFriendIds")
    @FormUrlEncoded
    Call<FriendIdsDTO>findFriendIds(@Field("userId") String userId);

    /**
     * 获取用户的好友列表
     * @param userid
     * @return
     */
    @POST("/findFriendList")
    @FormUrlEncoded
    Call<FriendListDTO>findFriendList(
            @Field("userid") String userid);


    /**
     * 好友请求列表
     * @param userid
     * @return
     */
    @POST("/applyAddFriendList")
    @FormUrlEncoded
    Call<ApplyAddFriendListDTO> applyAddFriendList( @Field("userid") String userid);
    /**
     * 更新好友关系
     * @param userid
     * @param friendid
     * @param status
     * @return
     */
    @POST("/updateFriend")
    @FormUrlEncoded
    Call<ResultDTO> updateFriend(
            @Field("userid") String userid,
            @Field("friendid") String friendid,
            @Field("status") int status);

    /**
     * 发送单聊信息
     * @param fromUserId
     * @param toUserId
     * @param message
     * @param objectName
     * @return
     */
    @POST("/sendMessage")
    @FormUrlEncoded
    Call<ResultDTO> sendMessage(
            @Field("fromUserId") String fromUserId,
            @Field("toUserId") String toUserId,
            @Field("message") String message,
            @Field("objectName") String objectName,
            @Field("msgType") String msgType);

    /**
     * 创建群组
     * @param userId
     * @param groupName
     * @return
     */
    @POST("/createGroup")
    @FormUrlEncoded
    Call<ResultDTO> createGroup(
            @Field("userId") String userId,
            @Field("describe")String describe,
            @Field("groupName") String groupName,
            @Field("groupIcon") String groupIcon);

    /**
     * 获取所有群组
     * @return
     */
    @POST("/findAllGroups")
    @FormUrlEncoded
    Call<GroupsDTO> findAllGroups(@Field("userId") String userId);

    /**
     * 根据名称查询群组
     * @param groupName
     * @return
     */
    @POST("/findGroupByName")
    @FormUrlEncoded
    Call<GroupsDTO>findGroupByName(@Field("groupName") String groupName);
    /**
     * 加入群组
     * @param userId
     * @param groupId
     * @param groupName
     * @return
     */
    @POST("/joinGroup")
    @FormUrlEncoded
    Call<ResultDTO> joinGroup(
            @Field("userId") String userId,
            @Field("groupId") String groupId,
            @Field("groupName") String groupName);

    /**
     * 退出群
     * @param userId
     * @param groupId
     * @return
     */
    @POST("/quitGroup")
    @FormUrlEncoded
    Call<ResultDTO>quitGroup(
            @Field("userId") String userId,
            @Field("groupId") String groupId);
    /**
     * 查询我加入的群组
     * @return
     */
    @POST("/findMyGroups")
    @FormUrlEncoded
    Call<GroupsDTO> findMyGroups(@Field("userId") String userId);

    /**
     * 获取所有的聊天室
     * @return
     */
    @POST("/findAllChatrooms")
    @FormUrlEncoded
    Call<AllChatroomsDTO> findAllChatrooms(@Field("userId") String userId);

    /**
     * 根据多个id查询多个用户的信息
     * @param ids
     * @return
     */
    @POST("/findUsersByIds")
    @FormUrlEncoded
    Call<UsersDTO> findUsersByIds(@Field("ids") String ids);

    /**
     * 同步方式获取用户信息
     * @param userid
     * @return
     */
    @POST("/findUserById")
    @FormUrlEncoded
    Call<UserDTO> synchFindUserById(@Field("userid") String userid);

    /**
     * 同步方式获取群组的信息
     * @param groupId
     * @return
     */
    @POST("/findGroupInfoById")
    @FormUrlEncoded
    Call<GroupDTO> synchFindGroupById(@Field("groupId") String groupId);

    /**
     * 同步获取上传指令token
     * @return
     */
    @POST("/getUploadToken")
     @FormUrlEncoded
    Call<UploadTokenDTO> synchGetUploadToken(@Field("userId") String userId);

    //////////
    //省市区//
    /////////

    /**
     * 获取所有的省
     * @return
     */
    @POST("/findAllProv")
    @FormUrlEncoded
    Call<ProvCityAreaDTO> findAllProv(@Field("userId") String userId);

    /**
     * 获取省下的市
     * @param code
     * @return
     */
    @POST("/findProvCity")
    @FormUrlEncoded
    Call<ProvCityAreaDTO> findProvCity(@Field("code") String code);

    /**
     * 查出所有的市
     * @return
     */
    @POST("/getAllCity")
    @FormUrlEncoded
    Call<ProvCityAreaDTO> getAllCity(@Field("userId") String userId);

    /**
     * 获取市下的区(县)
     * @param code
     * @return
     */
    @POST("/findCityArea")
    @FormUrlEncoded
    Call<ProvCityAreaDTO> findCityArea(@Field("code") String code);

    /**
     * 根据城市名称获取对应code值
     * @param name
     * @return
     */
    @POST("/findCodeByName")
    @FormUrlEncoded
    Call<CodeByNameDTO> findCodeByName(@Field("name") String name);

    /**
     * 更新用户信息
     * @param userId
     * @param nickName
     * @param icon
     * @param sex
     * @param phone
     * @param year
     * @param month
     * @param day
     * @param province
     * @param city
     * @param area
     * @param wx
     * @param qq
     * @return
     */
    @POST("/updateUserInfo")
    @FormUrlEncoded
    Call<ResultDTO> updateUserInfo(
            @Field("userId") String userId,
            @Field("nickName") String nickName,
            @Field("icon") String icon,
            @Field("sex") String sex,
            @Field("phone") String phone,
            @Field("year") String year,
            @Field("month") String month,
            @Field("day") String day,
            @Field("prov") String province,
            @Field("city") String city,
            @Field("area") String area,
            @Field("wx") String wx,
            @Field("qq") String qq);

    /**
     * 创建许愿条
     * @param userId
     * @param content
     * @param cityName
     * @return
     */
    @POST("/createWish")
    @FormUrlEncoded
    Call<ResultDTO> createWish(
            @Field("userId") String userId,
            @Field("content")String content,
            @Field("cityName") String cityName);

    /**
     * 为许愿条增加图片
     * @param imgUrl
     * @return
     */
    @POST("/addWishImage")
    @FormUrlEncoded
    Call<ResultDTO> addWishImage(
            @Field("wishId") String wishId,
            @Field("imgUrls") String imgUrl);

    /**
     * 为个人中心添加背景图片
     * @param picUrl
     * @return
     */
    @POST("/addMyCenterPic")
    @FormUrlEncoded
    Call<ResultDTO> addMyCenterPic(@Field("picUrl") String picUrl);

    /**
     * 查询个人中心背景图片
     * @return
     */
    @POST("/findMyCenterPic")
    @FormUrlEncoded
    Call<CenterPicDTO>findMyCenterPic(@Field("userId") String userId);


    /**
     * 查找我的许愿条
     * @return
     */
    @POST("/findMyWish")
    @FormUrlEncoded
    Call<WishsDTO> findMyWish(@Field("page") int page);

    /**
     * 根据城市查找许愿条
     * @return
     */
    @POST("/findWishByCity")
    @FormUrlEncoded
    Call<WishsDTO> findWishByCity(
            @Field("page") int page,
            @Field("cityName") String cityName);

    /**
     * 根据名称查询许愿条
     * @param page
     * @param name
     * @return
     */
    @POST("/findWishByName")
    @FormUrlEncoded
    Call<WishsDTO> findWishByName(
            @Field("page") int page,
            @Field("name") String name);
    /**
     * 提交个人意见
     * @param advise
     * @return
     */
    @POST("/addUserAdvise")
    @FormUrlEncoded
    Call<ResultDTO> addUserAdvise(@Field("advise") String advise);


    /**
     * 给许愿条点赞
     * @param wishId
     * @param type
     * @return
     */
    @POST("/likeWish")
    @FormUrlEncoded
    Call<ResultDTO> likeWish(
            @Field("likeId")String likeId,
            @Field("wishId") String wishId,
            @Field("type") String type);


    /**
     * 给许愿条评论
     * @param wishId
     * @param commText
     * @return
     */
    @POST("/commWish")
    @FormUrlEncoded
    Call<ResultDTO> commWish(
            @Field("wishId") String wishId,
            @Field("commText") String commText);

    /**
     * 检查更新
     * @param userId
     * @return
     */
    @POST("/checkForUpdate")
    @FormUrlEncoded
    Call<VersionDTO> checkForUpdate(@Field("userId") String userId);

    /**
     *上传文件到服务器
     * @param description
     * @param file
     * @return
     */
    @Multipart
    @POST("/crash")
    Call<ResultDTO> uploadFile(
            @Part("crashReport") RequestBody description,
            @Part MultipartBody.Part file);

}
