package com.oracle.rent.ch23.member.vo;

import java.io.Serializable;

public class MemberVO implements Serializable {
    // 필드 선언
    private String memId;
    private String memPassword;
    private String memName;
    private String memAddress;
    private String memPhoneNum;
	private String memRank;
	private int memPoint;
    // 생성자 선언
    public MemberVO() {}

    public MemberVO(String memId, String memPassword, String memName, String memAddress, String memPhoneNum, String memRank, int memPoint) {
        this.memId = memId;
        this.memPassword = memPassword;
        this.memName = memName;
        this.memAddress = memAddress;
        this.memPhoneNum = memPhoneNum;
		this.memRank = memRank;
		this.memPoint = memPoint;
    }

    public MemberVO(String memId, String memPassword, String memName, String memAddress, String memPhoneNum) {
        this.memId = memId;
        this.memPassword = memPassword;
        this.memName = memName;
        this.memAddress = memAddress;
        this.memPhoneNum = memPhoneNum;
    }

	public String getMemId() {
		return memId;
	}

	public void setMemId(String memId) {
		this.memId = memId;
	}

	public String getMemPassword() {
		return memPassword;
	}

	public void setMemPassword(String memPassword) {
		this.memPassword = memPassword;
	}

	public String getMemName() {
		return memName;
	}

	public void setMemName(String memName) {
		this.memName = memName;
	}

	public String getMemAddress() {
		return memAddress;
	}

	public void setMemAddress(String memAddress) {
		this.memAddress = memAddress;
	}

	public String getMemPhoneNum() {
		return memPhoneNum;
	}

	public void setMemPhoneNum(String memPhoneNum) {
		this.memPhoneNum = memPhoneNum;
	}
	
	public String getMemRank(){
		return memRank;
	}
	
	public void setMemRank(String memRank){
		this.memRank = memRank;
	}
	
	public int getMemPoint(){
		return memPoint;
	}

	public void setMemPoint(int memPoint){
		this.memPoint = memPoint;
	}
	
	
	
	
}