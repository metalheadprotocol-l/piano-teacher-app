package com.pianoteacher.app.model

/** One note of a lesson song, with an optional lyric/solfege syllable. */
data class SongNote(
    val midi: Int,
    val lyric: String = ""
)

data class Song(
    val id: String,
    val title: String,
    val difficulty: Difficulty,
    val notes: List<SongNote>
) {
    enum class Difficulty(val label: String) {
        BEGINNER("Beginner"),
        EASY("Easy"),
        MEDIUM("Medium")
    }
}

object Songs {

    // MIDI reference: C4=60 D4=62 E4=64 F4=65 G4=67 A4=69 B4=71 C5=72 D5=74

    val TWINKLE = Song(
        id = "twinkle",
        title = "Twinkle Twinkle Little Star",
        difficulty = Song.Difficulty.BEGINNER,
        notes = listOf(
            SongNote(60, "Twin"), SongNote(60, "kle"), SongNote(67, "twin"), SongNote(67, "kle"),
            SongNote(69, "lit"), SongNote(69, "tle"), SongNote(67, "star"),
            SongNote(65, "How"), SongNote(65, "I"), SongNote(64, "won"), SongNote(64, "der"),
            SongNote(62, "what"), SongNote(62, "you"), SongNote(60, "are"),
            SongNote(67, "Up"), SongNote(67, "a"), SongNote(65, "bove"), SongNote(65, "the"),
            SongNote(64, "world"), SongNote(64, "so"), SongNote(62, "high"),
            SongNote(67, "Like"), SongNote(67, "a"), SongNote(65, "dia"), SongNote(65, "mond"),
            SongNote(64, "in"), SongNote(64, "the"), SongNote(62, "sky"),
            SongNote(60, "Twin"), SongNote(60, "kle"), SongNote(67, "twin"), SongNote(67, "kle"),
            SongNote(69, "lit"), SongNote(69, "tle"), SongNote(67, "star"),
            SongNote(65, "How"), SongNote(65, "I"), SongNote(64, "won"), SongNote(64, "der"),
            SongNote(62, "what"), SongNote(62, "you"), SongNote(60, "are")
        )
    )

    val MARY = Song(
        id = "mary",
        title = "Mary Had a Little Lamb",
        difficulty = Song.Difficulty.BEGINNER,
        notes = listOf(
            SongNote(64, "Ma"), SongNote(62, "ry"), SongNote(60, "had"), SongNote(62, "a"),
            SongNote(64, "lit"), SongNote(64, "tle"), SongNote(64, "lamb"),
            SongNote(62, "lit"), SongNote(62, "tle"), SongNote(62, "lamb"),
            SongNote(64, "lit"), SongNote(67, "tle"), SongNote(67, "lamb"),
            SongNote(64, "Ma"), SongNote(62, "ry"), SongNote(60, "had"), SongNote(62, "a"),
            SongNote(64, "lit"), SongNote(64, "tle"), SongNote(64, "lamb"), SongNote(64, "its"),
            SongNote(62, "fleece"), SongNote(62, "was"), SongNote(64, "white"), SongNote(62, "as"),
            SongNote(60, "snow")
        )
    )

    val ODE_TO_JOY = Song(
        id = "ode",
        title = "Ode to Joy",
        difficulty = Song.Difficulty.EASY,
        notes = listOf(
            SongNote(64), SongNote(64), SongNote(65), SongNote(67),
            SongNote(67), SongNote(65), SongNote(64), SongNote(62),
            SongNote(60), SongNote(60), SongNote(62), SongNote(64),
            SongNote(64), SongNote(62), SongNote(62),
            SongNote(64), SongNote(64), SongNote(65), SongNote(67),
            SongNote(67), SongNote(65), SongNote(64), SongNote(62),
            SongNote(60), SongNote(60), SongNote(62), SongNote(64),
            SongNote(62), SongNote(60), SongNote(60)
        )
    )

    val HAPPY_BIRTHDAY = Song(
        id = "birthday",
        title = "Happy Birthday",
        difficulty = Song.Difficulty.EASY,
        notes = listOf(
            SongNote(60, "Hap"), SongNote(60, "py"), SongNote(62, "birth"), SongNote(60, "day"),
            SongNote(65, "to"), SongNote(64, "you"),
            SongNote(60, "Hap"), SongNote(60, "py"), SongNote(62, "birth"), SongNote(60, "day"),
            SongNote(67, "to"), SongNote(65, "you"),
            SongNote(60, "Hap"), SongNote(60, "py"), SongNote(72, "birth"), SongNote(69, "day"),
            SongNote(65, "dear"), SongNote(64, "friend"), SongNote(62, "..."),
            SongNote(70, "Hap"), SongNote(70, "py"), SongNote(69, "birth"), SongNote(65, "day"),
            SongNote(67, "to"), SongNote(65, "you")
        )
    )

    val JINGLE_BELLS = Song(
        id = "jingle",
        title = "Jingle Bells",
        difficulty = Song.Difficulty.MEDIUM,
        notes = listOf(
            SongNote(64, "Jin"), SongNote(64, "gle"), SongNote(64, "bells"),
            SongNote(64, "jin"), SongNote(64, "gle"), SongNote(64, "bells"),
            SongNote(64, "jin"), SongNote(67, "gle"), SongNote(60, "all"),
            SongNote(62, "the"), SongNote(64, "way"),
            SongNote(65, "Oh"), SongNote(65, "what"), SongNote(65, "fun"), SongNote(65, "it"),
            SongNote(65, "is"), SongNote(64, "to"), SongNote(64, "ride"),
            SongNote(64, "in"), SongNote(64, "a"), SongNote(64, "one"), SongNote(62, "horse"),
            SongNote(62, "o"), SongNote(64, "pen"), SongNote(62, "sleigh"), SongNote(67, "hey!")
        )
    )

    val ALL: List<Song> = listOf(TWINKLE, MARY, ODE_TO_JOY, HAPPY_BIRTHDAY, JINGLE_BELLS)
}
