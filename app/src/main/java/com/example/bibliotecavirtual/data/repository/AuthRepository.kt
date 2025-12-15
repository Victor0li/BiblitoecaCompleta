package com.example.bibliotecavirtual.data.repository

import com.example.bibliotecavirtual.data.Usuario
import com.example.bibliotecavirtual.data.db.UsuarioDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository(private val usuarioDao: UsuarioDao) {

    private val _currentUserId = MutableStateFlow<Long?>(null)
    val currentUserId: StateFlow<Long?> = _currentUserId.asStateFlow()

    private val _currentUser = MutableStateFlow<Usuario?>(null)
    val currentUser: StateFlow<Usuario?> = _currentUser.asStateFlow()

    private fun hashPassword(password: String): String {
        return password.reversed() + password.length
    }

    suspend fun register(nome: String, email: String, password: String): Long? {
        if (usuarioDao.countByEmail(email) > 0) {
            throw IllegalStateException("Este e-mail já está cadastrado.")
        }

        val senhaHashed = hashPassword(password)

        val novoUsuario = Usuario(
            nome = nome,
            email = email,
            senhaHash = senhaHashed
        )

        val idInserido = usuarioDao.inserir(novoUsuario)

        val usuarioLogado = novoUsuario.copy(id = idInserido)
        setSession(idInserido, usuarioLogado)

        return idInserido
    }

    suspend fun login(email: String, password: String): Long? {
        val usuario = usuarioDao.getUsuarioByEmail(email)
            ?: throw IllegalArgumentException("Usuário não encontrado.")

        val senhaHashed = hashPassword(password)

        if (usuario.senhaHash != senhaHashed) {
            throw IllegalArgumentException("Credenciais inválidas.")
        }

        setSession(usuario.id, usuario)
        return usuario.id
    }

    fun logout() {
        setSession(null, null)
    }

    private fun setSession(id: Long?, usuario: Usuario?) {
        _currentUserId.value = id
        _currentUser.value = usuario
    }
}
