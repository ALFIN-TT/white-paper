package com.alfie.whitepaper.data.database.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * from project_tb ORDER BY name ASC")
    fun getAllProjects(): Flow<List<Project>>

    @Query("SELECT * from project_tb WHERE name = :name")
    fun getProject(name: String): Flow<Project>

    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Item into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProject(project: Project)

    @Update
    suspend fun update(project: Project)

    @Query("DELETE from project_tb WHERE name = :projectName")
    fun deleteProject(projectName: String)

    @Delete
    suspend fun deleteProject(project: Project)
}