package com.dbtechprojects.simplenotes.models

import java.io.Serializable


data class NoteItem(
    val id: Int? = null,
    val title: String? = null,
    val note: String? = null,
    val timeStamp: String? = null,
): Serializable