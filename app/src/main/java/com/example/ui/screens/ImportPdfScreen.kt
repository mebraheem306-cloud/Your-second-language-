package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.ImportReport
import com.example.ui.theme.AccentTeal
import com.example.ui.theme.CorrectGreen
import com.example.ui.theme.DeepNavyPrimary
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportPdfScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var inputJson by remember {
        mutableStateOf(
            """{
  "lessons": [
    {
      "id": 12,
      "lessonNumber": 12,
      "title": "Job Interviews",
      "titleAr": "مقابلات العمل",
      "description": "Professional questions, strengths and weaknesses, polite responses.",
      "sourceFilename": "Lesson_12_Job_Interviews.pdf",
      "pageCount": 6
    }
  ]
}"""
        )
    }

    var report by remember { mutableStateOf<ImportReport?>(null) }
    var isImporting by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PDF Curriculum Importer", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = DeepNavyPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Curriculum Import Architecture", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "This structured import pipeline ingests extracted PDF lessons with OCR validation into the local Room database, supporting future course expansion beyond the initial 11 units.",
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Text("Structured Lesson JSON Payload", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = inputJson,
                    onValueChange = { inputJson = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .testTag("import_json_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item {
                Button(
                    onClick = {
                        isImporting = true
                        coroutineScope.launch {
                            val res = viewModel.repository.importJsonData(inputJson)
                            report = res
                            isImporting = false
                            viewModel.refreshCounts()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_run_import"),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepNavyPrimary),
                    enabled = !isImporting && inputJson.isNotBlank()
                ) {
                    if (isImporting) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Icon(Icons.Default.Upload, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ingest & Validate Content")
                    }
                }
            }

            // Import Report Card
            if (report != null) {
                item {
                    val r = report!!
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (r.success) CorrectGreen.copy(alpha = 0.12f) else MaterialTheme.colorScheme.errorContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (r.success) Icons.Default.CheckCircle else Icons.Default.Error,
                                    contentDescription = null,
                                    tint = if (r.success) CorrectGreen else MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (r.success) "Ingestion Report: Success" else "Ingestion Report: Error",
                                    fontWeight = FontWeight.Bold,
                                    color = if (r.success) CorrectGreen else MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = r.message,
                                fontSize = 13.sp,
                                color = if (r.success) CorrectGreen else MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        }
    }
}
