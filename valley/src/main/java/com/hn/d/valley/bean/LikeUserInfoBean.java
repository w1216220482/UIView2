package com.hn.d.valley.bean;

/**
 * Created by angcyo on 2017-01-15.
 */

public class LikeUserInfoBean {

    /**
     * avatar : http://static.bzsns.cn/pic/M00/00/69/CixiMlctybOAWCO9AAAbZx76WXw10.JPEG?w=200&h=200&s=1
     * username : 张伟
     * sex : 0
     * uid : 50004
     * grade : 1
     * is_auth : 1
     * signature :
     * company : 中国共产党
     * job : 国家主席
     * industry : 国务院
     */
    private String avatar;
    private String username;
    private String sex;
    private String uid;
    private String grade;
    private String is_auth;
    private String signature;
    private String company;
    private String job;
    private String industry;
    /**
     * is_contact : 1{
     "uid": "50001",
     "status": "1",
     "sex": "0",
     "username": "幽灵",
     "true_name": "",
     "is_auth": "0",
     "is_contact": 0,
     "introduce": "",
     "signature": ""，
     "grade":"1"
     }
     * contact_mark : 张伟
     * is_attention : 1
     */

    private int is_contact;
    private String contact_mark;
    private int is_attention;
    /**
     * 0-被永久锁定【封号】1-正常 2-被锁定，需解封
     * status : 1
     * true_name :
     * introduce :
     */

    private String status;
    private String true_name;
    private String introduce;
    /**
     * auth_type : 3
     * auth_desc : 国家体育队姚明
     * is_blacklist : 0
     * lng : 39.990912172420714
     * lat : 116.32715863448607
     * distance : 0
     * show_distance : 附近
     * created : 1481694406
     * show_time : 2016-12-14 13:46
     */

    private String auth_type;
    private String auth_desc;
    private int is_blacklist;
    private String lng;
    private String lat;
    private String distance;
    private String show_distance;
    private String created;
    private String show_time;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getIs_auth() {
        return is_auth;
    }

    public void setIs_auth(String is_auth) {
        this.is_auth = is_auth;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public int getIs_contact() {
        return is_contact;
    }

    public void setIs_contact(int is_contact) {
        this.is_contact = is_contact;
    }

    public String getContact_mark() {
        return contact_mark;
    }

    public void setContact_mark(String contact_mark) {
        this.contact_mark = contact_mark;
    }

    public int getIs_attention() {
        return is_attention;
    }

    public void setIs_attention(int is_attention) {
        this.is_attention = is_attention;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTrue_name() {
        return true_name;
    }

    public void setTrue_name(String true_name) {
        this.true_name = true_name;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getAuth_type() {
        return auth_type;
    }

    public void setAuth_type(String auth_type) {
        this.auth_type = auth_type;
    }

    public String getAuth_desc() {
        return auth_desc;
    }

    public void setAuth_desc(String auth_desc) {
        this.auth_desc = auth_desc;
    }

    public int getIs_blacklist() {
        return is_blacklist;
    }

    public void setIs_blacklist(int is_blacklist) {
        this.is_blacklist = is_blacklist;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getShow_distance() {
        return show_distance;
    }

    public void setShow_distance(String show_distance) {
        this.show_distance = show_distance;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getShow_time() {
        return show_time;
    }

    public void setShow_time(String show_time) {
        this.show_time = show_time;
    }
}
