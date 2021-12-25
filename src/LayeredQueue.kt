import java.util.*
import kotlin.NoSuchElementException
import kotlin.collections.ArrayDeque

class LayeredQueue<T : Measurable> {
    private val layer2queue = TreeMap<Int, ArrayDeque<T>>()

    fun isEmpty(): Boolean {
        while ( layer2queue.isNotEmpty() ) {
            val first = layer2queue.firstEntry()
            if ( first.value.isEmpty() ) {
                layer2queue.remove( first.key )
            } else {
                return false
            }
        }
        return true
    }

    fun isNotEmpty(): Boolean = !isEmpty()

    fun removeFirst(): T {
        if ( isEmpty() ) throw NoSuchElementException( "LayeredQueue is empty" )
        val firstEntry = layer2queue.firstEntry()
        val first = firstEntry.value.removeFirst()
        isEmpty()
        return first
    }

    fun add( element: T ) {
        layer2queue.getOrPut( element.getSize() ) { ArrayDeque() }.add( element )
    }

    operator fun iterator(): Iterator<T> = object: Iterator<T> {

        var currentKey: Int = layer2queue.firstKey() - 1
        var itr: Iterator<T>? = null

        override fun hasNext(): Boolean {
            if ( isEmpty() ) return false
            if ( itr != null && itr!!.hasNext() ) return true
            while ( currentKey < layer2queue.lastKey() ) {
                currentKey = layer2queue.higherKey( currentKey )
                itr = layer2queue[currentKey]!!.iterator()
                if ( itr!!.hasNext() ) return true
                layer2queue.remove( currentKey )
            }
            return false
        }

        override fun next(): T = itr!!.next()

    }
}