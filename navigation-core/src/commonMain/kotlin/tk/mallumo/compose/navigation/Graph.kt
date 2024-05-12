package tk.mallumo.compose.navigation

open class Graph(val id: String) {
    companion object{
        object ROOT:Graph("root")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Graph) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
