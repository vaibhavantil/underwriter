package com.hedvig.underwriter.util.logging

import com.hedvig.underwriter.util.logger
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class LogCallAspect {

    @Suppress("TooGenericExceptionCaught")
    @Around("@annotation(com.hedvig.underwriter.util.logging.LogCall)")
    fun logExecutionTime(joinPoint: ProceedingJoinPoint): Any {
        val start = System.currentTimeMillis()
        val signature = joinPoint.signature.toShortString()
        val result = try {
            logger.info("Calling $signature, parameters: [${getParams(joinPoint.args)}]")
            joinPoint.proceed()
        } catch (throwable: Throwable) {
            logger.error("Exception during executing $signature,", throwable)
            throw throwable
        }
        val duration = System.currentTimeMillis() - start
        logger.info("Finished executing: $signature, returned: '$result', duration: $duration ms")
        return result
    }

    private fun getParams(args: Array<Any>) =
        args.map { it.toMaskedString() }
            .joinToString(", ")
}
