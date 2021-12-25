class Edge( val cell: Cell, val dir: Direction ) {
    fun targetCell() = cell.move( dir )

    override fun equals( other: Any? ): Boolean {
        if ( other == null || other !is Edge ) return false
        return other.cell == cell && other.dir == dir
    }

    override fun hashCode(): Int {
        return cell.hashCode() * 4 + dir.hashCode()
    }

    override fun toString(): String = "$cell:$dir"
}

class Cell( val x: Int, val y: Int ) {
    fun move( dir: Direction, dist: Int = 1 ) = Cell( x + dir.dx * dist, y + dir.dy * dist )

    override fun equals( other: Any? ): Boolean {
        if ( other == null || other !is Cell ) return false
        return other.x == x && other.y == y
    }

    override fun hashCode(): Int = x * 997 + y

    override fun toString(): String = "( $x, $y )"
}

enum class Direction( val dx: Int, val dy: Int ) {
    LEFT( -1, 0 ) {
        override fun unaryMinus(): Direction = RIGHT
        override fun turnClockwise(): Direction = UP
        override fun turnCounterclockwise(): Direction = DOWN
    },
    UP( 0, 1 ) {
        override fun unaryMinus(): Direction = DOWN
        override fun turnClockwise(): Direction = RIGHT
        override fun turnCounterclockwise(): Direction = LEFT
    },
    RIGHT( 1, 0 ) {
        override fun unaryMinus(): Direction = LEFT
        override fun turnClockwise(): Direction = DOWN
        override fun turnCounterclockwise(): Direction = UP
    },
    DOWN( 0, -1 ) {
        override fun unaryMinus(): Direction = UP
        override fun turnClockwise(): Direction = LEFT
        override fun turnCounterclockwise(): Direction = RIGHT
    };

    abstract operator fun unaryMinus(): Direction
    abstract fun turnClockwise(): Direction
    abstract fun turnCounterclockwise(): Direction
}
