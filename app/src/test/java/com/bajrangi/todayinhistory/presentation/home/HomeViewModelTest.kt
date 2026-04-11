package com.bajrangi.todayinhistory.presentation.home

import app.cash.turbine.test
import com.bajrangi.todayinhistory.domain.model.HistoricalEvent
import com.bajrangi.todayinhistory.domain.usecase.GetEventsUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getEventsUseCase: GetEventsUseCase

    private val sampleEvents = listOf(
        HistoricalEvent(
            year = 1912,
            title = "Titanic",
            description = "Titanic sets sail.",
            month = 4, day = 11,
        ),
        HistoricalEvent(
            year = 1970,
            title = "Apollo 13",
            description = "Apollo 13 launched.",
            month = 4, day = 11,
        ),
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getEventsUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        every { getEventsUseCase(any(), any()) } returns flow {
            // Never emit — stays in loading.
        }

        val vm = HomeViewModel(getEventsUseCase)

        assertEquals(HomeUiState.Loading, vm.uiState.value)
    }

    @Test
    fun `emits Success when use case returns events`() = runTest {
        every { getEventsUseCase(any(), any()) } returns flowOf(
            Result.success(sampleEvents)
        )

        val vm = HomeViewModel(getEventsUseCase)
        advanceUntilIdle()

        val state = vm.uiState.value
        assertTrue(state is HomeUiState.Success)
        assertEquals(2, (state as HomeUiState.Success).events.size)
        assertEquals("Titanic", state.events[0].title)
    }

    @Test
    fun `emits Error when use case fails with no cache`() = runTest {
        every { getEventsUseCase(any(), any()) } returns flowOf(
            Result.failure(RuntimeException("Network error"))
        )

        val vm = HomeViewModel(getEventsUseCase)
        advanceUntilIdle()

        val state = vm.uiState.value
        assertTrue(state is HomeUiState.Error)
        assertTrue((state as HomeUiState.Error).message.contains("Network"))
    }

    @Test
    fun `refresh keeps existing content visible`() = runTest {
        // First emission: success
        every { getEventsUseCase(any(), any()) } returns flowOf(
            Result.success(sampleEvents)
        )

        val vm = HomeViewModel(getEventsUseCase)
        advanceUntilIdle()

        // Now simulate refresh failure — should keep showing events.
        every { getEventsUseCase(any(), any()) } returns flowOf(
            Result.failure(RuntimeException("Timeout"))
        )

        vm.refresh()
        advanceUntilIdle()

        val state = vm.uiState.value
        assertTrue(state is HomeUiState.Success)
        assertEquals(2, (state as HomeUiState.Success).events.size)
        assertEquals(false, state.isRefreshing)
    }

    @Test
    fun `selectDate resets to Loading and reloads`() = runTest {
        every { getEventsUseCase(any(), any()) } returns flowOf(
            Result.success(sampleEvents)
        )

        val vm = HomeViewModel(getEventsUseCase)
        advanceUntilIdle()

        val newEvents = listOf(
            HistoricalEvent(year = 1776, title = "Independence", description = "July 4th", month = 7, day = 4)
        )
        every { getEventsUseCase(7, 4) } returns flowOf(
            Result.success(newEvents)
        )

        vm.selectDate(7, 4)
        advanceUntilIdle()

        val state = vm.uiState.value
        assertTrue(state is HomeUiState.Success)
        assertEquals(1, (state as HomeUiState.Success).events.size)
        assertEquals("Independence", state.events[0].title)
        assertEquals(7, state.month)
        assertEquals(4, state.day)
    }

    @Test
    fun `selectDate with same date is no-op`() = runTest {
        every { getEventsUseCase(any(), any()) } returns flowOf(
            Result.success(sampleEvents)
        )

        val vm = HomeViewModel(getEventsUseCase)
        advanceUntilIdle()

        val stateBefore = vm.uiState.value

        // Select same date — should not reload.
        vm.selectDate(
            (stateBefore as HomeUiState.Success).month,
            stateBefore.day,
        )

        assertEquals(stateBefore, vm.uiState.value)
    }

    @Test
    fun `getEvent returns correct event by index`() = runTest {
        every { getEventsUseCase(any(), any()) } returns flowOf(
            Result.success(sampleEvents)
        )

        val vm = HomeViewModel(getEventsUseCase)
        advanceUntilIdle()

        assertEquals("Titanic", vm.getEvent(0)?.title)
        assertEquals("Apollo 13", vm.getEvent(1)?.title)
        assertEquals(null, vm.getEvent(99))
    }

    @Test
    fun `handles empty event list`() = runTest {
        every { getEventsUseCase(any(), any()) } returns flowOf(
            Result.success(emptyList())
        )

        val vm = HomeViewModel(getEventsUseCase)
        advanceUntilIdle()

        val state = vm.uiState.value
        assertTrue(state is HomeUiState.Success)
        assertTrue((state as HomeUiState.Success).events.isEmpty())
    }

    @Test
    fun `cache then fresh emits twice`() = runTest {
        // Repository emits cached data first, then fresh data.
        every { getEventsUseCase(any(), any()) } returns flow {
            emit(Result.success(listOf(sampleEvents[0])))  // cached (1 event)
            emit(Result.success(sampleEvents))              // fresh (2 events)
        }

        val vm = HomeViewModel(getEventsUseCase)

        vm.uiState.test {
            // Skip Loading
            awaitItem()

            // First emission: 1 cached event
            val cached = awaitItem()
            assertTrue(cached is HomeUiState.Success)
            assertEquals(1, (cached as HomeUiState.Success).events.size)

            // Second emission: 2 fresh events
            val fresh = awaitItem()
            assertTrue(fresh is HomeUiState.Success)
            assertEquals(2, (fresh as HomeUiState.Success).events.size)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
