package com.bussiness.curemegptapp.util



import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.bussiness.curemegptapp.util.AppConstant.Companion.KEY_IS_LOGGED_IN
import com.bussiness.curemegptapp.util.AppConstant.Companion.KEY_IS_SKIP_LOGIN
import com.bussiness.curemegptapp.util.AppConstant.Companion.KEY_USER_TYPE
import com.bussiness.curemegptapp.util.AppConstant.Companion.LAST_MOOD_DATE
import com.bussiness.curemegptapp.util.AppConstant.Companion.MOOD_PREFS
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * SessionManager - Handles user session data with SharedPreferences.
 * Safe for production usage with null handling and default values.
 */
class SessionManager @Inject constructor(@ApplicationContext context: Context) {


    private val preferences: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "app_preferences"
        private const val KEY_USER_ID = "key_user_id"
        private const val KEY_USER_NAME = "key_user_name"
        private const val KEY_TOKEN = "key_token"
        private const val KEY_SIGNUP_FLOW = "signup_flow"

        @Volatile
        private var instance: SessionManager? = null

        fun getInstance(context: Context): SessionManager {
            return instance ?: synchronized(this) {
                instance ?: SessionManager(context).also { instance = it }
            }
        }
    }

    /** Save user login state */
    fun setLogin(value: Boolean) {
        preferences.edit {
            putBoolean(KEY_IS_LOGGED_IN, value)
            if (value) putBoolean(KEY_IS_SKIP_LOGIN, false)
        }
    }

    /** Get user login state */
    fun isLoggedIn(): Boolean = preferences.getBoolean(KEY_IS_LOGGED_IN, false)

    /** Save user ID */
    fun setUserId(userId: String) {
        preferences.edit { putString(KEY_USER_ID, userId) }
    }

    /** Save user NAME */
    fun setUserName(userId: String) {
        preferences.edit { putString(KEY_USER_NAME, userId) }
    }

    /** Get user ID safely */
    fun getUserId(): String {
        return preferences.getString(KEY_USER_ID, "") ?: ""
    }

    /** Get user NAME safely */
    fun getUserName(): String {
        return preferences.getString(KEY_USER_NAME, "") ?: ""
    }

    /** Save auth token */
    fun setToken(token: String) {
        preferences.edit { putString(KEY_TOKEN, token) }
    }

    /** Get saved auth token safely */
    fun getToken(): String {
        return preferences.getString(KEY_TOKEN, "") ?: ""
    }

    fun setSignupFlow(active: Boolean) {
        preferences.edit { putBoolean(KEY_SIGNUP_FLOW, active) }
    }

    fun isSignupFlowActive(): Boolean {
        return preferences.getBoolean(KEY_SIGNUP_FLOW, false)
    }

    fun clearSignupFlow() {
        preferences.edit { remove(KEY_SIGNUP_FLOW) }
    }

    /** Clear session on logout */
    fun clearSession() {
        preferences.edit { clear() }
    }

    fun getTodayDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun saveMoodDate(context: Context) {
        preferences.edit {    putString(LAST_MOOD_DATE, getTodayDate())
            .apply()}
    }

    fun isMoodGivenToday(context: Context): Boolean {
        val savedDate =  preferences.getString(LAST_MOOD_DATE, "")
        return savedDate == getTodayDate()
    }

    /** Add alert carried from Case Chat */
    fun addChatAlert(alert: String) {
        val alerts = getChatAlerts().toMutableSet()
        alerts.add(alert)
        preferences.edit { putStringSet("key_chat_alerts", alerts) }
    }

    /** Get alerts carried from Case Chats */
    fun getChatAlerts(): Set<String> {
        return preferences.getStringSet("key_chat_alerts", emptySet()) ?: emptySet()
    }

    /** Remove specific alert carried from Case Chat */
    fun removeChatAlert(alert: String) {
        val alerts = getChatAlerts().toMutableSet()
        if (alerts.remove(alert)) {
            preferences.edit { putStringSet("key_chat_alerts", alerts) }
        }
    }

    /** Clear all custom chat alerts */
    fun clearChatAlerts() {
        preferences.edit { remove("key_chat_alerts") }
    }
}
