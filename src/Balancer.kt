class BalancerPart(
    private val inputs: HashSet<Edge>,
//    val outputs: Set<Edge>,
    // Intending to grow the first output and reappend the new output to the end of the list
    private val currentOutputs: List<Edge> = ArrayList( inputs ),
    val belts: PersistentList<Transport>?
    ) : Measurable {

    fun advance(): List<BalancerPart> {
        val results = ArrayList<BalancerPart>()
        if ( currentOutputs.isEmpty() ) return results
        results.addIfNotNull( tryAdvance( null ) )
        val output = currentOutputs.first()
        for ( dir in Direction.values() ) {
            if ( -dir == output.dir ) continue
            val belt = Belt( output.targetCell(), output.dir, dir )
            results.addIfNotNull( tryAdvance( belt ) )
        }
        for ( gap in 1 .. Underground.maxGap ) {
            val belt = Underground( output.targetCell(), output.dir, gap )
            results.addIfNotNull( tryAdvance( belt ) )
        }
        run {
            val splitter = Splitter( output.targetCell(), output.dir )
            results.addIfNotNull( tryAdvance( splitter ) )
        }
        run {
            val splitter = Splitter.fromRightCell( output.targetCell(), output.dir )
            results.addIfNotNull( tryAdvance( splitter ) )
        }
        return results
    }

    private fun tryAdvance( belt: Transport? ): BalancerPart? {
        val newOutputs = ArrayList( currentOutputs )
        newOutputs.removeAt( 0 )
        if ( belt == null ) {
            return BalancerPart( inputs, newOutputs, belts )
        }
        for ( cell in belt.getCells() ) {
            if ( belts?.getTransportAt( cell ) != null ) return null
        }
        val incomingList = newOutputs.filter { it.targetCell() in belt.getCells() }
        for ( incoming in incomingList ) {
            if ( belt !is Splitter ) return null
            newOutputs.remove( incoming )
        }
        for ( output in belt.getOutputs() ) {
            val target = belts?.getTransportAt( output.targetCell() )
            if ( target is Belt || target is Underground ) return null
            if ( target == null ) {
                newOutputs.add( output )
            }
        }
        return BalancerPart( inputs, newOutputs, PersistentList( belt, belts ) )
    }

    override fun getSize(): Int = belts?.getSize() ?: 0

    override fun hashCode(): Int = ( collectionHashCode( inputs ) * 37 + collectionHashCode( currentOutputs ) ) * 37 + belts.hashCode()

    override fun equals( other: Any? ): Boolean {
        if ( other == null || other !is BalancerPart ) return false
        return areSame( other.belts, belts ) && other.currentOutputs.toHashSet() == currentOutputs.toHashSet() && other.inputs == inputs
    }

//    fun checkBalance(): BalanceReport {
//        val graph = getGraph()
//    }
}

private fun <E> java.util.ArrayList<E>.addIfNotNull( element: E? ) {
    if ( element != null ) add( element )
}

fun <E> collectionHashCode( collection: Collection<E> ): Int {
    val hashes = ArrayList<Int>()
    for ( element in collection ) {
        hashes.add( element.hashCode() )
    }
    hashes.sort()
    return hashes.hashCode()
}