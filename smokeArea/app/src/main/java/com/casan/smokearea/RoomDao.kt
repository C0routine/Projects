package com.casan.smokearea

import android.content.Context
import androidx.room.*

@Entity
data class UserTimeTable(
    @PrimaryKey(autoGenerate = true) var id:Int?,
    @ColumnInfo(name = "time") val time:String,
    @ColumnInfo(name = "money") val money:Int
)

@Dao
interface UserDao{
    @Query("SELECT * FROM UserTimeTable")
    fun getTimeAll():List<UserTimeTable>

    @Insert
    fun insertTime(param:UserTimeTable)

    @Delete
    fun deleteTime(param:UserTimeTable)
}



@Database(entities = [UserTimeTable::class], version = 1)
abstract class AppDatabase:RoomDatabase(){
    abstract fun userDao():UserDao

    companion object{
        private var instance:AppDatabase? = null
        @Synchronized
        fun getInstance(context:Context):AppDatabase?{
            if(instance ==null){
                synchronized(AppDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext, AppDatabase::class.java, "AppDB").build()
                }
            }
            return instance
        }
    }
}