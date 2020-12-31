@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package tk.mallumo.compose.navigation

import android.os.Bundle
import java.util.concurrent.atomic.AtomicInteger

private val nodeIdGenerator = AtomicInteger()

open class ImplNode(
    val frameID: String,
    var args: Bundle,
    var nodeID: Int = nodeIdGenerator.getAndIncrement()
) {
    val identifier get() = "$frameID:$nodeID:"
}