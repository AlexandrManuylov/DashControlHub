package org.chaynik.dch.domain.usecase

import org.chaynik.dch.domain.model.commands.Commands

interface HandleCommandUseCase {
    fun execute(command: Commands)
}