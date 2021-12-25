import java.lang.IllegalArgumentException

interface Transport {
    fun getCells(): Array<Cell>
    fun getInputs(): Array<Edge>
    fun getOutputs(): Array<Edge>
}

class Belt( val cell: Cell, val inputDir: Direction, val outputDir: Direction ): Transport {

    val input = Edge( cell.move( -inputDir ), inputDir )
    val output = Edge( cell, outputDir )

    init {
        if ( -inputDir == outputDir ) {
            throw IllegalArgumentException( "Impossible belt is being created from $inputDir to $outputDir." )
        }
    }

    override fun getCells(): Array<Cell> = arrayOf( cell )
    override fun getInputs(): Array<Edge> = arrayOf( input )
    override fun getOutputs(): Array<Edge> = arrayOf( output )

    override fun equals( other: Any? ): Boolean {
        if ( other == null || other !is Belt ) return false
        return other.cell == cell && other.inputDir == inputDir && other.outputDir == outputDir
    }
    override fun hashCode(): Int = cell.hashCode() * 16 + inputDir.hashCode() * 4 + outputDir.hashCode()
    override fun toString(): String = "Belt$cell$inputDir-$outputDir"
}


class Underground( val startingCell: Cell, val dir: Direction, private val gap: Int ): Transport {
    companion object {
        var maxGap: Int = 8
            set( mg ) {
                if ( mg < 0 ) throw IllegalArgumentException( "Trying to set negative maximum underground belt gap." )
                field = mg
            }
    }

    val endingCell = startingCell.move( dir, gap + 1 )
    val input = Edge( startingCell.move( -dir ), dir )
    val output = Edge( endingCell, dir )

    init {
        if ( gap < 0 || gap > maxGap ) throw IllegalArgumentException( "Creating underground belt with gap=$gap which is larger than MAX_GAP=$maxGap" )
    }

    override fun getCells(): Array<Cell> = arrayOf( startingCell, endingCell )
    override fun getInputs(): Array<Edge> = arrayOf( input )
    override fun getOutputs(): Array<Edge> = arrayOf( output )

    override fun equals( other: Any? ): Boolean {
        if ( other == null || other !is Underground ) return false
        return other.startingCell == startingCell && other.dir == dir && other.gap == gap
    }
    override fun hashCode(): Int = ( startingCell.hashCode() * 31 + gap.hashCode() ) * 4 + dir.hashCode()
    override fun toString(): String = "Underground$startingCell$dir$gap"
}

class Splitter( val leftCell: Cell, val dir: Direction ): Transport {

    val rightCell = leftCell.move( dir.turnClockwise() )

    companion object {
        fun fromRightCell( rCell: Cell, d: Direction ) = Splitter( rCell.move( d.turnCounterclockwise() ), d )
    }

    override fun getCells(): Array<Cell> = arrayOf( leftCell, rightCell )
    override fun getInputs(): Array<Edge> = arrayOf( Edge( leftCell.move( -dir ), dir ), Edge( rightCell.move( -dir ), dir ) )
    override fun getOutputs(): Array<Edge> = arrayOf( Edge( leftCell, dir ), Edge( rightCell, dir ) )

    override fun equals(other: Any?): Boolean {
        if ( other == null || other !is Splitter ) return false
        return other.leftCell == leftCell && other.dir == dir
    }
    override fun hashCode(): Int = leftCell.hashCode() * 4 + dir.hashCode()
    override fun toString(): String = "Splitter$leftCell$dir"
}

fun PersistentList<Transport>.getTransportAt( cell: Cell ): Transport? {
    var current: PersistentList<Transport>? = this
    while ( current != null && cell !in current.element.getCells() ) {
        current = current.next
    }
    return current?.element
}

fun areSame( first: PersistentList<Transport>?, second: PersistentList<Transport>? ): Boolean {
    if ( first == null || second == null ) return first === second
    val firstElements = HashSet<Transport>()
    run {
        var cur1: PersistentList<Transport>? = first
        while ( cur1 != null ) {
            firstElements.add( cur1.element )
            cur1 = cur1.next
        }
    }

    var cur2: PersistentList<Transport>? = second
    while ( cur2 != null ) {
        if ( cur2.element !in firstElements ) return false
        firstElements.remove( cur2.element )
        cur2 = cur2.next
    }
    return firstElements.isEmpty()
}
