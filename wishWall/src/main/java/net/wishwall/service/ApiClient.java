package net.wishwall.service;

import android.content.Intent;

import net.wishwall.App;
import net.wishwall.Constants;
import net.wishwall.activities.LoginActivity;
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
import net.wishwall.domain.WishsDTO;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author panRongFu on 2016/4/8.
 * @Description
 * @email pan@ipushan.com
 */
public class ApiClient {

    static OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (request.body() == null || request.header("Content-Encoding") != null) {
                return chain.proceed(request);
            }

            String wToken = App.getWishToken();
            Request  compressedRequest = request.newBuilder()
                        .header("Authorization",wToken==null ? "abc":wToken)
                        .build();
            Response response = chain.proceed(compressedRequest);

            ResponseBody responseBody = response.body();
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();
            Charset charset =Charset.forName("utf-8");
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(Charset.forName("utf-8"));
            }
            String bodyString = buffer.clone().readString(charset);
            try {
                JSONObject object = new JSONObject(bodyString);
                String message = object.getString("message");
                int code = Integer.parseInt(object.getString("code"));
                if(message.equals(Constants.TOKEN_INVALID)&& code == 400){
                    Intent intent =new Intent(App.getContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    App.getContext().startActivity(intent);
                    return null;
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return response;
        }
    }).build();

    static Retrofit retrofit = new Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(App.baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    static ApiService apiService = retrofit.create(ApiService.class);

    /**
     * 用户注册
     * @param userName
     * @param password
     * @param callback
     */
    public static void userRegister(String userName, String password , Callback<RegisterDTO> callback){
        Call<RegisterDTO> call = apiService.userRegister(userName, password);
        call.enqueue(callback);
    }

    /**
     * 用户使用第三方平台登录时候默认注册一个账号
     * @param usrName
     * @param userIcon
     * @param userSex
     * @param callback
     */
    public static void defaultRegister(String userId,String usrName,String userIcon,String userSex,Callback<RegisterDTO> callback){
        Call<RegisterDTO> call = apiService.defaultRegister(userId,usrName,userIcon,userSex);
        call.enqueue(callback);
    }


    /**
     * 用户登录
     * @param username
     * @param password
     * @param callback
     */
    public static void userLogin(String username, String password, Callback<LoginDTO> callback){
        Call<LoginDTO> call = apiService.userLogin(username, password);
        call.enqueue(callback);
    }

    /**
     * 通过用户名查询用户信息
     * @param nickname
     * @param callback
     */
    public static void findUsersLikeName(String nickname, Callback<FriendLikeNameDTO> callback){
        Call<FriendLikeNameDTO> call = apiService.findUsersLikeName(nickname);
        call.enqueue(callback);
    }

    /**
     * 根据用户ID查询用户信息
     * @param userid
     * @param callback
     */
    public static void findUserById(String userid, Callback<UserDTO> callback){
        Call<UserDTO> call = apiService.findUserById(userid);
        call.enqueue(callback);
    }

    /**
     * 添加好友
     * @param userid
     * @param callback
     */
    public static void addFriend(String userid, String friendid, Callback<ResultDTO> callback){
        Call<ResultDTO> call = apiService.addFriend(userid,friendid);
        call.enqueue(callback);
    }

    /**
     * 申请添加好友
     * @param userid
     * @param callback
     */
    public static void applyAddFriend(String userid, String friendid, Callback<ResultDTO> callback){
        Call<ResultDTO> call = apiService.applyAddFriend(userid,friendid);
        call.enqueue(callback);
    }

    /**
     * 查询所有好友id
     * @param callback
     */
    public static void findFriendIds(Callback<FriendIdsDTO>callback){
        Call<FriendIdsDTO> call = apiService.findFriendIds(null);
        call.enqueue(callback);
    }

    /**
     * 获取好友列表
     * @param userid
     * @param callback
     */
    public static void findFriendList(String userid, Callback<FriendListDTO>callback){
        Call<FriendListDTO> call = apiService.findFriendList(userid);
        call.enqueue(callback);
    }

    /**
     * 申请添加好友列表
     * @param userid
     * @param callback
     */
    public static void applyAddFriendList(String userid,Callback<ApplyAddFriendListDTO> callback){
        Call<ApplyAddFriendListDTO> call = apiService.applyAddFriendList(userid );
        call.enqueue(callback);
    }

    /**
     * 更新好友关系
     * @param userid
     * @param friendid
     * @param status
     * @param callback
     */
    public static void updateFriend(String userid,String friendid,int status,Callback<ResultDTO> callback){
        Call<ResultDTO> call = apiService.updateFriend(userid, friendid, status);
        call.enqueue(callback);
    }

    /**
     * 发送单聊信息
     * @param fromUserId
     * @param toUserId
     * @param message
     * @param objectName
     * @param callback
     */
    public static void sendMessage(String fromUserId,String toUserId,String message,String objectName,String msgType,Callback<ResultDTO> callback){
        Call<ResultDTO> call = apiService.sendMessage(fromUserId, toUserId, message, objectName,msgType);
        call.enqueue(callback);
    }

    /**
     * 创建群
     * @param userid
     * @param groupName
     * @param callback
     */
    public static void createGroup(String userid, String groupName,Callback<ResultDTO> callback){
        Call<ResultDTO> call = apiService.createGroup(userid, groupName);
        call.enqueue(callback);
    }

    /**
     * 查询所有群
     * @param callback
     */
    public static void findAllGroups(Callback<GroupsDTO>callback){
        Call<GroupsDTO> call = apiService.findAllGroups(null);
        call.enqueue(callback);
    }

    /**
     * 根据名称查询群
     * @param groupName
     * @param callback
     */
    public static void findGroupByName(String groupName,Callback<GroupsDTO> callback){
        Call<GroupsDTO> call = apiService.findGroupByName(groupName);
        call.enqueue(callback);
    }
    /**
     * 加入群
     * @param userId
     * @param groupId
     * @param groupName
     * @param callback
     */
    public static void joinGroup(String userId,String groupId,String groupName,Callback<ResultDTO> callback){
        Call<ResultDTO> call = apiService.joinGroup(userId, groupId, groupName);
        call.enqueue(callback);
    }

    /**
     * 退出群
     * @param userId
     * @param groupId
     * @param callback
     */
    public static void quitGroup(String userId,String groupId,Callback<ResultDTO> callback){
        Call<ResultDTO> call = apiService.quitGroup(userId, groupId);
        call.enqueue(callback);
    }

    /**
     * 查询我加入的群的所有I
     * @param callback
     */
    public static void findMyGroups(Callback<GroupsDTO> callback){
        Call<GroupsDTO> call = apiService.findMyGroups(null);
        call.enqueue(callback);
    }

    /**
     * 获取所有的聊天室
     * @param callback
     */
    public static void findAllChatrooms(Callback<AllChatroomsDTO> callback){
        Call<AllChatroomsDTO> call = apiService.findAllChatrooms(null);
        call.enqueue(callback);
    }

    /**
     * 根据多个id查询多个用户信息
     * @param ids
     * @param callback
     */
    public static void findUsersByIds(String ids, Callback<UsersDTO> callback){
        Call<UsersDTO> call = apiService.findUsersByIds(ids);
        call.enqueue(callback);
    }

    /**
     * 同步的方法获取用户信息
     * @param userid
     * @return
     */
    public static retrofit2.Response<UserDTO> synchFindUserById(String userid){

        try{
            Call<UserDTO> call = apiService.synchFindUserById(userid);
            return call.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
       return null;
    }

    /**
     * 同步的方式获取群组的信息
     * @param groupId
     * @return
     */
    public static retrofit2.Response<GroupDTO> synchFindGroupById(String groupId){
        try{
            Call<GroupDTO> call = apiService.synchFindGroupById(groupId);
            return call.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 同步获取上传指令Token
     * @return
     */
    public static retrofit2.Response<UploadTokenDTO> synchGetUploadToken(){
        try{
            Call<UploadTokenDTO> call = apiService.synchGetUploadToken(null);
            return call.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //////////
    //省市区//
    /////////

    /**
     *  获取所有的省
     * @return
     */
    public static void findAllProv(Callback<ProvCityAreaDTO> callback){
        Call<ProvCityAreaDTO> call = apiService.findAllProv(null);
        call.enqueue(callback);
    }

    /**
     * 获取省下的市
     * @param code
     * @return
     */
    public static void findProvCity(String code ,Callback<ProvCityAreaDTO> callback){
        Call<ProvCityAreaDTO> call = apiService.findProvCity(code);
        call.enqueue(callback);
    }


    /**
     * 查询所有的市
     * @return
    */
    public static void getAllCity(Callback<ProvCityAreaDTO> callback){
        Call<ProvCityAreaDTO> call = apiService.getAllCity(null);
        call.enqueue(callback);
    }

    /**
     * 获取市下的区（县）
     * @param code
     * @return
     */
    public static void findCityArea(String code,Callback<ProvCityAreaDTO> callback){
        Call<ProvCityAreaDTO> call = apiService.findCityArea(code);
        call.enqueue(callback);
    }

    /**
     * 根据城市名称查询对应code值
     * @param name
     * @param callback
     */
    public static void findCodeByName(String name, Callback<CodeByNameDTO> callback){
        Call<CodeByNameDTO> call = apiService.findCodeByName(name);
        call.enqueue(callback);
    }

    /**
     * 更新个人信息
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
     * @param callback
     */
    public static void updateUserInfo(
                        String userId,String nickName, String icon,
                        String sex,  String phone, String year, String month,
                        String day, String province, String city, String area,
                        String wx, String qq,Callback<ResultDTO> callback){

        Call<ResultDTO> call = apiService.updateUserInfo(
                         userId, nickName, icon, sex, phone, year,
                         month, day, province, city, area, wx, qq);
        call.enqueue(callback);
    }

    /**
     * 创建许愿条
     * @param userId
     * @param content
     * @param cityName
     * @param callback
     */
    public static void createWish(String userId, String content,
                                  String cityName, Callback<ResultDTO> callback){
        Call<ResultDTO> call = apiService.createWish(userId, content, cityName);
        call.enqueue(callback);
    }

    /**
     * 为许愿条添加图片地址
     * @param imgUrls
     * @param callback
     */
    public static void addWishImage(String wishId,String imgUrls, Callback<ResultDTO> callback){

        Call<ResultDTO> call = apiService.addWishImage(wishId,imgUrls);
        call.enqueue(callback);
    }

    /**
     * 为个人中心添加背景图片
     * @param picUrl
     * @param callback
     */
    public static void addMyCenterPic(String picUrl, Callback<ResultDTO> callback){
        Call<ResultDTO> call = apiService.addMyCenterPic(picUrl);
        call.enqueue(callback);
    }

    /**
     * 查询个人中心图片
     * @param callback
     */
    public static void findMyCenterPic(Callback<CenterPicDTO>callback){
        Call<CenterPicDTO> call = apiService.findMyCenterPic(null);
        call.enqueue(callback);
    }

    /**
     * 查询我的许愿条
     * @param rows
     * @param callback
     */
    public static void findMyWish(int rows, Callback<WishsDTO> callback){
        Call<WishsDTO> call = apiService.findMyWish(rows);
        call.enqueue(callback);
    }

    /**
     * 根据城市查询许愿条
     * @param rows
     * @param cityName
     * @param callback
     */
    public static void findWishByCity(int rows,String cityName, Callback<WishsDTO> callback){
        Call<WishsDTO> call = apiService.findWishByCity(rows, cityName);
        call.enqueue(callback);
    }

    /**
     * 更加名称查许愿条
     * @param page
     * @param name
     * @param callback
     */
    public static void findWishByName(int page,String name,Callback<WishsDTO> callback){
        Call<WishsDTO> call = apiService.findWishByName(page, name);
        call.enqueue(callback);
    }
    /**
     * 提交个人意见
     * @param advise
     * @param callback
     */
    public static void addUserAdvise(String advise, Callback<ResultDTO> callback){
        Call<ResultDTO> call = apiService.addUserAdvise(advise);
        call.enqueue(callback);
    }

    /**
     * 点赞许愿条
     * @param type
     * @param wishId
     * @param callback
     */
    public static void likeWish(String likeId,String wishId,String type,Callback<ResultDTO> callback){
        Call<ResultDTO> call = apiService.likeWish(likeId,wishId,type);
        call.enqueue(callback);
    }

    /**
     * 评论许愿条
     * @param wishId
     * @param commText
     * @param callback
     */
    public static void commWish(String wishId,String commText,Callback<ResultDTO> callback){
        Call<ResultDTO> call = apiService.commWish(wishId, commText);
        call.enqueue(callback);
    }
}
