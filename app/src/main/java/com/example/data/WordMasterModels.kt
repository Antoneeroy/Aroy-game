package com.example.data

data class WordDefinition(
    val word: String,
    val partOfSpeech: String,
    val definition: String,
    val exampleSentence: String
)

data class WordCrosswordCell(
    val row: Int,
    val col: Int,
    val char: Char,
    val isRevealed: Boolean = false,
    val wordId: String
)

data class WordItem(
    val id: String,
    val text: String,
    val isFound: Boolean = false,
    val row: Int,
    val col: Int,
    val isHorizontal: Boolean
)

data class WordLevelData(
    val levelNumber: Int,
    val categoryName: String,
    val letters: List<Char>,
    val targetWords: List<String>,
    val bonusWords: List<String>,
    val definitions: Map<String, WordDefinition>,
    val clueImageUrl: String
)
