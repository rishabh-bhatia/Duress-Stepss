package com.rishabh.duressstepss.stepcounter.domain.usecase

import com.rishabh.duressstepss.stepcounter.domain.repository.StepRepository
import javax.inject.Inject

class SaveStepCountUseCase @Inject constructor(
    private val stepRepository: StepRepository
) {
    suspend operator fun invoke(count: Int) {
        stepRepository.saveStepCount(count)
    }
}
