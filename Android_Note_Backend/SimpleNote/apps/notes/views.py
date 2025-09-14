# apps/notes/views.py
from rest_framework import viewsets, filters
from rest_framework.decorators import action
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response
from .models import Note, Label
from .serializers import NoteSerializer, LabelSerializer

class NoteViewSet(viewsets.ModelViewSet):
    serializer_class = NoteSerializer
    permission_classes = [IsAuthenticated]
    filter_backends = [filters.SearchFilter]
    search_fields = ["title"]

    def get_queryset(self):
        return Note.objects.filter(user=self.request.user).order_by("-updated_at")

    def perform_create(self, serializer):
        # inject user so serializer.create receives it via validated_data
        serializer.save(user=self.request.user)

    @action(detail=False, methods=["get"])
    def recent(self, request):
        qs = self.get_queryset().order_by("-updated_at")[:20]
        return Response(self.get_serializer(qs, many=True).data)

    @action(detail=False, methods=["get"])
    def finished(self, request):
        qs = self.get_queryset().filter(is_done=True).order_by("-updated_at")[:10]
        return Response(self.get_serializer(qs, many=True).data)

class LabelViewSet(viewsets.ModelViewSet):
    # keep it simple for now; you can filter to only labels used by this user later
    queryset = Label.objects.all().order_by("name")
    serializer_class = LabelSerializer
    permission_classes = [IsAuthenticated]
