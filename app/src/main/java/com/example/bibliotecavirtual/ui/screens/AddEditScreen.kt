package com.example.bibliotecavirtual.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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

    // --- CORREÇÃO: Usa a mesma arquitetura da DetailScreen com StateFlow ---
    // 1. Efeito para carregar o livro se estivermos no modo de edição.
    LaunchedEffect(livroId) {
        if (isEditing) {
            viewModel.getLivroById(livroId)
        }
    }

    // 2. Observa o livro selecionado do ViewModel.
    val livro by viewModel.selectedLivro.collectAsState()

    // 3. Garante que o estado seja limpo ao sair da tela.
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearSelectedLivro()
        }
    }
    // ---------------------------------------------------------------------

    // Estados para os campos de entrada
    var titulo by remember { mutableStateOf("") }
    var autor by remember { mutableStateOf("") }
    var ano by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isLido by remember { mutableStateOf(false) }

    var isAttemptedSubmit by remember { mutableStateOf(false) }

    // Efeito para sincronizar o estado local quando o livro for carregado (no modo de edição)
    LaunchedEffect(livro) {
        if (isEditing && livro != null) {
            titulo = livro!!.titulo
            autor = livro!!.autor
            ano = livro!!.anoPublicacao.toString()
            genre = livro!!.genre
            description = livro!!.description
            isLido = livro!!.isLido
        }
    }

    // --- Lógica de Validação (sem mudanças) ---
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
            CenterAlignedTopAppBar(
                title = { Text(if (isEditing) "Editar Livro" else "Novo Livro") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        // No modo de edição, se o livro ainda não foi carregado, mostra um spinner.
        if (isEditing && livro == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            // Mostra o formulário para adicionar um novo livro ou quando o livro para edição foi carregado.
            FormContent(
                paddingValues = paddingValues,
                isEditing = isEditing,
                isFormValid = isFormValid,
                isAttemptedSubmit = isAttemptedSubmit,
                titulo = titulo, onTituloChange = { titulo = it }, isTituloValid = isTituloValid,
                autor = autor, onAutorChange = { autor = it }, isAutorValid = isAutorValid,
                genre = genre, onGenreChange = { genre = it }, isGenreValid = isGenreValid,
                ano = ano, onAnoChange = { if (it.length <= 4) ano = it.filter { char -> char.isDigit() } }, isAnoValid = isAnoValid,
                description = description, onDescriptionChange = { description = it }, isDescriptionValid = isDescriptionValid,
                isLido = isLido, onIsLidoChange = { isLido = it },
                onSave = {
                    isAttemptedSubmit = true
                    if (isFormValid) {
                        val livroParaSalvar = Livro(
                            id = if (isEditing) livroId else 0,
                            usuarioId = livro?.usuarioId ?: viewModel.currentUserId,
                            titulo = titulo,
                            autor = autor,
                            anoPublicacao = anoInt!!,
                            genre = genre,
                            description = description,
                            isFavorito = livro?.isFavorito ?: false,
                            isLido = isLido,
                            imageUrl = if (isEditing) livro?.imageUrl else null
                        )
                        if (isEditing) {
                            viewModel.atualizar(livroParaSalvar)
                        } else {
                            viewModel.inserir(livroParaSalvar)
                        }
                        navController.popBackStack()
                    }
                }
            )
        }
    }
}


// Composable auxiliar para o formulário, para manter o código principal mais limpo.
@Composable
private fun FormContent(
    paddingValues: PaddingValues,
    isEditing: Boolean,
    isFormValid: Boolean,
    isAttemptedSubmit: Boolean,
    titulo: String, onTituloChange: (String) -> Unit, isTituloValid: Boolean,
    autor: String, onAutorChange: (String) -> Unit, isAutorValid: Boolean,
    genre: String, onGenreChange: (String) -> Unit, isGenreValid: Boolean,
    ano: String, onAnoChange: (String) -> Unit, isAnoValid: Boolean,
    description: String, onDescriptionChange: (String) -> Unit, isDescriptionValid: Boolean,
    isLido: Boolean, onIsLidoChange: (Boolean) -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // (O conteúdo do seu formulário é colado aqui, sem alterações lógicas)
        OutlinedTextField(
            value = titulo, onValueChange = onTituloChange, label = { Text("Título") },
            modifier = Modifier.fillMaxWidth(), isError = isAttemptedSubmit && !isTituloValid,
            supportingText = { if (isAttemptedSubmit && !isTituloValid) Text("O título é obrigatório.") }
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = autor, onValueChange = onAutorChange, label = { Text("Autor") },
            modifier = Modifier.fillMaxWidth(), isError = isAttemptedSubmit && !isAutorValid,
            supportingText = { if (isAttemptedSubmit && !isAutorValid) Text("O autor é obrigatório.") }
        )
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = genre, onValueChange = onGenreChange, label = { Text("Gênero") },
                modifier = Modifier.weight(1f).padding(end = 8.dp), isError = isAttemptedSubmit && !isGenreValid,
                supportingText = { if (isAttemptedSubmit && !isGenreValid) Text("Obrigatório.") }
            )
            OutlinedTextField(
                value = ano, onValueChange = onAnoChange, label = { Text("Ano") },
                modifier = Modifier.weight(1f).padding(start = 8.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = isAttemptedSubmit && !isAnoValid,
                supportingText = { if (isAttemptedSubmit && !isAnoValid) Text("Ano inválido.") }
            )
        }
        Spacer(Modifier.height(20.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Livro já lido?", style = MaterialTheme.typography.titleMedium)
            Switch(checked = isLido, onCheckedChange = onIsLidoChange)
        }
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = description, onValueChange = onDescriptionChange, label = { Text("Descrição Completa") },
            modifier = Modifier.fillMaxWidth().heightIn(min = 160.dp), singleLine = false,
            isError = isAttemptedSubmit && !isDescriptionValid,
            supportingText = { if (isAttemptedSubmit && !isDescriptionValid) Text("A descrição é obrigatória.") }
        )
        Spacer(Modifier.height(32.dp))
        Button(onClick = onSave, modifier = Modifier.fillMaxWidth().height(56.dp)) {
            Text(if (isEditing) "CONFIRMAR ATUALIZAÇÃO" else "SALVAR NOVO LIVRO")
        }
    }
}
