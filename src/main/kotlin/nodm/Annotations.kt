package nodm

@Target(AnnotationTarget.CLASS)
annotation class NotesDocument(
        val server: String = "",
        val database: String = ""
)