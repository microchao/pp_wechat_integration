package com.iclp.wechat.integration.wxgateway.model;

public class XSocialRequestModel {

    private String WeChatID;

    private String Openid;

    private String Nickname;

    private String Keywords;

    public String getWeChatID() {
        return WeChatID;
    }

    public void setWeChatID(String weChatID) {
        WeChatID = weChatID;
    }

    public String getOpenid() {
        return Openid;
    }

    public void setOpenid(String openid) {
        Openid = openid;
    }

    public String getNickname() {
        return Nickname;
    }

    public void setNickname(String nickname) {
        Nickname = nickname;
    }

    public String getKeywords() {
        return Keywords;
    }

    public void setKeywords(String keywords) {
        Keywords = keywords;
    }
}
