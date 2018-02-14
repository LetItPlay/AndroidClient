package com.letitplay.maugry.letitplay.data_management.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import com.letitplay.maugry.letitplay.data_management.db.entity.Like

@Dao
abstract class LikeDao {
    @Insert
    abstract fun insert(like: Like)
}