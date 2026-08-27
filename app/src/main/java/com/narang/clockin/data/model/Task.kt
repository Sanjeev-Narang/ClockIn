package com.narang.clockin.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.StringRes
import com.google.firebase.Timestamp
import com.narang.clockin.R

/**
 * Firestore document shape:
 * users/{userId}/tasks/{taskId}
 * {
 *   title: String
 *   dueDateTime: number   // epoch millis; used for sorting
 *   dueDateLabel: String  // pre-formatted display text, e.g. "Today, 2:30 PM"
 *   tag: String           // "Urgent" | "Work" | "Personal" | ""
 *   priority: String      // "HIGH" | "NORMAL"
 *   isCompleted: Boolean
 *   schemaVersion: Int    // bumped when the document shape evolves
 * }
 *
 * Firestore is schema-less: it never enforces these fields, so documents of
 * different "versions" (missing fields, extra fields, differing types) can all
 * exist side by side. The loose schema lives here, in the app:
 *   - [fromData] reads any document defensively, coercing types and applying
 *     defaults, so a single surprising field never drops the whole document.
 *   - [toMap] writes the current shape explicitly (versioned), so future
 *     fields can be added lazily without a migration.
 */
data class Task(
    var id: String = "",
    var title: String = "",
    var dueDateTime: Long = 0L,
    var dueDateLabel: String = "",
    var tag: String = "",
    var priority: String = Priority.NORMAL.name,
    var isCompleted: Boolean = false,
    var schemaVersion: Int = SCHEMA_VERSION
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: Priority.NORMAL.name,
        parcel.readByte() != 0.toByte(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeLong(dueDateTime)
        parcel.writeString(dueDateLabel)
        parcel.writeString(tag)
        parcel.writeString(priority)
        parcel.writeByte(if (isCompleted) 1 else 0)
        parcel.writeInt(schemaVersion)
    }

    override fun describeContents(): Int = 0
    val section: Section
        get() = if (priority == Priority.HIGH.name) Section.HIGH_PRIORITY else Section.UPCOMING

    /** Explicit write shape. `id` is excluded: it is derived from the document ref. */
    fun toMap(): Map<String, Any> = mapOf(
        "title" to title,
        "dueDateTime" to dueDateTime,
        "dueDateLabel" to dueDateLabel,
        "tag" to tag,
        "priority" to priority,
        "isCompleted" to isCompleted,
        "schemaVersion" to schemaVersion
    )

    companion object CREATOR : Parcelable.Creator<Task> {
        const val SCHEMA_VERSION = 1

        override fun createFromParcel(parcel: Parcel): Task = Task(parcel)
        override fun newArray(size: Int): Array<Task?> = arrayOfNulls(size)

        /**
         * Loose-schema deserializer. Never returns null: every field is read
         * defensively so old and new document versions coexist and render.
         */
        fun fromData(data: Map<String, Any?>?, id: String): Task = Task(
            id = id,
            title = data.asString("title") ?: "",
            dueDateTime = data.toEpochMillis("dueDateTime") ?: 0L,
            dueDateLabel = data.asString("dueDateLabel") ?: "",
            tag = data.asString("tag") ?: "",
            priority = data.asString("priority") ?: Priority.NORMAL.name,
            isCompleted = data.asBoolean("isCompleted") ?: false,
            schemaVersion = (data?.get("schemaVersion") as? Number)?.toInt() ?: SCHEMA_VERSION
        )

        private fun Map<String, Any?>?.asString(key: String): String? =
            (this?.get(key) as? String)?.takeIf { it.isNotEmpty() }

        private fun Map<String, Any?>?.asBoolean(key: String): Boolean? {
            val value = this?.get(key) ?: return null
            return when (value) {
                is Boolean -> value
                is String -> when (value.lowercase()) {
                    "true" -> true
                    "false" -> false
                    else -> null
                }
                is Number -> value.toInt() != 0
                else -> null
            }
        }

        /** Coerces [Timestamp], [Date], and any [Number] representation to epoch millis. */
        private fun Map<String, Any?>?.toEpochMillis(key: String): Long? {
            val value = this?.get(key) ?: return null
            return when (value) {
                is Number -> value.toLong()
                is Timestamp -> value.toDate().time
                is java.util.Date -> value.time
                else -> null
            }
        }
    }
}

/**
 * Enum constants double as Firestore storage values via [.name] — once shipped,
 * a constant's name is frozen or existing documents stop deserializing to it.
 * Display text lives in [labelRes], so UI copy can change freely.
 */
enum class Priority(@StringRes val labelRes: Int) {
    HIGH(R.string.priority_high),
    NORMAL(R.string.priority_normal)
}

enum class Section(@StringRes val labelRes: Int) {
    HIGH_PRIORITY(R.string.priority_high),
    UPCOMING(R.string.section_upcoming)
}
