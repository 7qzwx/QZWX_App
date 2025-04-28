package com.qzwx.feature_accountbook.page.homepage

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NewLabel
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(modifier : Modifier = Modifier) {
    Scaffold(
        topBar = {
            HomePage_TopBar()
        }
    ) { paddingValues ->
        // Scaffold 内容
        LazyColumn(Modifier.padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally) {
            item() { HomePage_Card() }
            item() {
                Button(onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 12.dp, start = 12.dp),
                    shape = RoundedCornerShape(2.dp)) {
                    Icon(Icons.Filled.NewLabel, null)
                    Text("添加一条新记账", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            item() { HomePage_BudgetCard() }
        }
    }
}