package com.styl.pa

import com.styl.pa.utils.MockInterceptor
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.mockito.MockitoAnnotations

open class TestBase {

    protected lateinit var mockServer: MockWebServer

    @Before
    open fun setUp() {
        MockitoAnnotations.openMocks(this)
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setNewThreadSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        mockServer = MockWebServer()
        mockServer.start(8089)
        mockServer.setDispatcher(MockInterceptor())
    }

    @After
    open fun tearDown() {
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
        mockServer.shutdown()
    }
}