package com.example.Almasoft2.service

import com.example.Almasoft2.model.Usuario
import com.example.Almasoft2.model.UsuarioResponse
import com.example.Almasoft2.model.UsuarioUpdate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.sql.PreparedStatement


@Service
class ConexionService {

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    fun obtenerUsuario(): List<UsuarioResponse> {

        val sql = """
         SELECT 
            us.usuario_id,
            r.rol_nombre,
            us.usuario_documento,
            us.usuario_primer_nombre,
            us.usuario_segundo_nombre,
            us.usuario_primer_apellido,
            us.usuario_segundo_apellido,
            us.usuario_correo,
            us.usuario_direccion,
            t.telefono
        FROM usuario AS us
        INNER JOIN rol_usuario AS ru 
            ON us.usuario_id = ru.usuario_id
        INNER JOIN rol AS r 
            ON ru.rol_id = r.rol_id
        LEFT JOIN telefono AS t 
            ON us.usuario_id = t.usuario_id
        WHERE ru.estado_cred = true
    """.trimIndent()

        return jdbcTemplate.query(sql) { rs, _ ->
            UsuarioResponse(
                usuarioId = rs.getLong("usuario_id"),
                usuarioPrimerNombre = rs.getString("usuario_primer_nombre"),
                usuarioSegundoNombre = rs.getString("usuario_segundo_nombre"),
                usuarioPrimerApellido = rs.getString("usuario_primer_apellido"),
                usuarioSegundoApellido = rs.getString("usuario_segundo_apellido"),
                usuarioDocumento = rs.getString("usuario_documento"),
                usuarioCorreo = rs.getString("usuario_correo"),
                usuarioDireccion = rs.getString("usuario_direccion"),
                usuarioTelefono = rs.getString("telefono"),
                rolNombre = rs.getString("rol_nombre")
            )
        }
    }
    @Transactional
    fun insertarUsuario(
        usuarioPrimerNombre: String,
        usuarioSegundoNombre: String,
        usuarioPrimerApellido: String,
        usuarioSegundoApellido: String,
        usuarioDocumento: String,
        usuarioCorreo: String,
        usuarioDireccion: String,
        usuarioTelefono: String,
        usuarioCredencial: String,
        rolId: Long
    ): String {

        val sqlUsuario = """
        INSERT INTO usuario 
        (usuario_primer_nombre, usuario_segundo_nombre,
         usuario_primer_apellido, usuario_segundo_apellido,
         usuario_documento, usuario_correo,
         usuario_direccion, usuario_credencial)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    """.trimIndent()

        val keyHolder = GeneratedKeyHolder()

        jdbcTemplate.update({ connection ->
            val ps = connection.prepareStatement(sqlUsuario, arrayOf("usuario_id"))

            ps.setString(1, usuarioPrimerNombre)
            ps.setString(2, usuarioSegundoNombre)
            ps.setString(3, usuarioPrimerApellido)
            ps.setString(4, usuarioSegundoApellido)
            ps.setString(5, usuarioDocumento)
            ps.setString(6, usuarioCorreo)
            ps.setString(7, usuarioDireccion)
            ps.setString(8, usuarioCredencial)
            ps
        }, keyHolder)

        val usuarioId = keyHolder.key?.toLong()
            ?: return "Error generando ID"

        // Insertar teléfono
        val sqlTelefono = """
        INSERT INTO telefono (telefono, usuario_id)
        VALUES (?, ?)
    """.trimIndent()

        jdbcTemplate.update(sqlTelefono, usuarioTelefono, usuarioId)

        // Insertar rol
        val sqlRolUsuario = """
        INSERT INTO rol_usuario (usuario_id, rol_id)
        VALUES (?, ?)
    """.trimIndent()

        jdbcTemplate.update(sqlRolUsuario, usuarioId, rolId)

        return "Usuario creado con rol correctamente"
    }


    fun obtenerUsuarioById(id: Long): UsuarioResponse? {

        val sql = """
        SELECT 
            us.usuario_id,
            r.rol_nombre,
            us.usuario_documento,
            us.usuario_primer_nombre,
            us.usuario_segundo_nombre,
            us.usuario_primer_apellido,
            us.usuario_segundo_apellido,
            us.usuario_correo,
            us.usuario_direccion,
            t.telefono
        FROM usuario AS us
        INNER JOIN rol_usuario AS ru 
            ON us.usuario_id = ru.usuario_id
        INNER JOIN rol AS r 
            ON ru.rol_id = r.rol_id
        LEFT JOIN telefono AS t 
            ON us.usuario_id = t.usuario_id
        WHERE us.usuario_id = ?
    """.trimIndent()

        val lista = jdbcTemplate.query(sql, arrayOf(id)) { rs, _ ->
            UsuarioResponse(
                usuarioId = rs.getLong("usuario_id"),
                usuarioPrimerNombre = rs.getString("usuario_primer_nombre"),
                usuarioSegundoNombre = rs.getString("usuario_segundo_nombre"),
                usuarioPrimerApellido = rs.getString("usuario_primer_apellido"),
                usuarioSegundoApellido = rs.getString("usuario_segundo_apellido"),
                usuarioDocumento = rs.getString("usuario_documento"),
                usuarioCorreo = rs.getString("usuario_correo"),
                usuarioDireccion = rs.getString("usuario_direccion"),
                usuarioTelefono = rs.getString("telefono"),
                rolNombre = rs.getString("rol_nombre")
            )
        }

        return lista.firstOrNull()
    }

    fun actualizarUsuario(id: Long, usuario: UsuarioUpdate): String {

        val sqlUsuario = """
        UPDATE usuario SET
            usuario_primer_nombre = ?,
            usuario_segundo_nombre = ?,
            usuario_primer_apellido = ?,
            usuario_segundo_apellido = ?,
            usuario_correo = ?,
            usuario_direccion = ?,
            usuario_credencial = ?
        WHERE usuario_id = ?
    """.trimIndent()

        jdbcTemplate.update(
            sqlUsuario,
            usuario.usuarioPrimerNombre,
            usuario.usuarioSegundoNombre,
            usuario.usuarioPrimerApellido,
            usuario.usuarioSegundoApellido,
            usuario.usuarioCorreo,
            usuario.usuarioDireccion,
            usuario.usuarioCredencial,
            id
        )

        //  Actualizar rol en tabla intermedia
        val sqlRol = """
        UPDATE rol_usuario
        SET rol_id = ?
        WHERE usuario_id = ?
    """.trimIndent()

        jdbcTemplate.update(sqlRol, usuario.rolId, id)

        //  Actualizar teléfono
        val sqlTelefono = """
        UPDATE telefono
        SET telefono = ?
        WHERE usuario_id = ?
    """.trimIndent()

        jdbcTemplate.update(sqlTelefono, usuario.usuarioTelefono, id)

        return "Usuario actualizado correctamente"
    }

    fun eliminarUsuario(id: Long): String {

        jdbcTemplate.update("DELETE FROM telefono WHERE usuario_id = ?", id)

        jdbcTemplate.update("DELETE FROM rol_usuario WHERE usuario_id = ?", id)

        jdbcTemplate.update("DELETE FROM usuario WHERE usuario_id = ?", id)

        return "Usuario eliminado correctamente"
    }

   }