package com.styl.pa

// annotation to exclude a function/class from jacoco test report (coverage)
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS, AnnotationTarget.FIELD)
annotation class ExcludeFromJacocoGeneratedReport