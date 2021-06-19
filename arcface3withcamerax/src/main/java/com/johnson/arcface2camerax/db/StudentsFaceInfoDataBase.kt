package com.johnson.arcface2camerax.db

import android.content.Context
import androidx.annotation.NonNull
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [FaceInfoBean::class], version = 1,exportSchema = false)
abstract class StudentsFaceInfoDataBase : RoomDatabase(),FaceInfoDatabase {
    abstract fun getFaceInfoDao(): FaceInfoDao

    companion object {

        var MIGRATION_1_2 : Migration = object : Migration(1, 2) {
            override fun migrate(@NonNull database: SupportSQLiteDatabase) {
                println("migrate12============" + database.version)
                database.execSQL("ALTER TABLE FaceInfo "
                        + " ADD COLUMN role INTEGER")
//                database.execSQL("create table aaaa(uid int primary key,firstName text,lastName text,age text,address text)")
//                database.execSQL("insert into aaaa select uid  ,firstName ,lastName ,age,address from Userjava")
//                database.execSQL("drop table Userjava")
//                database.execSQL("alter table  aaaa rename to Userjava")
            }
        }
        @Volatile
        private var instance: StudentsFaceInfoDataBase? = null

        fun getDBInstace(context: Context): StudentsFaceInfoDataBase {

            if (instance == null) {

                synchronized(StudentsFaceInfoDataBase::class) {

                    if (instance == null) {

                        instance = Room.databaseBuilder(
                                context.applicationContext,
                                StudentsFaceInfoDataBase::class.java,
                                "StudentsFaceInfo_DB.db"
                        )
                                .allowMainThreadQueries()
//                                .addMigrations(MIGRATION_1_2)
                                .fallbackToDestructiveMigration()//迁移数据库如果发生错误，将会重新创建数据库，而不是发生崩溃
                                .build()
                    }
                }
            }
            return instance!!
        }

    }
}