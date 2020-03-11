package com.anima.networkrequestTest

/**
 * Created by jianjianhong on 19-10-31
 */

data class UserInfo(val user:User, val token:String)
data class User(val userName:String, val userPassword:String, val userChineseName:String)
