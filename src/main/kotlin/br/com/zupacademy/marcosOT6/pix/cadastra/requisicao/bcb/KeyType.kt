package br.com.zupacademy.marcosOT6.pix.cadastra.requisicao.bcb

import br.com.zupacademy.marcosOT6.pix.cadastra.TipoDeChave

enum class KeyType {
    CPF,
    CNPJ,
    PHONE,
    EMAIL,
    RANDOM;

    companion object {
        fun convert(tipoDeChave: TipoDeChave): KeyType {
            val keyType = when (tipoDeChave) {
                TipoDeChave.EMAIL -> EMAIL
                TipoDeChave.CHAVE_ALEATORIA -> RANDOM
                TipoDeChave.TELEFONE -> PHONE
                else -> CPF
            }
            return keyType
        }
    }

}