package br.com.zupacademy.marcosOT6.pix.detalhar.requisicao.bcb

data class BankAccount (
    val participant: String,
    val branch: String,
    val accountNumber: String,
    val accountType: String
)