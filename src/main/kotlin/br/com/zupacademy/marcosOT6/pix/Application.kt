package br.com.zupacademy.marcosOT6.pix

import io.micronaut.runtime.Micronaut.*
fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("br.com.zupacademy.marcosOT6.pix")
		.start()
}

