package com.example.bibliotecavirtual.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.bibliotecavirtual.data.Livro
import com.example.bibliotecavirtual.ui.viewmodel.LivroViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LivroDetailScreen(
    navController: NavHostController,
    livroId: Int,
    viewModel: LivroViewModel
) {
    LaunchedEffect(livroId) {
        viewModel.getLivroById(livroId)
    }

    val livro by viewModel.selectedLivro.collectAsState()

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearSelectedLivro()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        livro?.titulo ?: "Detalhes",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    if (livro != null) {
                        IconButton(onClick = { navController.navigate("add_edit/${livro!!.id}") }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar Livro")
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
                )
            )
        }
    ) { paddingValues ->
        if (livro == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            }
        } else {
            DetalhesDoLivroContent(
                paddingValues = paddingValues,
                livro = livro!!,
                viewModel = viewModel,
                navController = navController
            )
        }
    }
}

@Composable
private fun DetalhesDoLivroContent(
    paddingValues: PaddingValues,
    livro: Livro,
    viewModel: LivroViewModel,
    navController: NavHostController
) {
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val onSecondaryColor = MaterialTheme.colorScheme.onSecondary
    val errorColor = MaterialTheme.colorScheme.error
    val favoriteColor = Color(0xFFE8B923)

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Card(
            modifier = Modifier.fillMaxWidth(0.6f),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            AsyncImage(
                model = livro.imageUrl?.replace("http://", "https://"),
                contentDescription = "Capa do Livro ${livro.titulo}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f),
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = livro.titulo,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "de ${livro.autor}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatusTag(
                icon = if (livro.isLido) Icons.Filled.Check else Icons.Filled.MenuBook,
                label = if (livro.isLido) "LIDO" else "MARCAR COMO LIDO",
                color = if (livro.isLido) secondaryColor else MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = if (livro.isLido) onSecondaryColor else MaterialTheme.colorScheme.onSurface,
                onClick = { viewModel.toggleLido(livro) },
                modifier = Modifier.weight(1f)
            )
            StatusTag(
                icon = if (livro.isFavorito) Icons.Filled.Star else Icons.Outlined.StarOutline,
                label = if (livro.isFavorito) "FAVORITO" else "FAVORITAR",
                color = if (livro.isFavorito) favoriteColor.copy(alpha = 0.8f) else MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurface,
                onClick = { viewModel.toggleFavorito(livro) },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Divider()
        Spacer(modifier = Modifier.height(24.dp))

        InfoRow(label = "Gênero", value = livro.genre)
        InfoRow(label = "Publicação", value = livro.anoPublicacao.toString())
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Sinopse",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceContainerLow
        ) {
            Text(
                livro.description.ifBlank { "Nenhuma sinopse disponível." },
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
        }
        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = {
                viewModel.deletar(livro)
                navController.popBackStack()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = errorColor,
                contentColor = MaterialTheme.colorScheme.onError
            ),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
            Text("Remover Livro", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun StatusTag(icon: ImageVector, label: String, color: Color, contentColor: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color, contentColor = contentColor),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
