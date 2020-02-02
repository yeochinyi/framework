package org.koin.sample

import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.inject
import org.koin.core.logger.Level

/**
 * HelloApplication - Application Class
 * use HelloService
 *
 * https://start.insert-koin.io/#/quickstart/kotlin
 */
class HelloApplication : KoinComponent {

    // Inject HelloService
    val helloService: HelloService by inject()

    // display our data
    fun sayHello() = println(helloService.hello())
}

/**
 * run app from here
 */
fun main() {

    startKoin {
        printLogger()
        modules(helloModule)
    }
    HelloApplication().sayHello()
}