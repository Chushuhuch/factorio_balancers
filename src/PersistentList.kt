interface Measurable {
    fun getSize(): Int
}

class PersistentList<T>( val element: T, val next: PersistentList<T>? ): Measurable {

    private val size: Int = 1 + ( next?.size ?: 0 )

    override fun getSize(): Int = size

    override fun hashCode(): Int {
        val hashes = ArrayList<Int>()
        var cur: PersistentList<T>? = this
        while ( cur != null ) {
            hashes.add( cur.element.hashCode() )
            cur = cur.next
        }
        hashes.sort()
        return hashes.hashCode()
    }

    override fun equals( other: Any? ): Boolean {
        throw RuntimeException( "Is not intended to be called" )
    }
}