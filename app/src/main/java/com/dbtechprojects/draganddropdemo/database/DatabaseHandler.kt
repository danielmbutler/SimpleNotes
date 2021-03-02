package com.dbtechprojects.draganddropdemo.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.provider.ContactsContract
import com.dbtechprojects.draganddropdemo.models.NoteItem


//creating the database logic, extending the SQLiteOpenHelper base class
class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1 // Database version
        private const val DATABASE_NAME = "NotesDatabase" // Database name
        private const val TABLE_HAPPY_PLACE = "NotesTable" // Table Name

        //All the Columns names
        private const val KEY_ID = "_id"
        private const val KEY_TITLE = "title"
        private const val KEY_NOTE = "note"
        private const val KEY_TIMESTAMP = "TimeStamp"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        //creating table with fields
        val CREATE_HAPPY_PLACE_TABLE = ("CREATE TABLE " + TABLE_HAPPY_PLACE + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_NOTE + " TEXT,"
                + KEY_TIMESTAMP + " TEXT)")
        db?.execSQL(CREATE_HAPPY_PLACE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_HAPPY_PLACE")
        onCreate(db)
    }

    /**
     * Function to insert a Happy Place details to SQLite Database.
     */
    fun addNote(noteitem: NoteItem): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, noteitem.title) // HappyPlaceModelClass TITLE
        contentValues.put(KEY_TIMESTAMP, noteitem.TimeStamp) // HappyPlaceModelClass IMAGE
        contentValues.put(
            KEY_NOTE,
            noteitem.note
        ) // HappyPlaceModelClass DESCRIPTION
        contentValues.put(KEY_ID, noteitem.id) // HappyPlaceModelClass DATE

        // Inserting Row
        val result = db.insert(TABLE_HAPPY_PLACE, null, contentValues)
        //2nd argument is String containing nullColumnHack  

        db.close() // Closing database connection
        return result
    }

    /**
     * Function to read all the list of Happy Places data which are inserted.
     */
    fun getHappyPlacesList(): ArrayList<NoteItem> {

        // A list is initialize using the data model class in which we will add the values from cursor.
        val happyPlaceList: ArrayList<NoteItem> = ArrayList()

        val selectQuery = "SELECT  * FROM $TABLE_HAPPY_PLACE" // Database select query

        val db = this.readableDatabase

        try {
            val cursor: Cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val place = NoteItem(
                            cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                            cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                            cursor.getString(cursor.getColumnIndex(KEY_NOTE)),
                            cursor.getString(cursor.getColumnIndex(KEY_TIMESTAMP))

                    )
                    happyPlaceList.add(place)

                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        return happyPlaceList
    }

    /**
     * Function to update record
     */
    fun updateHappyPlace(happyPlace: NoteItem): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, happyPlace.title) // HappyPlaceModelClass TITLE
        contentValues.put(KEY_NOTE, happyPlace.note) // HappyPlaceModelClass IMAGE
        contentValues.put(
            KEY_TIMESTAMP,
            happyPlace.TimeStamp
        ) // HappyPlaceModelClass DESCRIPTION
        contentValues.put(KEY_ID, happyPlace.id) // HappyPlaceModelClass DATE

        // Updating Row
        val success =
            db.update(
                    TABLE_HAPPY_PLACE, contentValues,
                    KEY_ID + "=" + happyPlace.id,
                    null
            )
        //2nd argument is String containing nullColumnHack

        db.close() // Closing database connection
        return success
    }

    /**
     * Function to delete happy place details.
     */
    fun deleteHappyPlace(id: Int): Int {
        val db = this.writableDatabase
        // Deleting Row
        val success = db.delete(TABLE_HAPPY_PLACE, "$KEY_ID=$id", null)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }
}
