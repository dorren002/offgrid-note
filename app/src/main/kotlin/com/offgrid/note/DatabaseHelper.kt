package com.offgrid.note

import android.content.Context
import com.offgrid.note.database.OffgridDatabase
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.android.AndroidSqliteDriver

fun createDatabase(context: Context): OffgridDatabase {
    val driver: SqlDriver = AndroidSqliteDriver(
        OffgridDatabase.Schema,
        context,
        "offgrid.db"
    )
    return OffgridDatabase(driver)
}
