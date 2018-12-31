package com.yashon.chat.vo;

import javax.persistence.Column;
import javax.persistence.Id;

public class UsersVo {

    private String id;

    private String userName;

    private String faceImage;

    private String faceImageBig;

    private String nickName;

    private String qrCode;


    public String getUserName() {
        return userName;
    }

    public String getFaceImage() {
        return faceImage;
    }

    public String getFaceImageBig() {
        return faceImageBig;
    }

    public String getNickName() {
        return nickName;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setFaceImage(String faceImage) {
        this.faceImage = faceImage;
    }

    public void setFaceImageBig(String faceImageBig) {
        this.faceImageBig = faceImageBig;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}