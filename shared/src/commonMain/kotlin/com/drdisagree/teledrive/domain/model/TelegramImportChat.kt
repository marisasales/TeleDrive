package com.drdisagree.teledrive.domain.model

data class TelegramImportChat(
    val chatId: Long,
    val title: String,
    val isChannel: Boolean
)
