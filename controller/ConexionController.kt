package com.example.Almasoft2.controller

import com.example.Almasoft2.model.Usuario
import com.example.Almasoft2.model.UsuarioResponse
import com.example.Almasoft2.model.UsuarioUpdate
import com.example.Almasoft2.service.ConexionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


@RestController
class ConexionController {

    @Autowired
    lateinit var conexionService: ConexionService

    @GetMapping ("/usuarios")
    fun obtenerUsuarios(): List<UsuarioResponse> {
        return  conexionService.obtenerUsuario()
    }

   // @GetMapping("/detalles")
    //fun obtenerDetalles(): List<String> {
    //    return conexionService.obtenerDetalles()
    //}

    @PostMapping("/crearUsuarios")
    fun crearUsuario(@RequestBody usuario: Usuario): String {

        return conexionService.insertarUsuario(
            usuario.usuarioPrimerNombre,
            usuario.usuarioSegundoNombre,
            usuario.usuarioPrimerApellido,
            usuario.usuarioSegundoApellido,
            usuario.usuarioDocumento,
            usuario.usuarioCorreo,
            usuario.usuarioDireccion,
            usuario.usuarioTelefono,
            usuario.usuarioCredencial,
            usuario.rolId
        )
    }

    @GetMapping("/usuario/{id}")
    fun obtenerUsuarioById(@PathVariable id: Long): ResponseEntity<Any> {

        val usuario = conexionService.obtenerUsuarioById(id)

        return if (usuario != null) {
            ResponseEntity.ok(usuario)
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Usuario no encontrado")
        }
    }

    @PutMapping("/usuarioUpdate/{id}")
    fun actualizarUsuario(
        @PathVariable id: Long,
        @RequestBody usuario: UsuarioUpdate
    ): String {
        return conexionService.actualizarUsuario(id, usuario)
    }

    @DeleteMapping("/usuarioDelete/{id}")
    fun eliminarUsuario(@PathVariable id: Long): ResponseEntity<String> {

        conexionService.eliminarUsuario(id)

        return ResponseEntity.ok("Usuario eliminado correctamente")
    }

}