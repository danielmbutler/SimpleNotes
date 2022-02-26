package com.dbtechprojects.simplenotes.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "notes")
@Parcelize
data class NoteItem(
    @PrimaryKey
    val id: Int? = null,
    @ColumnInfo(name = "title") val title: String? = null,
    @ColumnInfo(name = "note") val note: String? = null,
    @ColumnInfo(name = "timeStamp")val timeStamp: String? = null,
): Parcelable
