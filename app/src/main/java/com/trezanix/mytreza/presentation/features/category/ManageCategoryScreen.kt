package com.trezanix.mytreza.presentation.features.category

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trezanix.mytreza.domain.model.Category
import com.trezanix.mytreza.presentation.theme.BrandBlue
import com.trezanix.mytreza.presentation.util.CategoryColors
import com.trezanix.mytreza.presentation.util.CategoryIcons
import com.trezanix.mytreza.presentation.util.getCategoryIcon
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageCategoryScreen(
    onNavigateUp: () -> Unit,
    viewModel: CategoryViewModel = hiltViewModel()
) {
    val categories by viewModel.categories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var selectedTab by remember { mutableStateOf("EXPENSE") }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredList = categories.filter { it.type == selectedTab }

    // State for Dialogs
    var showEditDialog by remember { mutableStateOf<Category?>(null) }
    var showDeleteDialog by remember { mutableStateOf<Category?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Atur Kategori", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF5F7FA))
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = BrandBlue,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .size(64.dp)
                    .shadow(12.dp, CircleShape, spotColor = BrandBlue.copy(alpha = 0.4f))
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(32.dp))
            }
        },
        containerColor = Color(0xFFF5F7FA)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            // 1. MODERN SEGMENTED TABS
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(4.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    ModernTabItem(
                        text = "Pengeluaran",
                        isSelected = selectedTab == "EXPENSE",
                        onClick = { selectedTab = "EXPENSE" },
                        modifier = Modifier.weight(1f)
                    )
                    ModernTabItem(
                        text = "Pemasukan",
                        isSelected = selectedTab == "INCOME",
                        onClick = { selectedTab = "INCOME" },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 2. LIST KATEGORI (CARD STYLE)
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BrandBlue)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (filteredList.isEmpty()) {
                        item {
                            EmptyStateView()
                        }
                    } else {
                        items(filteredList) { category ->
                            val isSystemCategory = category.userId == null
                            ModernCategoryCard(
                                category = category,
                                isSystemCategory = isSystemCategory,
                                onClick = { 
                                    if (!isSystemCategory) {
                                        showEditDialog = category 
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // --- DIALOGS ---

    // 1. Add Dialog
    if (showAddDialog) {
        AddCategoryDialog(
            initialType = selectedTab,
            onDismiss = { showAddDialog = false },
            onSave = { name, type, icon, color ->
                viewModel.createCategory(name, type, icon, color) {
                    showAddDialog = false
                }
            }
        )
    }

    // 2. Edit Dialog (Reusing AddCategoryDialog with initial values)
    showEditDialog?.let { category ->
        AddCategoryDialog(
            initialType = category.type,
            initialName = category.name,
            initialIcon = category.icon ?: "category",
            initialColor = category.color ?: "#000000",
            isEditMode = true,
            onDismiss = { showEditDialog = null },
            onSave = { name, type, icon, color ->
                viewModel.updateCategory(category.id, name, type, icon, color) {
                    showEditDialog = null
                }
            },
            onDelete = {
                showEditDialog = null
                showDeleteDialog = category
            }
        )
    }

    // 3. Delete Confirmation Dialog
    showDeleteDialog?.let { category ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Hapus Kategori?") },
            text = { Text("Apakah Anda yakin ingin menghapus kategori '${category.name}'? Transaksi yang menggunakan kategori ini mungkin akan kehilangan referensinya.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCategory(category.id) {
                            showDeleteDialog = null
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Batal")
                }
            },
            containerColor = Color.White
        )
    }
}

// --- KOMPONEN UI MODERN ---

@Composable
fun ModernTabItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        if (isSelected) BrandBlue else Color.Transparent,
        animationSpec = tween(300), label = "bg"
    )
    val textColor by animateColorAsState(
        if (isSelected) Color.White else Color.Gray,
        animationSpec = tween(300), label = "text"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null // Hilangkan ripple effect agar clean
            ) { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}



@Composable
fun ModernCategoryCard(
    category: Category,
    isSystemCategory: Boolean,
    onClick: () -> Unit
) {
    val parsedColor = try { Color(android.graphics.Color.parseColor(category.color ?: "#CCCCCC")) } catch (e: Exception) { Color.Gray }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = Color.LightGray.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSystemCategory) Color(0xFFFAFAFA) else Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .then(if (!isSystemCategory) Modifier.clickable { onClick() } else Modifier)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Container (Lingkaran Warna)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(parsedColor.copy(alpha = 0.15f)), // Background transparan warna icon
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getCategoryIcon(category.icon),
                    contentDescription = null,
                    tint = parsedColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Nama Kategori
            Text(
                text = category.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black.copy(alpha = 0.8f),
                modifier = Modifier.weight(1f)
            )

            // Actions: Edit (Implicit via click) and Delete
            
            if (!isSystemCategory) {
                // Chevron Arrow
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = Color.LightGray,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                // Lock Icon for System Category
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "System Category",
                    tint = Color.LightGray.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyStateView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(60.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Belum ada kategori", color = Color.Gray, fontWeight = FontWeight.Medium)
        Text("Tambah kategori baru sekarang!", color = Color.LightGray, fontSize = 12.sp)
    }
}

// --- DIALOG WIZARD (Tetap dipertahankan dengan sedikit styling) ---
@Composable
fun AddCategoryDialog(
    initialType: String,
    initialName: String = "",
    initialIcon: String = "fastfood",
    initialColor: String = "#F44336",
    isEditMode: Boolean = false,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var name by remember { mutableStateOf(initialName) }
    var selectedIcon by remember { mutableStateOf(initialIcon) }
    var selectedColor by remember { mutableStateOf(initialColor) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    if (isEditMode) "Edit Kategori" else "Tambah Kategori",
                    fontWeight = FontWeight.Bold
                )
                // Delete Icon in Top Right if in Edit Mode
                if (isEditMode && onDelete != null) {
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus Kategori",
                            tint = Color.Red
                        )
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(androidx.compose.foundation.rememberScrollState())
            ) {
                // 1. Name Input
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Kategori") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandBlue,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Icon Picker
                Text("Pilih Ikon", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.height(180.dp).fillMaxWidth().border(1.dp, Color.LightGray.copy(alpha=0.3f), RoundedCornerShape(8.dp)).padding(4.dp)) {
                    LazyVerticalGrid(columns = GridCells.Adaptive(48.dp)) {
                        items(CategoryIcons.iconKeys) { iconKey ->
                            val isSelected = selectedIcon == iconKey
                            IconButton(
                                onClick = { selectedIcon = iconKey },
                                modifier = Modifier.padding(2.dp)
                            ) {
                                Icon(
                                    imageVector = getCategoryIcon(iconKey),
                                    contentDescription = null,
                                    tint = if (isSelected) BrandBlue else Color.Gray,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Color Picker
                Text("Pilih Warna", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(40.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(120.dp)
                ) {
                    items(CategoryColors.colors) { colorHex ->
                        val isSelected = selectedColor == colorHex
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(android.graphics.Color.parseColor(colorHex)))
                                .clickable { selectedColor = colorHex }
                                .border(
                                    width = if (isSelected) 3.dp else 0.dp,
                                    color = if (isSelected) Color.Black.copy(alpha = 0.5f) else Color.Transparent,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                         onSave(name, initialType, selectedIcon, selectedColor)
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Batal", color = Color.Gray) }
        }
    )
}