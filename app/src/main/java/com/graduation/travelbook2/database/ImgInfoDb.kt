package com.graduation.travelbook2.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ImgInfo::class], version = 1)
abstract class ImgInfoDb: RoomDatabase() {
    abstract fun imgInfoDao(): ImgInfoDao
    companion object {
        private var instance: ImgInfoDb? = null

        @Synchronized
        fun getInstance(context: Context): ImgInfoDb? {
            if (instance == null) {
                synchronized(ImgInfoDb::class){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ImgInfoDb::class.java,
                        "imginfo-database4"
                    ).build()
                }
            }
            return instance
        }
    }

}