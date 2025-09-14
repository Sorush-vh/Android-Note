package ir.sharif.androidsample.ui.util


import java.time.Instant
import java.time.format.DateTimeFormatter

fun isoIn(msFromNow: Long): String =
  DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochMilli(System.currentTimeMillis() + msFromNow))


fun formatDelay(ms: Long): String {
  if (ms <= 0L) return "now"
  val s = ms / 1000
  val d = s / (24 * 3600)
  val h = (s % (24 * 3600)) / 3600
  val m = (s % 3600) / 60
  return buildString {
    append("in ")
    var added = false
    if (d > 0) { append("${d}d"); added = true }
    if (h > 0) { if (added) append(" "); append("${h}h"); added = true }
    if (m > 0 || !added) { if (added) append(" "); append("${m}m") }
  }
}
