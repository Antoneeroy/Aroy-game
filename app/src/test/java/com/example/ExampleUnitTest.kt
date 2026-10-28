package com.example

import com.example.data.PuzzleCategory
import com.example.data.PuzzleDifficulty
import com.example.data.WordMasterLevelRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
        val cat8 = PuzzleCategory.forLevel(8)
        assertEquals(PuzzleCategory.ANIMALS, cat1)
        assertEquals(PuzzleCategory.ANIMALS, cat8)
    }

    @Test
    fun testWordLevelRepository() {
        val lvl1 = WordMasterLevelRepository.getLevel(1)
        assertEquals(1, lvl1.levelNumber)
        assertTrue(lvl1.targetWords.contains("NATURE"))
        assertNotNull(lvl1.definitions["NATURE"])
        assertEquals("noun", lvl1.definitions["NATURE"]?.partOfSpeech)
    }
}
