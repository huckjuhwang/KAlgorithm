package org.example.programmers


/**
 * @see: https://school.programmers.co.kr/learn/courses/30/lessons/340213
 */
fun main() {
    println(
        Solution().solution(
            "30:00", "00:08", "00:00", "00:05", listOf("prev").toTypedArray()
        )
    )
}


class Solution {
    fun solution(video_len: String, pos: String, op_start: String, op_end: String, commands: Array<String>): String {
        val videoTime = Time.of(video_len)
        val openning = Openning(
            startTime = Time.of(op_start),
            endTime = Time.of(op_end),
        )

        /**
         * 오프닝 시간 사이에 있다면, 오프닝 시간 종료시점으로 이동
         */
        var currentTime = Time.of(pos).checkOpenning(openning)
        commands.forEach { command ->
            when (command) {
                "prev" -> currentTime = currentTime.prev(openning)
                "next" -> currentTime = currentTime.next(videoTime, openning)
            }
        }

        return currentTime.toString()
    }


}

data class Openning(
    val startTime: Time,
    val endTime: Time,
) {

}

data class Time(
    val minute: Int,
    val second: Int,
) {

    fun checkOpenning(open: Openning): Time {
        return if(isBetween(open.startTime, open.endTime)) { open.endTime } else { this }
    }

    /**
     * startTime, endTime 입력 받아, 요구 사항을 만족하는지 판별한다.
     */
    private fun isBetween(startTime: Time, endTime: Time): Boolean {
        return convertSecond(this) in convertSecond(startTime) .. convertSecond(endTime)
    }


    /**
     * 앞으로 댕기기(-10초)
     */
    fun prev(open: Openning): Time {
        val currentSecond = convertSecond(this) - 10
        if(currentSecond < 0) {
            return DEFAULT_TIME.checkOpenning(open)
        }

        return of(currentSecond).checkOpenning(open)
    }


    /**
     * 뒤로 댕기기(+10초)
     */
    fun next(videoTime: Time, open: Openning): Time {
        val videoSecond = convertSecond(videoTime)

        val currentSecond = convertSecond(this) + 10
        if(currentSecond > videoSecond) {
            return of(videoSecond)
        }

        return of(currentSecond).checkOpenning(open)
    }


    private fun convertSecond(time: Time): Int {
        return (time.minute * 60) + time.second
    }


    override fun toString(): String {
        val minuteStr = if(minute < 10) { "0$minute" } else { minute }
        val secondStr = if(second < 10)  { "0$second" } else { second }

        return "$minuteStr:$secondStr"
    }

    companion object {
        private val DEFAULT_TIME = Time(minute = 0, second = 0)

        fun of(data: String): Time {
            val temp = data.split(":")

            return Time(
                minute = temp[0].toInt(),
                second = temp[1].toInt()
            )
        }

        fun of(second: Int): Time {
            return kotlin.runCatching {
                Time(
                    minute = second / 60,
                    second = second % 60
                )
            }.getOrDefault(DEFAULT_TIME)
        }
    }
}