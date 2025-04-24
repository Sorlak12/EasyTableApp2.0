package com.example.easytableapp.modelo

data class Usuario (
    val IDUsuario: Int,
    val NombreUsuario: String,
    val MailUsuario: String,
    val PWDHash: String,
    val Habilitado: Int,
    val IDRol: Int
)