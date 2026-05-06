package com.offgrid.note.database

import android.content.Context
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.android.AndroidSqliteDriver

fun createDatabase(context: Context): OffgridDatabase {
    val driver = AndroidSqliteDriver(
        OffgridDatabase.Schema,
        context,
        "offgrid.db"
    )
    return OffgridDatabase(driver)
}
