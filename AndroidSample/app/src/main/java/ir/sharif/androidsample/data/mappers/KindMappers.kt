package ir.sharif.androidsample.data.mappers

import ir.sharif.androidsample.data.dto.NoteKind as DtoKind
import ir.sharif.androidsample.data.model.NoteKind as ModelKind

fun ModelKind.toDto(): DtoKind = when (this) {
  ModelKind.SHOPPING -> DtoKind.SHOPPING
  ModelKind.IDEAS    -> DtoKind.IDEAS
  ModelKind.GOALS    -> DtoKind.GOALS
  ModelKind.ROUTINE  -> DtoKind.ROUTINE
}
