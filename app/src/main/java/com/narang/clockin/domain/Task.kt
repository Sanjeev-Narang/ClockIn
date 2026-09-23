package com.narang.clockin.domain

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.StringRes
import com.narang.clockin.R

data class Task(
    var id: String = "",
    var title: String = "",
    var tag: String = "",
    var priority: String = Priority.NORMAL.name,
    var isCompleted: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: Priority.NORMAL.name,
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(tag)
        parcel.writeString(priority)
        parcel.writeByte(if (isCompleted) 1 else 0)
    }

    override fun describeContents(): Int = 0

    val section: Section
        get() = if (priority == Priority.HIGH.name) Section.HIGH_PRIORITY else Section.UPCOMING

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task = Task(parcel)
        override fun newArray(size: Int): Array<Task?> = arrayOfNulls(size)
    }
}

enum class Priority(@StringRes val labelRes: Int) {
    HIGH(R.string.priority_high),
    NORMAL(R.string.priority_normal)
}

enum class Section(@StringRes val labelRes: Int) {
    HIGH_PRIORITY(R.string.priority_high),
    UPCOMING(R.string.section_upcoming)
}
