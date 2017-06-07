package com.androidbolts.debugdrawer.debug


import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.TextView
import com.androidbolts.debugdrawer.R
import com.androidbolts.debugdrawer.data.model.NetworkLogEntry


import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LogAdapter(context: Context, c: Cursor) : CursorAdapter(context, c, false) {
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private val dateFormat: SimpleDateFormat = SimpleDateFormat("h:mm:ss a EEE", Locale.US)

    override fun newView(context: Context, cursor: Cursor, viewGroup: ViewGroup): View {
        return layoutInflater.inflate(R.layout.list_item_log_entry, viewGroup, false)
    }

    override fun bindView(view: View, context: Context, cursor: Cursor) {
        (view.findViewById(R.id.tvLog) as TextView).text = formatString(cursor.getString(1))
        (view.findViewById(R.id.tvDate) as TextView).text = dateFormat.format(Date(cursor.getLong(2)))
    }

    fun getLogEntry(position: Int): NetworkLogEntry {
        cursor.moveToPosition(position)
        val log = cursor.getString(1)
        val createdAt = cursor.getLong(2)
        return NetworkLogEntry(log, createdAt)
    }

    private fun formatString(text: String): String {

        val json = StringBuilder()
        var indentString = ""

        (0..text.length - 1)
                .asSequence()
                .map { text[it] }
                .forEach {
                    when (it) {
                        '{', '[' -> {
                            json.append("\n" + indentString + it + "\n")
                            indentString += "\t"
                            json.append(indentString)
                        }
                        '}', ']' -> {
                            indentString = indentString.replaceFirst("\t".toRegex(), "")
                            json.append("\n" + indentString + it)
                        }
                        ',' -> json.append(it + "\n" + indentString)

                        else -> json.append(it)
                    }
                }

        return json.toString()
    }

}
