import java.lang.Integer.max
import java.lang.Integer.min
import kotlin.collections.HashSet

private const val M = 2

fun main() {
    val inputs = HashSet<Edge>( M )
    for ( i in 0 until M ) inputs.add( Edge( Cell( -1, i ), Direction.RIGHT ) )
    val start = BalancerPart( inputs, belts = null )
    var expandedQueue = LayeredQueue<BalancerPart>()
    val visited = HashSet<BalancerPart>()
    expandedQueue.add( start )
    visited.add( start )
    var step = 0
    while ( true ) {
        val rectangle = Rectangle( /*-1 - step / 4*/ 0, 3 + step, -1 - step / 4, M + step / 4 )

        val queue = LayeredQueue<BalancerPart>()
        run {
            val tmpQueue = expandedQueue
            expandedQueue = LayeredQueue()
            for ( bp in tmpQueue ) {
                if ( rectangle.inside( bp.belts ) ) {
                    queue.add( bp )
                } else {
                    expandedQueue.add( bp )
                }
            }
        }

        while ( queue.isNotEmpty() ) {
            val bp = queue.removeFirst()
            println( "-----------" )
            println( bp.belts?.toStringExt() )
            println( "-----------" )
            println()
//            readln()
            val nexts = bp.advance()
            for ( next in nexts ) {
                if ( next in visited ) continue
                if ( next.belts == null || rectangle.inside( next.belts.element ) ) {
                    queue.add( next )
                } else {
                    expandedQueue.add( next )
                }
                visited.add( bp )
            }
        }

        step ++
    }
}

class Rectangle( val minX: Int, val maxX: Int, val minY: Int, val maxY: Int ) {
    fun inside( transport: Transport ): Boolean {
        for ( cell in transport.getCells() ) {
            if ( cell.x < minX || cell.x > maxX || cell.y < minY || cell.y > maxY ) return false
        }
        return true
    }

    fun inside( transports: PersistentList<Transport>? ): Boolean {
        var cur = transports
        while ( cur != null ) {
            if ( !inside( cur.element ) ) return false
            cur = cur.next
        }
        return true
    }
}

fun PersistentList<Transport>.toStringExt(): String {
    val belts = HashSet<Transport>()
    run {
        var cur: PersistentList<Transport>? = this
        while ( cur != null ) {
            belts.add( cur.element )
            cur = cur.next
        }
    }
    val cells = belts.flatMap { it.getCells().asList() }
    val area = Rectangle( cells.minOf { it.x }, cells.maxOf { it.x }, min( 0, cells.minOf { it.y } ), max( M - 1, cells.maxOf { it.y } ) )
    val result = Array( area.maxY - area.minY + 1 ) { CharArray( area.maxX - area.minX + 1 ) { '.' } }
    for ( belt in belts ) {
        when ( belt ) {
            is Belt -> {
                val ch = when ( belt.inputDir ) {
                    Direction.LEFT -> {
                        when ( belt.outputDir ) {
                            Direction.LEFT -> '←'
                            Direction.UP -> '└'
                            Direction.RIGHT -> 'X'
                            Direction.DOWN -> '┍'
                        }
                    }
                    Direction.UP -> {
                        when ( belt.outputDir ) {
                            Direction.LEFT -> '┓'
                            Direction.UP -> '↑'
                            Direction.RIGHT -> '┍'
                            Direction.DOWN -> 'X'
                        }
                    }
                    Direction.RIGHT -> {
                        when ( belt.outputDir ) {
                            Direction.LEFT -> 'X'
                            Direction.UP -> '┙'
                            Direction.RIGHT -> '→'
                            Direction.DOWN -> '┓'
                        }
                    }
                    Direction.DOWN -> {
                        when ( belt.outputDir ) {
                            Direction.LEFT -> '┙'
                            Direction.UP -> 'X'
                            Direction.RIGHT -> '└'
                            Direction.DOWN -> '↓'
                        }
                    }
                }
                result[belt.cell.y - area.minY][belt.cell.x - area.minX] = ch
            }
            is Underground -> {
                val ( ch1, ch2 ) = when ( belt.dir ) {
                    Direction.LEFT -> Pair( '⭰', '↤' )
                    Direction.UP -> Pair( '⭱', '↥' )
                    Direction.RIGHT -> Pair( '⭲', '↦' )
                    Direction.DOWN -> Pair( '⭳', '↧' )
                }
                result[belt.startingCell.y - area.minY][belt.startingCell.x - area.minX] = ch1
                result[belt.endingCell.y - area.minY][belt.endingCell.x - area.minX] = ch2
            }
            is Splitter -> {
                val ( ch1, ch2 ) = when ( belt.dir ) {
                    Direction.LEFT -> Pair( '↽', '↼' )
                    Direction.UP -> Pair( '↿', '↾' )
                    Direction.RIGHT -> Pair( '⇀', '⇁' )
                    Direction.DOWN -> Pair( '⇂', '⇃' )
                }
                result[belt.leftCell.y - area.minY][belt.leftCell.x - area.minX] = ch1
                result[belt.rightCell.y - area.minY][belt.rightCell.x - area.minX] = ch2
            }
        }
    }
    return result.reversed().joinToString( "\n" ) { String( it ) }
}