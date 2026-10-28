package com.example.data

object WordMasterLevelRepository {

    private val baseLevelPacks = listOf(
        WordLevelData(
            levelNumber = 1,
            categoryName = "Nature & Forest",
            letters = listOf('N', 'A', 'T', 'U', 'R', 'E'),
            targetWords = listOf("NATURE", "RATE", "TRUE", "TEAR", "TUNE", "RUN", "NUT", "NET"),
            bonusWords = listOf("RANT", "NEAR", "ANTE", "TUNA", "RUE", "URN"),
            definitions = mapOf(
                "NATURE" to WordDefinition("NATURE", "noun", "The physical world and its phenomena, including plants and animals.", "We enjoyed the quiet beauty of nature."),
                "RATE" to WordDefinition("RATE", "noun", "A measure, quantity, or frequency typically one measured against another.", "The pulse rate was normal."),
                "TRUE" to WordDefinition("TRUE", "adjective", "In accordance with fact or reality; accurate.", "Her prediction came true."),
                "TEAR" to WordDefinition("TEAR", "noun", "A drop of clear salty liquid produced by glands in your eyes.", "A tear rolled down his cheek."),
                "TUNE" to WordDefinition("TUNE", "noun", "A succession of musical notes forming a melody.", "She whistled a cheerful tune."),
                "RUN" to WordDefinition("RUN", "verb", "Move at a speed faster than a walk.", "They run every morning in the park."),
                "NUT" to WordDefinition("NUT", "noun", "A fruit consisting of a hard shell protecting an edible kernel.", "Squirrels gather nuts for winter."),
                "NET" to WordDefinition("NET", "noun", "A mesh fabric used for catching things or enclosing space.", "The fish swam into the net.")
            ),
            clueImageUrl = "https://picsum.photos/seed/aroy_word_clue_1/600/400"
        ),
        WordLevelData(
            levelNumber = 2,
            categoryName = "Space & Cosmos",
            letters = listOf('P', 'L', 'A', 'N', 'E', 'T'),
            targetWords = listOf("PLANET", "PLANT", "PLATE", "PALE", "LATE", "TAPE", "LEAP", "PLAN"),
            bonusWords = listOf("PANT", "PLEAT", "PEAT", "LANE", "PANE"),
            definitions = mapOf(
                "PLANET" to WordDefinition("PLANET", "noun", "A celestial body moving in an elliptical orbit around a star.", "Earth is the third planet from the sun."),
                "PLANT" to WordDefinition("PLANT", "noun", "A living organism of the kind exemplified by trees and herbs.", "The green plant soaked in sunlight."),
                "PLATE" to WordDefinition("PLATE", "noun", "A flat, usually round dish from which food is served or eaten.", "He set a steaming plate on the table."),
                "PALE" to WordDefinition("PALE", "adjective", "Light in color or having little color.", "The moon looked pale in the morning sky."),
                "LATE" to WordDefinition("LATE", "adjective", "Doing something or taking place after the expected time.", "Don't be late for the meeting."),
                "TAPE" to WordDefinition("TAPE", "noun", "A narrow strip of material used for holding things together.", "He sealed the package with strong tape."),
                "LEAP" to WordDefinition("LEAP", "verb", "Spring or jump quickly from a surface through the air.", "Dolphins leap joyfully out of water."),
                "PLAN" to WordDefinition("PLAN", "noun", "A detailed proposal for doing or achieving something.", "They drafted a brilliant master plan.")
            ),
            clueImageUrl = "https://picsum.photos/seed/aroy_word_clue_2/600/400"
        ),
        WordLevelData(
            levelNumber = 3,
            categoryName = "Wild Animals",
            letters = listOf('F', 'O', 'R', 'E', 'S', 'T'),
            targetWords = listOf("FOREST", "STORE", "FROST", "ROSE", "SOFT", "FORT", "REST", "SORE"),
            bonusWords = listOf("FORE", "FRET", "ROTE", "SORT", "TORE"),
            definitions = mapOf(
                "FOREST" to WordDefinition("FOREST", "noun", "A large area covered chiefly with trees and undergrowth.", "Deer thrive deep inside the forest."),
                "STORE" to WordDefinition("STORE", "noun", "A retail establishment selling items to the public.", "They purchased supplies at the village store."),
                "FROST" to WordDefinition("FROST", "noun", "A deposit of small white ice crystals formed on the ground.", "Morning frost glistened on the leaves."),
                "ROSE" to WordDefinition("ROSE", "noun", "A prickly bush or shrub that typically bears fragrant flowers.", "She smelled the fragrant red rose."),
                "SOFT" to WordDefinition("SOFT", "adjective", "Easy to mold, cut, compress, or fold; not hard or firm.", "The kitten has soft silky fur."),
                "FORT" to WordDefinition("FORT", "noun", "A fortified building or strategic military position.", "Ancient stone walls surrounded the fort."),
                "REST" to WordDefinition("REST", "verb", "Cease work or movement in order to relax or recover strength.", "Take a few minutes to rest your eyes.")
            ),
            clueImageUrl = "https://picsum.photos/seed/aroy_word_clue_3/600/400"
        ),
        WordLevelData(
            levelNumber = 4,
            categoryName = "Ocean & Marine",
            letters = listOf('S', 'I', 'L', 'V', 'E', 'R'),
            targetWords = listOf("SILVER", "LIVER", "LIVE", "VEIL", "RISE", "SLIM", "FIRE"),
            bonusWords = listOf("SIRE", "LIRE", "VILE", "RILE"),
            definitions = mapOf(
                "SILVER" to WordDefinition("SILVER", "noun", "A precious shiny grayish-white metallic element.", "The ring was forged from pure silver."),
                "LIVER" to WordDefinition("LIVER", "noun", "A large vital organ in the abdomen of vertebrates.", "The liver performs vital metabolic functions."),
                "LIVE" to WordDefinition("LIVE", "verb", "Remain alive; make one's home in a particular place.", "Wild whales live in deep oceans."),
                "VEIL" to WordDefinition("VEIL", "noun", "A piece of fine material worn to protect or conceal the face.", "Mist hung like a veil over the mountains."),
                "RISE" to WordDefinition("RISE", "verb", "Move from a lower position to a higher one; ascend.", "We watched the morning sun rise.")
            ),
            clueImageUrl = "https://picsum.photos/seed/aroy_word_clue_4/600/400"
        ),
        WordLevelData(
            levelNumber = 5,
            categoryName = "Birds & Sky",
            letters = listOf('F', 'L', 'I', 'G', 'H', 'T'),
            targetWords = listOf("FLIGHT", "LIGHT", "FIGHT", "GIFT", "LIFT", "HIGH", "HIT"),
            bonusWords = listOf("HILT", "THIG", "FIG", "LIT"),
            definitions = mapOf(
                "FLIGHT" to WordDefinition("FLIGHT", "noun", "The action or process of flying through the air.", "The eagle soared on its majestic flight."),
                "LIGHT" to WordDefinition("LIGHT", "noun", "The natural agent that stimulates sight and makes things visible.", "Morning light poured through the window."),
                "FIGHT" to WordDefinition("FIGHT", "verb", "Take part in a violent struggle involving the exchange of physical blows.", "They fight bravely for a noble cause."),
                "GIFT" to WordDefinition("GIFT", "noun", "A thing given willingly to someone without payment; a present.", "She unwrapped the thoughtful birthday gift."),
                "LIFT" to WordDefinition("LIFT", "verb", "Raise to a higher position or level.", "Lift the heavy crate with your knees."),
                "HIGH" to WordDefinition("HIGH", "adjective", "Extending upwards a great distance.", "The mountain peak was exceptionally high.")
            ),
            clueImageUrl = "https://picsum.photos/seed/aroy_word_clue_5/600/400"
        ),
        WordLevelData(
            levelNumber = 6,
            categoryName = "Garden & Flowers",
            letters = listOf('G', 'A', 'R', 'D', 'E', 'N'),
            targetWords = listOf("GARDEN", "DANGER", "RANGE", "GRADE", "READ", "DEAR", "NEAR", "GEAR"),
            bonusWords = listOf("RANG", "AGED", "DARE", "EARN", "DRAG"),
            definitions = mapOf(
                "GARDEN" to WordDefinition("GARDEN", "noun", "A piece of ground adjoining a house, used for growing flowers, fruit, or vegetables.", "Fresh roses bloomed in the garden."),
                "DANGER" to WordDefinition("DANGER", "noun", "The possibility of suffering harm or injury.", "The warning sign indicated sudden danger."),
                "RANGE" to WordDefinition("RANGE", "noun", "The area of variation between upper and lower limits.", "A vast mountain range stretched out."),
                "GRADE" to WordDefinition("GRADE", "noun", "A particular level of rank, quality, or achievement.", "She earned an outstanding grade on the exam."),
                "READ" to WordDefinition("READ", "verb", "Look at and comprehend the meaning of written words.", "He loves to read adventures before bed."),
                "DEAR" to WordDefinition("DEAR", "adjective", "Regarded with deep affection; cherished.", "She wrote a letter to her dear friend.")
            ),
            clueImageUrl = "https://picsum.photos/seed/aroy_word_clue_6/600/400"
        ),
        WordLevelData(
            levelNumber = 7,
            categoryName = "Mountain Stream",
            letters = listOf('S', 'T', 'R', 'E', 'A', 'M'),
            targetWords = listOf("STREAM", "MASTER", "SMART", "STEAM", "TEAM", "STAR", "REST", "MEAT"),
            bonusWords = listOf("TAME", "MARE", "SEAM", "RATE", "MATE"),
            definitions = mapOf(
                "STREAM" to WordDefinition("STREAM", "noun", "A small, narrow river of clear running water.", "Cool mountain water flowed in the stream."),
                "MASTER" to WordDefinition("MASTER", "noun", "A skilled practitioner of a particular art or craft.", "The chess grandmaster studied the board."),
                "SMART" to WordDefinition("SMART", "adjective", "Having or showing a quick-witted intelligence.", "That was a remarkably smart strategy."),
                "STEAM" to WordDefinition("STEAM", "noun", "The vapor into which water is converted when heated.", "Steam rose steadily from the kettle."),
                "TEAM" to WordDefinition("TEAM", "noun", "A group of players forming one side in a competitive game.", "The dedicated team celebrated victory together.")
            ),
            clueImageUrl = "https://picsum.photos/seed/aroy_word_clue_7/600/400"
        ),
        WordLevelData(
            levelNumber = 8,
            categoryName = "Golden Spring",
            letters = listOf('S', 'P', 'R', 'I', 'N', 'G'),
            targetWords = listOf("SPRING", "GRIP", "RING", "SING", "SPIN", "PIG", "PIN"),
            bonusWords = listOf("RIG", "PRIG", "RIPS", "SNIP"),
            definitions = mapOf(
                "SPRING" to WordDefinition("SPRING", "noun", "The season after winter and before summer, in which vegetation begins to appear.", "Wildflowers bloom across the hills in spring."),
                "GRIP" to WordDefinition("GRIP", "verb", "Take and keep a firm hold of; grasp tightly.", "He held the handle with a secure grip."),
                "RING" to WordDefinition("RING", "noun", "A small circular band worn on a finger as an ornament.", "The gold ring sparkled in the light."),
                "SING" to WordDefinition("SING", "verb", "Make musical sounds with the voice.", "Birds sing at the break of dawn.")
            ),
            clueImageUrl = "https://picsum.photos/seed/aroy_word_clue_8/600/400"
        )
    )

