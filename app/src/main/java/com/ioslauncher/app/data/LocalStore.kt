package com.ioslauncher.app.data

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

/**
 * Very small persistence layer for the built-in apps (notes, reminders,
 * call log). Line-based text in SharedPreferences: no database, no JSON
 * library, negligible memory - which is the point on a low-end phone.
 */
object LocalStore {

    private const val PREFS = "launcher_builtin"
    private const val REC = "\u001E"
    private const val FLD = "\u001F"

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    // ---------- Notes ----------

    val notes: SnapshotStateList<Note> = mutableStateListOf()
    private var notesLoaded = false

    fun loadNotes(context: Context) {
        if (notesLoaded) return
        notesLoaded = true
        val raw = prefs(context).getString("notes", null) ?: return
        notes.clear()
        raw.split(REC).forEach { block ->
            if (block.isBlank()) return@forEach
            val parts = block.split(FLD)
            if (parts.size >= 3) {
                notes.add(Note(parts[0], parts[1], parts[2].toLongOrNull() ?: 0L))
            }
        }
    }

    fun saveNotes(context: Context) {
        val raw = notes.joinToString(REC) { "${it.id}$FLD${it.title}$FLD${it.updatedAt}" }
        prefs(context).edit().putString("notes", raw).apply()
    }

    fun addNote(context: Context, title: String): Note {
        val note = Note(
            id = System.currentTimeMillis().toString(),
            title = title.ifBlank { "New Note" },
            updatedAt = System.currentTimeMillis()
        )
        notes.add(0, note)
        saveNotes(context)
        return note
    }

    fun updateNote(context: Context, id: String, title: String) {
        val idx = notes.indexOfFirst { it.id == id }
        if (idx >= 0) {
            notes[idx] = notes[idx].copy(title = title, updatedAt = System.currentTimeMillis())
            saveNotes(context)
        }
    }

    fun deleteNote(context: Context, id: String) {
        notes.removeAll { it.id == id }
        saveNotes(context)
    }

    // ---------- Reminders ----------

    val reminders: SnapshotStateList<Reminder> = mutableStateListOf()
    private var remindersLoaded = false

    fun loadReminders(context: Context) {
        if (remindersLoaded) return
        remindersLoaded = true
        val raw = prefs(context).getString("reminders", null) ?: return
        reminders.clear()
        raw.split(REC).forEach { block ->
            if (block.isBlank()) return@forEach
            val p = block.split(FLD)
            if (p.size >= 3) {
                reminders.add(Reminder(p[0], p[1], p[2] == "1"))
            }
        }
    }

    fun saveReminders(context: Context) {
        val raw = reminders.joinToString(REC) {
            "${it.id}$FLD${it.title}$FLD${if (it.done) "1" else "0"}"
        }
        prefs(context).edit().putString("reminders", raw).apply()
    }

    fun addReminder(context: Context, title: String) {
        if (title.isBlank()) return
        reminders.add(Reminder(System.currentTimeMillis().toString(), title, false))
        saveReminders(context)
    }

    fun toggleReminder(context: Context, id: String) {
        val idx = reminders.indexOfFirst { it.id == id }
        if (idx >= 0) {
            reminders[idx] = reminders[idx].copy(done = !reminders[idx].done)
            saveReminders(context)
        }
    }

    fun deleteReminder(context: Context, id: String) {
        reminders.removeAll { it.id == id }
        saveReminders(context)
    }

    // ---------- Call log ----------

    val callLog: SnapshotStateList<CallEntry> = mutableStateListOf()
    private var callsLoaded = false

    fun loadCalls(context: Context) {
        if (callsLoaded) return
        callsLoaded = true
        val raw = prefs(context).getString("calls", null) ?: return
        callLog.clear()
        raw.split(REC).forEach { block ->
            if (block.isBlank()) return@forEach
            val p = block.split(FLD)
            if (p.size >= 3) {
                callLog.add(CallEntry(p[0], p[1], p[2].toLongOrNull() ?: 0L))
            }
        }
    }

    fun saveCalls(context: Context) {
        val raw = callLog.joinToString(REC) { "${it.number}$FLD${it.type}$FLD${it.at}" }
        prefs(context).edit().putString("calls", raw).apply()
    }

    fun addCall(context: Context, number: String, type: String) {
        if (number.isBlank()) return
        callLog.add(0, CallEntry(number, type, System.currentTimeMillis()))
        if (callLog.size > 100) callLog.removeAt(callLog.lastIndex)
        saveCalls(context)
    }
}

data class Note(val id: String, val title: String, val updatedAt: Long)
data class Reminder(val id: String, val title: String, val done: Boolean)
data class CallEntry(val number: String, val type: String, val at: Long)
