package com.graduation.travelbook2

/**
* 사용자 계정 정보 모델 클래스
 * */
data class UserDto(
    val idToken : String,   // Firebase Uid (고유 토큰정보)
    val emailId : String?,  // 이메일
    val password : String,  // 비밀번호

)
