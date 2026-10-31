package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.CashNoteEntity
import com.example.data.model.CashType
import com.example.data.model.NoteEntity
import com.example.data.model.Priority
import com.example.data.model.Subtask
import com.example.data.model.TodoEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("OmniNote", appName)
  }

  @Test
  fun `verify todo subtasks serialization and formatting`() {
    val subtasks = listOf(
      Subtask(title = "Task 1", isCompleted = true),
      Subtask(title = "Task 2", isCompleted = false)
    )
    val raw = TodoEntity.formatSubtasks(subtasks)
    val todo = TodoEntity(title = "Parent Task", subtasksRaw = raw)
    val parsed = todo.getSubtasks()

    assertEquals(2, parsed.size)
    assertEquals("Task 1", parsed[0].title)
    assertTrue(parsed[0].isCompleted)
    assertEquals("Task 2", parsed[1].title)
    assertTrue(!parsed[1].isCompleted)
  }

  @Test
  fun `verify cash note properties`() {
    val cash = CashNoteEntity(
      title = "Freelance Project",
      amount = 1500.0,
      type = CashType.INCOME,
      category = "Salary",
      paymentMethod = "Bank Transfer"
    )
    assertEquals(CashType.INCOME, cash.type)
    assertEquals(1500.0, cash.amount, 0.001)
  }
}
