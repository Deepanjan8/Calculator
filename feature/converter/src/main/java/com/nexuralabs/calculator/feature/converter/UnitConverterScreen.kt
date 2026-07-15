package com.nexuralabs.calculator.feature.converter

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Immutable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nexuralabs.calculator.core.navigation.NexuraRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitConverterScreen(navController: NavController) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = { navController.popBackStack() },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    // Offload animation translations and alpha changes of the container to GPU
                    // to prevent layout re-composition during rapid drag or fling gestures.
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Unit Converter",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
            HorizontalDivider()
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(converterCategories, key = { it.name }) { category ->
                    Card(
                        onClick = {
                            navController.popBackStack()
                            navController.navigate(NexuraRoutes.converterDetail(category.name))
                        },
                        modifier = Modifier
                            .aspectRatio(1f)
                            .graphicsLayer {
                                // Cache individual category cards on GPU to avoid redraws on fling
                            },
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = category.icon,
                                contentDescription = category.name,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Immutable
data class ConverterCategory(val name: String, val icon: ImageVector)

private val converterCategories = listOf(
    ConverterCategory("Length", Icons.Default.Straighten),
    ConverterCategory("Area", Icons.Default.Square),
    ConverterCategory("Volume", Icons.Default.LocalDrink),
    ConverterCategory("Mass", Icons.Default.Scale),
    ConverterCategory("Temperature", Icons.Default.Thermostat),
    ConverterCategory("Time", Icons.Default.AccessTime),
    ConverterCategory("Speed", Icons.Default.Speed),
    ConverterCategory("Pressure", Icons.Default.Compress),
    ConverterCategory("Energy", Icons.Default.Bolt)
)
