package iclp.pp.ppsearch.model;

import lombok.Data;

@Data
public class RequestJsonModel {
   private String name;

    private  String signature;

    private  String timestamp;

    private  String nonce;

    private  String openid;

    private  String echostr;

    private String content;

    private  String toUserName;
}
