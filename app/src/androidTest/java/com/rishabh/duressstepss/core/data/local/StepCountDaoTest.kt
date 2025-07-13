package com.rishabh.duressstepss.core.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.rishabh.duressstepss.stepcounter.data.local.entity.StepCountEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class StepCountDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var dao: StepCountDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.stepCountDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndGetLatestStepCount_returnsLatest() = runTest {
        // Given two step count entities
        val entity1 = StepCountEntity(timestamp = 1000L, count = 100)
        val entity2 = StepCountEntity(timestamp = 2000L, count = 200)

        // When the first entity is inserted
        dao.insert(entity1)

        // Then the latest step count flow should emit it
        dao.getLatestStepCount().test {
            assertThat(awaitItem()?.count).isEqualTo(100)

            // When the second, newer entity is inserted
            dao.insert(entity2)

            // Then the flow should emit the new latest count
            assertThat(awaitItem()?.count).isEqualTo(200)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
