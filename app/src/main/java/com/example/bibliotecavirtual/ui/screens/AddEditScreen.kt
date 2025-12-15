package com.example.bibliotecavirtual.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.bibliotecavirtual.data.Livro
import com.example.bibliotecavirtual.ui.viewmodel.LivroViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    navController: NavHostController,
    livroId: Int,
    viewModel: LivroViewModel
) {
    val isEditing = livroId != 0

    var hasLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(livroId) {
        if (isEditing) {
            viewModel.getLivroById(livroId)
        }
    }

    val livroState by viewModel.selectedLivro.collectAsState()
    val livro = livroState

    var titulo by remember { mutableStateOf("") }
    var autor by remember { mutableStateOf("") }
    var ano by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isLido by remember { mutableStateOf(false) }
    var isAttemptedSubmit by remember { mutableStateOf(false) }

    LaunchedEffect(livro) {
        if (isEditing && livro != null && !hasLoaded) {
            titulo = livro.titulo
            autor = livro.autor
            ano = livro.anoPublicacao.toString()
            genre = livro.genre
            description = livro.description
            isLido = livro.isLido
            hasLoaded = true
        }
    }

    val anoInt = ano.toIntOrNull()
    val isAnoValid = anoInt != null && ano.length == 4
    val isFormValid =
        titulo.isNotBlank() &&
                autor.isNotBlank() &&
                genre.isNotBlank() &&
                description.isNotBlank() &&
                isAnoValid

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (isEditing) "Editar Livro" else "Novo Livro") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->

        if (isEditing && !hasLoaded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                isError = isAttemptedSubmit && titulo.isBlank()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = autor,
                onValueChange = { autor = it },
                label = { Text("Autor") },
                modifier = Modifier.fillMaxWidth(),
                isError = isAttemptedSubmit && autor.isBlank()
            )

            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = genre,
                    onValueChange = { genre = it },
                    label = { Text("Gênero") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    isError = isAttemptedSubmit && genre.isBlank()
                )

                OutlinedTextField(
                    value = ano,
                    onValueChange = {
                        if (it.length <= 4) ano = it.filter(Char::isDigit)
                    },
                    label = { Text("Ano") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = isAttemptedSubmit && !isAnoValid
                )
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Livro já lido?")
                Switch(
                    checked = isLido,
                    onCheckedChange = { isLido = it }
                )
            }

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 160.dp),
                isError = isAttemptedSubmit && description.isBlank()
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    isAttemptedSubmit = true
                    if (isFormValid && livro != null) {
                        val livroSalvar = Livro(
                            id = if (isEditing) livroId else 0,
                            usuarioId = livro.usuarioId,
                            titulo = titulo,
                            autor = autor,
                            anoPublicacao = anoInt!!,
                            genre = genre,
                            description = description,
                            isFavorito = livro.isFavorito,
                            isLido = isLido,
                            imageUrl = livro.imageUrl
                        )

                        if (isEditing) {
                            viewModel.atualizar(livroSalvar)
                        } else {
                            viewModel.inserir(livroSalvar)
                        }

                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(if (isEditing) "CONFIRMAR ATUALIZAÇÃO" else "SALVAR NOVO LIVRO")
            }
        }
    }
}
