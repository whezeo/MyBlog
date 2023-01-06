package com.xl.domain.enums;

/**
 * 返回类型的枚举类
 */
public enum AppHttpCodeEnum {
    // 成功
    SUCCESS(200,"操作成功"),
    // 登录
    NEED_LOGIN(401,"需要登录后操作"),
    NO_OPERATOR_AUTH(403,"无权限操作"),
    SYSTEM_ERROR(500,"出现错误"),
    USERNAME_EXIST(501,"用户名已存在"),
    PHONENUMBER_EXIST(502,"手机号已存在"), EMAIL_EXIST(503, "邮箱已存在"),
    REQUIRE_USERNAME(504, "必需填写用户名"),
    LOGIN_ERROR(505,"用户名或密码错误"),
    CONTENT_NOT_NULL(506, "评论内容不能为空"),
    FILE_TYPE_ERROR(507,"文件类型不符合" )
    ,
    USER_NOT_NULL(508, "用户信息不能为空")
    ,
    NICKNAME_EXIST(501,"昵称已存在" ),
    TAG_NOT_FIND(555, "标签不存在"),
    UPDATE_MENU_ERROR(500,"修改菜单'写博文'失败，上级菜单不能选择自己" ),
    DELETE_MENU_ERROR(500,"存在子菜单不允许删除" );
    int code;
    String msg;

    AppHttpCodeEnum(int code, String errorMessage){
        this.code = code;
        this.msg = errorMessage;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
