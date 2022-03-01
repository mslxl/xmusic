package io.github.mslxl.xmusic.common.addon.processor

enum class LoginStatus {
    SUCCESS,
    UNKNOWN_ERROR,
    NEED_CAP,
    UNSUPPORTED
}

interface LoginProcessor {
    val supportPwd: Boolean
        get() = false

     fun pwdLogin(username: String, password: String, cap: String? = null): LoginStatus {
         return LoginStatus.UNSUPPORTED
     }

    val supportPhone: Boolean
        get() = false

    suspend fun phoneRequestVerifyCode(num: Int, cap: String? = null): LoginStatus {
        return LoginStatus.UNSUPPORTED
    }

    suspend fun phoneLogin(num: Int, verifyCode: String, cal: String? = null): LoginStatus {
        return LoginStatus.UNSUPPORTED
    }
}