package ir.sharif.androidsample.ui.util

import ir.sharif.androidsample.data.dto.NoteUpsert
import kotlinx.coroutines.CoroutineScope
import ir.sharif.androidsample.data.dto.NoteKind as DtoKind
import ir.sharif.androidsample.data.model.NoteKind as ModelKind


import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun CoroutineScope.debouncedPatch(
  waitMs: Long = 350L,
  block: suspend () -> Unit
): () -> Unit {
  var job: Job? = null
  return {
    job?.cancel()
    job = launch {
      delay(waitMs)
      runCatching { block() }
    }
  }
}



fun argbIntToHex(argb: Int): String = "#%08X".format(argb)

fun modelToDtoKind(k: ModelKind): DtoKind = when (k) {
  ModelKind.SHOPPING -> DtoKind.SHOPPING
  ModelKind.IDEAS    -> DtoKind.IDEAS
  ModelKind.GOALS    -> DtoKind.GOALS
  ModelKind.ROUTINE  -> DtoKind.ROUTINE
}
