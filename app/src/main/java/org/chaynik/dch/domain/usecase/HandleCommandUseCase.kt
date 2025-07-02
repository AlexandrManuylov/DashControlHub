package org.chaynik.dch.domain.usecase

interface HandleCommandUseCase {
    fun execute(command: String)
}