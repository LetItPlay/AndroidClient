package com.letitplay.maugry.letitplay.data_management.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import com.letitplay.maugry.letitplay.data_management.db.entity.UserToken

@Dao
abstract class UserTokenDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertUserToken(token: UserToken)
}