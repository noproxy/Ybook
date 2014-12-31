package me.toxz.kotlin

import com.koushikdutta.async.http.WebSocket
import kotlin.reflect.KMemberFunction0
import com.ybook.app.net.getMainUrl
import java.util.ArrayList

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

//
//public fun <T : Any?> T.from(judge: () -> T): FluentCallbackImpl<T> {
//    return FluentCallbackImpl(judge)
//}
//
//
//public class FluentCallbackImpl<T : Any?>(val judge: () -> T) {
//    var succeed: ((T) -> Unit)? = null
//    var fail: ((T) -> Unit)? = null
//    var answer: T = null
//    var result: Boolean = false
//
//    fun supposing(whether: (T) -> Boolean): FluentCallbackImpl<T> {
//        result = whether(answer)
//        return this
//    }
//
//    fun then(f: (T) -> Unit): FluentCallbackImpl<T> {
//        succeed = f
//        return this
//    }
//
//    fun or(f: (T) -> Unit): FluentCallbackImpl<T> {
//        fail = f
//        return this
//    }
//
//    fun addCondition(condition: Any, f: () -> Unit): FluentCallbackImpl<T> {
//        return this
//    }
//
//    fun exec() {
//        Thread {
//            answer = judge()
//            if (result)
//                succeed?.invoke(answer)
//            else
//                fail?.invoke(answer)
//        }.start()
//    }
//
//}

fun test() {
    var url: String? = null

    //    url.from { getMainUrl() }.supposing { it != null }.then { println("url is: " + it) }.or { println("get url fail!") }.exec()

    object : FluentCallback <String?, Boolean>() {}
            .from { getMainUrl() }
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

    public fun from(f: () -> D): FluentCallback<D, C> {
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

public class UrlFluentCallBack : FluentCallback <String, Boolean>()
