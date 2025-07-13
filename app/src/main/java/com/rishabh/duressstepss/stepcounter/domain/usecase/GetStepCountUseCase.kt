package com.rishabh.duressstepss.stepcounter.domain.usecase

import com.rishabh.duressstepss.stepcounter.domain.repository.StepRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStepCountUseCase @Inject constructor(
    private val stepRepository: StepRepository
) {
    operator fun invoke(): Flow<Int> {
        return stepRepository.getStepCount()
    }
}
