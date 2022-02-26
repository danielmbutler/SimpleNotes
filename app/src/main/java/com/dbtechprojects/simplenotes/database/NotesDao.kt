package com.dbtechprojects.simplenotes.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dbtechprojects.simplenotes.models.NoteItem

@Dao
interface NotesDao {
    @Query("SELECT * FROM notes")
    fun getAllNotes(): LiveData<List<NoteItem>>

    @Update
    suspend fun updateNote(noteItem: NoteItem)

    @Insert
    suspend fun insertNote(noteItem: NoteItem)

    @Query("DELETE FROM notes WHERE id=:noteId")
    suspend fun delete(noteId: Int) : Int
}