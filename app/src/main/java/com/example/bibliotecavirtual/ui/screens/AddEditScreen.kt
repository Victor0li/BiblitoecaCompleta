package com.example.bibliotecavirtual.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bibliotecavirtual.data.Livro
import com.example.bibliotecavirtual.ui.viewmodel.LivroViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    navController: NavController,
    livroId: Int,
    viewModel: LivroViewModel
) {
    val isEditing = livroId != 0
    val livroState = if (isEditing) viewModel.getLivroById(livroId).observeAsState() else remember { mutableStateOf<Livro?>(null) }
    val livro = livroState.value

    // Estados para os campos de entrada
    var titulo by remember { mutableStateOf(livro?.titulo ?: "") }
    var autor by remember { mutableStateOf(livro?.autor ?: "") }
    var ano by remember { mutableStateOf(livro?.anoPublicacao?.toString() ?: "") }
    var genre by remember { mutableStateOf(livro?.genre ?: "") }
    var description by remember { mutableStateOf(livro?.description ?: "") }
    var isLido by remember { mutableStateOf(livro?.isLido ?: false) }

    // Estado para a validação (feedback de erro)
    var isAttemptedSubmit by remember { mutableStateOf(false) }

    LaunchedEffect(livro) {
        if (isEditing && livro != null) {
            titulo = livro.titulo
            autor = livro.autor
            ano = livro.anoPublicacao.toString()
            genre = livro.genre
            description = livro.description
            isLido = livro.isLido
        }
    }

    // --- Lógica de Validação ---
    val anoInt = ano.toIntOrNull()
    val isAnoValid = anoInt != null && anoInt > 0 && ano.length == 4
    val isTituloValid = titulo.isNotBlank()
    val isAutorValid = autor.isNotBlank()
    val isGenreValid = genre.isNotBlank()
    val isDescriptionValid = description.isNotBlank()
    val isFormValid = isTituloValid && isAutorValid && isGenreValid && isDescriptionValid && isAnoValid
    // ---------------------------

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar( // Usando CenterAlignedTopAppBar
                title = {
                    Text(
                        text = if (isEditing) "Editar Informações" else "Novo Livro",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
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

            // --- Seção de Dados Principais (Card) ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "Informações Básicas",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Substituído por OutlinedTextField
                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { titulo = it },
                        label = { Text("Título") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = isAttemptedSubmit && !isTituloValid,
                        supportingText = { if (isAttemptedSubmit && !isTituloValid) Text("O título é obrigatório.") }
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Substituído por OutlinedTextField
                    OutlinedTextField(
                        value = autor,
                        onValueChange = { autor = it },
                        label = { Text("Autor") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = isAttemptedSubmit && !isAutorValid,
                        supportingText = { if (isAttemptedSubmit && !isAutorValid) Text("O autor é obrigatório.") }
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        // Substituído por OutlinedTextField
                        OutlinedTextField(
                            value = genre,
                            onValueChange = { genre = it },
                            label = { Text("Gênero") },
                            modifier = Modifier.weight(1f).padding(end = 8.dp),
                            isError = isAttemptedSubmit && !isGenreValid,
                            supportingText = { if (isAttemptedSubmit && !isGenreValid) Text("Obrigatório.") }
                        )

                        // Substituído por OutlinedTextField
                        OutlinedTextField(
                            value = ano,
                            onValueChange = { if (it.length <= 4) ano = it.filter { char -> char.isDigit() } },
                            label = { Text("Ano") },
                            placeholder = { Text("Ex: 2024") },
                            modifier = Modifier.weight(1f).padding(start = 8.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = isAttemptedSubmit && !isAnoValid,
                            supportingText = { if (isAttemptedSubmit && !isAnoValid) Text("Ano (4 dígitos) inválido.") }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Controle de Status Lido
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Livro já lido?",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Switch(
                            checked = isLido,
                            onCheckedChange = { isLido = it }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Seção de Descrição (Standalone) ---
            Text(
                "Detalhes do Conteúdo",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            // Já estava como OutlinedTextField
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição Completa") },
                placeholder = { Text("Digite um resumo detalhado sobre o livro...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 160.dp),
                singleLine = false,
                isError = isAttemptedSubmit && !isDescriptionValid,
                supportingText = { if (isAttemptedSubmit && !isDescriptionValid) Text("A descrição é obrigatória.") }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- Botão de Ação Principal (Salvar/Atualizar) ---
            Button(
                onClick = {
                    isAttemptedSubmit = true
                    if (isFormValid) {
                        val livroParaSalvar = Livro(
                            id = if (isEditing) livroId else 0,
                            titulo = titulo,
                            autor = autor,
                            anoPublicacao = anoInt!!,
                            genre = genre,
                            description = description,
                            isFavorito = livro?.isFavorito ?: false,
                            isLido = isLido
                        )
                        if (isEditing) {
                            viewModel.atualizar(livroParaSalvar)
                        } else {
                            viewModel.inserir(livroParaSalvar)
                        }
                        navController.popBackStack()
                    }
                },
                enabled = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Text(
                    text = if (isEditing) "CONFIRMAR ATUALIZAÇÃO" else "SALVAR NOVO LIVRO",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}