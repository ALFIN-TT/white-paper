package com.alfie.whitepaper.data.database.room

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

const val PROJECT_TABLE_NAME = "project_tb"

@Keep
@Entity(tableName = PROJECT_TABLE_NAME)
data class Project(
    @PrimaryKey(autoGenerate = false)
    val name: String = "",
    val drawing: String = "",
    @ColumnInfo(name = "is_temp")
    var isTemp: Boolean = false,
    var thumbnail: String = ""
)