    fun getLevel(levelNumber: Int): WordLevelData {
        val index = (levelNumber - 1) % baseLevelPacks.size
        val template = baseLevelPacks[index]
        return template.copy(
            levelNumber = levelNumber,
            clueImageUrl = "https://picsum.photos/seed/aroy_word_clue_${levelNumber}/600/400"
        )
    }

    val anagramBlitzWordSets: List<Pair<List<Char>, List<String>>> = listOf(
        Pair(listOf('S', 'T', 'A', 'R', 'L', 'I', 'T'), listOf("STAR", "TAIL", "TRAIL", "LAST", "LIST", "STIR", "RAIL", "SAIL", "SALT", "STARLIT")),
        Pair(listOf('C', 'A', 'S', 'T', 'L', 'E', 'S'), listOf("CASTLE", "SCALE", "STALE", "TALES", "SEALS", "LEAST", "STEAL", "LACES", "SLATE")),
        Pair(listOf('B', 'R', 'I', 'G', 'H', 'T', 'S'), listOf("BRIGHT", "RIGHT", "SIGHT", "SHIRT", "GIRTH", "BIRTH", "GRIT", "BITS")),
        Pair(listOf('D', 'A', 'N', 'C', 'I', 'N', 'G'), listOf("DANCE", "ACID", "GAIN", "RING", "INCA", "CANDID", "AND", "CAN", "DIG"))
    )
}
