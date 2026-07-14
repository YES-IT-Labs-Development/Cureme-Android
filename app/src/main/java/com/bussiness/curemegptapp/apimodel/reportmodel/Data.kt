package com.bussiness.curemegptapp.apimodel.reportmodel

data class Data(
    val ai_message  :  String? = "",
    val attachments : MutableList<String>? = null,
    val chat_date   :   String? = "",
    val chat_id     :     Int? = 0,
    val family_name : String? = "",
    val files_count : Int? = 0,
    val severity    :    String? = "",
    val title       :       String? = "",
    val user_name   :   String? = ""
)