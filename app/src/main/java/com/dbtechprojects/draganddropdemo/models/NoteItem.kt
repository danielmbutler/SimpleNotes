package com.dbtechprojects.draganddropdemo.models

import java.io.Serializable


data class NoteItem(
    val id: Int? = null,
    val title: String? = null,
    val note: String? = null,
    val TimeStamp: String? = null,
): Serializable