package nodm

interface TypeAdapter<out T> {
    fun unmarshal(notesValue: List<Any?>, mapper: Mapper): T
    fun marshal(value: @UnsafeVariance T): List<Any?>
}