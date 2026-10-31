package com.example.ui.cash

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.CashNoteEntity
import com.example.data.model.CashType
import com.example.data.model.TodoEntity
import com.example.ui.components.getCategoryIcon
import com.example.ui.components.getPaymentMethodIcon
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import java.util.UUID

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CashNoteDialog(
    initialCashNote: CashNoteEntity? = null,
    prefilledTodo: TodoEntity? = null,
    onDismiss: () -> Unit,
    onSave: (CashNoteEntity) -> Unit
) {
    var type by remember { mutableStateOf(initialCashNote?.type ?: CashType.EXPENSE) }
    var amountText by remember {
        mutableStateOf(
            initialCashNote?.amount?.toString()
                ?: prefilledTodo?.estimatedCost?.let { if (it > 0) it.toString() else "" }
                ?: ""
        )
    }
    var title by remember {
        mutableStateOf(
            initialCashNote?.title
                ?: prefilledTodo?.title
                ?: ""
        )
    }
    var category by remember {
        mutableStateOf(
            initialCashNote?.category
                ?: when (prefilledTodo?.category?.lowercase()) {
                    "shopping" -> "Shopping"
                    "work" -> "Bills & Utilities"
                    else -> "Food & Dining"
                }
        )
    }
    var paymentMethod by remember { mutableStateOf(initialCashNote?.paymentMethod ?: "Cash") }
    var referenceSlip by remember {
        mutableStateOf(
            initialCashNote?.referenceSlip
                ?: "SLIP-${UUID.randomUUID().toString().take(6).uppercase()}"
        )
    }
    var noteText by remember { mutableStateOf(initialCashNote?.noteText ?: "") }
    var relatedTodoTitle by remember {
        mutableStateOf(initialCashNote?.relatedTodoTitle ?: prefilledTodo?.title)
    }

    var titleError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }

    val categories = listOf(
        "Food & Dining",
        "Bills & Utilities",
        "Salary",
        "Shopping",
        "Transport",
        "Investment",
        "Health",
        "Entertainment",
        "Other"
    )

    val paymentMethods = listOf("Cash", "Credit Card", "Debit Card", "Bank Transfer", "Digital Wallet", "Crypto")

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth(0.94f)
            .heightIn(max = 700.dp)
            .testTag("cash_note_dialog"),
        title = {
            Text(
                text = if (initialCashNote == null) "Record Cash Note" else "Edit Cash Note",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Type Selector: Income (Green) vs Expense (Red)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Expense button
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (type == CashType.EXPENSE) ExpenseRed else ExpenseRed.copy(alpha = 0.12f),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { type = CashType.EXPENSE }
                            .testTag("cash_type_expense")
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            Text(
                                text = "Expense",
                                fontWeight = FontWeight.Bold,
                                color = if (type == CashType.EXPENSE) Color.White else ExpenseRed
                            )
                        }
                    }

                    // Income button
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (type == CashType.INCOME) IncomeGreen else IncomeGreen.copy(alpha = 0.12f),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { type = CashType.INCOME }
                            .testTag("cash_type_income")
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            Text(
                                text = "Income",
                                fontWeight = FontWeight.Bold,
                                color = if (type == CashType.INCOME) Color.White else IncomeGreen
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Amount Input
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { input ->
                        if (input.isEmpty() || input.matches(Regex("""^\d*\.?\d{0,2}$"""))) {
                            amountText = input
                            if (input.toDoubleOrNull() != null) amountError = false
                        }
                    },
                    label = { Text("Amount *") },
                    prefix = { Text("$ ", fontWeight = FontWeight.Bold) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = amountError,
                    supportingText = { if (amountError) Text("Valid amount is required") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cash_amount_input")
                )

                // Quick Increment Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(10, 50, 100, 500).forEach { inc ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable {
                                val current = amountText.toDoubleOrNull() ?: 0.0
                                amountText = String.format("%.2f", current + inc)
                                amountError = false
                            }
                        ) {
                            Text(
                                text = "+$$inc",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Title / Description
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (it.isNotBlank()) titleError = false
                    },
                    label = { Text("Title / Merchant / Source *") },
                    isError = titleError,
                    supportingText = { if (titleError) Text("Title is required") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cash_title_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Category Selection
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            leadingIcon = {
                                Icon(
                                    imageVector = getCategoryIcon(cat),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            label = { Text(cat, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            modifier = Modifier.testTag("cash_cat_$cat")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Payment Method Selection
                Text(
                    text = "Payment Method",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    paymentMethods.forEach { method ->
                        FilterChip(
                            selected = paymentMethod == method,
                            onClick = { paymentMethod = method },
                            leadingIcon = {
                                Icon(
                                    imageVector = getPaymentMethodIcon(method),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            label = { Text(method, fontSize = 11.sp) },
                            modifier = Modifier.testTag("payment_method_$method")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Reference Slip #
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = referenceSlip,
                        onValueChange = { referenceSlip = it },
                        label = { Text("Slip / Receipt Voucher #") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("cash_slip_input")
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = {
                            referenceSlip = "SLIP-${UUID.randomUUID().toString().take(6).uppercase()}"
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Autorenew, contentDescription = "Regenerate slip number")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Linked Task note
                if (!relatedTodoTitle.isNullOrBlank()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Linked Task: $relatedTodoTitle",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Memo & Remarks
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("Cash Memo & Itemized Remarks") },
                    maxLines = 4,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cash_memo_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amountVal = amountText.toDoubleOrNull()
                    if (amountVal == null || amountVal <= 0) {
                        amountError = true
                        return@Button
                    }
                    if (title.isBlank()) {
                        titleError = true
                        return@Button
                    }

                    val entity = initialCashNote?.copy(
                        title = title.trim(),
                        amount = amountVal,
                        type = type,
                        category = category,
                        paymentMethod = paymentMethod,
                        referenceSlip = referenceSlip.trim(),
                        noteText = noteText.trim(),
                        relatedTodoTitle = relatedTodoTitle
                    ) ?: CashNoteEntity(
                        title = title.trim(),
                        amount = amountVal,
                        type = type,
                        category = category,
                        paymentMethod = paymentMethod,
                        referenceSlip = referenceSlip.trim(),
                        noteText = noteText.trim(),
                        relatedTodoTitle = relatedTodoTitle,
                        transactionDate = System.currentTimeMillis()
                    )
                    onSave(entity)
                },
                modifier = Modifier.testTag("save_cash_note_btn")
            ) {
                Text(if (initialCashNote == null) "Save Cash Note" else "Update Note")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_cash_note_btn")
            ) {
                Text("Cancel")
            }
        }
    )
}
