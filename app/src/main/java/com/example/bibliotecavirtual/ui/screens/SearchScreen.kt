package com.example.bibliotecavirtual.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.bibliotecavirtual.data.Livro
import com.example.bibliotecavirtual.ui.viewmodel.LivroViewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController, viewModel: LivroViewModel) {

    val searchedLivro by viewModel.searchedLivro.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
    val errorMessage by viewModel.searchErrorMessage.collectAsStateWithLifecycle()

    var isbnInput by remember { mutableStateOf("") }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearSearchState()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Buscar Livro por ISBN") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- Campo de Input de ISBN ---
            OutlinedTextField(
                value = isbnInput,
                onValueChange = { isbnInput = it.filter { char -> char.isDigit() || char == '-' } },
                label = { Text("ISBN (10 ou 13 dígitos)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (isbnInput.isNotBlank()) {
                        viewModel.searchBook(isbnInput.trim().replace("-", "")) // Remove hifens e espaços
                    }
                },
                enabled = isbnInput.isNotBlank() && !isSearching,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isSearching) "Buscando..." else "PESQUISAR")
            }

            Spacer(modifier = Modifier.height(32.dp))
            Divider()
            Spacer(modifier = Modifier.height(32.dp))

            if (isSearching) {
                CircularProgressIndicator()
            } else if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "Erro desconhecido.",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(16.dp)
                )
            } else if (searchedLivro != null) {
                LivroSearchResult(
                    livro = searchedLivro!!,
                    onAddToShelf = {
                        viewModel.inserir(searchedLivro!!)
                        navController.popBackStack()
                    }
                )
            } else {
                Text("Digite o ISBN para buscar um livro.")
            }
        }
    }
}

@Composable
fun LivroSearchResult(livro: Livro, onAddToShelf: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            livro.imageUrl?.let { url ->
                AsyncImage(
                    model = url.replace("http://", "https://"),
                    contentDescription = "Capa do Livro ${livro.titulo}",
                    modifier = Modifier
                        .size(height = 160.dp, width = 100.dp)
                        .padding(bottom = 16.dp)
                )
            } ?: run {
                Icon(
                    Icons.Default.Book,
                    contentDescription = "Sem capa",
                    modifier = Modifier.size(80.dp).padding(bottom = 16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                "LIVRO ENCONTRADO",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                livro.titulo,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "por ${livro.autor}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AssistChip(onClick = {}, label = { Text(livro.genre) })
                AssistChip(onClick = {}, label = { Text(livro.anoPublicacao.toString()) })
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                livro.description.take(200) + if (livro.description.length > 200) "..." else "",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))


            Button(
                onClick = onAddToShelf,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Icon(Icons.Default.Book, contentDescription = null, Modifier.padding(end = 8.dp))
                Text("ADICIONAR À MINHA ESTANTE")
            }
        }
    }
}