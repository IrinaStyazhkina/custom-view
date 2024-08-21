package irastyazhkina.customview.model

enum class AnimationType(val value: Int) {
    ROTATION(0),
    SEQUENTIAL(1),
    BIDIRECTIONAL(2);

    companion object {
        fun fromInt(value: Int) = AnimationType.values().first { it.value == value }
    }
}