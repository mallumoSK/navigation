package tk.mallumo.compose.navigation

import android.os.Bundle
import java.util.concurrent.atomic.AtomicInteger

private val nodeIdGenerator by lazy {
    AtomicInteger()
}

open class ImplNode(
    val frameID: String,
    var args: Bundle,
    private var nodeID: Int = nodeIdGenerator.getAndIncrement()
) {
    val identifier get() = "$frameID:$nodeID:"

    override operator fun equals(other: Any?): Boolean {
        return other is ImplNode && other.identifier == this.identifier
    }

    override fun hashCode(): Int {
        var result = args.hashCode()
        result = 31 * result + identifier.hashCode()
        return result
    }
}