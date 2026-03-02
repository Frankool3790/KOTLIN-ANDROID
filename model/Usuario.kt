package com.example.Almasoft2.model

    data class Usuario(

        val usuarioPrimerNombre: String,
        val usuarioSegundoNombre: String,
        val usuarioPrimerApellido: String,
        val usuarioSegundoApellido: String,
        val usuarioDocumento: String,
        val usuarioCorreo: String,
        val usuarioDireccion: String,
        val usuarioTelefono: String,
        val usuarioCredencial: String,
        val rolId: Long
    )

    data class UsuarioResponse(

        val usuarioId: Long,
        val usuarioPrimerNombre: String,
        val usuarioSegundoNombre: String?,
        val usuarioPrimerApellido: String,
        val usuarioSegundoApellido: String?,
        val usuarioDocumento: String,
        val usuarioCorreo: String,
        val usuarioDireccion: String,
        val usuarioTelefono: String?,
        val rolNombre: String
    )

data class UsuarioUpdate(

    val usuarioPrimerNombre: String,
    val usuarioSegundoNombre: String?,
    val usuarioPrimerApellido: String,
    val usuarioSegundoApellido: String?,
    val usuarioCorreo: String,
    val usuarioDireccion: String,
    val usuarioTelefono: String?,
    val usuarioCredencial: String,
    val rolId: Long
)


