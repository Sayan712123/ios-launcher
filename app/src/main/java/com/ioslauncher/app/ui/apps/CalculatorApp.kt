package com.ioslauncher.app.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ioslauncher.app.ui.components.IosTopBar
import com.ioslauncher.app.ui.theme.IosColors
import kotlin.math.roundToLong

/** iOS Calculator: dark keypad, orange operators, live expression line. */
@Composable
fun CalculatorApp(onBack: () -> Unit) {
    var display by remember { mutableStateOf("0") }
    var accumulator by remember { mutableStateOf<Double?>(null) }
    var pendingOp by remember { mutableStateOf<String?>(null) }
    var freshEntry by remember { mutableStateOf(true) }
    var history by remember { mutableStateOf("") }

    fun format(v: Double): String {
        if (v.isNaN() || v.isInfinite()) return "Error"
        val rounded = (v * 1_000_000.0).roundToLong() / 1_000_000.0
        return if (rounded == rounded.toLong().toDouble()) rounded.toLong().toString()
        else rounded.toString()
    }

    fun applyPending(): Double? {
        val current = display.toDoubleOrNull() ?: 0.0
        val acc = accumulator ?: return current
        return when (pendingOp) {
            "+" -> acc + current
            "\u2212" -> acc - current
            "\u00D7" -> acc * current
            "\u00F7" -> if (current == 0.0) 0.0 else acc / current
            else -> current
        }
    }

    fun applyOperator(op: String) {
        val result = applyPending() ?: 0.0
        accumulator = result
        display = format(result)
        pendingOp = op
        freshEntry = true
        history = "${format(result)} $op"
    }

    fun doEquals() {
        val result = applyPending() ?: return
        history = ""
        display = format(result)
        accumulator = null
        pendingOp = null
        freshEntry = true
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        IosTopBar(title = "Calculator", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            Text(history, color = IosColors.SecondaryLabel, fontSize = 20.sp, maxLines = 1)
            Text(
                display,
                color = Color.White,
                fontSize = if (display.length > 8) 56.sp else 76.sp,
                fontWeight = FontWeight.Light,
                maxLines = 1,
                textAlign = TextAlign.End
            )
            Spacer(Modifier.height(12.dp))
        }

        val rows = listOf(
            listOf("AC", "\u00B1", "%", "\u00F7"),
            listOf("7", "8", "9", "\u00D7"),
            listOf("4", "5", "6", "\u2212"),
            listOf("1", "2", "3", "+"),
            listOf("0", ".", "=")
        )

        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            rows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    row.forEach { key ->
                        val isOperator = key in listOf("\u00F7", "\u00D7", "\u2212", "+", "=")
                        val isTop = key in listOf("AC", "\u00B1", "%")
                        val wide = key == "0"
                        Box(
                            modifier = Modifier
                                .weight(if (wide) 2.1f else 1f)
                                .aspectRatio(if (wide) 2.1f else 1f)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isOperator -> IosColors.Orange
                                        isTop -> Color(0xFFA5A5A5)
                                        else -> Color(0xFF333333)
                                    }
                                )
                                .clickable {
                                    when (key) {
                                        "AC" -> {
                                            display = "0"
                                            accumulator = null
                                            pendingOp = null
                                            freshEntry = true
                                            history = ""
                                        }
                                        "\u00B1" -> display =
                                            format((display.toDoubleOrNull() ?: 0.0) * -1)
                                        "%" -> display =
                                            format((display.toDoubleOrNull() ?: 0.0) / 100.0)
                                        "=" -> doEquals()
                                        "+", "\u2212", "\u00D7", "\u00F7" -> applyOperator(key)
                                        "." -> if (!display.contains(".")) {
                                            display += "."
                                            freshEntry = false
                                        }
                                        else -> {
                                            display = if (freshEntry || display == "0") key
                                            else display + key
                                            freshEntry = false
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                key,
                                color = if (isTop) Color.Black else Color.White,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}
