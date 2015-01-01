package me.toxz.kotlin

import com.koushikdutta.async.http.WebSocket
import kotlin.reflect.KMemberFunction0
import com.ybook.app.net.getMainUrl
import java.util.ArrayList
import kotlin.jvm.internal.Intrinsic

/**
 * Created by Carlos on 2014/12/31.
 */

private val LOG_PREFIX = "ybook_"
private val LOG_PREFIX_LENGTH = LOG_PREFIX.length()
private val MAX_LOG_TAG_LENGTH = 23

public inline fun <T : Any> T.after(f: (T) -> Unit): T {
    f(this)
    return this
}

public fun <T : Any> T.makeTag(): String {
    return makeTag(this.javaClass.getSimpleName())
}

public fun makeTag(str: String): String {
    if (str.length() > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) {
        return LOG_PREFIX + str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH - 1)
    }

    return LOG_PREFIX + str
}


public fun <T : Any?> from(calculate: () -> T): FluentCallback1<T> {
    return FluentCallback1(calculate)
}


public class FluentCallback1<T : Any?>(protected val calculate: () -> T) {
    protected var succeedFunction: ((T) -> Unit)? = null
    protected var failFunction: ((T) -> Unit)? = null
    protected var data: T = null
    protected var result: Boolean = false
    protected var supposing: ((T) -> Boolean)? = null

    fun supposing(whether: (T) -> Boolean): FluentCallback1<T> {
        this.supposing = whether
        return this
    }

    fun then(f: (T) -> Unit): FluentCallback1<T> {
        succeedFunction = f
        return this
    }

    fun or(f: (T) -> Unit): FluentCallback1<T> {
        failFunction = f
        return this
    }

    fun exec() {
        Thread {
            data = calculate()
            if (supposing!!(data))
                succeedFunction?.invoke(data)
            else
                failFunction?.invoke(data)
        }.start()
    }

}

fun test() {
    var url: String? = null

    from { getMainUrl() }.supposing { it != null }.then { println("url is: " + it) }.or { println("get url fail!") }.exec()

    object : FluentCallback <String?, Boolean>() {}
            .by { getMainUrl() }
            .supposing { it != null }
            .then (true to { it -> LoginWith(it!!) })
            .either(false to { it -> println("failed") })
            .exec()
}

fun LoginWith(url: String) {

}


public abstract class FluentCallback <D, C> {
    var handlers: ArrayList<Pair<C, ((D) -> Unit)>>? = null
    var firstCondition: Pair<C, ((D) -> Unit)>? = null
    var lastCondition: ((D) -> Unit)? = null
    var judge: ((D) -> C)? = null
    var from: (() -> D)? = null
    var result: C? = null

    public fun by(f: () -> D): FluentCallback<D, C> {
        from = f
        return this
    }


    public fun supposing(condition: (D) -> C): FluentCallback<D, C> {
        judge = condition
        return this
    }

    fun then(p: Pair<C, ((D) -> Unit)>): FluentCallback<D, C> {
        firstCondition = p
        return this
    }

    public fun either(p: Pair<C, ((D) -> Unit)>): FluentCallback <D, C> {
        handlers ?: ArrayList<Pair<C, ((D) -> Unit)>>().add(p)
        return this
    }

    public fun other(f: (D) -> Unit): FluentCallback <D, C> {
        lastCondition = f
        return this
    }

    public fun exec() {
        if (firstCondition == null || from == null) {
            throw IllegalStateException("you must invoke 'then' functions!")
        }
        Thread {
            val d = from!!()
            val j = judge!!(d)
            var isHandler = false
            if (j.equals(firstCondition!!.first)) {
                firstCondition!!.second.invoke(d)
                isHandler = true
            }
            if (handlers != null) {
                for (p in handlers!!) {
                    if (j.equals(p.first)) {
                        p.second.invoke(d)
                        isHandler = true
                        break
                    }
                }
            }
            if (!isHandler) {
                lastCondition?.invoke(d)
            }
        }.start()
    }

}
