# apps/notes/models.py
import uuid
from django.conf import settings
from django.db import models

class Label(models.Model):
    name = models.CharField(max_length=64, unique=True)
    def __str__(self): return self.name

class Note(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)  # ✅ default!
    user = models.ForeignKey(settings.AUTH_USER_MODEL, on_delete=models.CASCADE, related_name="notes")

    KIND_CHOICES = (
        ("shopping", "Shopping"), ("ideas", "Ideas"),
        ("goals", "Goals"), ("routine", "Routine"),
    )
    kind        = models.CharField(max_length=16, choices=KIND_CHOICES)
    title       = models.CharField(max_length=255, blank=True, default="")
    pinned      = models.BooleanField(default=False)
    is_done     = models.BooleanField(default=False)
    finished_at = models.DateTimeField(null=True, blank=True)

    bg_color    = models.CharField(max_length=9, blank=True, default="")
    reminder_at = models.DateTimeField(null=True, blank=True)

    labels      = models.ManyToManyField(Label, blank=True)
    data        = models.JSONField(default=dict, blank=True)

    created_at  = models.DateTimeField(auto_now_add=True)
    updated_at  = models.DateTimeField(auto_now=True)

    class Meta:
        ordering = ["-updated_at"]
