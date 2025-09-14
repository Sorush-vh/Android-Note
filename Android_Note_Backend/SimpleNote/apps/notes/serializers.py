from rest_framework import serializers
from .models import Note, Label

class LabelSerializer(serializers.ModelSerializer):
    class Meta:
        model = Label
        fields = ["id", "name"]

class NoteSerializer(serializers.ModelSerializer):
    # Accept labels from clients as a list of strings (write side only)
    labels = serializers.ListField(
        child=serializers.CharField(),
        required=False,
        write_only=True
    )

    class Meta:
        model = Note
        fields = [
            "id", "kind", "title", "pinned", "is_done", "finished_at",
            "bg_color", "reminder_at", "labels", "data",
            "created_at", "updated_at",
        ]
        read_only_fields = ["finished_at", "created_at", "updated_at"]

    def to_representation(self, instance):
        """Return labels as list[str] on read."""
        data = super().to_representation(instance)
        # inject labels names for output
        data["labels"] = list(instance.labels.values_list("name", flat=True))
        return data

    def _apply_labels(self, note, labels):
        """Upsert labels based on names and set the M2M."""
        objs = []
        for name in labels or []:
            name = (name or "").strip()
            if not name:
                continue
            obj, _ = Label.objects.get_or_create(name=name)
            objs.append(obj)
        if objs:
            note.labels.set(objs)
        else:
            note.labels.clear()

    def create(self, validated_data):
        labels = validated_data.pop("labels", None)  # list[str] or None
        note = Note.objects.create(**validated_data)  # user injected via .save(user=...)
        if labels is not None:
            self._apply_labels(note, labels)
        return note

    def update(self, instance, validated_data):
        labels = validated_data.pop("labels", None)  # list[str] or None
        for k, v in validated_data.items():
            setattr(instance, k, v)
        instance.save()
        if labels is not None:
            self._apply_labels(instance, labels)
        return instance
