package com.example.bibliotecavirtual.data.repository

import com.example.bibliotecavirtual.data.Usuario
import com.example.bibliotecavirtual.data.db.UsuarioDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Repositório responsável pela lógica de autenticação e gerenciamento de sessão
 * (quem está logado no momento).
 */
class AuthRepository(private val usuarioDao: UsuarioDao) {

    // --- GERENCIAMENTO DE SESSÃO ---

    // O Flow que rastreia o ID do usuário logado (Long? = deslogado)
    private val _currentUserId = MutableStateFlow<Long?>(null)
    val currentUserId: StateFlow<Long?> = _currentUserId.asStateFlow()

    // O Flow que rastreia os dados completos do usuário logado (Usuario? = deslogado)
    private val _currentUser = MutableStateFlow<Usuario?>(null)
    val currentUser: StateFlow<Usuario?> = _currentUser.asStateFlow()

    // Tentativa simples de hashing (apenas para fins de demonstração local)
    // Em um aplicativo real, NUNCA use este método! Use bibliotecas robustas (ex: BCrypt).
    private fun hashPassword(password: String): String {
        return password.reversed() + password.length
    }

    // --- FUNÇÕES DE AUTENTICAÇÃO ---

    /**
     * Tenta registrar um novo usuário no banco de dados.
     * @return O ID do usuário inserido, ou null se o email já estiver em uso.
     */
    suspend fun register(nome: String, email: String, password: String): Long? {
        if (usuarioDao.countByEmail(email) > 0) {
            // Email já cadastrado
            throw IllegalStateException("Este e-mail já está cadastrado.")
        }

        // Simula o hash da senha
        val senhaHashed = hashPassword(password)

        val novoUsuario = Usuario(
            nome = nome,
            email = email,
            senhaHash = senhaHashed
        )

        // Insere o usuário e retorna o ID (chave primária)
        val idInserido = usuarioDao.inserir(novoUsuario)

        // Loga o usuário automaticamente
        val usuarioLogado = novoUsuario.copy(id = idInserido)
        setSession(idInserido, usuarioLogado)

        return idInserido
    }

    /**
     * Tenta logar um usuário.
     * @return O ID do usuário logado, ou null em caso de falha.
     */
    suspend fun login(email: String, password: String): Long? {
        val usuario = usuarioDao.getUsuarioByEmail(email)

        if (usuario == null) {
            throw IllegalArgumentException("Usuário não encontrado.")
        }

        val senhaHashed = hashPassword(password)

        if (usuario.senhaHash == senhaHashed) {
            // Senha correta: Inicia a sessão
            setSession(usuario.id, usuario)
            return usuario.id
        } else {
            throw IllegalArgumentException("Credenciais inválidas.")
        }
    }

    /**
     * Encerra a sessão do usuário.
     */
    fun logout() {
        setSession(null, null)
    }

    /**
     * Função auxiliar para definir o estado da sessão.
     */
    private fun setSession(id: Long?, usuario: Usuario?) {
        _currentUserId.value = id
        _currentUser.value = usuario
    }
}