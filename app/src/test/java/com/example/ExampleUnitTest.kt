package com.example

import com.example.data.PuzzleCategory
import com.example.data.PuzzleDifficulty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testPuzzleDifficulties() {
        assertEquals(16, PuzzleDifficulty.EASY.totalPieces)
        assertEquals(36, PuzzleDifficulty.MEDIUM.totalPieces)
        assertEquals(48, PuzzleDifficulty.HARD.totalPieces)
        assertEquals(64, PuzzleDifficulty.EXPERT.totalPieces)
    }

    @Test
    fun testCategoryCycle() {
        val cat1 = PuzzleCategory.forLevel(1)
        val cat10 = PuzzleCategory.forLevel(10)
        assertEquals(PuzzleCategory.ANIMALS, cat1)
        assertEquals(PuzzleCategory.ANIMALS, cat10)
        assertEquals(9, PuzzleCategory.entries.size)
        assertTrue(PuzzleCategory.entries.contains(PuzzleCategory.FLOWERS))
        assertTrue(PuzzleCategory.entries.contains(PuzzleCategory.ANIMALS))
        assertTrue(PuzzleCategory.entries.contains(PuzzleCategory.BIRDS))
    }
}
