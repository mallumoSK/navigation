package tk.mallumo.compose.navigation

interface ImplBackStack {
    fun add(endOffset: Int = 0, nodes: List<Pair<Node, ArgumentsNavigation>>)
    fun clearAll()
    fun clear(startOffset: Int = 0, endOffset: Int = 0)
    fun size():Int
}
