

package com.example.jettip

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jettip.Components.inputField
import com.example.jettip.ui.theme.JetTipTheme
import com.example.jettip.util.calculateTotalPerPerson
import com.example.jettip.util.calculateTotalTip
import com.example.jettip.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            myApp {
                mainContent()
            }
        }
    }
}

@Composable
fun topHeader(totalPerPerson: Double = 134.0000) {
    Surface(
        modifier = Modifier
            .wrapContentSize(align = Alignment.TopCenter)
            .width(370.dp)
            .padding(40.dp)
            .height(150.dp)
            .shadow(12.dp, shape = RoundedCornerShape(corner = CornerSize(12.dp)))
            .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp))),
        color = Color(0xFFCF9FFF)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val total = "%.2f".format(totalPerPerson)
            Text(
                "Total Per Person",
                fontWeight = FontWeight(500),
                fontStyle = FontStyle.Italic,
                fontSize = 25.sp
            )
            Text(
                "$ $total",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 30.sp
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
fun mainContent() {
    BillForm { billAmt ->
        Log.d("AMT", "mainContent: $billAmt")
    }
}

@Composable
fun myApp(content: @Composable () -> Unit) {
    JetTipTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    onValChange: (String) -> Unit = {}
) {
    val totalBillState = remember {
        mutableStateOf("")
    }

    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty() && totalBillState.value.toDoubleOrNull() != null
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    var PersonSplit = remember {
        mutableIntStateOf(1) // Start with 1 to avoid issues
    }

    val sliderPositionState = remember {
        mutableStateOf(0f)
    }

    val range = IntRange(start = 1, endInclusive = 100)

    val tipPercentage = (sliderPositionState.value * 100).toInt()

    val tipAmountState = remember {
        mutableStateOf(0.0)
    }

    val totalPerPersonState = remember{
        mutableStateOf(0.0)
    }

    topHeader(totalPerPerson = totalPerPersonState.value)

    Surface(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth()
            .wrapContentSize(),
        shape = RoundedCornerShape(corner = CornerSize(12.dp)),
        border = BorderStroke(1.dp, Color.LightGray),
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            inputField(
                valueState = totalBillState,
                labelId = "Enter Bill",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions
                    onValChange(totalBillState.value.trim())

                    keyboardController?.hide()
                }
            )

            if (validState) {  // Only show these elements if the bill is valid

                Row(
                    modifier = Modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        "Split",
                        modifier = Modifier.align(
                            alignment = Alignment.CenterVertically
                        )
                    )

                    Spacer(modifier = Modifier.width(120.dp))

                    Row(
                        modifier = Modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        RoundIconButton(imageVector = Icons.Default.Remove, onClick = {
                            Log.d("Icon", "BillForm: Remove")
                            PersonSplit.value =
                                if (PersonSplit.value > 1) PersonSplit.value - 1
                                else 1

                            totalPerPersonState.value =
                                calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(), splitBy = PersonSplit.value,tipPercentage = tipPercentage)
                        })

                        Text(
                            text = "${PersonSplit.value}",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 9.dp, end = 9.dp)
                        )

                        RoundIconButton(imageVector = Icons.Default.Add, onClick = {
                            Log.d("Icon", "BillForm: Add")
                            if (PersonSplit.value < range.last) PersonSplit.value =
                                PersonSplit.value + 1

                            totalPerPersonState.value =
                                calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(), splitBy = PersonSplit.value,tipPercentage = tipPercentage)
                        })
                    }
                }

                Row(
                    modifier = Modifier.padding(horizontal = 3.dp, vertical = 12.dp)
                ) {
                    Text(
                        "Tip",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )

                    Spacer(modifier = modifier.width(200.dp))

                    Text(
                        " $ ${tipAmountState.value}",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("$tipPercentage %")

                    Spacer(modifier = Modifier.height(14.dp))

                    Slider(
                        value = sliderPositionState.value, onValueChange = { newVal ->
                            sliderPositionState.value = newVal
                            tipAmountState.value =
                                calculateTotalTip(
                                    totalBill = totalBillState.value.toDouble(),
                                    tipPercentage = tipPercentage
                                )

                            totalPerPersonState.value =
                                calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(), splitBy = PersonSplit.value,tipPercentage = tipPercentage)
                        },
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                    )
                }
            }
        }
    }
}
