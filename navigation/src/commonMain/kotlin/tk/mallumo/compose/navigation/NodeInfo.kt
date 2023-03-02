@file:Suppress("CanBeParameter")

package tk.mallumo.compose.navigation

private var nodeIdGenerator = 0

open class NodeInfo(
    id: String,
    var args: ArgumentsNavigation,
    private val atomicId: Int = (nodeIdGenerator++)
) : Node(id) {
    val identifier = "$id:$atomicId"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NodeInfo) return false
        if (!super.equals(other)) return false

        if (args != other.args) return false
        if (identifier != other.identifier) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + identifier.hashCode()
        return result
    }


}
