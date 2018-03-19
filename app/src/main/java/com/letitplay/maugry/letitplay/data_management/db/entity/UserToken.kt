package com.letitplay.maugry.letitplay.data_management.db.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey


@Entity(tableName = "user")
data class UserToken(
        @PrimaryKey @ColumnInfo(name = "user_token")
        val userToken: String
)